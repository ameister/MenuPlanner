package ch.bbv.control;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

import ch.bbv.bo.Menu;
import ch.bbv.bo.Week;
import ch.bbv.bo.Weekday;

public class WeekNavigatorTest {
	
	@Mock
	private EntityManager entityManagerMock;
	@Mock
	private EntityTransaction transactionMock;
	@Mock
	private TypedQuery<Week> queryMock;
	private WeekNavigator testee;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		testee = new WeekNavigator(entityManagerMock);
		when(entityManagerMock.getTransaction()).thenReturn(transactionMock);
		when(entityManagerMock.createNamedQuery("Week.findByNumber", Week.class)).thenReturn(queryMock);
		when(queryMock.getResultList()).thenReturn(new ArrayList<Week>());
	}
	
	
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
	
	@Test
	public void previous_week1current_week52previousYear(){
		Week week = new Week();
		Calendar cal = Calendar.getInstance();
		week.setNumber(1);
		week.buildDays(cal.get(Calendar.YEAR));
		testee.setCurrentWeek(week);
		Week result = testee.previous();
		assertEquals("52st week expected", Integer.valueOf(52), Integer.valueOf(result.getNumber()));
		Weekday day = result.getDays().get(0);
		Calendar expected = Calendar.getInstance();
		cal.setTime(day.getDate());
		assertEquals("previous year expected", Integer.valueOf(expected.get(Calendar.YEAR) - 1), Integer.valueOf(cal.get(Calendar.YEAR)));
				
	}
	
	@Test
	public void nextNext_week52current_week1NextYear(){
		Week week = new Week();
		Calendar cal = Calendar.getInstance();
		week.setNumber(52);
		week.buildDays(cal.get(Calendar.YEAR));
		testee.setCurrentWeek(week);
		Week result = testee.next();
		assertEquals("1st week expected", Integer.valueOf(1), Integer.valueOf(result.getNumber()));
		Calendar expected = Calendar.getInstance();
		assertEquals("next year expected", Integer.valueOf(expected.get(Calendar.YEAR) + 1), Integer.valueOf(result.getYear()));
		
	}
	
	@Test 
	public void getCurrentWeek_currentWeekNull_findCalled() {
		testee.getCurrentWeek();
		verify(entityManagerMock).createNamedQuery("Week.findByNumber", Week.class);
		int currentWeekNumber = WeekNavigator.cal.get(Calendar.WEEK_OF_YEAR);
		verify(queryMock).setParameter("weekNumber", currentWeekNumber);
		verify(queryMock).getResultList();
	}

	@Test 
	public void getCurrentWeek_currentWeekNotInDB_weekCreated() {
		Week result = testee.getCurrentWeek();
		assertNotNull("Week must be created", result);
		verify(transactionMock).commit();
		verify(entityManagerMock).persist(eq(result));
	}
	
	@Test
	public void reset_nextAndResetCalled_weekResetted() {
		testee.next();
		Week result = testee.reset();
		Calendar expected = Calendar.getInstance();
		Calendar actual = Calendar.getInstance();
		for (Weekday weekday : result.getDays()) {
			actual.setTime(weekday.getDate());
			if(expected.get(Calendar.DAY_OF_YEAR) == actual.get(Calendar.DAY_OF_YEAR)){
				return;
			}
		}
		fail("Week current Date containing expected");
	}
	
	@Test 
	public void getCurrentWeeksMenus_weekWithMenus_allMenusReturned() {
		Week week = mock(Week.class);
		List<Menu> menus = Lists.newArrayList();
		List<Weekday> days = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			Weekday day = new Weekday();
			Menu menu = new Menu();
			menus.add(menu);
			day.addMenu(menu);
			days.add(day);
		}
		when(week.getMenus()).thenReturn(menus);
		testee.setCurrentWeek(week);
		assertEquals("All Menu must be contained", menus.size(), testee.getCurrentWeeksMenus().size());
	}

}
