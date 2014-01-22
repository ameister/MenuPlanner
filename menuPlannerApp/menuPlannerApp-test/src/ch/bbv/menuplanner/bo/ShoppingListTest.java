package ch.bbv.menuplanner.bo;

import android.test.AndroidTestCase;


public class ShoppingListTest extends AndroidTestCase{
	
	public void isContained_listPosWithSameName_true() {
		String name = "double Name";
		ListPos pos = new ListPos();
		pos.setName(name);
		ShoppingList testee = new ShoppingList(1, 2014);
		testee.addPos(pos);
		boolean result = testee.isContained(name);
		assertTrue("Name must be contained", result);
	}

}
