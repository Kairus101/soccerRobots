package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Graphics;

public class goalieModule extends RobotModule {
	goalieModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}

	@Override
	int assessUtility(Team team) {
		return 50;
	}

	double tolerance = 4;
	@Override
	boolean execute(Team team) {
		Vec2 ballPos = team.getBallPosition();
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?0:SCREEN_WIDTH,SCREEN_HEIGHT/2);
		Vec2 vel = me.getVelocity();
		double idealX;
		double idealY;
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
		double idealRotation = Math.PI;
		double angleToBall = Math.atan2(ballPos.y-goalpos.y, ballPos.x-goalpos.x);
		idealX = team.getTeamNumber()==0?10+Math.cos(angleToBall)*30:SCREEN_WIDTH-10+Math.cos(angleToBall)*30;
		idealY = SCREEN_HEIGHT/2+Math.sin(angleToBall)*55;
		idealPosition = new Vec2((float)idealX, (float) idealY);
		
		double distance = Math.sqrt((idealY-pos.y)*(idealY-pos.y) + (idealX-pos.x)*(idealX-pos.x));
		
		// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
		double vecX = (idealX-pos.x)*2 - vel.x/6;
		double vecY = (idealY-pos.y)*2 - vel.y/6;
		
		if (distance > tolerance/2){ //go go!
			float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
			team.SetSpinInput(robotNumber, (float) (angleOffset*10-me.getAngularVelocity()));
			team.SetSpeedInput(robotNumber, (float) (1-Math.abs(angleOffset)));
			
		}else{
				//System.out.println("Angle difference: "+angleDifference(idealRotation, robot[i].getDirection()));
				team.SetSpinInput(robotNumber, (float) MathFunctions.angleDifference(idealRotation, me.getDirection()));
				float angleOffset = (float) MathFunctions.headingDifference(me, pos.x-vel.x, pos.y-vel.y);
				team.SetSpeedInput(robotNumber, (float) (-velocity-Math.abs(angleOffset)));
				team.SetSpeedInput(robotNumber, 0);
		}
		return true;
	}

	@Override
	void render(Graphics g) {
	}
}
