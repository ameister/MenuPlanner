package ch.bbv.menuplanner.control;

import java.util.List;

import ch.bbv.menuplanner.bo.CondimentPos;
import ch.bbv.menuplanner.bo.ListPos;
import ch.bbv.menuplanner.bo.Menu;
import ch.bbv.menuplanner.bo.ShoppingList;


public class ShoppingListGenerator {

	//add Amounts
	public ShoppingList generateList(List<Menu> menus, int weeknumber, int year) {
		ShoppingList list = new ShoppingList(weeknumber, year);
		for (Menu menu : menus) {
			for (CondimentPos pos : menu.getCondiments()) {
				if(!list.isContained(pos.getCondimentName())) {
					ListPos listPos = new ListPos();
					listPos.setName(pos.getCondimentName());
					list.addPos(listPos);
				}
			}
		}
		return list;
	}
}
