package ch.bbv.ui;

import java.util.List;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import ch.bbv.bo.Menu;
import ch.bbv.bo.Week;
import ch.bbv.bo.Weekday;
import ch.bbv.control.MenuController;
import ch.bbv.control.WeekNavigator;

import com.google.common.collect.Lists;
import com.sun.javafx.collections.ObservableListWrapper;

//TODO
// - init week correct
// - refactor:
// -- clean up ui
// - Auto rezeise?
// - Context Menu for new Menus
//

public class MenuPlanner extends Application {

	private WeekNavigator weekNavigator;
	private List<TableView<Menu>> tables = Lists.newArrayList();
	private List<Node> tableFooters = Lists.newArrayList();

	private Stage mainStage;
	private EntityManager em;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initHibernate();
		weekNavigator = new WeekNavigator(em);
		primaryStage.setTitle("Menu Planner");
		Group group = new Group();
		Scene scene = new Scene(group);
		primaryStage.setWidth(700);
		primaryStage.setHeight(500);

		final Label label = new Label("Menu Plan Week 1");
		label.setFont(new Font("Arial", 20));

		final GridPane pane = new GridPane();
		fillTable(pane);

		Node box = createButtonBox(label);

		InvalidationListener listener = new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				refresh(label, pane);
			}

		};
		weekNavigator.addListener(listener);
		VBox vBox = new VBox();
		vBox.getChildren().addAll(box, pane);

		group.getChildren().addAll(vBox);

		primaryStage.setScene(scene);
		primaryStage.show();
		mainStage = primaryStage;
		refresh(label, pane);
	}

	private void initHibernate() {
		em = Persistence.createEntityManagerFactory("HibernateApp").createEntityManager();
	}

	private void refresh(final Label label, final GridPane pane) {
		label.setText(weekNavigator.getCurrentWeek().toString());
		fillTable(pane);
	}

	private Node createButtonBox(final Label label) {
		Button nextBtn = new Button();
		nextBtn.setText("Next");
		nextBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				weekNavigator.next();
			}
		});
		Button prevBtn = new Button();
		prevBtn.setText("Previous");
		prevBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				weekNavigator.previous();
			}
		});

		HBox box = new HBox();
		box.getChildren().add(prevBtn);
		box.getChildren().add(label);
		box.getChildren().add(nextBtn);
		return box;
	}

	private Node createTableFooter(final Weekday day, final ObservableList<Menu> data) {
		HBox box = new HBox();
		Button createMenuBtn = new Button("Add Menu");
		createMenuBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showDialog(null, day, data);
			}
		});
		box.getChildren().add(createMenuBtn);
		return box;
	}

	private void showDialog(Menu currentMenu, Weekday dayToAddMenu, List<Menu> menus) {
		MenuController menuController = new MenuController(menus, dayToAddMenu, em);
		menuController.setCurrentMenu(currentMenu);
		MenuPane dialog = new MenuPane(menuController);
		dialog.initOwner(mainStage);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.sizeToScene();
		dialog.show();
	}

	private void fillTable(GridPane pane) {
		pane.getChildren().removeAll(tables);
		pane.getChildren().removeAll(tableFooters);
		tableFooters.clear();
		tables.clear();
		Week week = weekNavigator.getCurrentWeek();
		int i = 0;
		for (Weekday day : week.getDays()) {
			ObservableList<Menu> data = new ObservableListWrapper<Menu>(day.getMenus());
			TableView<Menu> table = new TableView<Menu>(data);
			TableColumn<Menu, String> column = new TableColumn<Menu, String>(day.getName());
			column.setCellValueFactory(new Callback<CellDataFeatures<Menu, String>, ObservableValue<String>>() {
				public ObservableValue<String> call(CellDataFeatures<Menu, String> p) {
					// p.getValue() returns the Person instance for a particular
					// TableView row
					return new ReadOnlyObjectWrapper<String>(p.getValue().getName());
				}
			});

			table.getColumns().add(column);
			tables.add(table);
			table.setEditable(true);
			addContextMenu(table, day, data);
			column.setPrefWidth(90);
			table.setPrefSize(92, 200);
			Node tableFooter = createTableFooter(day, data);
			tableFooters.add(tableFooter);
			pane.addColumn(i++, table, tableFooter);
		}
	}
	
	private void addContextMenu(final TableView<Menu> table, final Weekday dayofEditedMenu, final List<Menu> menus) {
		final ContextMenu cm = new ContextMenu();
		MenuItem cmItem1 = new MenuItem("Edit");
		cmItem1.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	showDialog(table.getSelectionModel().getSelectedItem(), dayofEditedMenu, menus);
		    }
		});

		cm.getItems().add(cmItem1);
		table.addEventHandler(MouseEvent.MOUSE_CLICKED,
		    new EventHandler<MouseEvent>() {
		        @Override public void handle(MouseEvent e) {
		            if (e.getButton() == MouseButton.SECONDARY)  
		                cm.show(mainStage, e.getScreenX(), e.getScreenY());
		        }
		});
	}

}
