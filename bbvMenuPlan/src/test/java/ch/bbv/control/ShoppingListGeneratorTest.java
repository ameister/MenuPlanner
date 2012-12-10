package ch.bbv.control;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;

import ch.bbv.bo.Condiment;
import ch.bbv.bo.CondimentPos;
import ch.bbv.bo.ListPos;
import ch.bbv.bo.Menu;
import ch.bbv.bo.ShoppingList;

import com.google.common.collect.Lists;


public class ShoppingListGeneratorTest {
	
	@Test
	public void generate_oneMenu_allCondimentContained(){
		Menu menu = new Menu();
		CondimentPos condimentPos1 = new CondimentPos();
		CondimentPos condimentPos2 = new CondimentPos();
		condimentPos1.setCondiment(new Condiment("Condiment 1"));
		condimentPos2.setCondiment(new Condiment("Condiment 2"));
		menu.addPos(condimentPos1);
		menu.addPos(condimentPos2);
		List<Menu> menus = Lists.newArrayList(menu);
		ShoppingListGenerator testee = new ShoppingListGenerator();
		ShoppingList result = testee.generateList(menus);
		List<ListPos> pos = result.getPos();
		assertEquals("Two ListPos expected", Integer.valueOf(2), Integer.valueOf(pos.size()));
		assertEquals("Condiment 1", pos.get(0).getName());
		assertEquals("Condiment 2", pos.get(1).getName());
	}
	
	@Test
	public void generate_twoMenusWithSameCondiment_oneItemForeSameCondiment(){
		Menu menu1 = new Menu();
		Menu menu2 = new Menu();
		CondimentPos condimentPos1 = new CondimentPos();
		CondimentPos condimentPos2 = new CondimentPos();
		condimentPos1.setCondiment(new Condiment("Condiment 1"));
		condimentPos2.setCondiment(new Condiment("Condiment 1"));
		menu1.addPos(condimentPos1);
		menu2.addPos(condimentPos2);
		List<Menu> menus = Lists.newArrayList(menu1, menu2);
		ShoppingListGenerator testee = new ShoppingListGenerator();
		ShoppingList result = testee.generateList(menus);
		List<ListPos> pos = result.getPos();
		assertEquals("One ListPos expected", Integer.valueOf(1), Integer.valueOf(pos.size()));
		assertEquals("Condiment 1", pos.get(0).getName());
	}

}
