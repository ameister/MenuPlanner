package ch.bbv.bo;

import static org.junit.Assert.*;
import org.junit.Test;


public class ShoppingListTest {
	
	@Test
	public void isContained_listPosWithSameName_true() {
		String name = "double Name";
		ListPos pos = new ListPos();
		pos.setName(name);
		ShoppingList testee = new ShoppingList();
		testee.addPos(pos);
		boolean result = testee.isContained(name);
		assertTrue("Name must be contained", result);
	}

}
