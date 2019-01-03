package appUIFX;

import java.io.IOException;

import framework2FX.AppStatus;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.ResourceLoaderFX;

public abstract class AppFXMLComponent{
	private final String fxmlFilename;
	
	public AppFXMLComponent(String fxmlFilename) {
		this.fxmlFilename = fxmlFilename;
	}
	
	public Node loadAsNode() {
		Node node = null;
		try {
			FXMLLoader loader = ResourceLoaderFX.loadFXMLLoader(fxmlFilename);
			loader.setController(this);
			node = loader.load();
		} catch(IOException e) {
			try {
				AppStatus as = AppStatus.get();
				if(as != null && as.getPrimaryStage() != null)
					AppAlerts.showJavaExceptionMessage(as.getPrimaryStage(), "Program Crashed", "Could not load graphical interface.", e);
			}finally {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return node;
	}
	
	
	public void loadNewScene(Stage stage, int width, int height) {
		try {
			FXMLLoader loader = ResourceLoaderFX.loadFXMLLoader(fxmlFilename);
			loader.setController(this);
			
			Scene scene = new Scene(loader.load(), width, height);
			stage.setScene(scene);
		} catch (Exception e) {
			try {
				AppAlerts.showJavaExceptionMessage(stage, "Program Crashed", "Could not load graphical interface.", e);
			}finally {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
}
