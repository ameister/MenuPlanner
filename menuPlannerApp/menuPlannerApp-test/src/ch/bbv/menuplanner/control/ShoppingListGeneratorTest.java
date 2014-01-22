package ch.bbv.menuplanner.control;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import ch.bbv.menuplanner.bo.Condiment;
import ch.bbv.menuplanner.bo.CondimentPos;
import ch.bbv.menuplanner.bo.ListPos;
import ch.bbv.menuplanner.bo.Menu;
import ch.bbv.menuplanner.bo.ShoppingList;


public class ShoppingListGeneratorTest extends AndroidTestCase {
	
	public void testGenerate_oneMenu_allCondimentContained(){
		Menu menu = new Menu();
		CondimentPos condimentPos1 = new CondimentPos();
		CondimentPos condimentPos2 = new CondimentPos();
		condimentPos1.setCondiment(new Condiment("Condiment 1"));
		condimentPos2.setCondiment(new Condiment("Condiment 2"));
		menu.addPos(condimentPos1);
		menu.addPos(condimentPos2);
		List<Menu> menus = new ArrayList<Menu>();
		menus.add(menu);
		ShoppingListGenerator testee = new ShoppingListGenerator();
		ShoppingList result = testee.generateList(menus, 1, 2014);
		List<ListPos> pos = result.getPos();
		assertEquals("Two ListPos expected", Integer.valueOf(2), Integer.valueOf(pos.size()));
		assertEquals("Condiment 1", pos.get(0).getName());
		assertEquals("Condiment 2", pos.get(1).getName());
	}
	
	public void testGenerate_twoMenusWithSameCondiment_oneItemForeSameCondiment(){
		Menu menu1 = new Menu();
		Menu menu2 = new Menu();
		CondimentPos condimentPos1 = new CondimentPos();
		CondimentPos condimentPos2 = new CondimentPos();
		condimentPos1.setCondiment(new Condiment("Condiment 1"));
		condimentPos2.setCondiment(new Condiment("Condiment 1"));
		menu1.addPos(condimentPos1);
		menu2.addPos(condimentPos2);
		List<Menu> menus = new ArrayList<Menu>();
		menus.add(menu1);
		menus.add(menu2);
		ShoppingListGenerator testee = new ShoppingListGenerator();
		ShoppingList result = testee.generateList(menus, 1, 2014);
		List<ListPos> pos = result.getPos();
		assertEquals("One ListPos expected", Integer.valueOf(1), Integer.valueOf(pos.size()));
		assertEquals("Condiment 1", pos.get(0).getName());
	}

}
