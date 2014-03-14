package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class getBehindBallModule extends RobotModule {
	getBehindBallModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}

	@Override
	int assessUtility(Team team) {
		return 80;
	}

	double tolerance = 10;
	double distance = 80;
	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		Vec2 ballPos = team.getBallPosition().mul(100);
		Vec2 ballVel = team.getBallVelocity().mul(100);
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?600:0,300);
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);

		double angleFromGoalToBall = MathFunctions.angleTo(ballPos.add(ballVel), goalpos);

		double idealX = ballPos.add(ballVel).x-Math.cos(angleFromGoalToBall)*distance;
		double idealY = ballPos.add(ballVel).y-Math.sin(angleFromGoalToBall)*distance;

		double distanceToBall = MathFunctions.distance(pos, ballPos);
		double distanceToIdeal = MathFunctions.distance(pos, new Vec2((float)idealX, (float)idealY));


		double angleToBall = MathFunctions.angleTo(pos, ballPos);
		double angleToIdeal = MathFunctions.angleTo(pos, new Vec2((float)idealX, (float)idealY));

		double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToIdeal));
		if (Math.abs(angleDifference) < Math.PI/4 && distanceToBall+30<distanceToIdeal){
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			return false;		
		}
		
		

		
		double idealRotation = 0;
		
		double distance = Math.sqrt((idealY-pos.y)*(idealY-pos.y) + (idealX-pos.x)*(idealX-pos.x));
		
		// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
		double vecX = (idealX-pos.x) - vel.x;
		double vecY = (idealY-pos.y) - vel.y;
		
		if (distance > tolerance/2){ //go go!
			float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
			team.SetSpinInput(robotNumber, (float) (angleOffset*10));
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
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 ballPos = team.getBallPosition().mul(100);
		Vec2 ballVel = team.getBallVelocity().mul(100);
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?600:0,300);
		double angleFromGoalToBall = MathFunctions.angleTo(ballPos.add(ballVel), goalpos);
		double idealX = ballPos.x-Math.cos(angleFromGoalToBall)*distance;
		double idealY = ballPos.y-Math.sin(angleFromGoalToBall)*distance;
		
		g.setColor(Color.white);
		//g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
	}
}
