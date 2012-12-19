package ch.bbv.ui;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ch.bbv.bo.ListPos;
import ch.bbv.bo.Menu;
import ch.bbv.bo.ShoppingList;
import ch.bbv.control.ShoppingListGenerator;

import com.sun.javafx.collections.ObservableListWrapper;

public class ShoppinglistStage extends Stage {
	private final ShoppingListGenerator listGenerator = new ShoppingListGenerator();

	public ShoppinglistStage(List<Menu> menus) {
		setTitle("Einkaufsliste");

		ShoppingList shoppingList = listGenerator.generateList(menus);
		ObservableList<ListPos> data = new ObservableListWrapper<ListPos>(shoppingList.getPos());
		ListView<ListPos> table = new ListView<ListPos>(data);
		table.setPrefSize(310, 200);
		Label listHeader = new Label("Artikel");

		GridPane mainPane = new GridPane();
		mainPane.setHgap(5);
		mainPane.setVgap(5);
		mainPane.setPadding(new Insets(10));
		Node closeButton = createCloseButton();
		GridPane.setHalignment(listHeader, HPos.CENTER);
		GridPane.setHalignment(closeButton, HPos.RIGHT);
		mainPane.addRow(0, listHeader);
		mainPane.addRow(1, table);
		mainPane.addRow(2, closeButton);
		Group group = new Group();
		Scene scene = new Scene(group, 350, 350);
		group.getChildren().add(mainPane);
		setScene(scene);
	}

	private Node createCloseButton() {
		Button okButton = new Button("Close");
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});
		return okButton;
	}
}
