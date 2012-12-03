package ch.bbv.bo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import com.google.common.collect.Sets;

import ch.bbv.bo.Week.DayOfWeek;

@Entity
public class Weekday implements Observable{
	private static DateFormat df = DateFormat.getDateInstance();
	
	@Id
	private Date date;
	private DayOfWeek dayOfWeek;
	private final List<Menu> menus = new ArrayList<Menu>();
	
	private final Set<InvalidationListener> listeners = Sets.newHashSet();
	
	@Override
	public void addListener(InvalidationListener arg0) {
		listeners.add(arg0);
	}

	@Override
	public void removeListener(InvalidationListener arg0) {
		listeners.remove(arg0);		
	}
	
	private void fireStateChange() {
		for (InvalidationListener listener : listeners) {
			listener.invalidated(this);
		}
	}

	public String getName() {
		return dayOfWeek != null ?dayOfWeek.name() : "?";
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void addMenu(Menu menu) {
		menus.add(menu);
		fireStateChange();
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
