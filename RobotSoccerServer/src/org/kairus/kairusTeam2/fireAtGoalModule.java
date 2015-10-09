package org.kairus.kairusTeam2;

import org.jbox2d.common.Vec2;
import org.kairus.api.Game;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class fireAtGoalModule extends RobotModule {

	int initialRobot;
	fireAtGoalModule(int robotNumber, Team team) {
		super(robotNumber, team);
		initialRobot = robotNumber;
		staticUtility = true;
	}

	int range = 150;
	int smallRange = 30;
	float largeAngle = 0.18f;

	boolean spin=false;
	int direction = 1;


	Vec2 lastBallVel = new Vec2(0,0);
	@Override
	int assessUtility(Team team) {
		spin = false;
		//try to fire if we have clear line of sight
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();

		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);

		Vec2 ballVel = team.getBallVelocity();
		if (Game.simulation && ballVel.x == 0 && ballVel.y == 0) ballVel = lastBallVel;
		lastBallVel = ballVel;

		Vec2 ballPos = team.getBallPosition();

		// Add more prediction, if we have to turn also.
		double angleFromGoal = MathFunctions.headingDifference(me, goalpos.x, goalpos.y);
		double distanceToBallNoVel = MathFunctions.distance(pos, ballPos);
		ballPos = ballPos.add(ballVel.mul((float) Math.abs(angleFromGoal)*1));


		double distanceToBall = MathFunctions.distance(pos, ballPos);
		double angleToBall = MathFunctions.angleTo(pos, ballPos);
		double angleToGoal = MathFunctions.angleTo(pos, goalpos);

		double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToGoal));
		double headerDiff = MathFunctions.headingDifference(me, goalpos.x, goalpos.y);
		//System.out.println(angleDifference);
		
		//if (distanceToBall < smallRange)
		//	System.out.println(angleDifference+" "+angleToBall);
		
		if (((distanceToBall < range && angleDifference < largeAngle) || (distanceToBall < smallRange && angleDifference < Math.PI/4) ) ){//&& headerDiff < Math.PI/6){
			//System.out.println("firing! "+distanceToBall);
			//System.out.println("Robot: "+robotNumber+" utility: "+((int) ((Math.PI-angleDifference)*100)));
			if (initialRobot == 0 && org.kairus.api.Server.getState() == "freeKick"){
				return (int) ((1-angleDifference)*100)+115;
			}
			return 100;//(int) ((1-angleDifference)*100);
		}//else
		//System.out.println("d: "+distanceToBall+" x1:"+pos.x+" y1:"+pos.y+" x2:"+ballPos.x+" y2:"+ballPos.y);
		if (distanceToBallNoVel < smallRange){// && angleDifference < Math.PI){
			if (team.getTeamNumber() == 0)
				System.out.println(initialRobot+" "+angleDifference);
			
			direction = -1;
			if (angleToBall < 0 || angleToBall > Math.PI)
				direction = 1;
			if (team.getTeamNumber() == 1) direction *= -1;
			spin = true;
			return 150;
		}
		return 0;
	}

	double tolerance = 0;
	@Override
	boolean execute(Team team) {
		if (spin){ // kick
			idealPosition = null; // Don't swap out of this behavior!
			team.SetSpinInput(robotNumber, direction);
			team.SetSpeedInput(robotNumber, 0);
			return true;
		}
		Vec2 ballPos = team.getBallPosition();
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX;
		double idealY;
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
		double idealRotation = 0;
		//System.out.println(robotNumber+": Fire!");
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH-10:10,SCREEN_HEIGHT/2);
		double angleToBall = MathFunctions.angleTo(pos, ballPos);
		double angleToGoal = MathFunctions.angleTo(pos, goalpos);

		double angleToAim = (angleToBall+angleToGoal)/2;

		idealX = pos.x+Math.cos(angleToAim)*300;//ballPos.x;
		idealY = pos.y+Math.sin(angleToAim)*300;//ballPos.y;


		idealRotation = 0;

		boolean toReturn = driveToPoint(idealX, idealY, 2, idealRotation, 1, 50);
		idealPosition = null; // Don't swap out of this behavior!
		return toReturn;
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.red);
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH-10:10,SCREEN_HEIGHT/2);
		g.drawLine((float)pos.x, (float)pos.y, goalpos.x, goalpos.y);
		g.drawOval((float)pos.x-range, (float)pos.y-range, range*2, range*2);
		g.drawOval((float)pos.x-smallRange, (float)pos.y-smallRange, smallRange*2, smallRange*2);

		Vec2 ballPos = team.getBallPosition();
		double angleToBall = MathFunctions.angleTo(pos, ballPos);
		double angleToGoal = MathFunctions.angleTo(pos, goalpos);
		double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToGoal));
		double headerDiff = MathFunctions.headingDifference(me, goalpos.x, goalpos.y);
		g.drawLine((float)pos.x, (float)pos.y, (float)(pos.x+Math.cos(angleToGoal-largeAngle)*range), (float)(pos.y+Math.sin(angleToGoal-largeAngle)*range));
		g.drawLine((float)pos.x, (float)pos.y, (float)(pos.x+Math.cos(angleToGoal+largeAngle)*range), (float)(pos.y+Math.sin(angleToGoal+largeAngle)*range));
	}

	void alwaysRender(Graphics g){
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(new Color(1, 0, 0, 0.25f));
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH-10:10,SCREEN_HEIGHT/2);
		g.drawLine((float)pos.x, (float)pos.y, goalpos.x, goalpos.y);
		g.drawOval((float)pos.x-range, (float)pos.y-range, range*2, range*2);
		g.drawOval((float)pos.x-smallRange, (float)pos.y-smallRange, smallRange*2, smallRange*2);

		Vec2 ballPos = team.getBallPosition();
		double angleToBall = MathFunctions.angleTo(pos, ballPos);
		double angleToGoal = MathFunctions.angleTo(pos, goalpos);
		double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToGoal));
		double headerDiff = MathFunctions.headingDifference(me, goalpos.x, goalpos.y);
		g.drawLine((float)pos.x, (float)pos.y, (float)(pos.x+Math.cos(angleToGoal-largeAngle)*range), (float)(pos.y+Math.sin(angleToGoal-largeAngle)*range));
		g.drawLine((float)pos.x, (float)pos.y, (float)(pos.x+Math.cos(angleToGoal+largeAngle)*range), (float)(pos.y+Math.sin(angleToGoal+largeAngle)*range));
		g.drawLine((float)pos.x, (float)pos.y, (float)(pos.x+Math.cos(angleToGoal-Math.PI/4)*smallRange), (float)(pos.y+Math.sin(angleToGoal-Math.PI/4)*smallRange));
		g.drawLine((float)pos.x, (float)pos.y, (float)(pos.x+Math.cos(angleToGoal+Math.PI/4)*smallRange), (float)(pos.y+Math.sin(angleToGoal+Math.PI/4)*smallRange));

	}
}
