package appFX.appUI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;


//public class LatexControl extends BorderPane {
//	
//}



public class LatexNode extends AnchorPane {
	
	private String latex;
	private float fontSize;
	private String color;
	private String textColor;
	private HtmlEventHandler eventHandler;
	private WebEngine engine;
	private WebView latexWebView;
	
	public LatexNode (String latex) {
		this(latex, .7f);
	}
	
	public LatexNode (String latex, float fontSize) {
		this(latex, fontSize, "#00000000");
	}
	
	public LatexNode (String latex, float fontSize, String color) {
		this(latex, fontSize, color, "#000000");
	}
	
	public LatexNode (String latex, float fontSize, String color, String textColor) {
		this.eventHandler = new HtmlEventHandler();
		this.latex = latex;
		this.fontSize = fontSize;
		this.color = color;
		this.textColor = textColor;
		LatexView lv = new LatexView();
		getChildren().add(lv.loadAsNode());
		setMinWidth(AnchorPane.USE_PREF_SIZE);
		setMinHeight(AnchorPane.USE_PREF_SIZE);
		setPrefWidth(AnchorPane.USE_COMPUTED_SIZE);
		setPrefHeight(AnchorPane.USE_COMPUTED_SIZE);
		setMaxWidth(AnchorPane.USE_PREF_SIZE);
		setMaxHeight(AnchorPane.USE_PREF_SIZE);
	}
	
	
	public void setLatex(String latex) {
		if(eventHandler.latexLoaded) {
			eventHandler.latexLoaded = false;
			latex = latex.replace("\\", "\\\\");
			engine.executeScript("window.setLatex(\"" + latex + "\")");
		} else {
			this.latex = latex;
		}
	}
	
	public void setFontSize(float fontSize) {
		if(eventHandler.htmlLoaded)
			engine.executeScript("window.setFontSize(\"" + fontSize + "em\")");
		else
			this.fontSize = fontSize;
	}
	
	public void setColor(String color) {
		if(eventHandler.htmlLoaded)
			engine.executeScript("window.setColor(\"" + color + "\")");
		else
			this.color = color;
	}
	
	public void setTextColor(String color) {
		if(eventHandler.htmlLoaded)
			engine.executeScript("window.setTextColor(\"" + color + "\")");
		else
			this.textColor = color;
	}
	
	
	public class LatexView extends AppFXMLComponent implements Initializable {
		
		public WebView webView;
		
		private LatexView() {
			super("LatexView.fxml");
		}
		
		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			String html = getHtml();
			
			webView.setContextMenuEnabled(false);
			latexWebView = webView;
			
			engine = webView.getEngine();
			engine.loadContent(html);
			engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
	            if (newState == Worker.State.SUCCEEDED)
	            	eventHandler.addFunctionHandlerToDocument();
	        });
		}
		
		
		
		
		private String getHtml() {
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
			
			latex = null;
			fontSize = -1;
			color = null;
			textColor = null;
			
			return html;
		}
		
		private String setArg(String html, String arg, String param) {
			String[] parts = html.split(arg);
			return parts[0] + param + parts[1];
		}
	
	
	}

	public class HtmlEventHandler {
		private boolean latexLoaded = false, htmlLoaded = false;
		
		private void addFunctionHandlerToDocument() {
	        JSObject window = (JSObject) engine.executeScript("window");
	        window.setMember("app", this);
	    }
		
		public void setLatexLoaded() {
			latexLoaded = true;
			
			if(latex != null) {
				eventHandler.latexLoaded = false;
				String modified = latex.replace("\\", "\\\\");
				latex = null;
				engine.executeScript("window.setLatex(\"" + modified + "\")");
			}
		}
		
		public void setHtmlLoaded() {
			if(htmlLoaded) return;
			
			htmlLoaded = true;
			
			if (fontSize != -1) {
				engine.executeScript("window.setFontSize(\"" + fontSize + "em\")");
				fontSize = -1;
			}
			if (color != null) {
				engine.executeScript("window.setColor(\"" + color + "\")");
				color = null;
			}
			if (textColor != null) {
				engine.executeScript("window.setTextColor(\"" + textColor + "\")");
				textColor = null;
			}
		}
		
		public void setSize(int width, int height) {
			latexWebView.setMinSize(width, height);
			latexWebView.setPrefSize(width, height);
			latexWebView.setMaxSize(width, height);
		}
	}
}
