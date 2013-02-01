package ch.bbv.control.action;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionModel;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;


public class RemoveAction<T> implements EventHandler<ActionEvent> {
	private final List<T> items;
	private final EntityManager em;
	private final SelectionModel<T> model;
	private final boolean doCommit;
	private final boolean delete;

	public RemoveAction(List<T> items, SelectionModel<T> model, EntityManager em, boolean doCommit, boolean delete) {
		this.items = items;
		this.model = model;
		this.em = em;
		this.delete = delete;
		this.doCommit = doCommit;
	}

	@Override
	public void handle(ActionEvent event) {
		T item = model.getSelectedItem();
		items.remove(item);
		item = em.merge(item);
		if(delete) {
			em.remove(item);
		}
		if(doCommit) {
			EntityTransaction transaction = em.getTransaction();
			transaction.begin();
			transaction.commit();
		}
	}

}
