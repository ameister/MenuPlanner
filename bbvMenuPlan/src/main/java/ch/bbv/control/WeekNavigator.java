package ch.bbv.control;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ch.bbv.bo.Week;
import ch.bbv.bo.Weekday;

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
		initWeek(currentWeekNumber);
		return currentWeek;
	}

	private void initWeek(int currentWeekNumber) {
		int year = currentWeek == null ? cal.get(Calendar.YEAR) : currentWeek.getYear();
		if(currentWeekNumber < 1 ) {
			currentWeekNumber = 52;
			year--;
		}
		if(currentWeekNumber > 52 ) {
			currentWeekNumber = 1;
			year++;
		}
		if (currentWeek == null) {
			TypedQuery<Week> query = entityManager.createNamedQuery("Week.findByNumber", Week.class);
			query.setParameter("weekNumber", currentWeekNumber);
			List<Week> weeks = query.getResultList();
			currentWeek = Iterables.getFirst(weeks, null);
		}
		if (currentWeek == null) {
			entityManager.getTransaction().begin();
			Week week = new Week();
			week.setNumber(currentWeekNumber);
			week.buildDays(year);
			entityManager.persist(week);
			for (Weekday day : week.getDays()) {
				entityManager.persist(day);
			}
			entityManager.getTransaction().commit();
			currentWeek = week;
		}
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
		int newNumber = currentWeek.getNumber() + amount;
		setCurrentWeek(null);
		initWeek(newNumber);
		fireStateChange();
		return currentWeek;
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
