package org.kairus.kairusTeam2;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Server;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class stopMovingModule extends RobotModule {
	int initialRobot;
	stopMovingModule(int robotNumber, Team team) {
		super(robotNumber, team);
		staticUtility = true;
		initialRobot = robotNumber;
	}

	@Override
	int assessUtility(Team team) {
		if (org.kairus.api.Server.getState() == "stop"){
			return 120;
		}
		return 0;
	}

	
	@Override
	boolean execute(Team team) {
		team.SetSpinInput(robotNumber, 0);
		team.SetSpeedInput(robotNumber, 0);
		return true;
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.green);
		//g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
	}
}
