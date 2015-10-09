package org.kairus.kairusTeam;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class BeserkerModule extends RobotModule {

	BeserkerModule(int robotNumber, Team team) {
		super(robotNumber, team);
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
		int toReturn =(int) -((lowestDistance-lowestEnemyDistance)/5);
		//if (toReturn < 20) toReturn = 20;
		
		float ballx = team.getBallPosition().x;
		if (team.getTeamNumber() == 0 && ballx<SCREEN_WIDTH/3 || team.getTeamNumber() == 1 && ballx>SCREEN_WIDTH*2/3)
			return 0;
		
		return toReturn;
	}

	double tolerance = 0;

	double idealX;
	double idealY;
	@Override
	boolean execute(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();

		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);
		
		Robot[] others = team.getEnemyRobots();
		Robot closest = null;
		double closestDist = 99999;
		for (Robot r:others){
			if (MathFunctions.distance(goalpos, r.getPosition()) < closestDist){
				closestDist = MathFunctions.distance(goalpos, r.getPosition());
				closest = r;
			}		
		}
		idealX = closest.getPosition().x;//ballPos.x;
		idealY = closest.getPosition().y;//ballPos.y;
		
		// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
		double vecX = (idealX-pos.x);
		double vecY = (idealY-pos.y);
		
		float angleOffset = (float) MathFunctions.headingDifference(me, pos.x+vecX, pos.y+vecY);
		
		team.SetSpinInput(robotNumber, (float) (angleOffset>0?1:-1)/100);
		float speed = (float) (1-Math.abs(angleOffset))+0.25f;
		team.SetSpeedInput(robotNumber, speed/5);
		idealPosition = new Vec2((float)idealX, (float) idealY);
		return true;
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);
		g.setColor(Color.white);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
		g.setColor(Color.blue);
		g.drawLine((float)pos.x, (float)pos.y, (float)goalpos.x, (float)goalpos.y);
		
	}
}
