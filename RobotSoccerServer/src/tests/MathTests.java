package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kairus.kairusTeam.MathFunctions;


public class MathTests {
	@Test
	public void mathTests2() {
		assertEquals(-1, MathFunctions.angleDifference(-1, 0), 0.1);
	} 
	@Test
	public void mathTestsSimple() {
		assertEquals(1, MathFunctions.angleDifference(1, 0), 0.1);
	} 
}
