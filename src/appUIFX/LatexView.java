package appUIFX;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.AppStatus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class LatexView extends AppFXMLComponent implements Initializable, ChangeListener<Node>{
	
	
	public static Node mkView(String latex) {
		return new LatexView(latex).loadAsNode(AppStatus.get().getPrimaryStage());
	}
	
	
	
	
	public WebView webView;
	public BorderPane root;
	private String latex;
	
	private LatexView(String latex) {
		super("LatexView.fxml");
		this.latex = latex;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String html = null;
		try {
			html = utils.ResourceLoader.getHTMLString("LatexDisplay.html");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		webView.getEngine().loadContent(html.replaceAll("LATEX_HERE", latex));
		webView.parentProperty().addListener(this);
	}

	@Override
	public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
		newValue.onMouseClickedProperty().addListener((a, b, c) -> {
			String result1 = webView.getEngine().executeScript(
				    "window.getComputedStyle(document.body, null).getPropertyValue('width')"
			).toString();
			String result2 = webView.getEngine().executeScript(
				    "window.getComputedStyle(document.body, null).getPropertyValue('height')"
			).toString();
			System.out.println(result1 + " " + result2);
		});
		
		
		String result1 = webView.getEngine().executeScript(
			    "window.getComputedStyle(document.body, null).getPropertyValue('width')"
		).toString();
		String result2 = webView.getEngine().executeScript(
			    "window.getComputedStyle(document.body, null).getPropertyValue('height')"
		).toString();
		int length1 = result1.length() - 2;
		int length2 = result2.length() - 2;
		int width = Integer.parseInt(result1.substring(0, length1));
		int height = Integer.parseInt(result2.substring(0, length2));
		webView.setMinSize(width, height);
		webView.setPrefSize(width, height);
	}
	
	
	
}
