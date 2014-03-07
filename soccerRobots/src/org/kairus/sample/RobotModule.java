package org.kairus.sample;

import org.kairus.api.Team;
import org.kairus.api.constants;
import org.newdawn.slick.Graphics;

abstract public class RobotModule implements Comparable<RobotModule>, constants {
	protected int robotNumber;
	protected Team team;
	private int utility;
	RobotModule(int robotNumber, Team team){
		this.robotNumber = robotNumber;
		this.team = team;
	}
	abstract int assessUtility(Team team);
	protected void setUtility(Team team){
		utility = assessUtility(team);
		if (utility>100) utility = 100;
		if (utility<0)   utility = 0;
	}
	public int getUtility(){
		return utility;
	}
	abstract boolean execute(Team team);
	abstract void render(Graphics g);
	public int compareTo(RobotModule other){
		return Integer.compare(other.utility,utility);
	}
}
