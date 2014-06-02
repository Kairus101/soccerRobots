package org.kairus.sample;

import java.util.ArrayList;
import java.util.Collections;

import org.jbox2d.common.Vec2;
import org.kairus.api.Team;
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
	ArrayList<RobotModule> attacker2Modules = new ArrayList<RobotModule>();
	ArrayList<RobotModule> defender2Modules = new ArrayList<RobotModule>();
	
	ArrayList<ArrayList<RobotModule>> Modules = new ArrayList<ArrayList<RobotModule>>();
	
	int framesBetweenCalculation = 1;
	int currentFrame = 0;
	
	private void initModules(){
		Modules.add(goalieModules);
		Modules.add(defenderModules);
		Modules.add(attackerModules);
		Modules.add(attacker2Modules);
		Modules.add(defender2Modules);
		
		// Core behaviors
		
		int i = 0;
		for (ArrayList<RobotModule> m:Modules){
			if (i != 0)
				m.add(new fireAtGoalModule(i, this)); // fire at goals when possible
			m.add(new ballUnstuckModule(i, this)); // Get ball out of stuck places
			m.add(new robotUnstuckModule(i, this)); // Get robot out of stuck places
			m.add(new spiralModule(i, this)); // Default behavior
			i++;
		}

		//goalie
		goalieModules.add(new goalieModule(0, this));
		
		//Attacker positioning
		attackerModules.add(new getBehindBallModule(2, this));
		attacker2Modules.add(new getBehindBallModule(3, this));
		
		//defense
		defenderModules.add(new defenseModule(1, this));
		defender2Modules.add(new defenseModule(4, this));
		attacker2Modules.add(new defenseModule(3, this));
		
	}
	
	public void gameFrame() {
		
		if (++currentFrame>=framesBetweenCalculation){
			currentFrame=0;

			//update all utility values.
			for (ArrayList<RobotModule> m:Modules)
				for (RobotModule r:m)
					r.setUtility(this);

			//sort the modules by utility
			for (ArrayList<RobotModule> m:Modules)
				Collections.sort(m);
		}
		
		//Swap robots if they'd rather be where the other is
		for (ArrayList<RobotModule> m:Modules)
			m.get(0).calculateDistanceFromIdeal();
		
		//if (getTeamNumber() == 1){
		for (ArrayList<RobotModule> m:Modules)
			for (ArrayList<RobotModule> n:Modules)
				if (m!=n && m.get(0).idealPosition != null && n.get(0).idealPosition != null)
					if (MathFunctions.distance(m.get(0).idealPosition, n.get(0).position) < m.get(0).distanceFromIdeal
					  &&MathFunctions.distance(n.get(0).idealPosition, m.get(0).position) < n.get(0).distanceFromIdeal){
						// Swap modules of robots
						int r1 = n.get(0).robotNumber;
						int r2 = m.get(0).robotNumber;
						for (RobotModule r:n)
							r.robotNumber = r2;
						for (RobotModule r:m)
							r.robotNumber = r1;
						m.get(0).calculateDistanceFromIdeal();
						n.get(0).calculateDistanceFromIdeal();
					}
		//}
		//run the modules in order until one works.
		for (ArrayList<RobotModule> m:Modules)
			for (RobotModule r:m)
				if (r.execute(this))break;

		
	}

	@Override
	public void render(Graphics g) {
		for (ArrayList<RobotModule> m:Modules)
			for (RobotModule r:m)
				r.render(g);
	}
}
