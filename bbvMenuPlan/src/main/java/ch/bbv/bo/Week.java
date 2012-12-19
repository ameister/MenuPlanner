package ch.bbv.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.google.common.collect.Iterables;

@Entity
@NamedQueries({
@NamedQuery(
name="Week.findByNumber",
query="SELECT w FROM Week w WHERE w.number = :weekNumber AND w.year = :year")})
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
	@GeneratedValue
	private long id;
	@Column(nullable = false)
	private int number;
	@Column(nullable = false, name= "WEEK_YEAR")
	private int year;
	@OneToMany(mappedBy = "week", cascade = {CascadeType.PERSIST})
	@OrderBy("dayOfWeek")
	private List<Weekday> days = new ArrayList<Weekday>();

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getYear() {
		return year;
	}
	
	private void addDay(Weekday day) {
		days.add(day);
		day.setWeek(this);
	}

	public List<Weekday> getDays() {
		return days;
	}
	public void setDays(List<Weekday> days) {
		this.days = days;
	}
	
	public List<Menu> getMenus() {
		List<Menu> menus = new ArrayList<Menu>();
		for (Weekday day : getDays()) {
			menus.addAll(day.getMenus());
		}
		return menus;
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
		setYear(year);
	}
	
	@Override
	public String toString() {
		return "Woche " + getNumber() + " " + Iterables.getFirst(days, null) + " - " + Iterables.getLast(days, null);
	}

}
