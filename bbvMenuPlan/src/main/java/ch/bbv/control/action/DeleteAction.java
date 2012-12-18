package ch.bbv.control.action;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionModel;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;


public class DeleteAction<T> implements EventHandler<ActionEvent> {
	private final List<T> items;
	private final EntityManager em;
	private SelectionModel<T> model;
	private boolean doCommit;

	public DeleteAction(List<T> items, SelectionModel<T> model, EntityManager em, boolean doCommit) {
		this.items = items;
		this.model = model;
		this.em = em;
		this.doCommit = doCommit;
	}

	@Override
	public void handle(ActionEvent event) {
		T item = model.getSelectedItem();
		items.remove(item);
		item = em.merge(item);
		em.remove(item);
		if(doCommit) {
			EntityTransaction transaction = em.getTransaction();
			transaction.begin();
			transaction.commit();
		}
	}

}
