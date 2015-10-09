package org.kairus.kairusTeam;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class spiralModule extends RobotModule {
	int initialRobot;
	spiralModule(int robotNumber, Team team) {
		super(robotNumber, team);
		staticUtility = true;
		initialRobot = robotNumber;
		if (team.getTeamNumber() == 1)
			frame = (float) ((2*Math.PI)/10f);
	}

	@Override
	int assessUtility(Team team) {
		frame+=0.005*1;//((KairusTeam)team).framesBetweenCalculation;
		if (org.kairus.api.Server.getState() == "circles"){
			return 105;
		}
		return 1;
	}

	double tolerance = 10;
	float frame = 0f;
	int radius = 150;
	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX = SCREEN_WIDTH/2 +Math.cos(frame+(initialRobot/5f)*2f*Math.PI)*radius;
		double idealY = SCREEN_HEIGHT/2+Math.sin(frame+(initialRobot/5f)*2f*Math.PI)*radius;
		if (initialRobot == 0)
			System.out.println(idealX);

		return driveToPoint(idealX, idealY, 1, 0, 20, 100);
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX = SCREEN_WIDTH/2 +Math.cos(frame+(initialRobot/5f)*2f*Math.PI)*radius;
		double idealY = SCREEN_HEIGHT/2+Math.sin(frame+(initialRobot/5f)*2f*Math.PI)*radius;
		
		g.setColor(Color.blue);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
		
	}
}
