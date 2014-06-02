package org.kairus.api;

import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.kairus.sample.KairusTeam;
import org.kairus.sample.MathFunctions;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame implements constants, ContactListener{
	public static float screenW=FIELD_WIDTH*SCALE;
	public static float screenH=FIELD_HEIGHT*SCALE;
	private Team[] teams = new Team[2];
	boolean valid = false;

	World w = new World(new Vec2(0,0));

	final int k_triangleCategory = 0x0002;
	final int k_triangleMask = 0xFFFF;

	ArrayList<Body> bodies = new ArrayList<Body>();

	Body ball;
	Body left;
	Body right;
	int leftPoints = 0;
	int rightPoints = 0;

	static Image robot;
	private AppGameContainer app;

	public Game(Team[] teams){
		super("Soccer");

		
		
		// paddles
		PolygonShape polygon = new PolygonShape();
		polygon.setAsBox(ROBOT_WIDTH/2, ROBOT_WIDTH/2);
		//polygon.set(vertices, 3);
		FixtureDef robotShapeDef = new FixtureDef();
		robotShapeDef.shape = polygon;
		robotShapeDef.density = 1f;		
		for (int t = 0; t < 2; t++){
			Robot[] teamsRobots = new Robot[5];
			for (int i = 0; i < 5; i++){
				BodyDef robotBodyDef = new BodyDef();
				robotBodyDef.type = BodyType.DYNAMIC;
				robotBodyDef.position.set((float)Math.random()*FIELD_WIDTH, (float)Math.random()*FIELD_HEIGHT);
				Body paddle = w.createBody(robotBodyDef);
				paddle.createFixture(robotShapeDef);
				paddle.m_angularDamping = ROBOT_SPIN_DAMPING;
				paddle.m_linearDamping = ROBOT_LINEAR_DAMPING;
				bodies.add(paddle);
				teamsRobots[i] = new Robot(paddle);
			}
			teams[t].robot = teamsRobots;
			teams[1-t].enemyRobot=teamsRobots;
		}


		CircleShape circle = new CircleShape();
		circle.m_radius = BALL_DIAMETER/2;
		FixtureDef circleShapeDef = new FixtureDef();
		circleShapeDef.shape = circle;
		circleShapeDef.density = 1.0f;
		BodyDef circleBodyDef = new BodyDef();
		circleBodyDef.type = BodyType.DYNAMIC;
		circleBodyDef.position.set(FIELD_WIDTH/2, FIELD_HEIGHT/2);
		ball = w.createBody(circleBodyDef);
		ball.createFixture(circleShapeDef);

		/////// WALLS
		
		//top
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(FIELD_WIDTH/2, 0f);
		groundDef.type = BodyType.STATIC;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(FIELD_WIDTH, 0);
		Body top = w.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 0.2f;
		groundFixture.restitution = 0.8f;
		groundFixture.shape = groundShape;
		top.createFixture(groundFixture);
		bodies.add(top);
		//bottom
		groundDef.position.set(FIELD_WIDTH/2, FIELD_HEIGHT);
		Body bottom = w.createBody(groundDef);
		bottom.createFixture(groundFixture);
		bodies.add(bottom);
		//left
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(0, FIELD_HEIGHT/2);
		wallDef.type = BodyType.STATIC;
		PolygonShape wallShape = new PolygonShape();
		wallShape.setAsBox(0, FIELD_HEIGHT);
		left = w.createBody(wallDef);
		FixtureDef wallFixture = new FixtureDef();
		wallFixture.density = 0.2f;
		wallFixture.restitution = 0.8f;
		wallFixture.shape = wallShape;
		left.createFixture(wallFixture);
		bodies.add(left);
		//right
		wallDef.position.set(FIELD_WIDTH, FIELD_HEIGHT/2);
		right = w.createBody(wallDef);
		right.createFixture(wallFixture);
		bodies.add(right);

		if (teams.length == 2){
			this.teams = teams;
		}else
			System.exit(1);

		teams[0].setBall(ball);
		teams[1].setBall(ball);
		
		try {
			app = new AppGameContainer(this);
			app.setTargetFrameRate(30);
			app.setUpdateOnlyWhenVisible(false);
			app.setAlwaysRender(true);
			app.setDisplayMode((int)screenW, (int)screenH, false);
			app.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		if (rendering){
			g.setBackground(Color.black);
			int i = 0;
			for (Team t:teams){
				t.render(g);
				if (i++==0) //set colors
					g.setColor(Color.green); 
				else 
					g.setColor(Color.red);
				for (Robot r:t.robot)
					r.render(g);
			}
			Vec2 ballPos = ball.getPosition().mul(SCALE);
			//ball.applyForceToCenter(new Vec2(0,0.1f));
			g.drawOval(ballPos.x-(BALL_DIAMETER*SCALE)/2, ballPos.y-(BALL_DIAMETER*SCALE)/2, BALL_DIAMETER*SCALE, BALL_DIAMETER*SCALE);
	
			g.setColor(Color.white);
			g.fillRect(0  , screenH/2-(GOAL_WIDTH*SCALE)/2, 3  , (GOAL_WIDTH*SCALE));
			g.fillRect(screenW-3, screenH/2-(GOAL_WIDTH*SCALE)/2, 3, (GOAL_WIDTH*SCALE));
	
			g.drawString("Center pull: "+(centerPull?"en":"dis")+"abled", 10, 30);
		}
		String scoreString = "score "+leftPoints+" - "+rightPoints;
		g.drawString(scoreString, screenW/2-g.getFont().getWidth(scoreString)/2, 50);
	}
	public void init(GameContainer gc) throws SlickException {
		try {
			robot = new Image("data/robot.png");
			robot = robot.getScaledCopy((ROBOT_WIDTH*SCALE)/50f);
		} catch (SlickException e1) {
			e1.printStackTrace();
		}
		w.setContactListener(this);
	}
	boolean inGoal = false;
	public void update(GameContainer gc, int delta) throws SlickException {
		for (Team t:teams){
			t.gameFrame();
			for (Robot r:t.robot)
				r.update();
		}

		w.step(1/30f, 30, 30);
		
		if (ballReset){
			ball.setType(BodyType.STATIC);
			ball.setTransform(new Vec2(FIELD_WIDTH/2,FIELD_HEIGHT/2), 0);
			ball.setLinearVelocity(new Vec2(0,0));
			ball.setAngularVelocity(0);
			ball.synchronizeTransform();
			ball.setType(BodyType.DYNAMIC);
			ballReset = false;
		}

		Vec2 ballPos = ball.getPosition().mul(SCALE);
		//ball manipulation
		Input input = gc.getInput();
		boolean mouse = input.isMouseButtonDown(0);
		boolean mouse2 = input.isMouseButtonDown(1);
		int x = input.getMouseX();
		int y = input.getMouseY();
		if (mouse2){
			ball.applyForceToCenter(new Vec2(x-ballPos.x, y-ballPos.y).mul(0.0001f));
		}
		
		//APPLY tiny FORCE TOWARDS CENTER
		if (centerPull)
			ball.applyForceToCenter(new Vec2(SCREEN_WIDTH/2-ballPos.x, SCREEN_HEIGHT/2-ballPos.y).mul(0.0000001f));

		if (oldBallPos != null){
			float xDiff = ballPos.x-oldBallPos.x;
			float yDiff = ballPos.y-oldBallPos.y;
			if (oldBallPos != null && Math.sqrt(xDiff*xDiff+yDiff*yDiff) < movementTolerance)
				countTillReset++;
			else
				countTillReset = 0;
			if (countTillReset > maxCountTillReset){
				resetTimer = 100;
			}
		}
		oldBallPos = ballPos;
		
		if (ballPos.y>screenH/2-(GOAL_WIDTH*SCALE)/2 && ballPos.y<screenH/2+(GOAL_WIDTH*SCALE)/2){
			if (ballPos.x>SCREEN_WIDTH-7.5){
				leftPoints++;
				resetTimer = 100;
			}else if (ballPos.x<7.5){
				rightPoints++;
				resetTimer = 100;
			}
		}
		resetTimer--;
		if (resetTimer>0) ballReset = true;
		
	}
	int countTillReset = 0;
	int maxCountTillReset = 1000;
	int movementTolerance = 2;
	Vec2 oldBallPos;

	boolean centerPull = true;
	boolean rendering = true;
	
	int resetTimer = 0;
	
	private int framerate = 30;
	public void keyPressed(int key,char c){
		if (c == 'z') {
			framerate = 30-framerate;
			app.setTargetFrameRate(framerate);
		}
		if (c == 'x')
			centerPull = !centerPull;
		if (c == 'r')
			rendering = !rendering;
	}

	boolean ballReset = false;
	@Override
	public void beginContact(Contact c) {
		if (c.getFixtureB().getBody()==ball){
			if (c.getFixtureA().getBody()==left){
				Vec2 ballPos = ball.getPosition().mul(SCALE);
				if (ballPos.y>screenH/2-(GOAL_WIDTH*SCALE)/2 && ballPos.y<screenH/2+(GOAL_WIDTH*SCALE)/2){
					rightPoints++;
					ballReset = true;
					resetTimer = 100;
				}
			}else if (c.getFixtureA().getBody()==right){
				Vec2 ballPos = ball.getPosition().mul(SCALE);
				if (ballPos.y>screenH/2-(GOAL_WIDTH*SCALE)/2 && ballPos.y<screenH/2+(GOAL_WIDTH*SCALE)/2){
					leftPoints++;
					ballReset = true;
					resetTimer = 100;
				}
			}
		}
	}

	@Override
	public void endContact(Contact arg0) {}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {}
}
