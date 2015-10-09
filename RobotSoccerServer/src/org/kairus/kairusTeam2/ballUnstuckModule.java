package org.kairus.kairusTeam2;

import org.jbox2d.common.Vec2;
import org.kairus.api.Game;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ballUnstuckModule extends RobotModule {
	ballUnstuckModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}
	
	int range = 22;
	Vec2 lastBallVel = new Vec2(0,0);
	float ballVelThreshhold = 0.25f;
	
	@Override
	int assessUtility(Team team) {
		
		
		
		
		Vec2 ballPos = team.getBallPosition();
		Vec2 myPos = team.getRobots()[robotNumber].getPosition();

		Vec2 ballVel = team.getBallVelocity().mul(1);
		if (Game.simulation && ballVel.x == 0 && ballVel.y == 0) ballVel = lastBallVel;
		lastBallVel = ballVel;

		//System.out.println("Vel: "+lastBallVel.length()+" dist: "+MathFunctions.distance(myPos, ballPos));
		if (MathFunctions.distance(myPos, ballPos) <= range && lastBallVel.length() < ballVelThreshhold){
			return 85;
		}
		
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
		team.SetSpeedInput(robotNumber, 0);
		//System.out.println("Running!");
		idealPosition = pos;
		return true;
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.green);
		g.drawOval((float)pos.x-range, (float)pos.y-range, range*2, range*2);
	}
}
