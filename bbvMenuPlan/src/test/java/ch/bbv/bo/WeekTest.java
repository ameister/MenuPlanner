package ch.bbv.bo;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

import ch.bbv.bo.Week.DayOfWeek;

public class WeekTest {
	
	private Calendar cal = Calendar.getInstance();
	
	@Before
	public void setUpYear() {
		cal.set(Calendar.YEAR, 2012);
	}

	@Test
	public void getDays_daysFilled_correctOrder() {
		Week testee = new Week();
		testee.setNumber(1);
		testee.buildDays(cal.get(Calendar.YEAR));
		List<Weekday> result = testee.getDays();
		assertEquals(7, result.size());
		int i = 0;
		for (Weekday weekday : result) {
			assertEquals(DayOfWeek.values()[i++], weekday.getDayOfWeek());
		}
	}
	
	@Test
	public void getDays_week1Year2012_20120102(){
		Week testee = new Week();
		testee.setNumber(1);
		testee.buildDays(cal.get(Calendar.YEAR));
		List<Weekday> result = testee.getDays();
		cal.setTime(Iterables.getFirst(result, null).getDate());
		Calendar expected = Calendar.getInstance();
		expected.set(2012, 0, 2);
		assertEquals(expected.get(Calendar.DAY_OF_YEAR), cal.get(Calendar.DAY_OF_YEAR));
		expected.set(2012, 0, 8);
		cal.setTime(result.get(6).getDate());
		assertEquals(expected.get(Calendar.DAY_OF_YEAR), cal.get(Calendar.DAY_OF_YEAR));
	}

}
