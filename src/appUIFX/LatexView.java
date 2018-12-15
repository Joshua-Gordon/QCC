package appUIFX;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.AppStatus;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class LatexView extends AppFXMLComponent implements Initializable{
	
	
	public static Node mkView(String latex, float fontSize, String color, String textColor) {
		return new LatexView(latex, fontSize, color, textColor).loadAsNode(AppStatus.get().getPrimaryStage());
	}
	
	public AnchorPane root;
	public WebView webView;
	private WebEngine engine;
	private String latex;
	private float fontSize;
	private String color;
	private String textColor;
	
	public LatexView(String latex, float fontSize, String color, String textColor) {
		super("LatexView.fxml");
		this.latex = latex;
		this.fontSize = fontSize;
		this.color = color;
		this.textColor = textColor;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String html = getHtml(latex, fontSize, color);
		
		webView.setContextMenuEnabled(false);
		
		engine = webView.getEngine();
		engine.loadContent(html);
		engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED)
                addFunctionHandlerToDocument();
        });
	}
	
	private void addFunctionHandlerToDocument() {
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("app", this);
    }
	
	public void setSize(int width, int height) {
		webView.setMinSize(width, height);
		webView.setPrefSize(width, height);
		webView.setMaxSize(width, height);
	}
	
	
	private String getHtml(String latex, float fontSize, String color) {
		String html = null;
		try {
			html = utils.ResourceLoader.getHTMLString("LatexDisplay.html");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		html = setArg(html, "LATEX_HERE", latex);
		html = setArg(html, "FONT_SIZE", Float.toString(fontSize));
		html = setArg(html, "BACK_COLOR", color);
		html = setArg(html, "TEXT_COLOR", textColor);
		return html;
	}
	
	private String setArg(String html, String arg, String param) {
		String[] parts = html.split(arg);
		return parts[0] + param + parts[1];
	}
	
	public void setLatex(String latex) {
		latex = latex.replace("\\", "\\\\");
		engine.executeScript("window.setLatex(\"" + latex + "\")");
	}
	
	public void setFontSize(float fontSize) {
		engine.executeScript("window.setFontSize(\"" + fontSize + "em\")");
	}
	
	public void setColor(String color) {
		engine.executeScript("window.setColor(\"" + color + "\")");
	}
	
	public void setTextColor(String color) {
		engine.executeScript("window.setTextColor(\"" + color + "\")");
	}
	
}
