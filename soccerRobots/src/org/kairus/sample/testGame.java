package org.kairus.sample;

import org.kairus.api.Game;
import org.kairus.api.Team;

public class testGame {

	public static void main(String[] args) {
		new Game(new Team[]{new KairusTeam(0), new KairusTeam(1)});
	}

}
