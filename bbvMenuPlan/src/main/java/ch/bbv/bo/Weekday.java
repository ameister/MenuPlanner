package ch.bbv.bo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import ch.bbv.bo.Week.DayOfWeek;

@Entity
public class Weekday {
	private static DateFormat df = DateFormat.getDateInstance();
	
	@Id
	@GeneratedValue
	private long id;
	private Date date;
	private DayOfWeek dayOfWeek;
	@ManyToOne
	private Week week;
	@ManyToMany
	private List<Menu> menus = new ArrayList<Menu>();
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return dayOfWeek != null ?dayOfWeek.name() : "?";
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void addMenu(Menu menu) {
		menus.add(menu);
		menu.addDay(this);
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
	
	public void setWeek(Week week) {
		this.week = week;		
	}
	
	public Week getWeek() {
		return week;
	}
	
	@Override
	public String toString() {
		return getDate() != null ? df.format(getDate()) : "?";
	}
}
