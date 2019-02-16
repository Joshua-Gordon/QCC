package testLib;

import appFX.appUI.LatexNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Test extends Application {
	
	
	public static void main (String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) {

		
		
		primaryStage.setScene(new Scene((Parent) getView(), 300, 300));
		primaryStage.show();
	}
	
	
	public Node getView() {
		BorderPane bp = new BorderPane();
		LatexNode lv = new LatexNode("\\( \\Theta \\)", .7f, "#FF0000", "#00FF00", 
		(ln) ->  {
			System.out.println("Here");
			Thread thread = new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(() -> {
					WritableImage snapshot = ln.snapshot(new SnapshotParameters(), null);
					bp.setCenter(new ImageView(snapshot));
				});
			});
			thread.start();
		});
		bp.setLeft(lv);
		
		return bp;
	}
	

	
	
}
