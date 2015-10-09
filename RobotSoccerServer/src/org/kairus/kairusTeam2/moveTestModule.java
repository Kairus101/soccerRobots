package org.kairus.kairusTeam2;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class moveTestModule extends RobotModule {
	
	int initialRobot;
	moveTestModule(int robotNumber, Team team) {
		super(robotNumber, team);
		initialRobot = robotNumber;
		if (initialRobot == 3) distance = 150; //Attacker2 stays further back.
		staticUtility = true;
	}

	@Override
	int assessUtility(Team team) {
		return 100;
	}

	double tolerance = 0;
	double distance = 35;

	double idealX;
	double idealY;
	
	Vec2 lastBallVel = new Vec2(0,0);
	
	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		Vec2 ballVel = team.getBallVelocity().mul(1);
		if (ballVel.x == 0 && ballVel.y == 0) ballVel = lastBallVel;
		lastBallVel = ballVel;
		
		Vec2 ballPos = team.getBallPosition().add(ballVel.mul(2f));

		idealX = ballPos.x;
		idealY = ballPos.y;
		idealPosition = new Vec2((float)idealX, (float) idealY);
		return driveToPoint(idealX, idealY, 1, 0, 5, 40);
	}

	@Override
	void render(Graphics g) {
		
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.white);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
		
	}
}
