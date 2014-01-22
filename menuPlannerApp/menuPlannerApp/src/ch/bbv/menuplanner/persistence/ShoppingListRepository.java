package ch.bbv.menuplanner.persistence;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import ch.bbv.menuplanner.R;
import ch.bbv.menuplanner.bo.ListPos;
import ch.bbv.menuplanner.bo.ShoppingList;
import ch.bbv.menuplanner.bo.ShoppingListKey;

public class ShoppingListRepository {
	private final Activity context;

	public ShoppingListRepository(Activity context) {
		this.context = context;
	}

	public void saveShoppingList(ShoppingList list) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.savedShoppingList), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		Set<String> itemNames = retrieveNames(list);
		editor.putStringSet(list.getKey().getKey(), itemNames);
		editor.commit();
	}

	private Set<String> retrieveNames(ShoppingList list) {
		Set<String> itemNames = new HashSet<String>();
		for (ListPos item : list.getPos()) {
			itemNames.add(item.getName());
		}
		return itemNames;
	}

	public ShoppingList loadShoppingList(ShoppingListKey key) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.savedShoppingList), Context.MODE_PRIVATE);
		Set<String> itemNames = sharedPref.getStringSet(key.getKey(), new HashSet<String>());
		return generateList(itemNames, key.getWeek(), key.getYear());
	}
	
	public void deleteShoppingList(ShoppingListKey key) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.savedShoppingList), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.remove(key.getKey());
		editor.commit();
	}

	public Set<ShoppingListKey> loadAllKeys() {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.savedShoppingList), Context.MODE_PRIVATE);
		Set<String> keySet = sharedPref.getAll().keySet();
		Set<ShoppingListKey> shoppingListKeys = new HashSet<ShoppingListKey>();
		for (String key : keySet) {
			ShoppingListKey listKey = new ShoppingListKey(key);
			shoppingListKeys.add(listKey);
		}
		return shoppingListKeys;
	}

	private ShoppingList generateList(Set<String> itemNames, int weeknumber, int year) {
		ShoppingList list = new ShoppingList(weeknumber, year);
		int pos = 0;
		for (String item : itemNames) {
			ListPos position = new ListPos();
			position.setName(item);
			position.setPos(pos++);
			list.addPos(position);
		}
		return list;
	}

}
