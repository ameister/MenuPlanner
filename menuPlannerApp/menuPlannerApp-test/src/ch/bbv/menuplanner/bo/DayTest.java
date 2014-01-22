package ch.bbv.menuplanner.bo;

import java.util.Calendar;

import android.test.AndroidTestCase;

public class DayTest extends AndroidTestCase{
	

	public void testCurrentDay_toString_Weekday(){
		final Calendar cal = Calendar.getInstance();
		cal.set(2013, 06, 13);		
		final Day testee = new Day(1, cal.getTime());
		assertEquals("Samstag 13.07.2013", testee.toString());
	}
}
