package org.kairus.kairusTeam2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class KairusTeam2 extends Team {
	public KairusTeam2(int teamNumber) {
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
	CopyOnWriteArrayList<RobotModule> ModulesToRender = new CopyOnWriteArrayList<RobotModule>();

	int framesBetweenCalculation = 0;
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
			if (i != 0){
				//	m.add(new ballUnstuckModule(i, this)); // Get ball out of stuck places
			}
			m.add(new fireAtGoalModule(i, this)); // fire at goals when possible

			//m.add(new robotUnstuckModule(i, this)); // Get robot out of stuck places
			m.add(new spiralModule(i, this)); // Default behavior
			m.add(new resetModule(i, this)); // Resetting behavior
			m.add(new stopMovingModule(i, this)); // Manual placing behavior
			m.add(new dontGetDQdModule(i, this)); // Game rule behavior behavior
			//m.add(new moveTestModule(i, this));
			i++;
		}

		//goalie
		goalieModules.add(new goalieModule(0, this));
		goalieModules.add(new getBehindBallModule(0, this));

		//Attacker positioning
		attackerModules.add(new getBehindBallModule(2, this));
		attacker2Modules.add(new getBehindBallModule(3, this));
		attackerModules.add(new ballUnstuckModule(2, this)); // Get ball out of stuck places
		attacker2Modules.add(new ballUnstuckModule(3, this)); // Get ball out of stuck places

		//defense
		defenderModules.add(new defenseModule(1, this));
		defender2Modules.add(new defenseModule(4, this));
		//attacker2Modules.add(new defenseModule(3, this));
		//if (getTeamNumber()==0)
		//defender2Modules.add(new BeserkerModule(4, this));
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
		//}
		//run the modules in order until one works.
		//System.out.println("executing!");
		ModulesToRender.clear();

		for (ArrayList<RobotModule> m:Modules)
			for (RobotModule r:m){
				r.idealPosition = null;
				//System.out.println(r.getClass().getName());
				if (r.execute(this)){
					ModulesToRender.add(r);
					r.calculateDistanceFromIdeal();
					break;
				}
			}

		boolean redo = true;
		while (redo){
			redo = false;
			
			//if (getTeamNumber() == 1){
			for (int o = 0; o < Modules.size(); o++){
				if (redo) break;
				for (int p = o+1; p < Modules.size(); p++){
					if (redo) break;
					ArrayList<RobotModule> m = Modules.get(o);
					ArrayList<RobotModule> n = Modules.get(p);

					if (o!=p && m.get(0).idealPosition != null && n.get(0).idealPosition != null){
						double before1 = MathFunctions.distance(n.get(0).idealPosition, n.get(0).position);
						double before2 = MathFunctions.distance(m.get(0).idealPosition, m.get(0).position);
						double maxDistBeforeSwap = Math.max(before1,before2);
						double after1 = MathFunctions.distance(n.get(0).idealPosition, m.get(0).position);
						double after2 = MathFunctions.distance(m.get(0).idealPosition, n.get(0).position);
						double maxDistAfterSwap = Math.max(after1,after2);


						// Minimize highest movement
						if ((maxDistAfterSwap+60 < maxDistBeforeSwap) ||
						  // Minimize total time
						
						  (MathFunctions.distance(m.get(0).idealPosition, n.get(0).position)+0 < m.get(0).distanceFromIdeal
						  &&MathFunctions.distance(n.get(0).idealPosition, m.get(0).position)+0 < n.get(0).distanceFromIdeal
						  
						  
						  )
						  // Goalie never swaps to convenience the team
						  //&& (o>0 || (after1 < before1))
						){
							//System.out.println(maxDistAfterSwap+60 + " < "+maxDistBeforeSwap+" so swapping "+o+" and "+p);
							// Swap modules of robots
							int r1 = n.get(0).robotNumber;
							int r2 = m.get(0).robotNumber;
							for (RobotModule r:n)
								r.robotNumber = r2;
							for (RobotModule r:m)
								r.robotNumber = r1;
							Vec2 idealPos = m.get(0).idealPosition;
							m.get(0).idealPosition = n.get(0).idealPosition;
							m.get(0).idealPosition = idealPos;

							m.get(0).calculateDistanceFromIdeal();
							n.get(0).calculateDistanceFromIdeal();
							
							redo = true;
						}
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics g) {
		if (getTeamNumber() != 0) return;
		for (RobotModule m:ModulesToRender){
			m.render(g);
			
			Robot r = robot[m.robotNumber];
			String behaviorName = m.toString();
			behaviorName = behaviorName.substring(22, behaviorName.indexOf('@')-6);
			//behaviorName = behaviorName.split(".")[1];
			g.drawString(behaviorName, r.getDisplayPosition().x-g.getFont().getWidth(behaviorName)/2, r.getDisplayPosition().y+5);
		}
		

		for (ArrayList<RobotModule> a:Modules)
			for (RobotModule m:a)
				m.alwaysRender(g);
		
		g.setColor(new Color(1, 1, 1, 0.75f));
		
	}
}
