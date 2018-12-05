package appUIFX;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

public class LatexView extends AppFXMLComponent implements Initializable {
	
	WebView webView;
	private String latex;
	
	public LatexView(String latex) {
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
	}

	
	
}
