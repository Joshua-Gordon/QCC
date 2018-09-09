package framework2FX;

import java.util.ArrayList;
import java.util.Optional;

import appUIFX.AppAlerts;
import appUIFX.CircuitBoardView;
import appUIFX.Console;
import appUIFX.MainScene;
import appUIFX.TabView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import utils.Notifier;
import utils.Notifier.ReceivedEvent;

public final class AppStatus {
	
	private static AppStatus status = null;
	
	private final Stage primaryStage;
	private final MainScene mainscene;
	private Project project = null;
	private ArrayList<ReceivedEvent> changeListeners;
	private final Notifier notifierFan;
	private final Notifier notifier;
	private boolean isProjectModifed;
	
	
	/**
	 * Can only be set once throughout the whole application runtime
	 * 
	 * @param primaryStage
	 * @param mainScene
	 */
	public static void setAppStatus(Stage primaryStage, MainScene mainScene) {
		if(status != null)
			return;
		status = new AppStatus(primaryStage, mainScene);
	}
	
	
	public static AppStatus get() {
		return status;
	}
	
	
	
	
	
	
	
	
	
	private AppStatus(Stage primaryStage, MainScene mainScene) {
		this.primaryStage = primaryStage;
		this.mainscene = mainScene;
		this.changeListeners = new ArrayList<>();
		this.isProjectModifed = false;
		this.notifierFan = new Notifier();
		this.notifier = new Notifier(notifierFan);
		this.notifier.setReceivedEvent((source, method, args) -> {
			isProjectModifed = true;
		});
		this.notifierFan.setReceivedEvent((source, method, args) -> {
			for(ReceivedEvent re : changeListeners)
				re.receive(source, method, args);
		});
		
		for(TabView tv : TabView.values())
			addAppChangedListener(tv.getView());
	}
	
	
	
	
	
	
	
	
	public Console getConsole() {
		return (Console) TabView.CONSOLE.getView();
	}
	
	
	
	public void setFocusedProject(Project project) {
		
		if(project == null)
			return;
		
		if(isProjectModifed) {
			Optional<ButtonType> response = AppAlerts.showMessage(primaryStage,
					"The current project is not saved",
					"Do you want to continue without saving?", AlertType.CONFIRMATION);
			if(response.get() != ButtonType.APPLY)
				return;
		}
		
		// set previously focused project unfocused
		if(this.project != null)
			this.project.setReciever(null);
		
		
		notifier.sendChange(this, "setFocusedProject", project);
		this.project = project;
		this.project.setReciever(notifier);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void setProjectSavedFlag() {
		isProjectModifed = false;
	}
	
	
	public void addAppChangedListener(ReceivedEvent changeListener) {
		changeListeners.add(changeListener);
	}
	
	public void removeAppChangedListener(ReceivedEvent changeListener) {
		changeListeners.remove(changeListener);
	}
	
	
	public Project getFocusedProject() {
		return project;
	}
	
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}


}
