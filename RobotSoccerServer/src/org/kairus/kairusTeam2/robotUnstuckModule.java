package org.kairus.kairusTeam2;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class robotUnstuckModule extends RobotModule {
	robotUnstuckModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}

	int countTillMove = 0;
	int maxCountTillMove = 200/((KairusTeam2)team).framesBetweenCalculation;
	int maxCount = 230/((KairusTeam2)team).framesBetweenCalculation;
	int movementTolerance = 2;
	Vec2 oldPos;
	@Override
	int assessUtility(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 myPos = me.getPosition();
		if (oldPos != null && MathFunctions.distance(myPos, oldPos) < movementTolerance && (me.GetSpeedInput()>0.1 || Math.abs(me.GetSpinInput())>0.1))
			countTillMove++;
		else
			countTillMove--;
		oldPos = myPos;
		if (countTillMove > maxCount) countTillMove = maxCount;
		if (countTillMove > maxCountTillMove){
			return 96; // We need to get out.
		}
		return 0;
	}

	@Override
	boolean execute(Team team) {
		team.SetSpeedInput(robotNumber, -0.2f/5);
		team.SetSpinInput(robotNumber, 0f);
		return true;
	}

	@Override
	void render(Graphics g) {
	}
}
