package org.kairus.sample;

import java.util.ArrayList;
import java.util.Collections;

import org.jbox2d.common.Vec2;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class KairusTeam extends Team {
	public KairusTeam(int teamNumber) {
		super(teamNumber);
		initModules();
	}

	double tolerance = 10;
	float frame = 0;

	ArrayList<RobotModule> goalieModules = new ArrayList<RobotModule>();
	ArrayList<RobotModule> defenderModules = new ArrayList<RobotModule>();
	ArrayList<RobotModule> attackerModules = new ArrayList<RobotModule>();
	int framesBetweenCalculation = 5;
	int currentFrame = 0;
	
	private void initModules(){
		//defense
		goalieModules.add(new defenseModule(0, this));
		defenderModules.add(new defenseModule(1, this));
		attackerModules.add(new defenseModule(2, this));
		//spiral
		//goalieModules.add(new spiralModule(0, this));
		defenderModules.add(new spiralModule(1, this));
		attackerModules.add(new spiralModule(2, this));
	}
	
	public void gameFrame() {
		
		if (++currentFrame==framesBetweenCalculation){
			currentFrame=0;
			
			//update all utility values.
			for (RobotModule r:goalieModules)
				r.setUtility(this);
			for (RobotModule r:defenderModules)
				r.setUtility(this);
			for (RobotModule r:attackerModules)
				r.setUtility(this);

			//sort the modules by utility
			Collections.sort(goalieModules);
			Collections.sort(defenderModules);
			Collections.sort(attackerModules);
		}
		
		//run the modules in order until one works.
		for (RobotModule r:goalieModules)
			if (r.execute(this))break;
		for (RobotModule r:defenderModules)
			if (r.execute(this))break;
		for (RobotModule r:attackerModules)
			if (r.execute(this))break;
		
		
		
		
	}
	
	double angleDifference(double targetAngle, double currentAngle){
		double angle = (targetAngle-currentAngle) %(2*Math.PI);
		return angle += (angle>Math.PI) ? -2*Math.PI : (angle<-Math.PI) ? 2*Math.PI : 0;
	}
	
	double headingDifference(int robotNum, double positionX, double positionY){
		Vec2 pos = robot[robotNum].getPosition();
		double angle = Math.atan2(positionY-pos.y, positionX-pos.x);
		if (angle<0)angle+=2*Math.PI;
		return angleDifference(angle, robot[robotNum].getDirection());
	}

	@Override
	public void render(Graphics g) {
		
		for (RobotModule r:goalieModules)
			r.render(g);
		for (RobotModule r:defenderModules)
			r.render(g);
		for (RobotModule r:attackerModules)
			r.render(g);
	}
}
