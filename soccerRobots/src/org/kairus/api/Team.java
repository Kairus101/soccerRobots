package org.kairus.api;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Graphics;

public abstract class Team implements constants {
	protected Robot[] robot;
	protected Robot[] enemyRobot;
	public abstract void gameFrame();
	public abstract void render(Graphics g);
	int teamNumber;
	private Body ball;

	public Vec2 getBallPosition(){return ball.getPosition();}
	public Vec2 getBallVelocity(){return ball.getLinearVelocity();}
	public float getBallSpin(){return ball.getAngularVelocity();}

	public Robot[] getRobots(){
		return robot;
	}

	public Robot[] getEnemyRobots(){
		return enemyRobot;
	}
	
	void setBall(Body b){
		ball = b;
	}
	
	public Team(int teamNumber){
		this.teamNumber = teamNumber;
	}
	
	public int getTeamNumber(){return teamNumber;}
	
	/**
	 * @param robot Robot to set speed of
	 * @param speed The speed from -1 to 1 you want to move.
	 */
	public void SetSpeedInput(int robot, float speed) {
		this.robot[robot].SetSpeedInput(speed);
	}
	
	/**
	 * @param robot Robot to set spin of
	 * @param spinInput Between -1 and 1, where -1 will make the robot spin CCW. 
	 */
	public void SetSpinInput(int robot, float spinInput) {
		this.robot[robot].SetSpinInput(spinInput);
	}
}
