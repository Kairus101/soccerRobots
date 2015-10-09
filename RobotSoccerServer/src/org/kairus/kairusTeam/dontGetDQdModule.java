package org.kairus.kairusTeam;

import org.jbox2d.common.Vec2;
import org.kairus.api.Robot;
import org.kairus.api.Team;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class dontGetDQdModule extends RobotModule {

	dontGetDQdModule(int robotNumber, Team team) {
		super(robotNumber, team);
	}

	int goalZoneWidth = 30;
	int goalZoneHeight = 180;
	
	Vec2 mygoalpos = new Vec2(team.getTeamNumber()==0?0:SCREEN_WIDTH,SCREEN_HEIGHT/2);
	Vec2 goalpos = new Vec2(team.getTeamNumber()==0?SCREEN_WIDTH:0,SCREEN_HEIGHT/2);
	@Override
	int assessUtility(Team team) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		
		// Y is correct for the goal
		if ((pos.y>(SCREEN_HEIGHT/2)-goalZoneHeight/2) && (pos.y<(SCREEN_HEIGHT/2)+goalZoneHeight/2)){
			if (team.getTeamNumber()==0){
				if (pos.x > SCREEN_WIDTH-goalZoneWidth)
					return 100;
			}else{
				if (pos.x < goalZoneWidth)
					return 100;
			}
		}
			
		return 0;
	}

	double tolerance = 0;

	double idealX;
	double idealY;
	@Override
	boolean execute(Team team) {
		idealX = mygoalpos.x;
		idealY = mygoalpos.y;
		boolean toReturn = driveToPoint(idealX, idealY, 1, 0, 5, 50);
		idealPosition = null;
		return toReturn;
	}

	@Override
	void render(Graphics g) {
		Robot me = team.getRobots()[robotNumber];
		Vec2 pos = me.getPosition();
		g.setColor(Color.red);
		g.drawLine((float)pos.x, (float)pos.y, (float)idealX, (float)idealY);

		int x = (int) (team.getTeamNumber()==0?(SCREEN_WIDTH-goalZoneWidth):goalZoneWidth);
		int x2 = (int) (team.getTeamNumber()==0?SCREEN_WIDTH:0);
		int y1 = (int) ((SCREEN_HEIGHT/2)-goalZoneHeight/2);
		int y2 = (int) ((SCREEN_HEIGHT/2)+goalZoneHeight/2);
		g.drawLine(x, y1, x2, y1);
		g.drawLine(x, y2, x2, y2);
		g.drawLine(x, y1, x, y2);
		
	}
}
