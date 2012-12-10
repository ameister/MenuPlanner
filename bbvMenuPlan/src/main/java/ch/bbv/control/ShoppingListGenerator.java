package ch.bbv.control;

import java.util.List;

import ch.bbv.bo.CondimentPos;
import ch.bbv.bo.ListPos;
import ch.bbv.bo.Menu;
import ch.bbv.bo.ShoppingList;


public class ShoppingListGenerator {

	//add Amounts
	public ShoppingList generateList(List<Menu> menus) {
		ShoppingList list = new ShoppingList();
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
