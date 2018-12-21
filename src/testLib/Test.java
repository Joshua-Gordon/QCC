package testLib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import org.omg.Messaging.SyncScopeHelper;

import appUIFX.LatexNode;
import framework2FX.Project;
import framework2FX.gateModels.DefaultModel.DefaultModelType;
import framework2FX.gateModels.GateModelFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class Test extends Application {
	
	
	public static void main (String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Project p = Project.createNewTemplateProject();
		p.getCustomGates().put(GateModelFactory.makeGateModel("Joe", "Joe", "", DefaultModelType.UNIVERSAL, "[1, 2; 3, 4]"));
		System.out.println(p.getCustomGates().size());
		File f = new File("/home/quantumresearch/Desktop/joe.qcc");
		writeProject(p, f);
		Project p2 = loadProject(f.toURI());
		System.out.println(p2.getCustomGates().size());
	}
	
	public static Project loadProject(URI location) {
		File file = new File(location);
		
		Project project = null;
		
		if(!file.exists() || file.isDirectory())
			return null;
		
		try(
			FileInputStream inStream = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(inStream);
		){
			
			project =  (Project) ois.readObject();
			project.setReceiver(null);
			project.setProjectFileLocation(file.toURI());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return project;
	}
	
	public static boolean writeProject(Project project, File file) {
		try (
			FileOutputStream outStream = new FileOutputStream(file, false);
			ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
		){
			objOutStream.writeObject(project);
			
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public static void test1(Stage primaryStage) {
		Region r1 = new Region();
		r1.setPrefSize(100, 100);
		r1.setStyle("-fx-background-color: #FF0000;");
		
		Region r2 = new Region();
		r2.setPrefSize(100, 100);
		r2.setStyle("-fx-background-color: #FF0000;");
		
//		LatexView lv = new LatexView("$$" +  " {  1  \\over  { \\sqrt{  2  } }  }"  + "$$", 2f, "#00000000", "#000000");
		LatexNode lv = new LatexNode("the \\(e ^ i \\)", 2f, "#00000000", "#000000");
		
		BorderPane h = new BorderPane(lv, null, r1, null, r2);
		
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
//				lv.setFontSize(2);
				lv.setLatex("\\(e ^ i  e ^ i  e ^ i\\)");
//				lv.setColor("#0000FF");
//				lv.setTextColor("#FF0000");
			});
		});
		
		t.start();
	}
	
	
	
}
