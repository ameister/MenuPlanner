package ch.bbv.control;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import ch.bbv.bo.Menu;
import ch.bbv.bo.Week;
import ch.bbv.bo.Weekday;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

public class WeekNavigator implements Observable {
	private static final Calendar cal = Calendar.getInstance();
	private Week currentWeek;
	private final Set<InvalidationListener> listeners = Sets.newHashSet();
	
	public Week getCurrentWeek() {
		if(currentWeek == null) {
			int currentWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
			Week week = new Week();
			week.setNumber(currentWeekNumber);
			week.buildDays();
			fillInMenus(week.getDays());
			currentWeek = week;
			fireStateChange();
		}
		return currentWeek;
	}
	
	@VisibleForTesting
	void setCurrentWeek(Week currentWeek) {
		this.currentWeek = currentWeek;
	}

	public Week next() {
		return roll(1);
	}

	public Week previous() {
		return roll(-1);
	}

	private Week roll(int amount) {
		Week week = new Week();
		int newNumber = getCurrentWeek().getNumber() + amount;
		int year = currentWeek.getYear();
		if(newNumber < 1 ) {
			newNumber = 52;
			year--;
		}
		if(newNumber > 52 ) {
			newNumber = 1;
			year++;
		}
		week.setNumber(newNumber);
		week.buildDays(year);
		fillInMenus(week.getDays());
		currentWeek = week;
		fireStateChange();
		return currentWeek;
	}

	private void fillInMenus(List<Weekday> days) {
		for (Weekday day : days) {
			Menu menu = new Menu();
			menu.setName("Menu " + day.getName());
			day.addMenu(menu);
		}
	}
	
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

}
