package ch.bbv.control;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import ch.bbv.bo.Condiment;
import ch.bbv.bo.CondimentPos;
import ch.bbv.bo.Menu;
import ch.bbv.bo.Weekday;

public class MenuController {

	private final List<Menu> menuListToAddMenu;
	private final EntityManager entityManager;
	private final Weekday day;
	private Menu currentMenu;

	public MenuController(List<Menu> menus, Weekday day, EntityManager entityManager) {
		this.menuListToAddMenu = menus;
		this.day = day;
		this.entityManager = entityManager;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
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
	
	public CondimentPos crearteCondimentPos() {
		return new CondimentPos();
	}
	
	public void setCurrentMenu(Menu currentMenu) {
		if(currentMenu != null) {
			this.currentMenu = entityManager.merge(currentMenu);
			menuListToAddMenu.remove(currentMenu);
			entityManager.refresh(this.currentMenu);
			menuListToAddMenu.add(this.currentMenu);
		} else {
			this.currentMenu = null;
		}
	}
	
	public List<Condiment> findAllCondiments() {
		TypedQuery<Condiment> query = entityManager.createNamedQuery("Condiment.findAll", Condiment.class);
		return query.getResultList();
	}
	
	public List<Menu> findAllMenus() {
		TypedQuery<Menu> query = entityManager.createNamedQuery("Menu.findAll", Menu.class);
		return query.getResultList();
	}

	public void beginTransaction() {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
	}
	
	public void persist(Object entity) {
		entityManager.persist(entity);
	}
	
	public void commit() {
		if(menuListToAddMenu.contains(currentMenu)){
			menuListToAddMenu.remove(currentMenu);
			currentMenu.addDay(day);
		}
		menuListToAddMenu.add(currentMenu);
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
