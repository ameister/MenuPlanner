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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import ch.bbv.bo.Menu;
import ch.bbv.bo.Week;
import ch.bbv.bo.Weekday;
import ch.bbv.control.WeekNavigator;

import com.google.common.collect.Lists;
import com.sun.javafx.collections.ImmutableObservableList;

//TODO
// - refactor:
// -- clean up ui
// - Auto rezeise?
// - Context Menu for new Menus
//

public class MenuPlanner extends Application {

	private final WeekNavigator weekNavigator = new WeekNavigator();
	private List<TableView<Menu>> tables = Lists.newArrayList();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Menu Planner");
		Group group = new Group();
		Scene scene = new Scene(group);
		primaryStage.setWidth(500);
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
		vBox.getChildren().addAll(box,pane);

		group.getChildren().addAll(vBox);

		primaryStage.setScene(scene);
		primaryStage.show();

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

	private void fillTable(GridPane pane) {
		pane.getChildren().removeAll(tables);
		tables.clear();
		Week week = weekNavigator.getCurrentWeek();
		int i = 0;
		for (Weekday day : week.getDays()) {
			ObservableList<Menu> data = new ImmutableObservableList<Menu>(
					(Menu[]) day.getMenus().toArray(
							new Menu[day.getMenus().size()]));
			TableView<Menu> table = new TableView<Menu>(data);
			TableColumn<Menu, String> column = new TableColumn<Menu, String>(
					day.getName());
			column.setCellValueFactory(new Callback<CellDataFeatures<Menu, String>, ObservableValue<String>>() {
				public ObservableValue<String> call(
						CellDataFeatures<Menu, String> p) {
					// p.getValue() returns the Person instance for a particular
					// TableView row
					return new ReadOnlyObjectWrapper<String>(p.getValue()
							.getName());
				}
			});
			table.getColumns().add(column);
			tables.add(table);
			table.setEditable(true);
			table.setPrefSize(70, 200);
			pane.addColumn(i++, table);
		}
	}

}
