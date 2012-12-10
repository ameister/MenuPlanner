package ch.bbv.ui;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import ch.bbv.bo.ListPos;
import ch.bbv.bo.Menu;
import ch.bbv.bo.ShoppingList;
import ch.bbv.control.ShoppingListGenerator;

import com.sun.javafx.collections.ObservableListWrapper;

//TODO size and position
public class ShoppinglistStage extends Stage {
	private final ShoppingListGenerator listGenerator = new ShoppingListGenerator();

	public ShoppinglistStage(List<Menu> menus) {
		setTitle("Einkaufsliste");
		GridPane mainPane = new GridPane();

		ShoppingList shoppingList = listGenerator.generateList(menus);
		ObservableList<ListPos> data = new ObservableListWrapper<ListPos>(shoppingList.getPos());
		TableView<ListPos> table = new TableView<ListPos>(data);
		TableColumn<ListPos, String> column = new TableColumn<ListPos, String>("Artikel");
		column.setCellValueFactory(new Callback<CellDataFeatures<ListPos, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<ListPos, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getName());
			}
		});
		table.setPrefSize(310, 200);
		double prefColumnWidth = table.getPrefWidth();
		column.setPrefWidth(prefColumnWidth);
		table.getColumns().add(column);

		mainPane.addRow(0, table);
		mainPane.addRow(1, createButtonBox());
		Group group = new Group();
		Scene scene = new Scene(group, 400, 340);
		group.getChildren().add(mainPane);
		setScene(scene);
	}

	private Node createButtonBox() {
		HBox hBox = new HBox();
		Button okButton = new Button("Close");
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});
		hBox.getChildren().add(okButton);
		return hBox;
	}
}
