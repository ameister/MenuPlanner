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

	public MenuController(List<Menu> menus, Weekday day, EntityManager entityManager) {
		this.menuListToAddMenu = menus;
		this.day = day;
		this.entityManager = entityManager;
	}

	private Menu currentMenu;

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
		transaction.begin();
	}
	
	public void persist(Object entity) {
		entityManager.persist(entity);
	}
	
	public void commit() {
		menuListToAddMenu.add(currentMenu);
		entityManager.persist(currentMenu);
		currentMenu.setDay(day);
		commitTransaction();
		clear();
	}

	public void commitTransaction() {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.commit();
	}

	public void rollback() {
		entityManager.getTransaction().rollback();
		clear();
	}

	private void clear() {
		currentMenu = null;
	}
}
