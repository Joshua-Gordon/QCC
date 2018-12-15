package testLib;

import appUIFX.LatexView;
import framework2FX.gateModels.PresetGateType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Test extends Application {
	
	
	public static void main (String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		Region r1 = new Region();
		r1.setPrefSize(100, 100);
		r1.setStyle("-fx-background-color: #FF0000;");
		
		Region r2 = new Region();
		r2.setPrefSize(100, 100);
		r2.setStyle("-fx-background-color: #FF0000;");
		
//		LatexView lv = new LatexView("$$" +  " {  1  \\over  { \\sqrt{  2  } }  }"  + "$$", 2f, "#00000000", "#000000");
		LatexView lv = new LatexView("\\(" +  PresetGateType.HADAMARD.getModel().getLatex().get(0)  + "\\)", 2f, "#00000000", "#000000");
		AnchorPane n = (AnchorPane) lv.loadAsNode(primaryStage);
		
		BorderPane h = new BorderPane(n, null, r1, null, r2);
		
		h.setStyle("-fx-background-color: #00FF00;");
		
		primaryStage.setScene(new Scene((Parent)h, 1000, 500));
		primaryStage.show();
		
		Thread t = new Thread(() -> {
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
//			Platform.runLater(()-> {
//				lv.setFontSize(2.1f);
//			});
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
//			Platform.runLater(()-> {
//				lv.setFontSize(10f);
//				lv.setColor("#0000FF");
//			});
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Platform.runLater(()-> {
				System.out.println(PresetGateType.HADAMARD.getModel().getLatex().get(0));
//				lv.setFontSize(2);
//				lv.setLatex("\\(" + PresetGateType.HADAMARD.getModel().getLatex().get(0) + "\\)");
//				lv.setColor("#0000FF");
//				lv.setTextColor("#FF0000");
//				lv.setSize(100, 100);
			});
		});
		
		t.start();
	}
	
	
	
}
