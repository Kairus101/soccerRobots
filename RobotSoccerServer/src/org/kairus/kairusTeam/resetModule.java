package org.kairus.kairusTeam;

import java.util.HashMap;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Server;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class resetModule extends RobotModule {
	HashMap<String, double[][]> positionsMap = new HashMap<String, double[][]>();
	HashMap<String, String> oppositesMap = new HashMap<String, String>();
	int initialRobot;
	resetModule(int robotNumber, Team team) {
		super(robotNumber, team);
		staticUtility = true;
		initialRobot = robotNumber;

		
		positionsMap.put("reset", new double[][]{
			{30,SCREEN_HEIGHT/2,Math.PI/2},
			{2.8*SCREEN_WIDTH/8, 2.5*SCREEN_HEIGHT/6,0},
			{2.8*SCREEN_WIDTH/8, 3.5*SCREEN_HEIGHT/6,0},
			{2*SCREEN_WIDTH/8,  2.5*SCREEN_HEIGHT/6,Math.PI/2},
			{2*SCREEN_WIDTH/8,  3.5*SCREEN_HEIGHT/6,Math.PI/2},
		});
		oppositesMap.put("reset", "reset");
		
		positionsMap.put("penaltyAttack", new double[][]{
			{30,SCREEN_HEIGHT/2,Math.PI/2},
			{SCREEN_WIDTH-45*SCALE,SCREEN_HEIGHT/2,0},
			{SCREEN_WIDTH/2-20, 1*SCREEN_HEIGHT/4,0},
			{SCREEN_WIDTH/2-20, 2*SCREEN_HEIGHT/4,0},
			{SCREEN_WIDTH/2-20, 3*SCREEN_HEIGHT/4,0},
		});
		oppositesMap.put("penaltyAttack", "penaltyDefend");
		
		positionsMap.put("penaltyDefend", new double[][]{
			{30,SCREEN_HEIGHT/2,Math.PI/2},
			{SCREEN_WIDTH/2+20, 1*SCREEN_HEIGHT/5,Math.PI},
			{SCREEN_WIDTH/2+20, 2*SCREEN_HEIGHT/5,Math.PI},
			{SCREEN_WIDTH/2+20, 3*SCREEN_HEIGHT/5,Math.PI},
			{SCREEN_WIDTH/2+20, 4*SCREEN_HEIGHT/5,Math.PI},
		});
		oppositesMap.put("penaltyDefend", "penaltyAttack");
		
		positionsMap.put("topAdvantage", new double[][]{
			{30,SCREEN_HEIGHT/2,Math.PI/2},
			{SCREEN_WIDTH/2-20, 1*SCREEN_HEIGHT/5,0},
			{SCREEN_WIDTH/2-20, 2*SCREEN_HEIGHT/5,0},
			{SCREEN_WIDTH*3/4-20*SCALE, SCREEN_HEIGHT/6,0},
			{SCREEN_WIDTH*3/4-20*SCALE, SCREEN_HEIGHT/2+20,-Math.PI/2},
		});
		oppositesMap.put("topAdvantage", "topDefense");
		
		positionsMap.put("bottomAdvantage", new double[][]{
			{30,SCREEN_HEIGHT/2,Math.PI/2},
			{SCREEN_WIDTH/2-20, 3*SCREEN_HEIGHT/5,0},
			{SCREEN_WIDTH/2-20, 4*SCREEN_HEIGHT/5,0},
			{SCREEN_WIDTH*3/4-20*SCALE, 5*SCREEN_HEIGHT/6,0},
			{SCREEN_WIDTH*3/4-20*SCALE, SCREEN_HEIGHT/2-20,Math.PI/2},
		});
		oppositesMap.put("bottomAdvantage", "bottomDefense");
		
		positionsMap.put("topDefense", new double[][]{
			{30,SCREEN_HEIGHT/2-20,Math.PI/2},
			{SCREEN_WIDTH/2+20, 1*SCREEN_HEIGHT/5,Math.PI},
			{SCREEN_WIDTH/2+20, 2*SCREEN_HEIGHT/5,Math.PI},
			{SCREEN_WIDTH/4-20*SCALE, SCREEN_HEIGHT/6,0},
			{SCREEN_WIDTH/4-20*SCALE, SCREEN_HEIGHT/2+20,-Math.PI/2},
		});
		oppositesMap.put("topDefense", "bottomAdvantage");
		
		positionsMap.put("bottomDefense", new double[][]{
			{30,SCREEN_HEIGHT/2+20,Math.PI/2},
			{SCREEN_WIDTH/2+20, 3*SCREEN_HEIGHT/5,-Math.PI},
			{SCREEN_WIDTH/2+20, 4*SCREEN_HEIGHT/5,-Math.PI},
			{SCREEN_WIDTH/4-20*SCALE, 5*SCREEN_HEIGHT/6,0},
			{SCREEN_WIDTH/4-20*SCALE, SCREEN_HEIGHT/2-20,Math.PI/2},
		});
		oppositesMap.put("bottomDefense", "bottomAdvantage");
	}

	@Override
	int assessUtility(Team team) {
		if (positionsMap.get(org.kairus.api.Server.getState()) != null){
			return 150;
		}
		return 0;
	}
	/*
	Vec2[] Positions = new Vec2[]{
		new Vec2(50,                 SCREEN_HEIGHT/2),
		new Vec2(  SCREEN_WIDTH/8,   SCREEN_HEIGHT/6),
		new Vec2(  SCREEN_WIDTH/8, 5*SCREEN_HEIGHT/6),
		new Vec2(3*SCREEN_WIDTH/8,   SCREEN_HEIGHT/6),
		new Vec2(3*SCREEN_WIDTH/8, 5*SCREEN_HEIGHT/6),
	};*/

	
	
	double tolerance = 8;
	double idealX;
	double idealY;
	@Override
	boolean execute(Team team) {
		String position = org.kairus.api.Server.getState();
		if (team.getTeamNumber() == 1)
			position = oppositesMap.get(position);
		double[][] positions = positionsMap.get(org.kairus.api.Server.getState());
		double[] pos = positions[initialRobot];
		idealX = pos[0];
		if (team.getTeamNumber() == 1)
			idealX = SCREEN_WIDTH-idealX;
		idealY = pos[1];
		double idealRotation = pos[2];

		return driveToPoint(idealX, idealY, 0, idealRotation, 20, 50);
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.green);
		if (initialRobot == 0)
			g.setColor(Color.cyan);
		//Vec2 forwards = me.getForwards().mul(100);
		//g.drawLine((float)pos.x, (float)pos.y,(float)pos.x+forwards.x, (float)pos.y+forwards.y);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);
	}
}
