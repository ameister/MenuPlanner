package ch.bbv.control;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import ch.bbv.bo.Menu;
import ch.bbv.bo.Weekday;

public class MenuController {

	private final List<Menu> menuListToAddMenu;
	private final EntityManager entityManager;
	private final Weekday day;
	private Menu currentMenu;

	//TODO move Menu
	//TODO remove Menu
	public MenuController(List<Menu> menus, Weekday day, EntityManager entityManager) {
		this.menuListToAddMenu = menus;
		this.day = day;
		this.entityManager = entityManager;
	}

	Menu createMenu() {
		currentMenu = new Menu();
		return currentMenu;
	}

	public Menu getCurrentMenu() {
		if(currentMenu == null) {
			createMenu();
		}
		return currentMenu;
	}
	
	public void setCurrentMenu(Menu currentMenu) {
		this.currentMenu = currentMenu;
	}

	public void beginTransaction() {
		EntityTransaction transaction = entityManager.getTransaction();
		if(currentMenu != null) {
			entityManager.merge(currentMenu);
		}
		transaction.begin();
	}
	
	public void persist(Object entity) {
		entityManager.persist(entity);
	}
	
	public void commit() {
		if(!menuListToAddMenu.contains(currentMenu)){
			menuListToAddMenu.add(currentMenu);
			currentMenu.setDay(day);
		}
		entityManager.persist(currentMenu);
		commitTransaction();
		clear();
	}

	public void commitTransaction() {
		entityManager.getTransaction().commit();
	}

	public void rollback() {
		entityManager.getTransaction().rollback();
		clear();
	}

	private void clear() {
		currentMenu = null;
	}
}
