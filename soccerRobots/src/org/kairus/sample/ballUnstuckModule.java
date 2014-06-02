package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ballUnstuckModule extends RobotModule {
	ballUnstuckModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}

	int countTillSpin = 0;
	int maxCountTillSpin = 50/((KairusTeam)team).framesBetweenCalculation;
	int movementTolerance = 1;
	int range = 32;
	Vec2 oldBallPos;
	@Override
	int assessUtility(Team team) {
		Vec2 ballPos = team.getBallPosition();
		Vec2 myPos = team.getRobots()[robotNumber].getPosition();
		

		if (MathFunctions.distance(myPos, ballPos) <= range){
			if (oldBallPos != null && MathFunctions.distance(ballPos, oldBallPos) < movementTolerance)
				countTillSpin++;
			else
				countTillSpin = 0;
			if (countTillSpin > maxCountTillSpin){
				return 95;
			}
		}else
			countTillSpin = 0;
		
		oldBallPos = ballPos;
		
		return 0;
	}

	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		
		int spinDirection = 1;
		if (pos.y>=SCREEN_HEIGHT/2) spinDirection *= -1;
		if (team.getTeamNumber() == 1) spinDirection *= -1;
		
		team.SetSpinInput(robotNumber, spinDirection);
		team.SetSpeedInput(robotNumber, -0.1f);
		
		return true;
	}

	@Override
	void render(Graphics g) {
		/*
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.green);
		g.drawOval((float)pos.x-range, (float)pos.y-range, range*2, range*2);
		*/
	}
}
