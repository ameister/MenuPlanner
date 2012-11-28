package ch.bbv.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.common.collect.Iterables;

public class Week {
	private static final Calendar cal = Calendar.getInstance();
	public static enum DayOfWeek {
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY,
		SUNDAY
	}
	
	private int number;
	private final List<Weekday> days = new ArrayList<Weekday>();
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	private void addDay(Weekday day) {
		days.add(day);
	}

	public List<Weekday> getDays() {
		return days;
	}

	public List<Menu> getMenus() {

		List<Menu> menus = new ArrayList<Menu>();
		for (Weekday day : getDays()) {
			menus.addAll(day.getMenus());
		}
		return menus;
	}
	
	@Override
	public String toString() {
		return "Week " + getNumber() + " " + Iterables.getFirst(days, null) + " - " + Iterables.getLast(days, null);
	}

	public void buildDays() {
		if(getNumber() == 0) {
			throw new IllegalStateException("Unknown Weeknumber");
		}
		cal.set(Calendar.WEEK_OF_YEAR, getNumber());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		for (int i = 0; i < 7; i++) {
			Weekday day = new Weekday();
			day.setDayOfWeek(DayOfWeek.values()[i]);
			day.setDate(cal.getTime());
			cal.add(Calendar.DATE, 1);
			addDay(day);
		}
	}
}
