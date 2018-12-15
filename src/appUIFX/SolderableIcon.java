package appUIFX;

import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.AppStatus;
import framework2FX.solderedGates.Solderable;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import utils.customCollections.ImmutableArray;

public class SolderableIcon extends AppFXMLComponent implements Initializable {
	private static final int SINGLE_LINE_THIC = 1;
	private static final int MULTI_LINE_THIC = 2;
	private static final int LINE_WIDTH = 10;
	
	public AnchorPane icon;
	public HBox gate;
	public Label symbol;
	private Solderable s;
	
	public static Node mkIcon(Solderable s) {
		return new SolderableIcon(s).loadAsNode(AppStatus.get().getPrimaryStage());
	}
	
	private SolderableIcon(Solderable s) {
		super("SolderableIcon.fxml");
		this.s = s;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		symbol.setText(s.getSymbol());
		
		ImmutableArray<String> paramLatex = s.getArguments();
		
		if(!paramLatex.isEmpty()) {
			String paramString = "\\( ( " + paramLatex.get(0);
			
			for(int i = 1; i < paramLatex.size(); i++)
				paramString += " , " + paramLatex.get(i);
			
			paramString += " ) \\)";
			
			gate.getChildren().add(LatexView.mkView(paramString, 2.1f, "#FFFFFF", "#000000"));
		}
		
		
//		int middleHeight = (int) (icon.getHeight() / 2);
//		int width = (int) icon.getWidth();
//		
//		Line l1 = new Line(0, middleHeight, LINE_WIDTH, middleHeight);
//		Line l2 = new Line(width, middleHeight, width - LINE_WIDTH, middleHeight);
//		
//		if(s.getNumberOfRegisters() > 1) {
//			l1.setStrokeWidth(MULTI_LINE_THIC);
//			l2.setStrokeWidth(MULTI_LINE_THIC);
//		} else {
//			l1.setStrokeWidth(SINGLE_LINE_THIC);
//			l2.setStrokeWidth(SINGLE_LINE_THIC);
//		}
//		
//		icon.getChildren().add(l1);
//		icon.getChildren().add(l2);
	}
}