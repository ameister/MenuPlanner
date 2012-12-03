package ch.bbv.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.collect.Iterables;

@Entity
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
	@Id
	private long id;
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
		return "Woche " + getNumber() + " " + Iterables.getFirst(days, null) + " - " + Iterables.getLast(days, null);
	}

	public void buildDays(int year) {
		if(getNumber() == 0) {
			throw new IllegalStateException("Unknown Weeknumber");
		}
		cal.set(Calendar.YEAR, year);
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

	public int getYear() {
		cal.setTime(getDays().get(6).getDate());
		return cal.get(Calendar.YEAR);
	}

	public void buildDays() {
		cal.setTime(new Date());
		buildDays(cal.get(Calendar.YEAR));
		
	}
}
