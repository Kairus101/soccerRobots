package org.kairus.api;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Robot {
	//private float vdirection=0;
	
	//robot general stats
	private float drag = 0.90f;
	//private float spinDrag = 0.98f;
	private double speed = 1;//0.3
	private float spinSpeed = 0.03f;
	private double width = 50;
	private double height = 30;
	
	// robots input
	private float inputSpeed = 0;
	private float inputSpin = 0;
	
	Body body;
	
	
	public Robot(Body b) {
		body = b;
	}

	void update(){
		body.applyForceToCenter(new Vec2((float)(Math.cos(body.getAngle())*inputSpeed*speed), (float)(Math.sin(body.getAngle())*inputSpeed*speed)));
		body.applyAngularImpulse(inputSpin*spinSpeed);
	}

	/**
	 * @param direction Angle counter-clockwise from the positive x axis you want the robot to go in.
	 * @param speed The speed from -1 to 1 you want to move.
	 */
	public void SetSpeedInput(float speed) {
		if (speed<-1)speed=-1;
		if (speed> 1)speed= 1;
		inputSpeed = speed;
	}
	
	/**
	 * @param spinInput Between -1 and 1, where -1 will make the robot spin CCW. 
	 */
	public void SetSpinInput(float spinInput) {
		if (spinInput<-1)spinInput=-1;
		if (spinInput> 1)spinInput= 1;
		inputSpin = spinInput;
	}
	
	//Getters
	public double getSpinSpeed() {return spinSpeed;}
	public double getSpeed() {return speed;}
	public Vec2 getPosition() {return body.getWorldCenter().mul(100);}
	public Vec2 getDisplayPosition() {return body.getPosition().mul(100);}
	public float getDirection() {return body.getAngle();}
	public Vec2 getVelocity() {return body.getLinearVelocity().mul(100);}
	//public float getVdirection() {return vdirection;}

	public void render(Graphics g) {
		Vec2 origin = getDisplayPosition();//.mul(100);
		float direction = body.getAngle();
		Game.robot.setRotation((float)(direction*180f/Math.PI));
		Game.robot.draw((float) (origin.x-width/2), (float) (origin.y-width/2), g.getColor());
		
		//Game.robot.drawCentered(origin.x, origin.y);
		
	}
}
