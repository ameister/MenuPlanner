package ch.bbv.ui;

import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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
	private static final DataFormat menuDataFormat = new DataFormat("Object");
	private Menu selectedMenu;

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
		VBox vBox = new VBox();
		vBox.getChildren().addAll(box, mainPane, createFooter());

		primaryStage.setScene(scene);
		primaryStage.show();
		group.getChildren().addAll(vBox, createTicker(scene));
		refresh(label);
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
		box.getChildren().add(prevBtn);
		box.getChildren().add(label);
		box.getChildren().add(nextBtn);
		box.getChildren().add(resetBtn);
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

	private Node createFooter() {
		HBox box = new HBox();
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
		box.getChildren().add(generateListBtn);
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

	// TODO show all Menu
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
			addDragAndDrop(listView, day);
		}
	}

	private void addDragAndDrop(final ListView<Menu> source, final Weekday day) {
		source.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				/* drag was detected, start a drag-and-drop gesture */
				/* allow any transfer mode */
				Dragboard db = source.startDragAndDrop(TransferMode.MOVE);

				/* Put a string on a dragboard */
				ClipboardContent content = new ClipboardContent();
				selectedMenu = source.getSelectionModel().getSelectedItem();
				content.put(menuDataFormat, selectedMenu);
				db.setContent(content);

				event.consume();
			}
		});
		source.setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				

				event.consume();
			}
		});
		
		source.setOnDragOver(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		        /* data is dragged over the target */
		        /* accept it only if it is not dragged from the same node 
		         * and if it has a string data */
		    	Dragboard db = event.getDragboard();
				boolean hasContent = db.hasContent(menuDataFormat);
				boolean same = event.getSource() == source;
				if (!same) {
		            /* allow for both copying and moving, whatever user chooses */
		            event.acceptTransferModes(TransferMode.MOVE);
		            System.out.println("notsame");
		        }
				System.out.println("bla");
		        
		        event.consume();
		    }
		});

		source.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a Menu data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				ListView<Menu> dragSource = ((ListView<Menu>) event.getSource());
				if (!dragSource.equals(source)) {
					source.getItems().add(selectedMenu);
					selectedMenu.setDay(day);
					dragSource.getItems().remove(selectedMenu);
					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
			}
		});

	}

	//TODO own Class
	private Group createTicker(final Scene scene) {
		final Group tickerArea = new Group();
		final Rectangle tickerRect = RectangleBuilder.create().arcWidth(15).arcHeight(20).fill(new Color(0, 0, 0, .55)).x(0).y(0).width(scene.getWidth() - 6).height(30)
				.stroke(Color.rgb(255, 255, 255, .70)).build();
		Rectangle clipRegion = RectangleBuilder.create().arcWidth(15).arcHeight(20).x(0).y(0).width(scene.getWidth() - 6).height(30).stroke(Color.rgb(255, 255, 255, .70)).build();
		tickerArea.setClip(clipRegion);
		// Resize the ticker area when the window is resized
		tickerArea.setTranslateX(6);
		tickerArea.translateYProperty().bind(scene.heightProperty().subtract(tickerRect.getHeight() + 6));
		tickerRect.widthProperty().bind(scene.widthProperty().subtract(16));
		clipRegion.widthProperty().bind(scene.widthProperty().subtract(16));
		tickerArea.getChildren().add(tickerRect);
		// add news text
		Text news = TextBuilder.create().text("JavaFX 2.0 News! | 85 and sunny | :)").translateY(18).fill(Color.WHITE).build();
		tickerArea.getChildren().add(news);
		final TranslateTransition ticker = TranslateTransitionBuilder.create().node(news).duration(Duration.millis((scene.getWidth() / 300) * 15000))
				.fromX(scene.widthProperty().doubleValue()).toX(-scene.widthProperty().doubleValue()).fromY(19).interpolator(Interpolator.LINEAR).cycleCount(1).build();
		// when ticker has finished reset and replay ticker animation
		ticker.setOnFinished(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				ticker.stop();
				ticker.setFromX(scene.getWidth());
				ticker.setDuration(new Duration((scene.getWidth() / 300) * 15000));
				ticker.playFromStart();
			}
		});
		ticker.play();
		return tickerArea;
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
