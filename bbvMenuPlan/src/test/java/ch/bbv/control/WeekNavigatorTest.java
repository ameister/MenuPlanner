package ch.bbv.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import ch.bbv.bo.Week;
import ch.bbv.bo.Weekday;

public class WeekNavigatorTest {
	private final WeekNavigator testee = new WeekNavigator();
	
	
	@Test
	public void getCurrentWeek_currentDate_currentWeek(){
		Week result = testee.getCurrentWeek();
		assertNotNull("Current week must not be null", result);
		List<Weekday> days = result.getDays();
		Calendar expected = Calendar.getInstance();
		Calendar actual = Calendar.getInstance();
		for (Weekday weekday : days) {
			actual.setTime(weekday.getDate());
			if(expected.get(Calendar.DAY_OF_YEAR) == actual.get(Calendar.DAY_OF_YEAR)){
				return;
			}
		}
		fail("Week current Date containing expected");
	}
	
	@Test
	public void next_currentWeek_nextWeek(){
		Week currentWeek = testee.getCurrentWeek();
		Week result = testee.next();
		assertEquals("Next week expected", Integer.valueOf(currentWeek.getNumber() + 1), Integer.valueOf(result.getNumber()));
	}
	
	@Test
	public void previous_currentWeek_previousWeek(){
		Week currentWeek = testee.getCurrentWeek();
		Week result = testee.previous();
		assertEquals("Previous week expected", Integer.valueOf(currentWeek.getNumber() - 1), Integer.valueOf(result.getNumber()));
	}

}
