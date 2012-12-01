package ch.bbv.ui;

import java.math.BigDecimal;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import ch.bbv.bo.Condiment;
import ch.bbv.bo.CondimentPos;
import ch.bbv.bo.Menu;
import ch.bbv.control.MenuController;

import com.sun.javafx.collections.ObservableListWrapper;

public class MenuPane extends GridPane{
	 private final MenuController menuController = new MenuController();
	private TextField nameField;
	
	public MenuPane() {
		Menu menu = menuController.createMenu();
		
		final Label nameLabel = new Label("Name");
		nameField = new TextField();
		nameField.setText(menu.getName());
		addRow(0, nameLabel, nameField);
		
		ObservableList<CondimentPos> data = new ObservableListWrapper<CondimentPos>(
				menu.getCondiments());
		TableView<CondimentPos> table = new TableView<CondimentPos>(data);
		TableColumn<CondimentPos, BigDecimal> column1 = new TableColumn<CondimentPos, BigDecimal>("Menge");
		TableColumn<CondimentPos, String> column2 = new TableColumn<CondimentPos, String>("Einheit");
		TableColumn<CondimentPos, Condiment> column3 = new TableColumn<CondimentPos, Condiment>("Zutat");
		column1.setCellValueFactory(new Callback<CellDataFeatures<CondimentPos, BigDecimal>, ObservableValue<BigDecimal>>() {
			public ObservableValue<BigDecimal> call(
					CellDataFeatures<CondimentPos, BigDecimal> p) {
				// p.getValue() returns the Person instance for a particular
				// TableView row
				return new ReadOnlyObjectWrapper<BigDecimal>(p.getValue()
						.getAmount());
			}
		});
		column2.setCellValueFactory(new Callback<CellDataFeatures<CondimentPos, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<CondimentPos, String> p) {
				// p.getValue() returns the Person instance for a particular
				// TableView row
				return new ReadOnlyObjectWrapper<String>(p.getValue()
						.getUnit());
			}
		});
		column3.setCellValueFactory(new Callback<CellDataFeatures<CondimentPos, Condiment>, ObservableValue<Condiment>>() {
			public ObservableValue<Condiment> call(
					CellDataFeatures<CondimentPos, Condiment> p) {
				// p.getValue() returns the Person instance for a particular
				// TableView row
				return new ReadOnlyObjectWrapper<Condiment>(p.getValue()
						.getCondiment());
			}
		});
		table.getColumns().add(column1);
		table.getColumns().add(column2);
		table.getColumns().add(column3);
		addRow(1, table);
		addRow(2, createFooter(data));
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
				pos.setCondiment(new Condiment(condimentField.getText()));
				data.add(pos);
			}
		});
		hBox.getChildren().addAll(amountField, unitField, condimentField, addButton);
		return hBox;
	}
}
