package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class spiralModule extends RobotModule {
	spiralModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}

	@Override
	int assessUtility(Team team) {
		return 100;
	}

	double tolerance = 10;
	float frame = 0f;
	@Override
	boolean execute(Team team) {
		System.out.println(robotNumber+": Spiral!");
		frame+=0.02;
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX = 300+Math.cos(frame+(robotNumber/3f)*2f*Math.PI + (team.getTeamNumber()==0f?0:Math.PI/3f))*200;
		double idealY = 300+Math.sin(frame+(robotNumber/3f)*2f*Math.PI + (team.getTeamNumber()==0?0f:Math.PI/3f))*200;
		double vecX = (idealX-pos.x) - vel.x;
		double vecY = (idealY-pos.y) - vel.y;
		float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
		team.SetSpinInput(robotNumber, (float) (angleOffset));
		team.SetSpeedInput(robotNumber, (float) (1-Math.abs(angleOffset)));
		return true;
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX = 300+Math.cos(frame+(robotNumber/3f)*2f*Math.PI + (team.getTeamNumber()==0f?0:Math.PI/3f))*200;
		double idealY = 300+Math.sin(frame+(robotNumber/3f)*2f*Math.PI + (team.getTeamNumber()==0?0f:Math.PI/3f))*200;
		
		g.setColor(Color.blue);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
		
	}
}
