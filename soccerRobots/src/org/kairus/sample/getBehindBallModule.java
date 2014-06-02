package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class getBehindBallModule extends RobotModule {
	
	int initialRobot;
	getBehindBallModule(int robotNumber, Team team) {
		super(robotNumber, team);
		initialRobot = robotNumber;
		if (initialRobot == 3) distance = 70; //Attacker2 stays further back.
	}

	@Override
	int assessUtility(Team team) {
		return 80;
	}

	double tolerance = 0;
	double distance = 35;

	double idealX;
	double idealY;
	
	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		Vec2 ballPos = team.getBallPosition();
		Vec2 ballVel = team.getBallVelocity().mul(0.5f);
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);

		double angleFromGoalToBall = MathFunctions.angleTo(ballPos.add(ballVel), goalpos);

		idealX = ballPos.add(ballVel).x-Math.cos(angleFromGoalToBall)*distance;
		idealY = ballPos.add(ballVel).y-Math.sin(angleFromGoalToBall)*distance;
		idealPosition = new Vec2((float)idealX, (float) idealY);

		double distanceToBall = MathFunctions.distance(pos, ballPos);
		double distanceToIdeal = MathFunctions.distance(pos, new Vec2((float)idealX, (float)idealY));


		double angleToBall = MathFunctions.angleTo(pos, ballPos);
		double angleToIdeal = MathFunctions.angleTo(pos, new Vec2((float)idealX, (float)idealY));

		double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToIdeal));
		if (Math.abs(angleDifference) < Math.PI/8 && distanceToBall+15<distanceToIdeal){
			//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			return false;		
		}
		
		

		
		double idealRotation = 0;
		
		
		// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
		double vecX = (idealX-pos.x) - vel.x/6;
		double vecY = (idealY-pos.y) - vel.y/6;

		double distance = Math.sqrt(vecX*vecX + vecY*vecY);
		
		if (distance > tolerance/2){ //go go!
			float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
			int side = 1;
			if (angleOffset>Math.PI){
				angleOffset-=Math.PI;
				side*=-1;
			}
			team.SetSpinInput(robotNumber, (float) (angleOffset*10*side));
			team.SetSpeedInput(robotNumber, (float) (1-Math.abs(angleOffset))*side+0.25f*side);
		}else{
			//System.out.println("Angle difference: "+angleDifference(idealRotation, robot[i].getDirection()));
			team.SetSpinInput(robotNumber, (float) MathFunctions.angleDifference(idealRotation, me.getDirection()));
			float angleOffset = (float) MathFunctions.headingDifference(me, pos.x, pos.y);
			team.SetSpeedInput(robotNumber, (float) (-velocity*0.1-Math.abs(angleOffset)));
			//team.SetSpeedInput(robotNumber, 0);
		}
		return true;
	}

	@Override
	void render(Graphics g) {
		/*
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.white);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
		*/
	}
}
