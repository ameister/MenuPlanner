package ch.bbv.menuplanner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.bbv.menuplanner.bo.ListPos;
import ch.bbv.menuplanner.bo.ShoppingList;
import ch.bbv.menuplanner.persistence.ShoppingListRepository;
import ch.bbv.menuplanner.ui.SwipeDismissListViewTouchListener;

public class ShoppingListActivity extends Activity {

	private ShoppingList shoppingList;
	private final ShoppingListRepository repo = new ShoppingListRepository(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_list);

		shoppingList = (ShoppingList) getIntent().getSerializableExtra(MainActivity.SHOPPING_LIST);

		final ListView listView = (ListView) findViewById(R.id.shoppinglistView);
		final ArrayAdapter<ListPos> adapter = new ArrayAdapter<ListPos>(this, android.R.layout.simple_list_item_1, shoppingList.getPos());
		listView.setAdapter(adapter);

		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
			@Override
			public void onDismiss(ListView listView, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					adapter.remove(adapter.getItem(position));
				}
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public boolean canDismiss(int position) {
				return adapter.getItem(position) != null;
			}
		});
		listView.setOnTouchListener(touchListener);
		listView.setOnScrollListener(touchListener.makeScrollListener());


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
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping_list, menu);
		return true;
	}
	
	public void save(MenuItem item) {
		repo.saveShoppingList(shoppingList);
	}

}
