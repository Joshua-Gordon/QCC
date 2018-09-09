package framework2;

import appUIFX.AppFileIO;

/**
 * All application Commands are listed here.
 * @author quantumresearch
 *
 */
public enum AppCommand {
	OPEN_CUSTOM_GATE_EDITOR,
	OPEN_USER_PREFERENCES,
	
	EXPORT_TO_PNG_IMAGE,
	EXPORT_TO_QUIL,
	EXPORT_TO_QASM,
	EXPORT_TO_QUIPPER,
	
	IMPORT_FROM_QUIL,
	IMPORT_FROM_QASM,
	IMPORT_FROM_QUIPPER,
	
	OPEN_NEW_PROJECT,
	OPEN_PROJECT,
	SAVE_PROJECT_TO_FILESYSTEM,
	SAVE_PROJECT,
	
	ADD_ROW_TO_FOCUSED_CB,
	ADD_COLUMN_TO_FOCUSED_CB,
	REMOVE_ROW_FROM_FOCUSED_CB,
	REMOVE_COLUMN_FROM_FOCUSED_CB,
	
	RUN_QUIL,
	RUN_QASM,
	
	
	;
	
	
	public static void doAction(AppCommand actionCommand) {
		AppStatus status = AppStatus.get();
		
		switch(actionCommand) {
		
		case OPEN_CUSTOM_GATE_EDITOR:
			break;
		case OPEN_USER_PREFERENCES:
			break;
		
		
		case EXPORT_TO_PNG_IMAGE:
			break;
		case EXPORT_TO_QASM:
			break;
		case EXPORT_TO_QUIL:
			break;
		case EXPORT_TO_QUIPPER:
			break;
			
			
			
		case IMPORT_FROM_QASM:
			break;
		case IMPORT_FROM_QUIL:
			break;
		case IMPORT_FROM_QUIPPER:
			break;
			
			
		case OPEN_NEW_PROJECT:
			status.setFocusedProject(Project.createNewTemplateProject());
			break;
		case OPEN_PROJECT:
			Project project = AppFileIO.openProject(status.getPrimaryStage());
			if(project != null)
				status.setFocusedProject(project);
			break;
		case SAVE_PROJECT:
			if(AppFileIO.saveProject(status.getFocusedProject(), status.getPrimaryStage()) == AppFileIO.SUCCESSFUL)
				status.setProjectSavedFlag();
			break;
		case SAVE_PROJECT_TO_FILESYSTEM:
			if(AppFileIO.saveProjectAs(status.getFocusedProject(), status.getPrimaryStage()) == AppFileIO.SUCCESSFUL)
				status.setProjectSavedFlag();
			break;
			
			
			
		case REMOVE_COLUMN_FROM_FOCUSED_CB:
			break;
		case REMOVE_ROW_FROM_FOCUSED_CB:
			break;
		case ADD_COLUMN_TO_FOCUSED_CB:
			break;
		case ADD_ROW_TO_FOCUSED_CB:
			break;
			
			
			
		case RUN_QASM:
			break;
		case RUN_QUIL:
			break;
			
		}
	}
	
	
}
