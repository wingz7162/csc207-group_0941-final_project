package Maps;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the implementation of a Patient.
 * @author c3leungb
 *
 */
public class PatientTest {
	
	protected Patient testPatient;
	
	/**
	 * Initializes a testPatient as a Patient with random data.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		testPatient = new Patient("Vijay Vazirani", "1994-03-12",
				"452133");
		this.testPatient.setVitalsigns("12:45", "12:50", 40.0, 160, 75);
	}
	
	/**
	 * Disposes a tested Patient instance.
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		testPatient = null;
	}
	
	/**
	 * Tests that all the provided parameters are properly recorded,
	 * excluding testPatient.vitalsigns as it is a variable set by 
	 * setVitalsigns, which will be tested later on.
	 */
	@Test
	public void testPatient() {
		String expected = "Vijay Vazirani";
		assertTrue(expected.equals(this.testPatient.name));
		expected = "1994-03-12";
		assertTrue(expected.equals(this.testPatient.dob));
		expected = "452133";
		assertTrue(expected.equals(this.testPatient.hcn));
		expected = "12:45";
		assertTrue(this.testPatient.getVitalsigns().containsKey(expected));
	}
	
	/**
	 * Tests that getVitalsigns() returns testPatient.vitalsigns map which 
	 * contains time of arrivals as its key and temperature, blood pressure
	 * and heart rate as its values.
	 */
	@Test
	public void testGetVitalsigns() {
		ArrayList<Number> expected = new ArrayList<Number>();
		expected.add(40.0);
		expected.add(160);
		expected.add(75);
		assertEquals(expected, 
				this.testPatient.getVitalsigns().get("12:45").get("12:50"));
	}
	
	/**
	 * Tests that setVitalsigns properly adds a new key and vital signs to
	 * testPatient.vitalsigns map.
	 */
	@Test
	public void testSetVitalsigns() {
		this.testPatient.setVitalsigns("1:30", "1:32", 35.5, 120, 90);
		ArrayList<Number> expected = new ArrayList<Number>();
		expected.add(35.5);
		expected.add(120);
		expected.add(90);
		assertEquals(expected, 
				this.testPatient.getVitalsigns().get("1:30").get("1:32"));
	}

}
