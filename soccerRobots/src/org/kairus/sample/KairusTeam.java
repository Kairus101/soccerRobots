package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class KairusTeam extends Team {
	public KairusTeam(int teamNumber) {
		super(teamNumber);
	}

	double tolerance = 10;
	float frame = 0;
	
	public void gameFrame() {
		
		frame+=0.02;
		double idealX;
		double idealY;
		double idealRotation = 0;
		Vec2 ballPos = getBallPosition().mul(100);
		
		for (int i = 0;i<3;i++){
			Vec2 pos = robot[i].getPosition();
			Vec2 vel = robot[i].getVelocity();
			float angle = robot[i].getDirection();
			double velocity = Math.sqrt(vel.x*vel.x+vel.y*vel.y);
			
			if (i==0){ // goal keeper
				double angleToBall = Math.atan2(ballPos.y-pos.y, ballPos.x-pos.x);
				idealX = getTeamNumber()==0?10+Math.cos(angleToBall)*50:590+Math.cos(angleToBall)*50;
				idealY = 300+Math.sin(angleToBall)*50;
				idealRotation = getTeamNumber()==0?0:Math.PI;
				
			}else{

				//get behind ball
				double angleToGoal = Math.atan2(300-ballPos.y, (getTeamNumber()==0?600:0)-ballPos.x);
				
				double standBackDistance = i*100f - 100f;
				
				idealX = ballPos.x+Math.cos(angleToGoal-Math.PI)*standBackDistance;
				idealY = ballPos.y+Math.sin(angleToGoal-Math.PI)*standBackDistance;
				idealRotation = 0;

				if (getTeamNumber()==0 && i ==1)
					System.out.println(angleToGoal);
				
				
				//start position
				//double idealX = getTeamNumber()==0?100:500;
				//double idealY = 150+i*100;
				//idealRotation = getTeamNumber()==0?0:Math.PI;
				
				//circle
				//idealX = 300+Math.cos(frame+(i/3f)*2f*Math.PI + (getTeamNumber()==0f?0:Math.PI/3f))*100;
				//idealY = 300+Math.sin(frame+(i/3f)*2f*Math.PI + (getTeamNumber()==0?0f:Math.PI/3f))*100;
				//double idealRotation = 0;
				
			}
			double distance = Math.sqrt((idealY-pos.y)*(idealY-pos.y) + (idealX-pos.x)*(idealX-pos.x));
			
			// To get the optimum vector to stop, we neutralize our velocity, then point to the position. ie VecPosition-vecVelocity
			double vecX = (idealX-pos.x) - vel.x;
			double vecY = (idealY-pos.y) - vel.y;
			
			if (distance > tolerance/2){ //go go!
				float angleOffset = (float) headingDifference(i, pos.x+vecX, pos.y+vecY);

				robot[i].SetSpinInput(angleOffset);
				robot[i].SetSpeedInput((float) (1-Math.abs(angleOffset)));
			}else{
					//System.out.println("Angle difference: "+angleDifference(idealRotation, robot[i].getDirection()));
					robot[i].SetSpinInput((float) angleDifference(idealRotation, robot[i].getDirection()));

					float angleOffset = (float) headingDifference(i, pos.x-vel.x, pos.y-vel.y);
					robot[i].SetSpeedInput((float) (-velocity-Math.abs(angleOffset)));
					robot[i].SetSpeedInput(0);
			}
		}
		
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
		
		if (getTeamNumber()==0){
			Vec2 pos = robot[0].getPosition();
			Vec2 vel = robot[0].getVelocity();
			g.setColor(Color.blue);
			g.drawLine((float)pos.x, (float)pos.y, (float)(Math.cos(robot[0].getDirection())*300+pos.x), (float)(Math.sin(robot[0].getDirection())*300+pos.y));
			//g.setColor(Color.yellow);
			//g.drawLine((float)pos.x, (float)pos.y, 100f, 150f);
			//g.setColor(Color.red);
			//g.drawLine((float)pos.x, (float)pos.y, (float)(vel.x+pos.y), (float)(vel.y+pos.y));
			//g.drawOval(100f-(float)tolerance/2, 150f-(float)tolerance/2, (float)tolerance, (float)tolerance);
			

		}
		Vec2 ballPos = getBallPosition().mul(100);
		double angleToGoal = Math.atan2(300-ballPos.y, (getTeamNumber()==0?600:0)-ballPos.x);
		g.drawLine(ballPos.x, ballPos.y, (float)(Math.cos(angleToGoal)*100+ballPos.x), (float)(Math.sin(angleToGoal)*100+ballPos.y));
		g.drawLine(ballPos.x, ballPos.y, (float)(ballPos.x+Math.cos(angleToGoal-Math.PI)*50), (float)(ballPos.y+Math.sin(angleToGoal-Math.PI)*50));
		
	}
}
