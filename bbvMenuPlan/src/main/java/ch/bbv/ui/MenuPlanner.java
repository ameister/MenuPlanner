package ch.bbv.ui;

import java.util.List;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import ch.bbv.bo.Menu;
import ch.bbv.bo.Week;
import ch.bbv.bo.Weekday;
import ch.bbv.control.MenuController;
import ch.bbv.control.WeekNavigator;

import com.google.common.collect.Lists;
import com.sun.javafx.collections.ObservableListWrapper;

//TODO
// - refactor:
// -- clean up ui
// - Auto rezeise?

public class MenuPlanner extends Application {

	private WeekNavigator weekNavigator;
	private List<Node> nodesToRemove = Lists.newArrayList();

	private Stage mainStage;
	private EntityManager em;
	private final GridPane mainPane = new GridPane();
	private MenuTicker menuTicker;

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
		scene.getStylesheets().add("/style.css");
		primaryStage.setWidth(700);
		primaryStage.setHeight(500);
		mainStage = primaryStage;

		final Label label = new Label("Menu Plan Week 1");
		label.setFont(new Font("Arial", 20));

		initMenuListViews();

		Node box = createButtonBox(label);

		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				refresh(label);
			}
		};
		weekNavigator.addListener(listener);
		calibrateMainPane();
		GridPane pane = new GridPane();
		pane.setHgap(5);
		pane.setVgap(5);
		pane.setPadding(new Insets(5));
		Node footer = createFooter();
		pane.addRow(0, box);
		pane.addRow(1, mainPane);
		pane.addRow(2, footer);
		GridPane.setHalignment(footer, HPos.RIGHT);

		primaryStage.setScene(scene);
		primaryStage.show();
		menuTicker = new MenuTicker(scene);
		group.getChildren().addAll(pane, menuTicker);
		refresh(label);
	}
	
	private void calibrateMainPane() {
		mainPane.setHgap(5);
		mainPane.setVgap(5);
	}

	private void initHibernate() {
		em = Persistence.createEntityManagerFactory("HibernateApp").createEntityManager();
	}

	private void refresh(final Label label) {
		label.setText(weekNavigator.getCurrentWeek().toString());
		initMenuListViews();
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
		Button resetBtn = new Button();
		resetBtn.setText("Reset");
		resetBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				weekNavigator.reset();
			}
		});

		HBox box = new HBox();
		box.setPadding(new Insets(5));
		box.setSpacing(5.0);
		box.getChildren().add(prevBtn);
		box.getChildren().add(label);
		box.getChildren().add(nextBtn);
		box.getChildren().add(resetBtn);
		return box;
	}

	private Node createTableFooter(final Weekday day, final ObservableList<Menu> data) {
		Button createMenuBtn = new Button("Add Menu");
		createMenuBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showDialog(null, day, data);
			}
		});
		return createMenuBtn;
	}

	private Node createFooter() {
		Button generateListBtn = new Button("Generiere Einkaufsliste");
		generateListBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				List<Menu> currentWeeksMenus = weekNavigator.getCurrentWeeksMenus();
				ShoppinglistStage stage = new ShoppinglistStage(currentWeeksMenus);
				stage.initOwner(mainStage);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.sizeToScene();
				stage.show();
			}
		});
		return generateListBtn;
	}

	private void showDialog(Menu currentMenu, Weekday dayToAddMenu, List<Menu> menus) {
		MenuController menuController = new MenuController(menus, dayToAddMenu, em);
		menuController.setCurrentMenu(currentMenu);
		MenuStage dialog = new MenuStage(menuController);
		dialog.initOwner(mainStage);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.sizeToScene();
		dialog.show();
	}

	private void initMenuListViews() {
		mainPane.getChildren().removeAll(nodesToRemove);
		nodesToRemove.clear();
		Week week = weekNavigator.getCurrentWeek();
		int i = 0;
		for (Weekday day : week.getDays()) {
			ObservableList<Menu> data = new ObservableListWrapper<Menu>(day.getMenus());
			Label listHeader = new Label(day.getName());
			ListView<Menu> listView = new ListView<Menu>(data);
			Node tableFooter = createTableFooter(day, data);
			nodesToRemove.add(listView);
			nodesToRemove.add(listHeader);
			nodesToRemove.add(tableFooter);
			listView.setEditable(true);
			addContextMenu(listView, day, data);
			listView.setPrefSize(92, 200);
			mainPane.addColumn(i++, listHeader, listView, tableFooter);
			GridPane.setHalignment(tableFooter, HPos.CENTER);
			addDragAndDrop(listView, day);
			addSelectonListener(listView);
			addFocusListener(listView);
			addDoubleClickAction(listView, day);
		}
	}
	
	private void addSelectonListener(ListView<Menu> listView) {
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Menu>() {
			@Override
			public void changed(ObservableValue<? extends Menu> ov, Menu oldSelection, Menu newSelection) {
				if(newSelection != null) {
					menuTicker.refresh(newSelection.getTickerText());
				}
			}
		});
	}
	
	private void addFocusListener(final ListView<Menu> listView) {
		listView.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue.booleanValue()) {
					Menu selectedItem = listView.getSelectionModel().getSelectedItem();
					if(selectedItem != null) {
						menuTicker.refresh(selectedItem.getTickerText());
					}
				}
			}
		});
	}
	
	private void addDoubleClickAction(final ListView<Menu> listView, final Weekday dayToAddMenu) {
		listView.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
			    if (event.getClickCount()>1) {
			        Menu selectedItem = listView.getSelectionModel().getSelectedItem();
					if(selectedItem != null) {
			        	showDialog(selectedItem, dayToAddMenu, listView.getItems());
			        }
			    }
			}
		});
	}

	private void addDragAndDrop(final ListView<Menu> listView, final Weekday day) {
		listView.setOnDragDetected(new EventHandler<MouseEvent>() {
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
		});
		
		listView.setOnDragOver(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		    	Dragboard db = event.getDragboard();
				boolean hasContent = db.hasString();
				boolean same = event.getGestureSource().equals(listView);
				if (!same && hasContent) {
		            event.acceptTransferModes(TransferMode.MOVE);
		        }
		        event.consume();
		    }
		});

		listView.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				boolean success = false;
				@SuppressWarnings("unchecked")
				ListView<Menu> dragSource = ((ListView<Menu>) event.getGestureSource());
				if (!dragSource.equals(listView) && db.hasString()) {
					Long id = Long.parseLong(db.getString());
					List<Menu> menus = dragSource.getItems();
					em.getTransaction().begin();
					Menu menuToMove = null;
					for (Menu menu : menus) {
						if(menu.getId() == id.longValue()) {
							listView.getItems().add(menu);
							menu.setDay(day);
							menuToMove = menu;
							success = true;
						}
					}
					menus.remove(menuToMove);
					em.getTransaction().commit();
				}
				event.setDropCompleted(success);
				event.consume();
			}
		});
	}
	
	private void addContextMenu(final ListView<Menu> table, final Weekday dayofEditedMenu, final List<Menu> menus) {
		final ContextMenu cm = new ContextMenu();
		MenuItem cmItem1 = new MenuItem("Edit");
		cmItem1.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				showDialog(table.getSelectionModel().getSelectedItem(), dayofEditedMenu, menus);
			}
		});
		MenuItem cmItem2 = new MenuItem("Delete");
		cmItem2.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				EntityTransaction transaction = em.getTransaction();
				transaction.begin();
				Menu menu = table.getSelectionModel().getSelectedItem();
				menus.remove(menu);
				menu = em.merge(menu);
				em.remove(menu);
				transaction.commit();
			}
		});

		cm.getItems().add(cmItem1);
		cm.getItems().add(cmItem2);
		table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.SECONDARY)
					cm.show(mainStage, e.getScreenX(), e.getScreenY());
			}
		});
	}
}
