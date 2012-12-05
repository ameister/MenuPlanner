package ch.bbv.control;

import java.util.List;

import ch.bbv.bo.Menu;

//TODO Transaction handling
public class MenuController {
	
	private final List<Menu> menuListToAddMenu;
	
	public MenuController(List<Menu> menus) {
		this.menuListToAddMenu = menus;
	}
	private Menu currentMenu;
	
	public Menu createMenu() {
	    currentMenu = new Menu();
		return currentMenu;
	}

	public Menu getCurrentMenu() {
		return currentMenu;
	}

	public void commit() {
		menuListToAddMenu.add(currentMenu);
		clear();
	}
	
	public void rollback() {
		clear();
	}

	private void clear() {
		currentMenu = null;
	}
}
