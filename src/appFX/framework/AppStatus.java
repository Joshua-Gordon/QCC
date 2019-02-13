package appFX.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import appFX.appUI.AppAlerts;
import appFX.appUI.AppFileIO;
import appFX.appUI.MainScene;
import appFX.appUI.appViews.ConcreteTabView;
import appFX.appUI.appViews.Console;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.customCollections.eventTracableCollections.Notifier;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;


/**
 * Singleton class;
 * 
 * This instance contains all the states of the current session of this application. <br>
 * For example, a project is set focused here. <br>
 * <p>
 * Access to the console is here as well. <br>
 * 
 * @author Massimiliano Cutugno
 *
 */
public final class AppStatus implements EventHandler<WindowEvent> {
	
	private static AppStatus status = null;
	
	private final Stage primaryStage;
	private final MainScene mainscene;
	private Project project = null;
	private ArrayList<ReceivedEvent> changeListeners;
	private final Notifier notifierFan;
	private final Notifier notifier;
	private boolean isProjectModifed;
	
	
	/**
	 * Can only be set once throughout the whole application runtime <br>
	 * 
	 * Used to initiate the status of this application.
	 * 
	 * @param primaryStage
	 * @param mainScene
	 */
	public static void initiateAppStatus(Stage primaryStage, MainScene mainScene) {
		if(status != null)
			return;
		status = new AppStatus(primaryStage, mainScene);
		primaryStage.setOnCloseRequest(status);
	}
	
	/**
	 * @return the {@link AppStatus} instance of this program
	 */
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
			return false;
		});
		this.notifierFan.setReceivedEvent((source, method, args) -> {
			Iterator<ReceivedEvent> iter = changeListeners.iterator();
			while(iter.hasNext()) 
				if(iter.next().receive(source, method, args))
					iter.remove();
			return false;
		});
		
		for(ConcreteTabView tv : ConcreteTabView.values())
			addAppChangedListener(tv.getView());
		
		addAppChangedListener(mainScene);
	}
	
	
	
	
	
	
	
	/**
	 * @return the console controller of this application
	 */
	public Console getConsole() {
		return (Console) ConcreteTabView.CONSOLE.getView();
	}
	
	
	/**
	 * 
	 * Makes a {@link Project} instance focusable to this application
	 * 
	 * <b>REQUIRES:</b> that the project is not null <br>
	 * <b>ENSURES:</b> the GUI is notified of this change <br>
	 * <b>MODIFIES INSTANCE</b>
	 * @param project
	 */
	public void setFocusedProject(Project project) {
		
		if(project == null)
			return;
		
		if(isProjectModifed) {
			Optional<ButtonType> response = AppAlerts.showMessage(primaryStage,
					"The current project is not saved",
					"Do you want to continue without saving?", AlertType.CONFIRMATION);

			if(response.get() != ButtonType.OK)
				return;
		}
		
		// set previously focused project unfocused
		if(this.project != null)
			this.project.setReceiver(null);
		
		
		notifier.sendChange(this, "setFocusedProject", project);
		this.project = project;
		this.project.setReceiver(notifier);
		setProjectSavedFlag();
		
		if(project.getTopLevelCircuitName() != null)
			AppCommand.doAction(AppCommand.OPEN_GATE, project.getTopLevelCircuitName());
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Sets the current {@link Project} status to saved
	 * <b>MODIFIES INSTANCE</b>
	 */
	public void setProjectSavedFlag() {
		isProjectModifed = false;
	}
	
	
	/**
	 * 
	 * <b>MODIFIES INSTANCE</b>
	 * @param changeListener
	 */
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
	
	public MainScene getMainScene() {
		return mainscene;
	}

	@Override
	public void handle(WindowEvent event) {
		if(isProjectModifed && project != null) {
			Optional<ButtonType> options = AppAlerts.showMessage(primaryStage, "Project is not saved", 
					"The current project is not saved, would you like to save before exiting?", AlertType.CONFIRMATION);

			if(options.get() == ButtonType.OK)
				if(AppFileIO.saveProject(project, primaryStage) == AppFileIO.SUCCESSFUL)
					status.setProjectSavedFlag();
		}
	}


}
