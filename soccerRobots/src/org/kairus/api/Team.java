package org.kairus.api;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Graphics;

public abstract class Team implements constants {
	public Robot[] robot;
	public abstract void gameFrame();
	public abstract void render(Graphics g);
	int teamNumber;
	private Body ball;

	public Vec2 getBallPosition(){return ball.getPosition();}
	public Vec2 getBallVelocity(){return ball.getLinearVelocity();}
	public float getBallSpin(){return ball.getAngularVelocity();}

	void addRobots(Robot[] robot){
		this.robot = robot;
	}
	void setBall(Body b){
		ball = b;
	}
	
	public Team(int teamNumber){
		this.teamNumber = teamNumber;
	}
	
	public int getTeamNumber(){return teamNumber;}
}
