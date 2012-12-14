package ch.bbv.ui;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.util.Duration;

public class MenuTicker extends Group{
	
	private final Scene scene;
	private TranslateTransition ticker;
	private final Text news;
	
	public MenuTicker(Scene scene) {
		this.scene = scene;
		news = TextBuilder.create().translateY(18).fill(Color.WHITE).build();
		initTicker();
	}

	private void initTicker() {
		final Rectangle tickerRect = RectangleBuilder.create().arcWidth(15).arcHeight(20).fill(new Color(0, 0, 0, .55)).x(0).y(0).width(scene.getWidth() - 6).height(30)
				.stroke(Color.rgb(255, 255, 255, .70)).build();
		Rectangle clipRegion = RectangleBuilder.create().arcWidth(15).arcHeight(20).x(0).y(0).width(scene.getWidth() - 6).height(30).stroke(Color.rgb(255, 255, 255, .70)).build();
		setClip(clipRegion);
		// Resize the ticker area when the window is resized
		setTranslateX(6);
		translateYProperty().bind(scene.heightProperty().subtract(tickerRect.getHeight() + 6));
		tickerRect.widthProperty().bind(scene.widthProperty().subtract(16));
		clipRegion.widthProperty().bind(scene.widthProperty().subtract(16));
		getChildren().add(tickerRect);
		getChildren().add(news);
		ticker = TranslateTransitionBuilder.create()
				.node(news)
				.duration(Duration.millis((scene.getWidth() / 300) * 5000))
				.fromX(scene.widthProperty().doubleValue())
				.toX(-scene.widthProperty().doubleValue()).fromY(19)
				.interpolator(Interpolator.LINEAR).cycleCount(1)
				.build();
		// when ticker has finished reset and replay ticker animation
		ticker.setOnFinished(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent ae) {
				ticker.stop();
				ticker.setFromX(scene.getWidth());
				ticker.setDuration(new Duration((scene.getWidth() / 300) * 5000));
				ticker.playFromStart();
			}
		});
	}
	
	public void refresh(String text) {
		ticker.stop();
		news.setText(text);
		ticker.play();
	}
}
