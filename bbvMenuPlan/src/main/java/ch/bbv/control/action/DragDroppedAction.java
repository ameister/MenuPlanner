package ch.bbv.control.action;

import java.util.List;

import javax.persistence.EntityManager;

import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import ch.bbv.bo.Menu;
import ch.bbv.bo.Weekday;

public class DragDroppedAction implements EventHandler<DragEvent> {
	private final ListView<Menu> listView;
	private final Weekday day;
	private final EntityManager em;

	public DragDroppedAction(ListView<Menu> listView, Weekday day, EntityManager em) {
		this.listView = listView;
		this.day = day;
		this.em = em;
	}

	public void handle(DragEvent event) {
		Dragboard db = event.getDragboard();
		boolean success = false;
		@SuppressWarnings("unchecked")
		ListView<Menu> dragSource = ((ListView<Menu>) event.getGestureSource());
		if (!dragSource.equals(listView) && db.hasString()) {
			Long id = Long.parseLong(db.getString());
			List<Menu> menus = dragSource.getItems();
			Menu menuToRemove = null;
			for (Menu menu : menus) {
				if(menu.getId() == id.longValue()) {
					em.getTransaction().begin();
					listView.getItems().add(menu);
					menu.setDay(day);
					menuToRemove = menu;
					em.getTransaction().commit();
					success = true;
				}
			}
			menus.remove(menuToRemove);
		}
		event.setDropCompleted(success);
		event.consume();
	}
}