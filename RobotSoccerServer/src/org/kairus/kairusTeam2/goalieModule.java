package org.kairus.kairusTeam2;

import org.jbox2d.common.Vec2;
import org.kairus.api.Game;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class goalieModule extends RobotModule {
	goalieModule(int robotNumber, Team team) {
		super(robotNumber, team);
		staticUtility = true;
	}

	@Override
	int assessUtility(Team team) {
		return 50;
	}

	double tolerance = 8;
	double idealX;
	double idealY;
	Vec2 lastBallVel = new Vec2(0,0);

	@Override
	boolean execute(Team team) {
		Vec2 ballPos = team.getBallPosition();
		//ballPos.add(team.getBallVelocity().mul(2));
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?0:SCREEN_WIDTH,SCREEN_HEIGHT/2);
		Vec2 vel = me.getVelocity();
		
		Vec2 ballVel = team.getBallVelocity().mul(1);
		if (Game.simulation && ballVel.x == 0 && ballVel.y == 0) ballVel = lastBallVel;
		lastBallVel = ballVel;
		ballPos.add(lastBallVel.mul(3));
		
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
		double idealRotation = Math.PI/2;
		double angleToBall = Math.atan2(ballPos.y-goalpos.y, ballPos.x-goalpos.x);
		idealX = team.getTeamNumber()==0?10+Math.cos(angleToBall)*10:SCREEN_WIDTH-10+Math.cos(angleToBall)*10;
		idealY = SCREEN_HEIGHT/2+Math.sin(angleToBall)*55;

		return driveToPoint(idealX, idealY, 0, idealRotation, 8, 40);
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.green);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
	}
}
