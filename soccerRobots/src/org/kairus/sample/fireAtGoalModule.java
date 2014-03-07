package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class fireAtGoalModule extends RobotModule {
	fireAtGoalModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}

	@Override
	int assessUtility(Team team) {
		//try to fire if we have clear line of sight
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 ballPos = team.getBallPosition().mul(100);
		double distanceToBall = MathFunctions.distance(pos, ballPos);
		System.out.println("Robot: "+robotNumber+" distance: "+distanceToBall);
		if (distanceToBall < 90){
			double angleToBall = MathFunctions.angleTo(pos, ballPos);
			double angleToGoal = MathFunctions.angleTo(pos, new Vec2(team.getTeamNumber()==0?600:0,300));
			double angleDifference = Math.abs(MathFunctions.angleDifference(angleToBall, angleToGoal));
			System.out.println("Robot: "+robotNumber+" utility: "+((int) ((Math.PI-angleDifference)*100)));
			return (int) ((1-angleDifference)*100);
		}
		return 0;
	}

	double tolerance = 0;
	@Override
	boolean execute(Team team) {
		Vec2 ballPos = team.getBallPosition().mul(100);
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		double idealX;
		double idealY;
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
		double idealRotation = 0;
		System.out.println(robotNumber+": Fire!");
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?600:0,300);
		idealX = goalpos.x;//ballPos.x;
		idealY = goalpos.y;//ballPos.y;
		idealRotation = 0;
		
		double distance = Math.sqrt((idealY-pos.y)*(idealY-pos.y) + (idealX-pos.x)*(idealX-pos.x));
		
		// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
		double vecX = (idealX-pos.x) - vel.x;
		double vecY = (idealY-pos.y) - vel.y;
		
		if (distance > tolerance/2){ //go go!
			float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
			team.SetSpinInput(robotNumber, (float) (angleOffset*10-me.getAngularVelocity()*3));
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
		g.setColor(Color.green);
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?600:0,300);
		g.drawLine((float)pos.x, (float)pos.y, goalpos.x, goalpos.y);
		g.drawOval((float)pos.x-90, (float)pos.y-90, 180, 180);
	}
}
