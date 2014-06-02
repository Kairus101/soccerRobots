package org.kairus.api;

public interface constants {
	public static float SCALE = 300;
	public static float FIELD_WIDTH = 2.2f;
	public static float FIELD_HEIGHT = 1.8f;
	
	public static float SCREEN_WIDTH = FIELD_WIDTH*SCALE;
	public static float SCREEN_HEIGHT = FIELD_HEIGHT*SCALE;

	public static float ROBOT_WIDTH = 0.075f;
	public static float GOAL_WIDTH = 0.35f;
	public static float BALL_DIAMETER = 0.03267f; // should be 0.04267f ideally

	//robot general stats
	public static float ROBOT_DRAG = 0.95f;
	//private float spinDrag = 0.98f;
	public static double ROBOT_SPEED = 0.04;//0.3
	public static float ROBOT_SPIN_SPEED = 0.0002f;
	public static float ROBOT_SPIN_DAMPING = 25;
	public static float ROBOT_LINEAR_DAMPING = 5f;
}
