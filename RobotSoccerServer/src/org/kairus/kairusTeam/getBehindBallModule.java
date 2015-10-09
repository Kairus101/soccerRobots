package org.kairus.kairusTeam;

import org.jbox2d.common.Vec2;
import org.kairus.api.Game;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class getBehindBallModule extends RobotModule {

	double distance = 30;
	double addedBallDistance = 50;
	int initialRobot;
	getBehindBallModule(int robotNumber, Team team) {
		super(robotNumber, team);
		initialRobot = robotNumber;
		if (initialRobot == 3) {
			distance = 180; //Attacker2 stays further back.
			addedBallDistance = 90;
		}
		staticUtility = true;
	}

	@Override
	int assessUtility(Team team) {
		if (initialRobot == 0){
			if (org.kairus.api.Server.getState() == "freeKick")
				return 115;
			return 0;
		}

		Vec2 pos = team.getRobots()[robotNumber].getPosition();
		Vec2 ownGoalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);
		//if (MathFunctions.distance(ownGoalpos, pos) < 200)
		//	return 0;
		
		return 80;
	}

	double tolerance = 0;

	double idealX;
	double idealY;
	
	Vec2 lastBallVel = new Vec2(0,0);
	
	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		Vec2 ballVel = team.getBallVelocity().mul(0.5f);
		if (Game.simulation && ballVel.x == 0 && ballVel.y == 0) ballVel = lastBallVel;
		lastBallVel = ballVel;
		
		//System.out.println(team.getTeamNumber()+" "+robotNumber+" "+ballVel.x+" "+ballVel.y);
		
		Vec2 ballPos = team.getBallPosition().add(ballVel.mul(1f));
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);
		Vec2 ownGoalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);
		
		// Add more prediction, if we have to turn also.
		double angleFromGoal = MathFunctions.headingDifference(me, goalpos.x, goalpos.y);
		ballPos = ballPos.add(ballVel.mul((float) Math.abs(angleFromGoal)*1));
		
		
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);

		double angleFromGoalToBall = MathFunctions.angleTo(ballPos, goalpos);

		idealX = ballPos.x-Math.cos(angleFromGoalToBall)*distance;
		idealY = ballPos.y-Math.sin(angleFromGoalToBall)*distance;

		double distanceToBall = MathFunctions.distance(pos, ballPos);
		double distanceToIdeal = MathFunctions.distance(pos, new Vec2((float)idealX, (float)idealY));


		double angleToBall = MathFunctions.angleTo(pos, ballPos);

		idealX += Math.cos(angleToBall)*30;
		idealY += Math.sin(angleToBall)*30;
		double angleToIdeal = MathFunctions.angleTo(pos, new Vec2((float)idealX, (float)idealY));
		double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToIdeal));
		double idealRotation = MathFunctions.angleTo(pos, goalpos);
		
		if ( /*(initialRobot != 3) &&*/ ( idealX < 0 || idealX > SCREEN_WIDTH || idealY < 0 || idealY > SCREEN_HEIGHT)){
			idealX = ballPos.x;
			idealY = ballPos.y;
		}else if ((Math.abs(angleToBall) > Math.PI/2 && distanceToBall+30<distanceToIdeal) || MathFunctions.distance(ownGoalpos, pos) < 200){
			//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			return false;
		}
		
		//idealX += (idealX-pos.x)*0.5;
		//idealY += (idealY-pos.y)*0.5;
		

		return driveToPoint(idealX, idealY, 0, idealRotation, 4, 40);
	}

	@Override
	void render(Graphics g) {
		
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.white);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
		
	}
}
