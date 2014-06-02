package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Graphics;

public class defenseModule extends RobotModule {
	
	float[] angleOffsetArray = new float[]{-1.57f, -0.5f, 1.57f, 0f, 0.5f};
	
	int initialRobot;
	defenseModule(int robotNumber, Team team) {
		super(robotNumber, team);
		initialRobot = robotNumber;
	}

	@Override
	int assessUtility(Team team) {
		//go into defense if they are closer to the ball than us
		double lowestDistance = 99999;
		double lowestEnemyDistance = 99999;
		Robot[] myRobots = team.getRobots();
		Robot[] enemyRobots = team.getEnemyRobots();
		for (int i = 0;i<3;i++){
			double distance = MathFunctions.distance(myRobots[i].getPosition(), team.getBallPosition());
			if (distance<lowestDistance)lowestDistance=distance;
			distance = MathFunctions.distance(enemyRobots[i].getPosition(), team.getBallPosition());
			if (distance<lowestEnemyDistance)lowestEnemyDistance=distance;
		}
		//System.out.println(lowestDistance-lowestEnemyDistance+0.1);
		int toReturn =(int) ((lowestDistance-lowestEnemyDistance)/5);
		//if (toReturn < 20) toReturn = 20;
		return toReturn + 50;
	}

	double tolerance = 10;
	@Override
	boolean execute(Team team) {
		Vec2 ballPos = team.getBallPosition();
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		//Vec2 goalpos = new Vec2(team.getTeamNumber()==0?0:SCREEN_WIDTH,SCREEN_HEIGHT/2);
		double idealX;
		double idealY;
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
		double idealRotation = 0;
		//System.out.println(robotNumber+": Defense!");
		double angleToBall = Math.atan2(ballPos.y-pos.y, ballPos.x-pos.x);
		angleToBall += angleOffsetArray[initialRobot];
		//System.out.println(angleOffsetArray[initialRobot]);
		idealX = team.getTeamNumber()==0?10+Math.cos(angleToBall)*180:SCREEN_WIDTH-10+Math.cos(angleToBall)*180;
		idealY = 300+Math.sin(angleToBall)*150;
		idealPosition = new Vec2((float)idealX, (float) idealY);
		idealRotation = Math.PI;
		
		double distance = Math.sqrt((idealY-pos.y)*(idealY-pos.y) + (idealX-pos.x)*(idealX-pos.x));
		
		// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
		double vecX = (idealX-pos.x) - vel.x/6;
		double vecY = (idealY-pos.y) - vel.y/6;
		
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
		// TODO Auto-generated method stub
	}
}
