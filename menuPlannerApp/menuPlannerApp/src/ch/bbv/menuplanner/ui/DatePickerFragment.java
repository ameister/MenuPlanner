package ch.bbv.menuplanner.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	private final List<OnDateSetListener> listeners = new ArrayList<OnDateSetListener>();
	private final Date currentDate;
	
	
	public DatePickerFragment(Date currentDate) {
		this.currentDate = currentDate;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		notifyListeners(c.getTime());
	}
	
	public void addListener(OnDateSetListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(OnDateSetListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(Date date) {
		for (OnDateSetListener listener : listeners) {
			listener.onDateSet(date);
		}
	}
}