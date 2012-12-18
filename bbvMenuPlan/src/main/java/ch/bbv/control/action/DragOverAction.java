package ch.bbv.control.action;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragOverAction implements EventHandler<DragEvent> {

	public void handle(DragEvent event) {
		Dragboard db = event.getDragboard();
		boolean hasContent = db.hasString();
		boolean same = event.getTarget().equals(event.getGestureSource());
		if (!same && hasContent) {
	        event.acceptTransferModes(TransferMode.MOVE);
	    }
	    event.consume();
	}
}