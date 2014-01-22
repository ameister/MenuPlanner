package ch.bbv.menuplanner.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.bbv.menuplanner.bo.Day;
import ch.bbv.menuplanner.persistence.DayRepository;
import ch.bbv.menuplanner.persistence.OnTaskCompletedListener;

public class DayController implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Calendar calendar = Calendar.getInstance();

	private Day currentDay;
	private final List<OnTaskCompletedListener<List<Day>>> dayListeners = new ArrayList<OnTaskCompletedListener<List<Day>>>();

	private OnTaskCompletedListener<List<Day>> dayListener = new OnTaskCompletedListener<List<Day>>() {
		@Override
		public void onTaskCompleted(List<Day> result) {
			taskCompleted(result);
		}
	};

	private void taskCompleted(List<Day> result) {
		if (result.size() == 1 && result.get(0).getId() > 0) {
			currentDay = result.get(0);
			calendar.setTime(currentDay.getDate());
		}
		for (OnTaskCompletedListener<List<Day>> dayListener : dayListeners) {
			dayListener.onTaskCompleted(result);
		}
	}

	public void setCurrentDay(Day currentDay) {
		this.currentDay = currentDay;
		calendar.setTime(currentDay.getDate());
	}

	public Day getCurrentDay() {
		return currentDay;
	}

	public void next(DayRepository dao) {
		roll(1, dao);

	}

	public void previous(DayRepository dao) {
		roll(-1, dao);
	}

	public void loadCurrentDay(DayRepository dao) {
		if (currentDay == null) {
			currentDay = new Day(-1, calendar.getTime());
		}
		if (!isDayLoadedFromServer()) {
			dao.execute(currentDay);
			dao.addDayListener(dayListener);
		}
	}

	public boolean isDayLoadedFromServer() {
		return currentDay != null && currentDay.getId() > 0;
	}

	private void roll(int amount, DayRepository dao) {
		calendar.add(Calendar.DATE, amount);
		currentDay = new Day(-amount, calendar.getTime());
		dao.execute(currentDay);
		dao.addDayListener(dayListener);
	}

	public void reset(DayRepository dao) {
		Date now = new Date();
		calendar.setTime(now);
		currentDay = new Day(-1, calendar.getTime());
		dao.execute(currentDay);
		dao.addDayListener(dayListener);
	}

	public void addDayListener(OnTaskCompletedListener<List<Day>> listener) {
		dayListeners.add(listener);
	}

	public void removeDayListener(OnTaskCompletedListener<List<Day>> listener) {
		dayListeners.remove(listener);
	}

	public void loadDaysInSameWeek(DayRepository dao) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDay.getDate());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Date begin = cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		Date end = cal.getTime();
		dao.execute(new Day(-1, begin), new Day(-2, end));
		dao.addDayListener(dayListener);
	}

	// TODO test
	public int getWeeknumber() {
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	// TODO test
	public int getYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDay.getDate());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return cal.get(Calendar.YEAR);
	}
}
