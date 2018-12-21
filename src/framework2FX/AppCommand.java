package framework2FX;

import java.util.Optional;

import appUIFX.AppAlerts;
import appUIFX.AppFileIO;
import appUIFX.MainScene;
import appUIFX.appViews.CircuitBoardView;
import appUIFX.appViews.GateEditableView;
import appUIFX.appViews.GateModelView;
import framework2FX.gateModels.CircuitBoard;
import framework2FX.gateModels.DefaultModel;
import framework2FX.gateModels.GateModel;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.PrintStream;
import utils.PrintStream.SystemPrintStream;
import utils.customCollections.CommandParameterList;

/**
 * All application Commands are listed here.
 * @author quantumresearch
 *
 */
public enum AppCommand {
	HELP,
	
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
	
	REMOVE_GATE,
	EDIT_GATE,
	EDIT_AS_NEW_GATE,
	CREATE_GATE,
	CREATE_CIRCUIT_BOARD,
	CREATE_DEFAULT_GATE,
	CREATE_ORACLE_GATE,
	OPEN_GATE,
	SET_AS_TOP_LEVEL,
	REMOVE_TOP_LEVEL,
	LIST_USER_GATES,
	
	ADD_UNTITLED_CIRCUIT_BOARD,
	
	GET_GATE,
	GET_GATE_INSTANCES,
	
	;
	
	
	public static AppCommand getbyName(String command) { 
		for(AppCommand ac : AppCommand.values())
			if(ac.name().equalsIgnoreCase(command))
				return ac;
		return null;
	}
	
	public static Object doAction(AppCommand actionCommand, String ... parameters) {
		return doAction(SystemPrintStream.get(), actionCommand, new CommandParameterList(parameters));
	}
	
	
	public static Object doAction(PrintStream commandResponse, AppCommand actionCommand, CommandParameterList parameters) {
		AppStatus status = AppStatus.get();
		MainScene ms = status.getMainScene();
		Stage primaryStage = status.getPrimaryStage();
		Project currentProject = status.getFocusedProject();
		PrintStream console = status.getConsole();
		
		
		switch(actionCommand) {
		case HELP:
			commandResponse.println("Command List (Commands are not Case Sensitive): \n", Color.BLUE);			
			for(AppCommand command : AppCommand.values())
				commandResponse.println(command.name(), Color.GREEN);
			
			
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
			
			
			
			
			// TODO: For Some Reason Projects dont save correctly... Investigate gate this later
		case OPEN_NEW_PROJECT:
			status.setFocusedProject(Project.createNewTemplateProject());
			break;
		case OPEN_PROJECT:
			Project newProject = AppFileIO.openProject(primaryStage);
			if(newProject != null)
				status.setFocusedProject(newProject);
			break;
		case SAVE_PROJECT:
			if(AppFileIO.saveProject(currentProject, primaryStage) == AppFileIO.SUCCESSFUL)
				status.setProjectSavedFlag();
			break;
		case SAVE_PROJECT_TO_FILESYSTEM:
			if(AppFileIO.saveProjectAs(currentProject, primaryStage) == AppFileIO.SUCCESSFUL)
				status.setProjectSavedFlag();
			break;
			
			
			
		case REMOVE_COLUMN_FROM_FOCUSED_CB:
			String s = ms.getCenterFocusedView();
			if(s == null)
				commandResponse.printErrln("No circuit board is opened and focused");
			
			GateModel gm = currentProject.getGateModel(s);
			
			if(gm == null || !(gm instanceof CircuitBoard))
				commandResponse.printErrln("No circuit board is opened and focused");
			
			CircuitBoard cb = (CircuitBoard) gm;
			
			try {
				cb.removeColumns(cb.getColumns() - 1, cb.getColumns());
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not remove Column", e.getMessage(), AlertType.ERROR);
				e.printStackTrace();
			}
			
			
			break;
		case REMOVE_ROW_FROM_FOCUSED_CB:
			s = ms.getCenterFocusedView();
			if(s == null)
				commandResponse.printErrln("No circuit board is opened and focused");
			
			gm = currentProject.getGateModel(s);
			
			if(gm == null || !(gm instanceof CircuitBoard))
				commandResponse.printErrln("No circuit board is opened and focused");
			
			cb = (CircuitBoard) gm;
			
			try {
				cb.removeRows(cb.getRows() - 1, cb.getRows());
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not remove Row", e.getMessage(), AlertType.ERROR);
				e.printStackTrace();
			}
			
			break;
		case ADD_COLUMN_TO_FOCUSED_CB:
			s = ms.getCenterFocusedView();
			if(s == null)
				commandResponse.printErrln("No circuit board is opened and focused");
			
			gm = currentProject.getGateModel(s);
			
			if(gm == null || !(gm instanceof CircuitBoard))
				commandResponse.printErrln("No circuit board is opened and focused");
			
			cb = (CircuitBoard) gm;
			
			try {
				cb.addColumns(cb.getColumns(), 1);
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not add Column", e.getMessage(), AlertType.ERROR);
				e.printStackTrace();
			}
			
			break;
		case ADD_ROW_TO_FOCUSED_CB:
			s = ms.getCenterFocusedView();
			if(s == null)
				commandResponse.printErrln("No circuit board is opened and focused");
			
			gm = currentProject.getGateModel(s);
			
			if(gm == null || !(gm instanceof CircuitBoard))
				commandResponse.printErrln("No circuit board is opened and focused");
			
			cb = (CircuitBoard) gm;
			
			try {
				cb.addRows(cb.getRows(), 1);
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not add Row", e.getMessage(), AlertType.ERROR);
				e.printStackTrace();
			}
			
			break;
			
			
			
		case RUN_QASM:
			break;
		case RUN_QUIL:
			break;
			
			
		case CREATE_GATE:
			
			for(String param : parameters) {
				String[] parts = param.split("\\.");
				if(parts.length != 2) {
					commandResponse.printErrln("The formal name \"" + param +  "\" is not a valid name. It must have a proper extension.");
					continue;
				}
				if(parts[1].equals(DefaultModel.GATE_MODEL_EXTENSION)) {
					GateEditableView.openGateEditableView(parts[0]);
				}
			}
			break;
		case CREATE_CIRCUIT_BOARD:
			
			
			break;
		case CREATE_DEFAULT_GATE:
			GateEditableView.openGateEditableView();
			break;
		case CREATE_ORACLE_GATE:
			
			break;
		
		case EDIT_AS_NEW_GATE:
			for(String param : parameters) {
				gm = currentProject.getGateModel(param);
				if(gm == null) {
					commandResponse.printErrln("Gate \"" + param +  "\" does not exist");
					continue;
				}
				if(gm instanceof DefaultModel) {
					GateEditableView.openGateEditableView((DefaultModel) gm, true);
				}
			}
			break;
		case EDIT_GATE:
			for(String param : parameters) {
				gm = currentProject.getGateModel(param);
				if(gm == null) {
					commandResponse.println("Gate \"" + param +  "\" does not exist");
					continue;
				} else if (gm instanceof DefaultModel && ((DefaultModel)(gm)).isPreset()) {
					commandResponse.println("Gate \"" + param +  "\" is a preset gate and cannot be edited");
					continue;
				}
				if(gm instanceof DefaultModel) {
					GateEditableView.openGateEditableView((DefaultModel) gm, false);
				}
			}
			break;
		case REMOVE_GATE:
			for(String param : parameters) {
				gm = currentProject.getGateModel(param);
				if(gm == null) {
					commandResponse.printErrln("Gate \"" + param +  "\" does not exist");
					continue;
				} else if (gm instanceof DefaultModel && ((DefaultModel)(gm)).isPreset()) {
					commandResponse.printErrln("Gate \"" + param +  "\" is a preset gate and cannot be removed");
					continue;
				}
				
				Optional<ButtonType> options = AppAlerts.showMessage(primaryStage, "Remove Gate?", "Are you sure you want to remove this gate? "
						+ "All instances of this gate will be removed.", AlertType.CONFIRMATION);
				
				if(options.get() != ButtonType.APPLY)
					return null;
				
				if(gm instanceof DefaultModel) {
					currentProject.getCustomGates().remove(gm.getName());
				}
			}
			break;
		case OPEN_GATE:
			for(String param : parameters) {
				gm = currentProject.getGateModel(param);
				if(gm == null) {
					commandResponse.printErrln("Gate \"" + param +  "\" does not exist");
					continue;
				}
				
				if(gm instanceof DefaultModel) {
					ms.addView(new GateModelView((DefaultModel) gm));
				} else if (gm instanceof CircuitBoard) {
					CircuitBoardView.openCircuitBoard(gm.getFormalName());
				}
			}
			break;
		case SET_AS_TOP_LEVEL:
			gm = currentProject.getGateModel(parameters.get(0));
			if(gm == null) {
				commandResponse.println("Circuit Board \"" + parameters.get(0) + "\" does not exist");
				return null;
			}
			if(gm instanceof CircuitBoard) {
				if(gm.getFormalName().equals(currentProject.getTopLevelCircuitName())) {
					commandResponse.println("Circuit Board \"" + parameters.get(0) + "\" is already top level");
					return null;
				} else {
					currentProject.setTopLevelCircuitName(parameters.get(0));
				}
			} else {
				commandResponse.println("Gate \"" + parameters.get(0) + "\" is not a circuit board");
			}
			break;
		case REMOVE_TOP_LEVEL:
			if(currentProject.getTopLevelCircuitName() == null) {
				commandResponse.println("There is no circuit board set as top level");
				return null;
			}
			currentProject.setTopLevelCircuitName(null);
			break;
			
		case ADD_UNTITLED_CIRCUIT_BOARD:
			CircuitBoardView.openCircuitBoard(currentProject.addUntitledSubCircuit());
			break;
			
		case GET_GATE:
			gm = currentProject.getGateModel(parameters.get(0));
			if(gm == null) {
				commandResponse.printErrln("Gate \"" + parameters.get(0) +  "\" does not exist");
				return null;
			} else {
				commandResponse.println("Gate \"" + parameters.get(0) +  "\" retrieved");
				return gm;
			}
			
		case GET_GATE_INSTANCES:
			gm = currentProject.getGateModel(parameters.get(0));
			
			if(gm == null) {
				commandResponse.printErrln("Gate \"" + parameters.get(0) +  "\" does not exist");
				return null;
			}
			
			if(!(gm instanceof CircuitBoard)) {
				commandResponse.printErrln("Gate \"" + parameters.get(0) +  "\" must be a circuit board");
				return null;
			}
			
			
			if(!currentProject.containsGateModel(parameters.get(1))) {
				commandResponse.printErrln("Gate \"" + parameters.get(1) +  "\" does not exist");
				return null;
			}
			
			int insts = ((CircuitBoard) gm).getOccurrences(parameters.get(1));
			
			commandResponse.println(Integer.toString(insts));
			
			return insts;
			
		case LIST_USER_GATES:
			commandResponse.println("Project Circuit Boards:", Color.BLUE);
			for(String modelName : currentProject.getSubCircuits().getGateNameIterable())
				commandResponse.println(modelName);

			commandResponse.println("\nProject Custom Gates:", Color.BLUE);
			for(String modelName : currentProject.getCustomGates().getGateNameIterable())
				commandResponse.println(modelName);

			commandResponse.println("\nProject Custom Oracles:", Color.BLUE);
			for(String modelName : currentProject.getCustomOracles().getGateNameIterable())
				commandResponse.println(modelName);
			break;
		}
		
		return null;
	}
	
	
}
