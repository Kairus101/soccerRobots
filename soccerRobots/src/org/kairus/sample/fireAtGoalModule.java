package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class fireAtGoalModule extends RobotModule {

	int initialRobot;
	fireAtGoalModule(int robotNumber, Team team) {
		super(robotNumber, team);
		initialRobot = robotNumber;
	}

	int range = 150;
	
	@Override
	int assessUtility(Team team) {
		//try to fire if we have clear line of sight
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 ballPos = team.getBallPosition();
		double distanceToBall = MathFunctions.distance(pos, ballPos);
		double angleToBall = MathFunctions.angleTo(pos, ballPos);
		double angleToGoal = MathFunctions.angleTo(pos, new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH-10:10,SCREEN_HEIGHT/2));
		double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToGoal));
		if (distanceToBall < range && angleDifference < 5){
			//System.out.println("firing! "+distanceToBall);
			//System.out.println("Robot: "+robotNumber+" utility: "+((int) ((Math.PI-angleDifference)*100)));
			return (int) ((1-angleDifference)*100);
		}//else
			//System.out.println("d: "+distanceToBall+" x1:"+pos.x+" y1:"+pos.y+" x2:"+ballPos.x+" y2:"+ballPos.y);
		return 0;
	}

	double tolerance = 0;
	@Override
	boolean execute(Team team) {
		Vec2 ballPos = team.getBallPosition();
		idealPosition = new Vec2(ballPos.x, ballPos.y);
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX;
		double idealY;
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
		double idealRotation = 0;
		//System.out.println(robotNumber+": Fire!");
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH-10:10,SCREEN_HEIGHT/2);
		idealX = goalpos.x;//ballPos.x;
		idealY = goalpos.y;//ballPos.y;
		idealRotation = 0;
		
		double distance = Math.sqrt((idealY-pos.y)*(idealY-pos.y) + (idealX-pos.x)*(idealX-pos.x));
		
		// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
		double vecX = (idealX-pos.x)-vel.x;
		double vecY = (idealY-pos.y)-vel.y;
		
		if (distance > tolerance/2){ //go go!
			/*
			float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
			team.SetSpinInput(robotNumber, (float) (angleOffset*10-me.getAngularVelocity()*3));
			team.SetSpeedInput(robotNumber, (float) (1-Math.abs(angleOffset)));
			*/
			float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
			
			team.SetSpinInput(robotNumber, (float) (angleOffset>0?1:-1));
			float speed = (float) (1-Math.abs(angleOffset))+0.25f;
			if (initialRobot == 0 && Math.abs(angleOffset)>0.1) speed = 0;
			team.SetSpeedInput(robotNumber, speed>0?speed:0);
			return true;
		}else{
			return false;
		}
	}

	@Override
	void render(Graphics g) {
		/*
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.green);
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH-10:10,SCREEN_HEIGHT/2);
		g.drawLine((float)pos.x, (float)pos.y, goalpos.x, goalpos.y);
		g.drawOval((float)pos.x-range, (float)pos.y-range, range*2, range*2);
		*/
	}
}
