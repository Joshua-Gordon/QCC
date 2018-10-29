package appUIFX;

import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.CircuitBoard;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;

public class CircuitBoardView extends AppView implements Initializable{
	
	public static final int REFRESH_CANVAS_UNIT = 200;
	
	public Canvas display;
	public ScrollPane container;
	
	private int focusX = 0, focusY = 0;
	private double scale = 1;
	private CircuitBoard circuitBoard;
	
	public CircuitBoardView(String circuitBoardName, CircuitBoard board) {
		super("CircuitBoard.fxml", circuitBoardName, Layout.CENTER);
	}
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		
	}

	private void redraw() {
		CircuitBoardRenderContext.drawCircuitBoard(display.getGraphicsContext2D(), circuitBoard);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		container.widthProperty().addListener((event, prevValue, newValue) -> {
			int resizeUnit = (newValue.intValue() - (int) display.getWidth()) / REFRESH_CANVAS_UNIT;
			if(resizeUnit != 0)
				display.setWidth(display.getWidth() + resizeUnit * REFRESH_CANVAS_UNIT);
		});
		container.heightProperty().addListener((event, prevValue, newValue) -> {
			int resizeUnit = (newValue.intValue() - (int) display.getHeight()) / REFRESH_CANVAS_UNIT;
			if(resizeUnit != 0)
				display.setHeight(display.getHeight() + resizeUnit * REFRESH_CANVAS_UNIT);
		});
	}

}
