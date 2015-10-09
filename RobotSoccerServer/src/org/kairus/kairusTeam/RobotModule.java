package org.kairus.kairusTeam;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.kairus.api.constants;
import org.newdawn.slick.Graphics;

abstract public class RobotModule implements Comparable<RobotModule>, constants {
	protected int robotNumber;
	protected Team team;
	private int utility;
	Vec2 position;
	Vec2 idealPosition;
	double distanceFromIdeal=0;
	boolean staticUtility = false;
	
	RobotModule(int robotNumber, Team team){
		this.robotNumber = robotNumber;
		this.team = team;
	}
	abstract int assessUtility(Team team);
	protected void setUtility(Team team){
		utility = assessUtility(team);
		if (!staticUtility){
			if (utility>100) utility = 100;
			if (utility<0)   utility = 0;
		}
	}
	public int getUtility(){
		return utility;
	}
	public void calculateDistanceFromIdeal(){
		if (idealPosition == null) return;
		position = team.getRobots()[robotNumber].getPosition();
		distanceFromIdeal = MathFunctions.distance(position, idealPosition);
	}
	abstract boolean execute(Team team);
	abstract void render(Graphics g);
	void alwaysRender(Graphics g){};
	public int compareTo(RobotModule other){
		return Integer.compare(other.utility,utility);
	}

	double tolerance = 10;
	//stop: 0 = rotation, 1 = stop, 2 = return false
	public boolean driveToPoint(double idealX, double idealY, int stop, double angle, float speedReduction, float spinReduction, boolean bothDirections){
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		double distance = Math.sqrt((idealY-pos.y)*(idealY-pos.y) + (idealX-pos.x)*(idealX-pos.x));
		idealPosition = new Vec2((float)idealX, (float) idealY);

		float angleOffset = (float) MathFunctions.headingDifference(me, idealX, idealY);
		int direction = 1;
		if (bothDirections){
			if (angleOffset>=Math.PI/2){
				direction = -1;
				angleOffset -= Math.PI;
			}else if (angleOffset<=-Math.PI/2){
				direction = -1;
				angleOffset += Math.PI;
			}
		}
		
		if (distance > tolerance/2){ //go go!
			float speed = (float) (1-(Math.abs(angleOffset*2)));
			// Stop it from overshooting
			speed *= (distance/100) + 0.3;
			if (speed<0) speed = 0;
			team.SetSpeedInput(robotNumber, direction*speed/speedReduction);
			team.SetSpinInput(robotNumber, (float) ((angleOffset*2)/spinReduction));
		}else{
			if (stop == 0){
				team.SetSpinInput(robotNumber, (float) MathFunctions.angleDifference(angle, me.getDirection())/spinReduction);
			}else if (stop == 1){
				team.SetSpinInput(robotNumber, 0);
			}else if (stop == 2)
				return false;
			team.SetSpeedInput(robotNumber, 0);
		}
		return true;
	}
	public boolean driveToPoint(double idealX, double idealY, int stop, double angle, float speedReduction, float spinReduction){
		return driveToPoint(idealX, idealY, stop, angle, speedReduction, spinReduction, true);
	}
	
}
