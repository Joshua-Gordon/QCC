package testLib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.application.Application;
import javafx.stage.Stage;

public class Test extends Application {
	
	
	public static void main (String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		URL l = Test.class.getResource("/fonts/VastShadow-Regular.ttf");
		
		File f = null;
		try {
			f = new File(l.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		try {
			RandomAccessFile rfile = new RandomAccessFile(f, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("success");
		
		

		File f2 = new File("C:\\Users\\Massimiliano Cutugno\\git\\quantumcircuit\\bin\\fonts\\mplus-2m-bold.ttf");
		System.out.println(f2.exists());
		
		System.out.println(l.getPath());
		
		try {
			RandomAccessFile rfile = new RandomAccessFile(f2, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
