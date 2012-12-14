package ch.bbv.ui;

import java.math.BigDecimal;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ch.bbv.bo.Condiment;
import ch.bbv.bo.CondimentPos;
import ch.bbv.bo.Menu;
import ch.bbv.control.MenuController;

import com.google.common.base.Strings;
import com.sun.javafx.collections.ObservableListWrapper;

public class MenuPane extends Stage {
	private final MenuController menuController;
	private TextField nameField;

	public MenuPane(MenuController menuController) {
		Group group = new Group();
		Scene scene = new Scene(group, 600, 340);
		setScene(scene);
		this.menuController = menuController;
		Menu menu = menuController.getCurrentMenu();
		setTitle("Menu bearbeiten");
		GridPane mainPane = new GridPane();
		mainPane.setHgap(5);
		mainPane.setVgap(5);
		mainPane.setPadding(new Insets(5));

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
		Node buttonBox = createButtonBox();
		mainPane.addRow(3, buttonBox);
		group.getChildren().add(mainPane);
	}

	private Node createHeader(Menu menu) {
		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5));
		gridpane.setHgap(5);
		gridpane.setVgap(5);
		Label nameLbl = new Label("Name");
		nameField = new TextField();
		nameField.textProperty().bindBidirectional(menuController.getCurrentMenu().nameProperty());
		nameField.setPromptText("Name");
		nameField.requestFocus();
		gridpane.add(nameLbl, 0, 0);
		gridpane.add(nameField, 1, 0);
		return gridpane;
	}

	private Node createFooter(final List<CondimentPos> data) {
		HBox hBox = new HBox();
		final TextField amountField = new TextField();
		amountField.setPromptText("Menge");
		final TextField unitField = new TextField();
		unitField.setPromptText("Einheit");

		final ComboBox<Condiment> condimentBox = new ComboBox<Condiment>(FXCollections.observableArrayList(menuController.findAllCondiments()));
		condimentBox.setEditable(true);
		condimentBox.setConverter(new StringConverter<Condiment>() {
			@Override
			public String toString(Condiment condiment) {
				if (condiment == null) {
					return null;
				}
				return condiment.toString();
			}

			@Override
			public Condiment fromString(String string) {
				if (!Strings.isNullOrEmpty(string)) {
					Condiment condiment = new Condiment(string);
					condimentBox.getItems().add(condiment);
					return condiment;
				}
				return null;
			}
		});

		Button addButton = new Button("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CondimentPos pos = new CondimentPos();
				pos.setAmount(new BigDecimal(amountField.getText()));
				pos.setUnit(unitField.getText());
				Condiment condiment = condimentBox.getSelectionModel().getSelectedItem();
				pos.setCondiment(condiment);
				data.add(pos);
				menuController.persist(pos);
				menuController.persist(condiment);
				pos.setMenu(menuController.getCurrentMenu());
			}
		});
		hBox.getChildren().addAll(amountField, unitField, condimentBox, addButton);
		return hBox;
	}

	private Node createButtonBox() {
		Button okButton = new Button("Ok");
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				menuController.beginTransaction();
				menuController.commit();
				close();
			}
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				menuController.beginTransaction();
				menuController.rollback();
				close();
			}
		});

		cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		okButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		TilePane tileButtons = new TilePane(Orientation.HORIZONTAL);
		tileButtons.setHgap(10.0);
		tileButtons.setVgap(8.0);
		tileButtons.setAlignment(Pos.BASELINE_RIGHT);
		tileButtons.getChildren().addAll(cancelButton, okButton);
		return tileButtons;
	}
}
