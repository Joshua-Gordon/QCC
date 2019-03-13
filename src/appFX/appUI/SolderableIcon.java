package appFX.appUI;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.framework.gateModels.GateModel;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import utils.customCollections.ImmutableArray;

public class SolderableIcon extends AppFXMLComponent implements Initializable {

	
	public HBox gate;
	public Label symbol;
	private GateModel s;
	
	public static Node mkIcon(GateModel s) {
		return new SolderableIcon(s).loadAsNode();
	}
	
	private SolderableIcon(GateModel s) {
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
			
			LatexNode lv = new LatexNode(paramString, 10, "#00000000", "#000000");
			
			gate.getChildren().add(lv);
		}
	}
}