package ch.bbv.menuplanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import ch.bbv.menuplanner.bo.Day;
import ch.bbv.menuplanner.bo.Menu;
import ch.bbv.menuplanner.bo.ShoppingList;
import ch.bbv.menuplanner.bo.ShoppingListKey;
import ch.bbv.menuplanner.control.DayController;
import ch.bbv.menuplanner.control.ShoppingListGenerator;
import ch.bbv.menuplanner.persistence.DayRepository;
import ch.bbv.menuplanner.persistence.OnTaskCompletedListener;
import ch.bbv.menuplanner.persistence.ShoppingListRepository;
import ch.bbv.menuplanner.ui.DatePickerFragment;
import ch.bbv.menuplanner.ui.OnDateSetListener;
import ch.bbv.menuplanner.ui.SwipeDismissListViewTouchListener;

//TODO write loading...
public class MainActivity extends Activity {

	public static final String MENU = Menu.class.getName();
	public static final String SERVER_ADDRESS = "server.address";
	public static final String SHOPPING_LIST = ShoppingList.class.getName();

	private static final String CURRENT_DAY = Day.class.getName();

	private DayController dayController;

	private SimpleAdapter menuAdapter;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	private String serverAddress;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ShoppingListRepository repo;
	private ArrayAdapter<ShoppingListKey> drawerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dayController = new DayController();
		if (savedInstanceState != null) {
			serverAddress = savedInstanceState.getString(SERVER_ADDRESS);
			dayController.setCurrentDay((Day) savedInstanceState.getSerializable(CURRENT_DAY));
		} else {
			serverAddress = getIntent().getStringExtra(SERVER_ADDRESS);
		}

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		repo = new ShoppingListRepository(this);
		drawerAdapter = new ArrayAdapter<ShoppingListKey>(this, R.layout.drawer_list_item);
		mDrawerList.setAdapter(drawerAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(mDrawerList, new SwipeDismissListViewTouchListener.DismissCallbacks() {
			@Override
			public void onDismiss(ListView listView, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					ShoppingListKey item = drawerAdapter.getItem(position);
					repo.deleteShoppingList(item);
					drawerAdapter.remove(item);
				}
				drawerAdapter.notifyDataSetChanged();
			}

			@Override
			public boolean canDismiss(int position) {
				return drawerAdapter.getItem(position) != null;
			}
		});
		mDrawerList.setOnTouchListener(touchListener);
		mDrawerList.setOnScrollListener(touchListener.makeScrollListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		FlingListener flingDedector = new FlingListener();
		final GestureDetector mDetector = new GestureDetector(this, flingDedector);

		menuAdapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] { "menu", "condiment" }, new int[] { android.R.id.text1, android.R.id.text2 });
		final ListView listview = (ListView) findViewById(R.id.menuListView);
		listview.setAdapter(menuAdapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				Intent intent = new Intent(MainActivity.this, DisplayMenuActivity.class);
				intent.putExtra(MENU, (Serializable) listview.getItemAtPosition(position));
				startActivity(intent);
			}

		});
		listview.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return mDetector.onTouchEvent(event);
			}
		});

		dayController.addDayListener(new OnTaskCompletedListener<List<Day>>() {
			@Override
			public void onTaskCompleted(List<Day> result) {
				refreshUI(true);
			}
		});
		dayController.loadCurrentDay(new DayRepository(serverAddress));

	}

	private void refreshUI(boolean force) {
		if (force || dayController.isDayLoadedFromServer()) {
			final TextView tvDisplayDate = (TextView) findViewById(R.id.tvDate);
			Day currentDay = dayController.getCurrentDay();
			tvDisplayDate.setText(currentDay.toString());
			data.clear();
			for (Menu menu : currentDay.getMenus()) {
				Map<String, Object> datum = new HashMap<String, Object>(2);
				datum.put("menu", menu);
				datum.put("condiment", menu.getCondiments());
				data.add(datum);
			}
			menuAdapter.notifyDataSetChanged();
		}

		drawerAdapter.clear();
		drawerAdapter.addAll(repo.loadAllKeys());
		drawerAdapter.notifyDataSetChanged();
	}

	private void selectItem(Object object) {
		ShoppingList list = repo.loadShoppingList((ShoppingListKey) object);
		Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
		intent.putExtra(SHOPPING_LIST, list);
		startActivity(intent);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			if (mDrawerList.getVisibility() == View.VISIBLE) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(SERVER_ADDRESS, serverAddress);
		outState.putSerializable(CURRENT_DAY, dayController.getCurrentDay());
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI(false);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		serverAddress = intent.getStringExtra(SERVER_ADDRESS);
	}
	
	public void reset(View view) {
		dayController.reset(new DayRepository(serverAddress));
		dayController.loadCurrentDay(new DayRepository(serverAddress));
	}

	public void goToDate(View view) {
		final DatePickerFragment newFragment = new DatePickerFragment(dayController.getCurrentDay().getDate());
		newFragment.addListener(new OnDateSetListener() {

			@Override
			public void onDateSet(Date date) {
				dayController.setCurrentDay(new Day(-1, date));
				dayController.loadCurrentDay(new DayRepository(serverAddress));
				dayController.loadCurrentDay(new DayRepository(serverAddress));
			}
		});
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void generateShoppinglist(MenuItem item) {
		dayController.loadDaysInSameWeek(new DayRepository(serverAddress));
		dayController.addDayListener(new OnTaskCompletedListener<List<Day>>() {
			@Override
			public void onTaskCompleted(List<Day> result) {
				final List<Menu> menus = new ArrayList<Menu>();
				for (Day day : result) {
					menus.addAll(day.getMenus());
				}
				ShoppingListGenerator generator = new ShoppingListGenerator();
				ShoppingList generatedList = generator.generateList(menus, dayController.getWeeknumber(), dayController.getYear());
				Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
				intent.putExtra(SHOPPING_LIST, generatedList);
				startActivity(intent);
				dayController.removeDayListener(this);
			}
		});
	}

	public void changeServer(MenuItem item) {
		Intent intent = new Intent(MainActivity.this, StartActivity.class);
		startActivity(intent);
	}

	private class FlingListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			if (event1.getX() > event2.getX()) {
				dayController.next(new DayRepository(serverAddress));
			} else if (event1.getX() < event2.getX()) {
				dayController.previous(new DayRepository(serverAddress));
			}
			dayController.loadCurrentDay(new DayRepository(serverAddress));
			return true;
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(parent.getItemAtPosition(position));
		}
	}
}
