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
		frame+=0.02*((KairusTeam)team).framesBetweenCalculation;
		return 1;
	}

	double tolerance = 10;
	float frame = 0f;
	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX = SCREEN_WIDTH/2 +Math.cos(frame+(robotNumber/5f)*2f*Math.PI + (team.getTeamNumber()==0f?0:Math.PI/5f))*200;
		double idealY = SCREEN_HEIGHT/2+Math.sin(frame+(robotNumber/5f)*2f*Math.PI + (team.getTeamNumber()==0?0f:Math.PI/5f))*200;
		idealPosition = new Vec2((float)idealX, (float) idealY);
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
		double idealX = SCREEN_WIDTH/2 +Math.cos(frame+(robotNumber/3f)*2f*Math.PI + (team.getTeamNumber()==0f?0:Math.PI/3f))*200;
		double idealY = SCREEN_HEIGHT/2+Math.sin(frame+(robotNumber/3f)*2f*Math.PI + (team.getTeamNumber()==0?0f:Math.PI/3f))*200;
		
		g.setColor(Color.blue);
		//g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
		
	}
}
