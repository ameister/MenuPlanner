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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Callback;
import ch.bbv.bo.Condiment;
import ch.bbv.bo.CondimentPos;
import ch.bbv.bo.Menu;
import ch.bbv.control.CondimentConverter;
import ch.bbv.control.MenuController;
import ch.bbv.control.action.RemoveAction;

import com.sun.javafx.collections.ObservableListWrapper;

public class MenuStage extends Stage {
	private final MenuController menuController;
	private CondimentPos pos;
	private TextField amountField;
	private TextField unitField;
	private ComboBox<Condiment> condimentBox;
	

	public MenuStage(MenuController menuController) {
		Group group = new Group();
		Scene scene = new Scene(group, 600, 340);
		setScene(scene);
		this.menuController = menuController;
		setTitle("Menu bearbeiten");

		Menu menu = menuController.getCurrentMenu();
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
		addContextMenu(table);
		
		GridPane mainPane = new GridPane();
		mainPane.setHgap(5);
		mainPane.setVgap(5);
		mainPane.setPadding(new Insets(5));

		final Node header = createHeader();
		final Node buttonBox = createControlButtons();
		mainPane.addRow(0, header);
		mainPane.addRow(1, table);
		mainPane.addRow(2, createFooter(data));
		mainPane.addRow(3, buttonBox);
		group.getChildren().add(mainPane);
	}
	
	private void addContextMenu(TableView<CondimentPos> tableView) {
		final ContextMenu cm = new ContextMenu();
		MenuItem cmItem = MenuItemBuilder.create()
				.text("Delete")
				.onAction(new RemoveAction<CondimentPos>(tableView.getItems(), tableView.getSelectionModel(), menuController.getEntityManager(), false, true))
				.accelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN))
				.build();

		cm.getItems().add(cmItem);
		cmItem.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
		tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.SECONDARY)
					cm.show(MenuStage.this, e.getScreenX(), e.getScreenY());
			}
		});
	}

	private Node createHeader() {
		Label nameLbl = new Label("Name");
		TextField nameField = new TextField();
		nameField.textProperty().bindBidirectional(menuController.getCurrentMenu().nameProperty());
		nameField.setPromptText("Name");

		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(5));
		gridpane.setHgap(5);
		gridpane.setVgap(5);
		gridpane.add(nameLbl, 0, 0);
		gridpane.add(nameField, 1, 0);
		return gridpane;
	}

	private Node createFooter(final List<CondimentPos> data) {
		HBox hBox = new HBox();
		amountField = new TextField();
		amountField.setPromptText("Menge");
		unitField = new TextField();
		unitField.setPromptText("Einheit");

		condimentBox = new ComboBox<Condiment>(FXCollections.observableArrayList(menuController.findAllCondiments()));
		condimentBox.setEditable(true);
		condimentBox.setConverter(new CondimentConverter(condimentBox.getItems()));
		
		createAndBindPos();

		Button addButton = new Button("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				persistCondimentPos(data);
				unbindPos();
				createAndBindPos();
			}

		});
		condimentBox.setPrefWidth(175);
		hBox.getChildren().addAll(amountField, unitField, condimentBox, addButton);
		return hBox;
	}

	private void persistCondimentPos(final List<CondimentPos> data) {
		Condiment condiment = condimentBox.getSelectionModel().getSelectedItem();
		data.add(pos);
		Menu currentMenu = menuController.getCurrentMenu();
		menuController.persist(currentMenu);
		pos.setMenu(currentMenu);
		menuController.persist(condiment);
		menuController.persist(pos);
	}

	private void createAndBindPos() {
		pos = menuController.crearteCondimentPos();
		pos.condimentProperty().bind(condimentBox.getSelectionModel().selectedItemProperty());
		pos.amountProperty().bindBidirectional(amountField.textProperty());
		pos.unitProperty().bindBidirectional(unitField.textProperty());
	}

	private void unbindPos() {
		if(pos != null) {
			pos.condimentProperty().unbind();
			pos.amountProperty().unbindBidirectional(amountField.textProperty());
			pos.unitProperty().unbindBidirectional(unitField.textProperty());
		}
	}

	private Node createControlButtons() {
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
