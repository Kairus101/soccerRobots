package org.kairus.api;

public interface constants {
	public static float SCALE = 3f;
	public static float FIELD_WIDTH = 220f;
	public static float FIELD_HEIGHT = 180f;
	
	public static float SCREEN_WIDTH = FIELD_WIDTH*SCALE;
	public static float SCREEN_HEIGHT = FIELD_HEIGHT*SCALE;

	public static float ROBOT_WIDTH = 7.5f;
	public static float GOAL_WIDTH = 35f;
	public static float BALL_DIAMETER = 3.267f; // should be 0.04267f ideally

	//robot general stats
	public static float ROBOT_DRAG = 0.95f;
	//private float spinDrag = 0.98f;
	
	public static double ROBOT_SPEED = 200;//0.3
	public static float ROBOT_ACCELERATION = 15f;
	public static float ROBOT_LINEAR_DAMPING = 1f;

	public static float ROBOT_SPIN_SPEED = 300f;
	public static float ROBOT_SPIN_ACCELERATION = 3f;
	public static float ROBOT_SPIN_DAMPING = 5f;
}
