package ch.bbv.bo;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Iterables;

import ch.bbv.bo.Week.DayOfWeek;

public class WeekTest {
	
	@Test
	public void getDays_daysFilled_correctOrder() {
		Week testee = new Week();
		testee.setNumber(1);
		testee.buildDays();
		List<Weekday> result = testee.getDays();
		Assert.assertEquals(7, result.size());
		int i = 0;
		for (Weekday weekday : result) {
			Assert.assertEquals(DayOfWeek.values()[i++], weekday.getDayOfWeek());
		}
	}
	
	@Test
	public void getDays_week1Year2012_20120102(){
		Week testee = new Week();
		testee.setNumber(1);
		testee.buildDays();
		List<Weekday> result = testee.getDays();
		Calendar cal = Calendar.getInstance();
		cal.setTime(Iterables.getFirst(result, null).getDate());
		Calendar expected = Calendar.getInstance();
		expected.set(2012, 0, 2);
		Assert.assertEquals(expected.get(Calendar.DAY_OF_YEAR), cal.get(Calendar.DAY_OF_YEAR));
		expected.set(2012, 0, 8);
		cal.setTime(result.get(6).getDate());
		Assert.assertEquals(expected.get(Calendar.DAY_OF_YEAR), cal.get(Calendar.DAY_OF_YEAR));
	}

}
