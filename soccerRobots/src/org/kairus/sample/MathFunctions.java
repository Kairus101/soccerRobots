package org.kairus.sample;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;

public class MathFunctions {
	
	public static double distance(Vec2 vec1, Vec2 vec2){
		return Math.sqrt((vec1.x-vec2.x)*(vec1.x-vec2.x)+(vec1.y-vec2.y)*(vec1.y-vec2.y));
	}
	
	public static double angleTo(Vec2 vec1, Vec2 vec2){
		return Math.atan2(vec2.y-vec1.y, vec2.x-vec1.x);
	}
	
	public static double angleDifference(double targetAngle, double currentAngle){
		double angle = (targetAngle-currentAngle) %(2*Math.PI);
		return angle += (angle>Math.PI) ? -2*Math.PI : (angle<-Math.PI) ? 2*Math.PI : 0;
	}
	
	public static double headingDifference(Robot robot, double positionX, double positionY){
		Vec2 pos = robot.getPosition();
		double angle = Math.atan2(positionY-pos.y, positionX-pos.x);
		return angleDifference(angle, robot.getDirection())%(2*Math.PI);
	}
	
}
