package ch.bbv.control;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ch.bbv.bo.Menu;
import ch.bbv.bo.Week;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class WeekNavigator implements Observable {
	static final Calendar cal = Calendar.getInstance();
	private Week currentWeek;
	private final Set<InvalidationListener> listeners = Sets.newHashSet();
	private final EntityManager entityManager;

	public WeekNavigator(EntityManager entityManager) {
		if (entityManager == null) {
			throw new IllegalStateException("EntityManager must not be null");
		}
		this.entityManager = entityManager;
	}

	public Week getCurrentWeek() {
		int currentWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
		initWeek(currentWeekNumber, getYearOfWeek());
		return currentWeek;
	}

	private void initWeek(int currentWeekNumber, int year) {
		if(currentWeekNumber < 1 ) {
			currentWeekNumber = 52;
			year--;
		}
		if(currentWeekNumber > 52 ) {
			currentWeekNumber = 1;
			year++;
		}
		if (currentWeek == null) {
			currentWeek = loadWeek(currentWeekNumber, year);
		}
		if (currentWeek == null) {
			entityManager.getTransaction().begin();
			currentWeek = createWeek(currentWeekNumber, year);
			entityManager.persist(currentWeek);
			entityManager.getTransaction().commit();
		}
	}

	private int getYearOfWeek() {
		return currentWeek == null ? cal.get(Calendar.YEAR) : currentWeek.getYear();
	}

	private Week loadWeek(int currentWeekNumber, int year) {
		TypedQuery<Week> query = entityManager.createNamedQuery("Week.findByNumber", Week.class);
		query.setParameter("weekNumber", currentWeekNumber);
		query.setParameter("year", year);
		List<Week> weeks = query.getResultList();
		return Iterables.getFirst(weeks, null);
	}

	private Week createWeek(int currentWeekNumber, int year) {
		Week week = new Week();
		week.setNumber(currentWeekNumber);
		week.buildDays(year);
		return week;
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
		int newNumber = getWeekNumber() + amount;
		int year = getYearOfWeek();
		setCurrentWeek(null);
		initWeek(newNumber, year);
		fireStateChange();
		return currentWeek;
	}

	private int getWeekNumber() {
		return currentWeek == null ? cal.get(Calendar.WEEK_OF_YEAR) : currentWeek.getNumber();
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

	public Week reset() {
		setCurrentWeek(null);
		Week week = getCurrentWeek();
		fireStateChange();
		return week;
	}

	public List<Menu> getCurrentWeeksMenus() {
		return getCurrentWeek().getMenus();
	}

}
