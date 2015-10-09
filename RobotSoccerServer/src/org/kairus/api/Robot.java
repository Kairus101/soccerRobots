package org.kairus.api;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Graphics;

public class Robot implements constants{
	private float width = ROBOT_WIDTH*SCALE*1.2f;
	
	// robots input
	protected float inputSpeed = 0;
	protected float inputSpin = 0;
	protected float targetSpeed = 0;
	protected float targetSpin = 0;
	
	Body body;

	protected int robotNumber;
	protected int team;
	
	public Robot(Body b, int number, int team) {
		body = b;
		robotNumber = number;
		this.team=team;
	}
	
	protected void update(){
		float forwardsVelocity = Vec2.dot(getVelocity(), getForwards());
		float sidewaysVelocity = Vec2.dot(getVelocity(), getSideways());
		
		float linearChange = (float) ((inputSpeed*ROBOT_SPEED)-forwardsVelocity);
		float spinChange = (inputSpin*ROBOT_SPIN_SPEED)-getAngularVelocity();
		
		//System.out.println(linearChange);
		
		if (linearChange > ROBOT_ACCELERATION) {linearChange = ROBOT_ACCELERATION;}
		if (linearChange < -ROBOT_ACCELERATION) {linearChange = -ROBOT_ACCELERATION;}
		
		if (spinChange > ROBOT_SPIN_ACCELERATION) {spinChange = ROBOT_SPIN_ACCELERATION;}
		if (spinChange < -ROBOT_SPIN_ACCELERATION) {spinChange = -ROBOT_SPIN_ACCELERATION;}
		
		if (robotNumber == 0 && team == 0){
			//System.out.println("Target: "+inputSpeed*ROBOT_SPEED+" forwards velocity: "+forwardsVelocity+" Linear: "+linearChange);
			//System.out.println("TargetSpin: "+ inputSpin*ROBOT_SPIN_SPEED+ " RealSpin: "+ getAngularVelocity()+ " SpinAcc: "+ spinChange);
		}
		linearChange*=0.25;
		body.setLinearVelocity(body.getLinearVelocity().add(new Vec2((float)(Math.cos(body.getAngle())*linearChange), (float)(Math.sin(body.getAngle())*linearChange))));
		//body.applyForceToCenter(new Vec2((float)(Math.cos(body.getAngle())*linearChange), (float)(Math.sin(body.getAngle())*linearChange)));
		//body.applyForceToCenter(new Vec2((float)(Math.cos(body.getAngle())*inputSpeed*ROBOT_SPEED), (float)(Math.sin(body.getAngle())*inputSpeed*ROBOT_SPEED)));
		spinChange*=1.26;
		body.setAngularVelocity(body.getAngularVelocity()+spinChange);
		//body.applyAngularImpulse(spinChange);
		
		// Sideways friction
		body.setLinearVelocity(body.getLinearVelocity().add(new Vec2((float)(Math.cos(body.getAngle()+Math.PI/2)*sidewaysVelocity*-1), (float)(Math.sin(body.getAngle()+Math.PI/2)*sidewaysVelocity*-1)).mul(0.2f)));
	}

	/**
	 * @param direction Angle counter-clockwise from the positive x axis you want the robot to go in.
	 * @param speed The speed from -1 to 1 you want to move.
	 */
	protected void SetSpeedInput(float speed) {
		speed *= ROBOT_SPEED;
		if (speed<-1)speed=-1;
		if (speed> 1)speed= 1;
		inputSpeed = speed;
	}
	protected void SetTargetSpeed(float speed) {
		speed *= ROBOT_SPIN_SPEED;
		if (speed<-1)speed=-1;
		if (speed> 1)speed= 1;
		targetSpeed = speed;
	}
	
	/**
	 * @param spinInput Between -1 and 1, where -1 will make the robot spin CCW. 
	 */
	protected void SetSpinInput(float spinInput) {
		if (spinInput<-1)spinInput=-1;
		if (spinInput> 1)spinInput= 1;
		inputSpin = spinInput;
	}
	protected void SetTargetSpin(float spinInput) {
		if (spinInput<-1)spinInput=-1;
		if (spinInput> 1)spinInput= 1;
		targetSpin = spinInput;
	}
	
	//Getters
	public float GetSpinInput() {return inputSpin;}
	public float GetSpeedInput() {return inputSpeed;}
	public float GetTargetSpin() {return targetSpin;}
	public float GetTargetSpeed() {return targetSpeed;}
	public double getSpinSpeed() {return ROBOT_SPIN_SPEED;}
	public double getSpeed() {return ROBOT_SPEED;}
	public Vec2 getPosition() {return body.getWorldCenter().mul(SCALE);}
	public Vec2 getDisplayPosition() {return body.getPosition().mul(SCALE);}
	public float getDirection() {return (float) (body.getAngle()%(2*Math.PI));}
	public Vec2 getVelocity() {return body.getLinearVelocity().mul(SCALE);}
	public float getAngularVelocity() {return body.getAngularVelocity();}
	public Vec2 getForwards() {return new Vec2((float)Math.cos(body.getAngle()), (float) Math.sin(body.getAngle()));}
	public Vec2 getSideways() {return new Vec2((float)Math.cos(body.getAngle()+Math.PI/2), (float) Math.sin(body.getAngle()+Math.PI/2));}
	//public float getVdirection() {return vdirection;}

	protected void render(Graphics g) {
		Vec2 origin = getDisplayPosition();
		float direction = body.getAngle();
		Game.robot.setRotation((float)(direction*180f/Math.PI));
		//Game.robot.draw((float) origin.x, (float) origin.y, width, width, g.getColor());
		g.drawImage(Game.robot, origin.x-width/2, origin.y-width/2, g.getColor());
	}
}
