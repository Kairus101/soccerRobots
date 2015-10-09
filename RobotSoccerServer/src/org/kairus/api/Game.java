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
	public static float screenW=FIELD_WIDTH*SCALE;
	public static float screenH=FIELD_HEIGHT*SCALE;
	public Team[] teams = new Team[2];
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
	public static boolean simulation = false;
	boolean replay = false;
	boolean recording = false;
	boolean frameLimit = true;


	ArrayList<gameFrame> replayData = new ArrayList<gameFrame>();

	static Image robot;
	protected AppGameContainer app;

	public Game(Team[] teams){
		this(teams, false);
	}
	public Game(Team[] teams, boolean simulation){
		super("Soccer");

		Game.simulation = simulation;

		// paddles
		PolygonShape polygon = new PolygonShape();
		//polygon.setAsBox(ROBOT_WIDTH/2, ROBOT_WIDTH/2);
		float robotSize = ROBOT_WIDTH/2;
		polygon.set(
			new Vec2[]{
				new Vec2(robotSize*50/50f, robotSize*-50/50f),
				new Vec2(robotSize*50/50f, robotSize*-35/50f),
				new Vec2(robotSize*30/50f, robotSize*-28/50f),
				new Vec2(robotSize*-30/50f, robotSize*-28/50f),
				new Vec2(robotSize*-50/50f, robotSize*-35/50f),
				new Vec2(robotSize*-50/50f, robotSize*-50/50f),
			}
		, 6);
		
		FixtureDef robotShapeDef = new FixtureDef();
		robotShapeDef.shape = polygon;
		robotShapeDef.density = 1f;

		polygon = new PolygonShape();
		polygon.set(
			new Vec2[]{
				new Vec2(robotSize*50/50f, robotSize*50/50f),
				new Vec2(robotSize*50/50f, robotSize*35/50f),
				new Vec2(robotSize*30/50f, robotSize*28/50f),
				new Vec2(robotSize*-30/50f, robotSize*28/50f),
				new Vec2(robotSize*-50/50f, robotSize*35/50f),
				new Vec2(robotSize*-50/50f, robotSize*50/50f),
			}
		, 6);
		FixtureDef robotShapeDef2 = new FixtureDef();
		robotShapeDef2.shape = polygon;
		robotShapeDef2.density = 1f;

		polygon = new PolygonShape();
		polygon.set(
			new Vec2[]{
				new Vec2(robotSize*30/50f, robotSize*-28/50f),
				new Vec2(robotSize*-30/50f, robotSize*-28/50f),
				new Vec2(robotSize*-30/50f, robotSize*28/50f),
				new Vec2(robotSize*30/50f, robotSize*28/50f),
			}
		, 4);
		FixtureDef robotShapeDef3 = new FixtureDef();
		robotShapeDef3.shape = polygon;
		robotShapeDef3.density = 1f;
		
		for (int t = 0; t < 2; t++){
			Robot[] teamsRobots = new Robot[5];
			for (int i = 0; i < 5; i++){
				BodyDef robotBodyDef = new BodyDef();
				robotBodyDef.type = BodyType.DYNAMIC;
				
				robotBodyDef.position.set(FIELD_WIDTH * ((t*2)+1)/4, FIELD_HEIGHT * (i+1)/6);
				
				robotBodyDef.angle = (float)(Math.random()*Math.PI*2);
				Body paddle = w.createBody(robotBodyDef);
				paddle.createFixture(robotShapeDef);
				paddle.createFixture(robotShapeDef2);
				paddle.createFixture(robotShapeDef3);
				paddle.m_angularDamping = ROBOT_SPIN_DAMPING;
				paddle.m_linearDamping = ROBOT_LINEAR_DAMPING;
				bodies.add(paddle);
				teamsRobots[i] = new Robot(paddle, i, t);
			}
			teams[t].robot = teamsRobots;
			teams[1-t].enemyRobot=teamsRobots;
		}


		CircleShape circle = new CircleShape();
		circle.m_radius = BALL_DIAMETER/2;
		FixtureDef circleShapeDef = new FixtureDef();
		circleShapeDef.shape = circle;
		circleShapeDef.density = 3.0f;
		circleShapeDef.restitution = 0.3f;

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
		groundFixture.restitution = 0f;
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
		wallFixture.restitution = 0f;
		wallFixture.shape = wallShape;
		left.createFixture(wallFixture);
		bodies.add(left);
		//right
		wallDef.position.set(FIELD_WIDTH, FIELD_HEIGHT/2);
		right = w.createBody(wallDef);
		right.createFixture(wallFixture);
		bodies.add(right);


		/////// Corners
		//top-left
		BodyDef cornerDef = new BodyDef();
		cornerDef.angle=(float) (-Math.PI/4);
		cornerDef.position.set(5, 5);
		cornerDef.type = BodyType.STATIC;
		PolygonShape cornerShape = new PolygonShape();
		cornerShape.setAsBox(10, 1);
		Body corner = w.createBody(cornerDef);
		FixtureDef cornerFixture = new FixtureDef();
		cornerFixture.density = 0.2f;
		cornerFixture.restitution = 0f;
		cornerFixture.shape = cornerShape;
		corner.createFixture(cornerFixture);
		bodies.add(corner);
		//bottom-right
		cornerDef.position.set(FIELD_WIDTH-5, FIELD_HEIGHT-5);
		corner = w.createBody(cornerDef);
		corner.createFixture(cornerFixture);
		bodies.add(corner);
		bodies.add(corner);
		//bottom-left
		cornerDef.position.set(5, FIELD_HEIGHT-5);
		cornerDef.angle=(float) (Math.PI/4);
		corner = w.createBody(cornerDef);
		corner.createFixture(cornerFixture);
		bodies.add(corner);
		//top-right
		cornerDef.position.set(FIELD_WIDTH-5, 5);
		cornerDef.angle=(float) (Math.PI/4);
		corner = w.createBody(cornerDef);
		corner.createFixture(cornerFixture);
		bodies.add(corner);

		if (teams.length == 2){
			this.teams = teams;
		}else
			System.exit(1);

		teams[0].setBall(ball);
		teams[1].setBall(ball);

		try {
			app = new AppGameContainer(this);
			if (simulation)
				app.setTargetFrameRate(60);
			app.setUpdateOnlyWhenVisible(false);
			app.setAlwaysRender(true);
			app.setDisplayMode((int)screenW, (int)screenH+75, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public boolean inArea(int x, int y, int x2, int y2, int w, int h){
		return (x>x2 && x<x2+w && y>y2 && y<y2+h);
	}

	public void renderButton(int x, int y, int width, int height, String string1, String string2, boolean enabled, Graphics g){
		if (enabled){
			g.setColor(new Color(0.1f, 0.5f, 0.1f));
			g.fillRect(x, y, width, height);
			g.setColor(Color.white);
			g.drawString(string1, (x+width/2)-g.getFont().getWidth(string1)/2, y);
		}else{
			g.setColor(new Color(0.5f, 0.5f, 0.5f));
			g.fillRect(x, y, width, height);
			g.setColor(Color.white);
			g.drawString(string2, (x+width/2)-g.getFont().getWidth(string2)/2, y);
		}
	}
	
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setBackground(Color.black);
		g.setColor(new Color(0.3f, 0.3f, 0.3f));
		g.fillRect(0, screenH, screenW, 75);
		
		Input input = gc.getInput();
		boolean click = input.isMousePressed(0);
		boolean down = input.isMouseButtonDown(0);
		int x = input.getMouseX();
		int y = input.getMouseY();
		
		// Bottom interface
		if (true){//simulation){
			g.setColor(Color.white);
			// rendering
			if (click && inArea(x, y, 115, (int)screenH+3, 100, 20))
				rendering = !rendering;

			// Score
			String scoreString = "score "+leftPoints+" - "+rightPoints; 
			g.drawString(scoreString, screenW/2-g.getFont().getWidth(scoreString)/2, screenH+3);

			renderButton(115, (int)screenH+3, 100, 20, "Render", "Render", rendering, g);
			if (!rendering) return;
			// center pull
			if (click && inArea(x, y, 10, (int)screenH+3, 100, 20))
				centerPull = !centerPull;
			renderButton(10, (int)screenH+3, 100, 20, "Center pull", "Center pull", centerPull, g);
			
			// reset
			if (click && inArea(x, y, 0, (int)screenH+55, 60, 20))
				Server.setState("reset");
			renderButton(0, (int)screenH+55, 60, 20, "Reset", "Reset", Server.getState() == "reset", g);
			// circles
			if (click && inArea(x, y, 65, (int)screenH+55, 70, 20))
				Server.setState("circles");
			renderButton(65, (int)screenH+55, 70, 20, "Circles", "Circles", Server.getState() == "circles", g);
			// stop
			if (click && inArea(x, y, 140, (int)screenH+55, 60, 20))
				Server.setState("stop");
			renderButton(140, (int)screenH+55, 60, 20, "Stop", "Stop", Server.getState() == "stop", g);

			// PenaltyAttack
			if (click && inArea(x, y, (int)screenW/2+100, (int)screenH+7, 35, 10))
				Server.setState("penaltyAttack");
			renderButton((int)screenW/2+100, (int)screenH+7, 35, 8, "", "", Server.getState() == "penaltyAttack", g);
			// TopDefense
			if (click && inArea(x, y, (int)screenW/2+100, (int)screenH+20, 15, 15))
				Server.setState("topDefense");
			renderButton((int)screenW/2+100, (int)screenH+20, 15, 15, "", "", Server.getState() == "topDefense", g);
			// TopAdvantage
			if (click && inArea(x, y, (int)screenW/2+120, (int)screenH+20, 15, 15))
				Server.setState("topAdvantage");
			renderButton((int)screenW/2+120, (int)screenH+20, 15, 15, "", "", Server.getState() == "topAdvantage", g);
			// BottomDefense
			if (click && inArea(x, y, (int)screenW/2+100, (int)screenH+40, 15, 15))
				Server.setState("bottomDefense");
			renderButton((int)screenW/2+100, (int)screenH+40, 15, 15, "", "", Server.getState() == "bottomDefense", g);
			// BottomAdvantage
			if (click && inArea(x, y, (int)screenW/2+120, (int)screenH+40, 15, 15))
				Server.setState("bottomAdvantage");
			renderButton((int)screenW/2+120, (int)screenH+40, 15, 15, "", "", Server.getState() == "bottomAdvantage", g);
			// PenaltyDefend
			if (click && inArea(x, y, (int)screenW/2+100, (int)screenH+60, 35, 10))
				Server.setState("penaltyDefend");
			renderButton((int)screenW/2+100, (int)screenH+60, 35, 8, "", "", Server.getState() == "penaltyDefend", g);
			
			// replay
			if (click && inArea(x, y, (int)screenW/2-50, (int)screenH+30, 100, 20)){
				replay = !replay;
				frame = replayData.size()-1;
			}
			renderButton((int)screenW/2-50, (int)screenH+30, 100, 20, "Replay", "Replay", replay, g);
			
			// frameLimit
			if (click && inArea(x, y, (int)screenW/2-50, (int)screenH+55, 100, 20)){
				frameLimit = !frameLimit;
				if (frameLimit)
					gc.setTargetFrameRate(60);
				else
					gc.setTargetFrameRate(9999);
			}
			renderButton((int)screenW/2-50, (int)screenH+55, 100, 20, "FrameLimit", "FrameLimit", frameLimit, g);
			
			// Replay button
			if (!replay){
				if (click && inArea(x, y, (int)screenW-110, (int)screenH+3, 100, 20))
					recording = !recording;
				renderButton((int)screenW-110, (int)screenH+3, 100, 20, "Recording", "Idle", recording, g);
			}else{
				//forwards
				if (down && inArea(x, y, (int)screenW-60, (int)screenH+3, 20, 20)){
					frame = frame + 1;
					if (frame > replayData.size()-1) frame = replayData.size()-1;
				}
				renderButton((int)screenW-60, (int)screenH+3, 20, 20, ">", ">", false, g);
				//backwards
				if (down && inArea(x, y, (int)screenW-90, (int)screenH+3, 20, 20)){
					frame = frame - 1;
					if (frame < 0) frame = 0;
				}
				renderButton((int)screenW-90, (int)screenH+3, 20, 20, "<", "<", false, g);

				//fast forward
				if (down && inArea(x, y, (int)screenW-30, (int)screenH+3, 20, 20)){
					frame = frame + 10;
					if (frame > replayData.size()-1) frame = replayData.size()-1;
				}
				renderButton((int)screenW-30, (int)screenH+3, 20, 20, ">>", ">>", false, g);
				//fast backward
				if (down && inArea(x, y, (int)screenW-120, (int)screenH+3, 20, 20)){
					frame = frame - 10;
					if (frame < 0) frame = 0;
				}
				renderButton((int)screenW-120, (int)screenH+3, 20, 20, "<<", "<<", false, g);
			}
			g.drawString("Frames: "+replayData.size(), screenW-130, screenH+35);
		}
		if (replay){
			g.drawString("Frame "+(frame+1), screenW-130, screenH+23);
		}
		
		
		if (rendering){

			// Field
			g.setColor(Color.white);
			g.fillRect(0  , screenH/2-(GOAL_WIDTH*SCALE)/2, 3  , (GOAL_WIDTH*SCALE));
			g.fillRect(screenW-3, screenH/2-(GOAL_WIDTH*SCALE)/2, 3, (GOAL_WIDTH*SCALE));
			//corners
			int pos = 32;
			g.drawLine(0, pos, pos, 0);
			g.drawLine(0, screenH-pos, pos, screenH);
			g.drawLine(screenW-pos, 0, screenW, pos);
			g.drawLine(screenW-pos, screenH, screenW, screenH-pos);
			
			// Goal zones
			// outer
			g.drawRect(0*SCALE, 50*SCALE, 35*SCALE, 80*SCALE);
			g.drawRect(185*SCALE, 50*SCALE, 35*SCALE, 80*SCALE);
			// inner
			g.drawRect(0*SCALE, 65*SCALE, 15*SCALE, 50*SCALE);
			g.drawRect(205*SCALE, 65*SCALE, 15*SCALE, 50*SCALE);
			// arcs
			g.drawArc(31f*SCALE, 77.5f*SCALE, 8.5f*SCALE, 25*SCALE, -90f, 90f);
			g.drawArc(180.5f*SCALE, 77.5f*SCALE, 9f*SCALE, 25*SCALE, 90f, -90f);
			// Free ball points
			g.fillOval(54*SCALE, 29*SCALE, 2*SCALE, 2*SCALE);
			g.fillOval(166*SCALE, 29*SCALE, 2*SCALE, 2*SCALE);
			g.fillOval(54*SCALE, 149*SCALE, 2*SCALE, 2*SCALE);
			g.fillOval(166*SCALE, 149*SCALE, 2*SCALE, 2*SCALE);
			// Random points around the free ball points
			for (int i = 30; i<220; i+=112)
				for (int o = 30; o<180; o+=120){
					g.fillRect(i*SCALE, o*SCALE, 3*SCALE, 1*SCALE);
					g.fillRect((i+2)*SCALE, (o-1)*SCALE, 1*SCALE, 3*SCALE);
					g.fillRect((i+48)*SCALE, o*SCALE, 3*SCALE, 1*SCALE);
					g.fillRect((i+48)*SCALE, (o-1)*SCALE, 1*SCALE, 3*SCALE);
				}
			// Center circle
			g.drawOval(85*SCALE, 65*SCALE, 50*SCALE, 50*SCALE);
			/*
			Font font = new Font("Verdana", Font.BOLD, 32);
			TrueTypeFont ttf = new TrueTypeFont(font, true);
			ttf.drawString(95*SCALE, 83*SCALE, "FIRA");
			*/
			// Non-field things
			
			int i = 0;
			for (Team t:teams){
				if (i++==0) //set colors
					g.setColor(Color.green); 
				else 
					g.setColor(Color.red);
				for (Robot r:t.robot)
					r.render(g);
				t.render(g);
			}
			//System.out.println("Drawing ball!");
			Vec2 ballPos = ball.getPosition().mul(SCALE);
			//System.out.println("Drawing at: "+(ballPos.x-(BALL_DIAMETER*SCALE)/2)+", "+(ballPos.y-(BALL_DIAMETER*SCALE)/2));
			//ball.applyForceToCenter(new Vec2(0,0.1f));
			g.drawOval(ballPos.x-(BALL_DIAMETER*SCALE)/2, ballPos.y-(BALL_DIAMETER*SCALE)/2, BALL_DIAMETER*SCALE, BALL_DIAMETER*SCALE);

		}
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
	int frame = -1;
	public void update(GameContainer gc, int delta) throws SlickException {

		if (simulation){ // SIMULATION MODE
			if (replay){
				
				LoadFrame(frame);
				
				for (Team t:teams){
					t.gameFrame();
					for (Robot r:t.robot)
						r.update();
				}
				
			}else{
				for (Team t:teams){
					t.gameFrame();
					for (Robot r:t.robot)
						r.update();
				}

				w.step(1/60f, 30, 30);

				ball.setType(BodyType.DYNAMIC);

				Vec2 ballPos = ball.getPosition().mul(SCALE);
				//ball manipulation
				Input input = gc.getInput();
				boolean mouse = input.isMouseButtonDown(0);
				boolean mouse2 = input.isMouseButtonDown(1);
				int x = input.getMouseX();
				int y = input.getMouseY();
				if (mouse && y<screenH){
					ball.setType(BodyType.STATIC);
					ball.setTransform(new Vec2(x/SCALE,y/SCALE), 0);
					ball.setLinearVelocity(new Vec2(0,0));
					ball.setAngularVelocity(0);
					ball.synchronizeTransform();
					ball.setType(BodyType.DYNAMIC);
				}
				if (mouse2){
					ball.applyForceToCenter(new Vec2(x-ballPos.x, y-ballPos.y).mul(10f));
				}

				//APPLY tiny FORCE TOWARDS CENTER
				if (centerPull)
					ball.applyForceToCenter(new Vec2(SCREEN_WIDTH/2-ballPos.x, SCREEN_HEIGHT/2-ballPos.y).mul(0.1f));

				if (oldBallPos != null){
					float xDiff = ballPos.x-oldBallPos.x;
					float yDiff = ballPos.y-oldBallPos.y;
					if (oldBallPos != null && Math.sqrt(xDiff*xDiff+yDiff*yDiff) < movementTolerance)
						countTillReset++;
					else
						countTillReset = 0;
					if (countTillReset > maxCountTillReset){
						countTillReset = 0;
						resetBall();
					}
				}
				oldBallPos = ballPos;

				
				if (ballPos.y>screenH/2-(GOAL_WIDTH*SCALE)/2 && ballPos.y<screenH/2+(GOAL_WIDTH*SCALE)/2){
					boolean scored = false;
					if (ballPos.x>SCREEN_WIDTH-7.5){
						leftPoints++;
						scored = true;
					}else if (ballPos.x<7.5){
						rightPoints++;
						scored = true;
					}
					if (scored){
						resetBall();
					}
					
				}

				if (recording)
					AddFrame();
			}
		}
	}
	void resetBall(){
		ball.setType(BodyType.STATIC);
		ball.setTransform(new Vec2(FIELD_WIDTH/2,FIELD_HEIGHT/2), 0);
		ball.synchronizeTransform();
		ball.setType(BodyType.DYNAMIC);
		ball.setLinearVelocity(new Vec2(0,0.00001f));
		ball.setAngularVelocity(0);
		
		for (int team = 0; team < 2; team++){
			for (int robot = 0; robot < 5; robot++){
				Body b = teams[team].robot[robot].body;
				b.setType(BodyType.STATIC);
				b.setTransform(new Vec2((team*2+1)*FIELD_WIDTH/4,(robot+1)*FIELD_HEIGHT/6), (float) (Math.PI*team));
				b.synchronizeTransform();
				b.setType(BodyType.DYNAMIC);
			}
		}
	}
	int countTillReset = 0;
	int maxCountTillReset = 2000;
	int movementTolerance = 1;
	Vec2 oldBallPos;

	boolean centerPull = false;
	boolean rendering = true;

	public void keyPressed(int key,char c){
		
	}

	public void AddFrame(){
		gameFrame gf = new gameFrame();
		for (Robot r:teams[0].robot){
			Body b = r.body;
			Vec2 pos = b.getPosition();
			Vec2 vel = b.getLinearVelocity();
			gf.team1.add(new float[]{pos.x, pos.y, b.getAngle(), vel.x, vel.y, b.getAngularVelocity()});
		}
		for (Robot r:teams[1].robot){
			Body b = r.body;
			Vec2 pos = b.getPosition();
			Vec2 vel = b.getLinearVelocity();
			gf.team2.add(new float[]{pos.x, pos.y, b.getAngle(), vel.x, vel.y, b.getAngularVelocity()});
		}
		Vec2 vel = ball.getLinearVelocity();
		gf.ball = new float[]{ball.getPosition().x,ball.getPosition().y, ball.getAngle(), vel.x, vel.y, ball.getAngularVelocity()};
		replayData.add(gf);
	}

	public void LoadFrame(int frame){
		if (frame == -1 || frame >= replayData.size()) return;
		gameFrame gf = replayData.get(frame);
		int i = 0;
		for (Robot r:teams[0].robot){
			float[] data = gf.team1.get(i);
			r.body.setType(BodyType.STATIC);
			r.body.setTransform(new Vec2(data[0],data[1]), data[2]);
			r.body.setType(BodyType.DYNAMIC);
			r.body.setLinearVelocity(new Vec2(data[3],data[4]));
			r.body.setAngularVelocity(data[5]);
			i++;
		}
		i = 0;
		for (Robot r:teams[1].robot){
			float[] data = gf.team2.get(i);
			r.body.setType(BodyType.STATIC);
			r.body.setTransform(new Vec2(data[0],data[1]), data[2]);
			r.body.setType(BodyType.DYNAMIC);
			r.body.setLinearVelocity(new Vec2(data[3],data[4]));
			r.body.setAngularVelocity(data[5]);
			i++;
		}
		float[] data = gf.ball;
		ball.setType(BodyType.STATIC);
		ball.setTransform(new Vec2(data[0],data[1]), data[2]);
		ball.setType(BodyType.DYNAMIC);
		ball.setLinearVelocity(new Vec2(data[3],data[4]));
		ball.setAngularVelocity(data[5]);
	}

	@Override
	public void beginContact(Contact c) {
	}

	@Override
	public void endContact(Contact arg0) {}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {}
}
