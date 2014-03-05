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
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame implements constants, ContactListener{
	public static int screenW=600;
	public static int screenH=600;
	private Team[] teams = new Team[2];
	boolean valid = false;

	World w = new World(new Vec2(0,0));

	final int k_triangleCategory = 0x0002;
	final int k_triangleMask = 0xFFFF;

	ArrayList<Body> bodies = new ArrayList<Body>();

	Body ball;
	Body left;
	Body right;
	int ballRadius = 5;
	int goalHeight = 100;
	int leftPoints = 0;
	int rightPoints = 0;

	static Image robot;

	public Game(Team[] teams){
		super("Soccer");

		// paddles
		float width = 50;
		//float height = 30;
		//Vec2 vertices[] = new Vec2[3];
		//vertices[0] = new Vec2(0, -width/2/100);
		//vertices[1] = new Vec2(0, width/2/100);
		//vertices[2] = new Vec2(-height/100, 0);
		PolygonShape polygon = new PolygonShape();
		polygon.setAsBox(width/100f/2, width/100f/2);
		//polygon.set(vertices, 3);
		FixtureDef robotShapeDef = new FixtureDef();
		robotShapeDef.shape = polygon;
		robotShapeDef.density = 1f;		
		for (int t = 0; t < 2; t++){
			Robot[] teamsRobots = new Robot[3];
			for (int i = 0; i < 3; i++){
				BodyDef robotBodyDef = new BodyDef();
				robotBodyDef.type = BodyType.DYNAMIC;
				robotBodyDef.position.set((float)Math.random()*6f, (float)Math.random()*6f);
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
		circle.m_radius = ballRadius/100f;
		FixtureDef circleShapeDef = new FixtureDef();
		circleShapeDef.shape = circle;
		circleShapeDef.density = 1.0f;
		BodyDef circleBodyDef = new BodyDef();
		circleBodyDef.type = BodyType.DYNAMIC;
		circleBodyDef.position.set(3f, 3f);
		ball = w.createBody(circleBodyDef);
		ball.createFixture(circleShapeDef);

		//top
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(3f, 0f);
		groundDef.type = BodyType.STATIC;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(6f, 0);
		Body top = w.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 0.2f;
		groundFixture.restitution = 0.8f;
		groundFixture.shape = groundShape;
		top.createFixture(groundFixture);
		bodies.add(top);
		//bottom
		groundDef.position.set(3, 6);
		Body bottom = w.createBody(groundDef);
		bottom.createFixture(groundFixture);
		bodies.add(bottom);
		//left
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(0, 3);
		wallDef.type = BodyType.STATIC;
		PolygonShape wallShape = new PolygonShape();
		wallShape.setAsBox(0, 6f);
		left = w.createBody(wallDef);
		FixtureDef wallFixture = new FixtureDef();
		wallFixture.density = 0.2f;
		wallFixture.restitution = 0.8f;
		wallFixture.shape = wallShape;
		left.createFixture(wallFixture);
		bodies.add(left);
		//right
		wallDef.position.set(6, 3);
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
			AppGameContainer app = new AppGameContainer(this);
			app.setTargetFrameRate(30);
			app.setDisplayMode(screenW, screenH, false);
			app.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
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
		Vec2 ballPos = ball.getPosition().mul(100);
		//ball.applyForceToCenter(new Vec2(0,0.1f));
		g.drawOval(ballPos.x-ballRadius, ballPos.y-ballRadius, ballRadius*2, ballRadius*2);

		g.setColor(Color.white);
		g.fillRect(0  , screenW/2-goalHeight/2, 3  , goalHeight);
		g.fillRect(597, screenW/2-goalHeight/2, 3, goalHeight);

		String scoreString = "score "+leftPoints+" - "+rightPoints;
		g.drawString(scoreString, 300-g.getFont().getWidth(scoreString)/2, 50);
	}
	public void init(GameContainer gc) throws SlickException {
		try {
			robot = new Image("data/robot.png");
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

		w.step(1/30f, 8, 3);
		
		if (ballReset){
			ball.setType(BodyType.STATIC);
			ball.setTransform(new Vec2(3,3), 0);
			ball.setLinearVelocity(new Vec2(0,0));
			ball.setAngularVelocity(0);
			ball.synchronizeTransform();
			ball.setType(BodyType.DYNAMIC);
			ballReset = false;
		}

		Vec2 ballPos = ball.getPosition().mul(100);
		//ball manipulation
		Input input = gc.getInput();
		boolean mouse = input.isMouseButtonDown(0);
		boolean mouse2 = input.isMouseButtonDown(1);
		int x = input.getMouseX();
		int y = input.getMouseY();
		if (mouse2){
			ball.applyForceToCenter(new Vec2(x-ballPos.x, y-ballPos.y).mul(0.0025f));
		}

	}

	boolean ballReset = false;
	@Override
	public void beginContact(Contact c) {
		if (c.getFixtureB().getBody()==ball){
			if (c.getFixtureA().getBody()==left){
				Vec2 ballPos = ball.getPosition().mul(100);
				if (ballPos.y>screenW/2-goalHeight/2 && ballPos.y<screenW/2+goalHeight/2){
					rightPoints++;
					ballReset = true;
				}
			}else if (c.getFixtureA().getBody()==right){
				Vec2 ballPos = ball.getPosition().mul(100);
				if (ballPos.y>screenW/2-goalHeight/2 && ballPos.y<screenW/2+goalHeight/2){
					leftPoints++;
					ballReset = true;
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
