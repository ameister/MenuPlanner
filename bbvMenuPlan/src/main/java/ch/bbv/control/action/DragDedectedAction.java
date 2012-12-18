package ch.bbv.control.action;

import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import ch.bbv.bo.Menu;

public class DragDedectedAction implements EventHandler<MouseEvent> {
	private final ListView<Menu> listView;

	public DragDedectedAction(ListView<Menu> listView) {
		this.listView = listView;
	}

	public void handle(MouseEvent event) {
		Menu selectedMenu = listView.getSelectionModel().getSelectedItem();
		if(selectedMenu != null) {
			Dragboard db = listView.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString("" + selectedMenu.getId());
			db.setContent(content);
		}
		event.consume();
	}
}