package org.kairus.kairusTeam2;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class defenseModule extends RobotModule {
	
	float[] angleOffsetArray = new float[]{-1.57f, -0.35f, 1.57f, 0f, 0.35f};

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
		return toReturn + 80;
	}

	double tolerance = 10;
	double idealX;
	double idealY;
	@Override
	boolean execute(Team team) {
		Vec2 ballPos = team.getBallPosition();
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 vel = me.getVelocity();
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?0:SCREEN_WIDTH,SCREEN_HEIGHT/2);
		double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
		double idealRotation = 0;
		//System.out.println(robotNumber+": Defense!");
		double angleToBall = Math.atan2(ballPos.y-goalpos.y, ballPos.x-goalpos.x);
		angleToBall += angleOffsetArray[initialRobot];
		//System.out.println(angleOffsetArray[initialRobot]);
		idealX = team.getTeamNumber()==0?10+Math.cos(angleToBall)*180:SCREEN_WIDTH-10+Math.cos(angleToBall)*250;
		idealY = 300+Math.sin(angleToBall)*150;
		idealRotation = Math.PI;
		
		return driveToPoint(idealX, idealY, 0, idealRotation, 15, 80);
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.yellow);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
	}
}
