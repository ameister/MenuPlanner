package ch.bbv.menuplanner.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ShoppingList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final int weekNumber;
	private final int year;
	private List<ListPos> pos = new ArrayList<ListPos>();
	
	public ShoppingList(int weekNumber, int year) {
		this.weekNumber = weekNumber;
		this.year = year;
	}

	public int getWeekNumber() {
		return weekNumber;
	}
	
	public int getYear() {
		return year;
	}
	
	public ShoppingListKey getKey() {
		return new ShoppingListKey(weekNumber, year);
	}
	
	public List<ListPos> getPos() {
		return pos;
	}
	
	public void addPos(ListPos position) {
		pos.add(position);
	}

	public boolean isContained(String name) {
		for (ListPos pos : getPos()) {
			String posName = pos.getName();
			if(name == null && posName == null || posName.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
