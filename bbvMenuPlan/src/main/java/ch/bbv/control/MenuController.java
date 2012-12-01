package ch.bbv.control;

import ch.bbv.bo.Menu;

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
