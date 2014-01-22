package ch.bbv.menuplanner;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import ch.bbv.menuplanner.bo.CondimentPos;
import ch.bbv.menuplanner.bo.Menu;

public class DisplayMenuActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_display_message);
		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) intent.getSerializableExtra(MainActivity.MENU);

		final Menu menu = (Menu) map.get("menu");
		TextView titleLabel = (TextView) findViewById(R.id.menuTitle);
		titleLabel.setText(menu.getName());
		
		TableLayout tv = (TableLayout) findViewById(R.id.menutable);
		tv.removeAllViews();
		String column1 = getString(R.string.descColumnHeader);
		String column2 = getString(R.string.amountColumnHeader);
		String column3 = getString(R.string.unitColumnHeader);
		for (int i = -1; i < menu.getCondiments().size(); i++ ) {
			
			TableRow tr = new TableRow(this);
			TextView b = new TextView(this);
			b.setPadding(20, 20, 0, 0);
			b.setTextSize(20);
			tr.addView(b);

			TextView b1 = new TextView(this);
			b1.setPadding(20, 20, 0, 0);
			b1.setTextSize(20);
			b1.setGravity(Gravity.RIGHT);
			tr.addView(b1);

			TextView b2 = new TextView(this);
			b2.setPadding(20, 20, 20, 0);
			b2.setTextSize(20);

			int lineColor = Color.LTGRAY;
			int lineHight = 2;
			if (i > -1) {
				CondimentPos pos = menu.getCondiments().get(i);
				column1 = pos.getCondimentName();
				column2 = pos.getAmoutFormatted();
				column3 = pos.getUnit();
			} else {
				b.setTypeface(Typeface.DEFAULT_BOLD);
				b1.setTypeface(Typeface.DEFAULT_BOLD);
				b2.setTypeface(Typeface.DEFAULT_BOLD);
				lineColor = Color.BLACK;
				lineHight = 3;
			}
			b.setText(column1);
			b1.setText(column2);
			b2.setText(column3);

			tr.addView(b2);

			tv.addView(tr);

			final View vline1 = new View(this);
			vline1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, lineHight));
			vline1.setBackgroundColor(lineColor);
			tv.addView(vline1); // add line below each row
		}

		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
