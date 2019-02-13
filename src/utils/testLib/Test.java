package testLib;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Test extends Application {
	
	
	public static void main (String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane bp = new BorderPane();
		ObservableList<Node> nums = bp.getChildren();
		Button b  = new Button();
		nums.add(b);
//		nums.add(b);
		System.out.println(nums.size());
		
		
//		primaryStage.setScene(new Scene((Parent) getView(), 300, 300));
//		primaryStage.show();
	}
	
	
	public Node getView() {
		GridPane gp = new GridPane();
		
		Node first = getOther(40);
		Node second = getOther(80);
		
		GridPane.setConstraints(first, 0, 0, 1, 1);
		GridPane.setConstraints(second, 0, 1, 1, 1);
		
		gp.getChildren().add(first);
		gp.getChildren().add(second);
		
		AnchorPane other = new AnchorPane(gp);
		return other;
	}
	
	public Node getOther(int size) {
		AnchorPane pane = new AnchorPane();
		pane.setOnMouseClicked((e) -> {
			if(pane.getStyle().equals("-fx-background-color: #FF0000;"))
				pane.setStyle("-fx-border-color: blue ;\n" + 
						"    -fx-border-width: 5 ; \n" + 
						"    -fx-border-style: segments(10, 15, 15, 15)  line-cap round ;");
			else
				pane.setStyle("-fx-background-color: #FF0000;");
			System.out.println(pane.widthProperty());
		});
		pane.setMinSize(size, size);
		pane.setPrefSize(size, size);
		pane.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line l = new Line();
		l.setStartX(0);
		l.endXProperty().bind(pane.widthProperty());
		l.setStartY(0);
		l.endYProperty().bind(pane.heightProperty());
		pane.getChildren().add(l);
		return pane;
	}
	
	
}
