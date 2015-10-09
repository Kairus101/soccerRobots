package org.kairus.kairusTeam2;

import org.kairus.api.Team;
import org.newdawn.slick.Graphics;

public class EmptyTeam extends Team {
	public EmptyTeam(int teamNumber) {
		super(teamNumber);
	}
	public void gameFrame() {
	}

	@Override
	public void render(Graphics g) {
	}
}
