package tests;

import org.kairus.api.Game;
import org.kairus.api.Team;
import org.kairus.kairusTeam.KairusTeam;
import org.kairus.kairusTeam2.KairusTeam2;

public class RunSimulation {

	public static void main(String[] args) {
		new Game(new Team[]{new KairusTeam(0), new KairusTeam2(1) }, true);
	}

}
