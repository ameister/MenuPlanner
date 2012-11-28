package ch.bbv.bo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.bbv.bo.Week.DayOfWeek;

public class Weekday {
	private static DateFormat df = DateFormat.getDateInstance();
	private Date date;
	private DayOfWeek dayOfWeek;
	private final List<Menu> menus = new ArrayList<Menu>();

	public String getName() {
		return dayOfWeek != null ?dayOfWeek.name() : "?";
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void addMenu(Menu menu) {
		menus.add(menu);

	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Object getDayOfWeek() {
		return dayOfWeek;
	}
	
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	@Override
	public String toString() {
		return getDate() != null ? df.format(getDate()) : "?";
	}
}
