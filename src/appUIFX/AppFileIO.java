package appUIFX;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import appPreferencesFX.AppPreferences;
import framework2.AppStatus;
import framework2.Project;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import utils.Notifier;


public class AppFileIO implements AppPreferences{
	

	public static final String QUANTUM_PROJECT_EXTENSION = ".qcc";
	public static final ExtensionFilter QUANTUM_CIRCUIT_FILE = new ExtensionFilter("Quantum Circuit Project", "*" + QUANTUM_PROJECT_EXTENSION);
	public static final int QUIT = -1;
	public static final int SUCCESSFUL = 0;
	public static final int ERROR = 1;
	
	public static Project openProject(Stage stage) {
		
		// Set up file chooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Project");
		fileChooser.getExtensionFilters().add(QUANTUM_CIRCUIT_FILE);
		fileChooser.setSelectedExtensionFilter(QUANTUM_CIRCUIT_FILE);
		
		// display file chooser and wait for file selection
		File file = fileChooser.showOpenDialog(stage);
		
		// if user did not choose a file
		if(file == null)
			return null;
		
		// load project
		Project project = loadProject(file.toURI());
		
		// if project couldn't be loaded
		if(project == null)
			AppAlerts.showMessage(stage, "Could not open", "The project could be opened. Please try again.", AlertType.ERROR);
		
		return project;
		
	}
	
	
	
	
	public static Project loadPreviouslyClosedProject() {
		try {
			String location = Strings.PREVIOUS_PROJ_URL.get();
			if(location.equals(""))
				return null;
			URI uri = new URI(location);
			return loadProject(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
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
			project.setReciever(null);
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
	
	
	
	
	public static int saveProject(Project project, Stage stage) {
		if(project.getProjectFileLocation() == null)
			return saveProjectAs(project, stage);
		else
			return writeProject(project, new File(project.getProjectFileLocation()))? 0 : 1;
	}
	
	
	
	public static int saveProjectAs(Project project, Stage stage) {
		
		// Set up file chooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Project As");
		fileChooser.getExtensionFilters().add(QUANTUM_CIRCUIT_FILE);
		fileChooser.setSelectedExtensionFilter(QUANTUM_CIRCUIT_FILE);
		
		// display file chooser and wait for file selection
		File file = fileChooser.showSaveDialog(stage);
		
		// if user did not choose a file
		if(file == null)
			return -1;
		
		// adds appropriate extension if user did not specify
		file  = appendWithExtIfNeeded(file, QUANTUM_PROJECT_EXTENSION);
					
		// finally save the file
		boolean succesful = writeProject(project, file);
		if(succesful)
			project.setProjectFileLocation(file.toURI());
		else
			AppAlerts.showMessage(stage, "Could not save", "The project could not save. Please try again.", AlertType.ERROR);
		
		return succesful? 0 : 1;
	}
	
	
	
	private static File appendWithExtIfNeeded(File file, String ext) {
		String fileName = file.getName();
		
		if(fileName.length() <= ext.length() || !fileName.endsWith(ext))
			return new File(file.getParentFile(), fileName + ext);
		
		return file;
	}
	
	
	
	
	
	public static boolean writeProject(Project project, File file) {
		try (
			FileOutputStream outStream = new FileOutputStream(file, true);
			ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
		){
			objOutStream.writeObject(project);
			
		}catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	
	
	
}
