package ch.bbv.ui;

import java.math.BigDecimal;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import ch.bbv.bo.Condiment;
import ch.bbv.bo.CondimentPos;
import ch.bbv.bo.Menu;
import ch.bbv.control.MenuController;

import com.sun.javafx.collections.ObservableListWrapper;

//TODO size and position
// Binding
public class MenuPane extends Stage {
	private final MenuController menuController;
	private TextField nameField;

	public MenuPane(MenuController menuController) {
		this.menuController = menuController;
		Menu menu = menuController.getCurrentMenu();
		menuController.beginTransaction();
		setTitle("Menu bearbeiten");
		GridPane mainPane = new GridPane();

		final Node header = createHeader(menu);

		ObservableList<CondimentPos> data = new ObservableListWrapper<CondimentPos>(menu.getCondiments());
		TableView<CondimentPos> table = new TableView<CondimentPos>(data);
		TableColumn<CondimentPos, BigDecimal> column1 = new TableColumn<CondimentPos, BigDecimal>("Menge");
		TableColumn<CondimentPos, String> column2 = new TableColumn<CondimentPos, String>("Einheit");
		TableColumn<CondimentPos, Condiment> column3 = new TableColumn<CondimentPos, Condiment>("Zutat");
		column1.setCellValueFactory(new Callback<CellDataFeatures<CondimentPos, BigDecimal>, ObservableValue<BigDecimal>>() {
			public ObservableValue<BigDecimal> call(CellDataFeatures<CondimentPos, BigDecimal> p) {
				return new ReadOnlyObjectWrapper<BigDecimal>(p.getValue().getAmount());
			}
		});
		column2.setCellValueFactory(new Callback<CellDataFeatures<CondimentPos, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<CondimentPos, String> p) {
				return new ReadOnlyObjectWrapper<String>(p.getValue().getUnit());
			}
		});
		column3.setCellValueFactory(new Callback<CellDataFeatures<CondimentPos, Condiment>, ObservableValue<Condiment>>() {
			public ObservableValue<Condiment> call(CellDataFeatures<CondimentPos, Condiment> p) {
				return new ReadOnlyObjectWrapper<Condiment>(p.getValue().getCondiment());
			}
		});
		table.setPrefSize(510, 200);
		double prefColumnWidth = table.getPrefWidth() / 3;
		column1.setPrefWidth(prefColumnWidth);
		column2.setPrefWidth(prefColumnWidth);
		column3.setPrefWidth(prefColumnWidth);
		table.getColumns().add(column1);
		table.getColumns().add(column2);
		table.getColumns().add(column3);

		mainPane.addRow(0, header);
		mainPane.addRow(1, table);
		mainPane.addRow(2, createFooter(data));
		mainPane.addRow(3, createButtonBox());
		Group group = new Group();
		Scene scene = new Scene(group, 600, 340);
		group.getChildren().add(mainPane);
		setScene(scene);
	}

	private Node createHeader(Menu menu) {
		HBox box = new HBox();
		final Label nameLabel = new Label("Name");
		nameField = new TextField();
		nameField.setText(menu.getName());
		box.getChildren().addAll(nameLabel, nameField);
		return box;
	}

	private Node createFooter(final List<CondimentPos> data) {
		HBox hBox = new HBox();
		final TextField amountField = new TextField("Menge");
		final TextField unitField = new TextField("Einheit");
		final TextField condimentField = new TextField("Zutat");
		Button addButton = new Button();
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CondimentPos pos = new CondimentPos();
				pos.setAmount(new BigDecimal(amountField.getText()));
				pos.setUnit(unitField.getText());
				Condiment condiment = new Condiment(condimentField.getText());
				pos.setCondiment(condiment);
				data.add(pos);
				menuController.persist(pos);
				menuController.persist(condiment);
				pos.setMenu(menuController.getCurrentMenu());
			}
		});
		hBox.getChildren().addAll(amountField, unitField, condimentField, addButton);
		return hBox;
	}

	private Node createButtonBox() {
		HBox hBox = new HBox();
		Button okButton = new Button("Ok");
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				menuController.getCurrentMenu().setName(nameField.getText());
				menuController.commit();
				close();
			}
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				menuController.rollback();
				close();
			}
		});
		hBox.getChildren().addAll(okButton, cancelButton);
		return hBox;
	}
}
