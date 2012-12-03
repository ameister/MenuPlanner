package ch.bbv.control;

import ch.bbv.bo.Menu;

//TODO Transaction handling
public class MenuController {
	
	private Menu currentMenu;
	
	
	public Menu createMenu() {
	    currentMenu = new Menu();
		return currentMenu;
	}

	public Menu getCurrentMenu() {
		return currentMenu;
	}
}
