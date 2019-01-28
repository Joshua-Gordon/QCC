package appFX.framework;

import java.util.Optional;

import appFX.appUI.AppAlerts;
import appFX.appUI.AppFXMLComponent;
import appFX.appUI.AppFileIO;
import appFX.appUI.AppTab;
import appFX.appUI.CircuitBoardPropertiesView;
import appFX.appUI.GateEditableView;
import appFX.appUI.MainScene;
import appFX.appUI.appViews.BasicModelView;
import appFX.appUI.appViews.CircuitBoardView;
import appFX.framework.gateModels.BasicModel;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.OracleModel;
import appFX.framework.solderedGates.SolderedControl;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import appFX.framework.solderedGates.SpacerPin;
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
	RUN_SIMULATION,
	
	REMOVE_GATE,
	EDIT_GATE,
	EDIT_AS_NEW_GATE,
	CREATE_GATE,
	RENAME_GATE,
	CREATE_CIRCUIT_BOARD,
	CREATE_DEFAULT_GATE,
	CREATE_ORACLE_GATE,
	OPEN_GATE,
	SET_AS_TOP_LEVEL,
	REMOVE_TOP_LEVEL,
	LIST_USER_GATES,
	SHOW_RENDERED_LATEX_FOR_GATE,
	SHOW_LATEX_STRING_FOR_GATE,
	
	ADD_UNTITLED_CIRCUIT_BOARD,
	
	DEBUG_CIRCUIT_BOARD,
	
	GET_GATE,
	GET_GATE_INSTANCES,
	
	;
	
	
	public static AppCommand getbyName(String command) { 
		for(AppCommand ac : AppCommand.values())
			if(ac.name().equalsIgnoreCase(command))
				return ac;
		return null;
	}
	
	public static Object doAction(AppCommand actionCommand, Object ... parameters) {
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
			String code = Translator.exportToQUIL(currentProject);
			console.println(code);
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
			CircuitBoardView  cbv = getFocusedCircuitBoardView(ms, commandResponse);
			
			if(cbv == null)
				return null;
			
			
			CircuitBoardModel cb = cbv.getCircuitBoardModel();
			
			try {
				cb.removeColumns(cb.getColumns() - 1, cb.getColumns());
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not remove Column", e.getMessage(), AlertType.ERROR);
			}
			
			
			break;
		case REMOVE_ROW_FROM_FOCUSED_CB:
			cbv = getFocusedCircuitBoardView(ms, commandResponse);
			
			if(cbv == null)
				return null;
			
			cb = cbv.getCircuitBoardModel();
			
			try {
				cb.removeRows(cb.getRows() - 1, cb.getRows());
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not remove Row", e.getMessage(), AlertType.ERROR);
			}
			
			break;
		case ADD_COLUMN_TO_FOCUSED_CB:
			cbv = getFocusedCircuitBoardView(ms, commandResponse);
			
			if(cbv == null)
				return null;
			
			cb = cbv.getCircuitBoardModel();
			
			try {
				cb.addColumns(cb.getColumns(), 1);
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not add Column", e.getMessage(), AlertType.ERROR);
			}
			
			break;
		case ADD_ROW_TO_FOCUSED_CB:
			cbv = getFocusedCircuitBoardView(ms, commandResponse);
			
			if(cbv == null)
				return null;
			
			cb = cbv.getCircuitBoardModel();
			
			try {
				cb.addRows(cb.getRows(), 1);
			} catch (IllegalArgumentException e) {
				AppAlerts.showMessage(primaryStage, "Could not add Row", e.getMessage(), AlertType.ERROR);
			}
			
			break;
			
			
		
		case RUN_QASM:
			console.println("Running QASM", Color.BLUE);
			break;
		case RUN_QUIL:
			console.println("Running QUIL", Color.BLUE);
			break;
		case RUN_SIMULATION:
			console.println("Running Simulation", Color.BLUE);
			break;
		
		
			
		case RENAME_GATE:
			String oldGate = parameters.getString(0);
			String newGate = parameters.getString(1);
			
			if(oldGate.equals(newGate)) {
				commandResponse.println("The old name and new name are the same. No refactoring took place."); 
				return null;
			}
			
			GateModel gmOld = currentProject.getGateModel(oldGate);
			
			if(!assertExists(oldGate, gmOld, commandResponse))
				return null;
			
			GateModel gmNew = currentProject.getGateModel(newGate);
			
			if(gmNew != null) {
				if(gmNew.isPreset()) {
					commandResponse.printErrln("Gate \"" + newGate +  "\" is a preset gate and cannot be renamed");
					AppAlerts.showMessage(primaryStage, "Cannot rename Gate", 
							"The gate is renamed to preset Gate which cannot be modified", AlertType.ERROR);
					return null;
				}
				
				Optional<ButtonType> options = AppAlerts.showMessage(primaryStage, "Override Gate Model " + gmNew.getName(), 
						"A Gate Model with the name \"" + gmNew.getName() + "\" already exists, "
								+ "do you want to override this gate model?"
								+ " All instances of the previous implementation of \""
								+ gmNew.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(options.get() != ButtonType.OK)
					return null;
			}
			
			String newGateSymbol = gmOld.getSymbol();
			String newGateDescription = gmOld.getDescription();
			
			if(parameters.size() > 2) {
				newGateSymbol = parameters.getString(2);
				if(parameters.size() > 3)
					newGateDescription = parameters.getString(3);
			}

			
			GateModel replacement = gmOld.shallowCopyToNewName(newGate.split("\\.")[0], newGateSymbol, newGateDescription);
			
			if(gmOld instanceof CircuitBoardModel) {
				currentProject.getCircuitBoardModels().replace(oldGate, replacement);
				CircuitBoardView.openCircuitBoard(newGate);
			} else if (gmOld instanceof BasicModel) {
				currentProject.getCustomGates().replace(oldGate, replacement);
				ms.addView(new BasicModelView((BasicModel)replacement));
			} else if (gmOld instanceof OracleModel) {
				currentProject.getCustomOracles().replace(oldGate, replacement);
			} else {
				return null;
			}

			break;
		case CREATE_GATE:
			
			for(String param : parameters.stringIterable()) {
				String[] parts = param.split("\\.");
				
				String name = parts[0];
				String ext = parts[1]; 
				if(parts.length != 2) {
					commandResponse.printErrln("The formal name \"" + param +  "\" is not a valid name. It must have a proper extension.");
					continue;
				}
				if(ext.equals(BasicModel.GATE_MODEL_EXTENSION)) {
					GateEditableView.createNewGate(name);
				} else if (ext.equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
					CircuitBoardPropertiesView.createNewGate(name);
				} else if (ext.equals(OracleModel.ORACLE_MODEL_EXTENSION)) {
					
				}
			}
			break;
		case CREATE_CIRCUIT_BOARD:
			CircuitBoardPropertiesView.createNewGate();
			break;
		case CREATE_DEFAULT_GATE:
			GateEditableView.createNewGate();
			break;
		case CREATE_ORACLE_GATE:
			
			break;
		
		case EDIT_AS_NEW_GATE:
			for(String param : parameters.stringIterable()) {
				GateModel gm = currentProject.getGateModel(param);
				if(!assertExists(param, gm, commandResponse))
					continue;
				
				if(gm instanceof BasicModel) {
					GateEditableView.editAsNewGate(gm.getFormalName());
				} else if (gm instanceof CircuitBoardModel) {
					CircuitBoardPropertiesView.editAsNewGate(gm.getFormalName());
				} else if (gm instanceof OracleModel) {
					
				}
			}
			break;
		case EDIT_GATE:
			for(String param : parameters.stringIterable()) {
				GateModel gm = currentProject.getGateModel(param);
				if(!assertExists(param, gm, commandResponse))
					continue;
				
				if(gm.isPreset()) {
					commandResponse.println("Gate \"" + param +  "\" is a preset gate and cannot be edited");
					continue;
				}
				
				if(gm instanceof BasicModel) {
					GateEditableView.editGate(gm.getFormalName());
				} else if (gm instanceof CircuitBoardModel) {
					CircuitBoardPropertiesView.editGate(gm.getFormalName());
				} else if (gm instanceof OracleModel) {
					
				}
			}
			break;
		case REMOVE_GATE:
			for(String param : parameters.stringIterable()) {
				GateModel gm = currentProject.getGateModel(param);
				
				if(!assertExists(param, gm, commandResponse))
					continue;
				
				if (gm.isPreset()) {
					commandResponse.printErrln("Gate \"" + param +  "\" is a preset gate and cannot be removed");
					continue;
				}

				Optional<ButtonType> options = AppAlerts.showMessage(primaryStage, "Remove Gate?", "Are you sure you want to remove this gate? "
						+ "All instances of this gate will be removed.", AlertType.CONFIRMATION);
				
				if(options.get() != ButtonType.OK)
					return null;
				
				if(gm instanceof BasicModel) {
					currentProject.getCustomGates().remove(gm.getFormalName());
				} else if (gm instanceof CircuitBoardModel) {
					currentProject.getCircuitBoardModels().remove(gm.getFormalName());
				} else if ( gm instanceof OracleModel ) {
					currentProject.getCustomOracles().remove(gm.getFormalName());
				}
			}
			break;
			
		case OPEN_GATE:
			for(String param : parameters.stringIterable()) {
				GateModel gm = currentProject.getGateModel(param);
				if(!assertExists(param, gm, commandResponse))
					continue;
				
				if(gm instanceof BasicModel) {
					ms.addView(new BasicModelView((BasicModel) gm));
				} else if (gm instanceof CircuitBoardModel) {
					CircuitBoardView.openCircuitBoard(gm.getFormalName());
				}
			}
			break;
			
		case SET_AS_TOP_LEVEL:
			GateModel gm = currentProject.getGateModel(parameters.getString(0));
			
			if(!assertExists(parameters.getString(0), gm, commandResponse))
				return null;
			
			if(gm instanceof CircuitBoardModel) {
				if(gm.getFormalName().equals(currentProject.getTopLevelCircuitName())) {
					commandResponse.println("Circuit Board \"" + parameters.get(0) + "\" is already top level");
					return null;
				} else {
					currentProject.setTopLevelCircuitName(parameters.getString(0));
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
			gm = currentProject.getGateModel(parameters.getString(0));
			if(!assertExists(parameters.getString(0), gm, commandResponse))
				return null;
			return gm;
			
		case GET_GATE_INSTANCES:
			gm = currentProject.getGateModel(parameters.getString(0));
			
			if(!assertExists(parameters.getString(0), gm, commandResponse))
				return null;
			
			if(!(gm instanceof CircuitBoardModel)) {
				commandResponse.printErrln("Gate \"" + parameters.get(0) +  "\" must be a circuit board");
				return null;
			}
			
			
			if(!currentProject.containsGateModel(parameters.getString(1))) {
				commandResponse.printErrln("Gate \"" + parameters.get(1) +  "\" does not exist");
				return null;
			}
			
			int insts = ((CircuitBoardModel) gm).getOccurrences(parameters.getString(1));
			
			commandResponse.println(Integer.toString(insts));
			
			return insts;
			
		case LIST_USER_GATES:
			commandResponse.println("Project Circuit Boards:", Color.BLUE);
			for(String modelName : currentProject.getCircuitBoardModels().getGateNameIterable())
				commandResponse.println(modelName);

			commandResponse.println("\nProject Custom Gates:", Color.BLUE);
			for(String modelName : currentProject.getCustomGates().getGateNameIterable())
				commandResponse.println(modelName);

			commandResponse.println("\nProject Custom Oracles:", Color.BLUE);
			for(String modelName : currentProject.getCustomOracles().getGateNameIterable())
				commandResponse.println(modelName);
			break;
			
		case SHOW_RENDERED_LATEX_FOR_GATE:
			gm = currentProject.getGateModel(parameters.getString(0));
			
			if(!assertExists(parameters.getString(0), gm, commandResponse))
				return null;
			
			if(!(gm instanceof BasicModel)) {
				commandResponse.printErrln("Gate \"" + parameters.getString(0) +  "\" must be a basic gate");
				return null;
			}
			
			BasicModel bg = (BasicModel) gm;
			
			if(parameters.size() > 1) {
				for (int i = 1; i < parameters.size(); i++) {Object value = parameters.get(i);
					int index = -1;
					if(value instanceof String) {
						try {
							index = Integer.parseInt((String) value);
						} catch (NumberFormatException nfe) {
							commandResponse.printErrln("Parameter " + i + " is not a integer");
							continue;
						}
					} else {
						index = (int) parameters.get(i);
					}
					if(index < 0 || index >= bg.getLatex().size()) {
						commandResponse.printErrln("Parameter " + i + " is not a sufficent index.");
						continue;
					}
					
					
					commandResponse.printLatexln("$$" + bg.getLatex().get(index) + "$$");
				}
			} else {
				for(int i = 0; i < bg.getLatex().size(); i++)
					commandResponse.printLatexln("$$" + bg.getLatex().get(i) + "$$");
			}
			break;
			
		case SHOW_LATEX_STRING_FOR_GATE:
			gm = currentProject.getGateModel(parameters.getString(0));
			
			if(!assertExists(parameters.getString(0), gm, commandResponse))
				return null;
			
			if(!(gm instanceof BasicModel)) {
				commandResponse.printErrln("Gate \"" + parameters.getString(0) +  "\" must be a basic gate");
				return null;
			}
			
			bg = (BasicModel) gm;
			
			if(parameters.size() > 1) {
				for (int i = 1; i < parameters.size(); i++) {
					Object value = parameters.get(i);
					int index = -1;
					if(value instanceof String) {
						try {
							index = Integer.parseInt((String) value);
						} catch (NumberFormatException nfe) {
							commandResponse.printErrln("Parameter " + i + " is not a integer");
							continue;
						}
					} else {
						index = (int) parameters.get(i);
					}
					if(index < 0 || index >= bg.getLatex().size()) {
						commandResponse.printErrln("Parameter " + i + " is not a sufficent index.");
						continue;
					}
					
					commandResponse.println(bg.getLatex().get(index));
				}
			} else {
				for(int i = 0; i < bg.getLatex().size(); i++)
					commandResponse.println(bg.getLatex().get(i));
			}
			break;
			
		case DEBUG_CIRCUIT_BOARD:
			CircuitBoardModel model = (CircuitBoardModel) currentProject.getCircuitBoardModels().get(parameters.getString(0));
			if(!assertExists(parameters.getString(0), model, commandResponse))
				return null;
				
			int solderID = -1;
			SolderedGate sg = null;
			
			String[][] debugS = new String[model.getColumns()][model.getRows()];
			
			int[] largestSpaces = new int[model.getColumns()];
			
			for(int c = 0; c < model.getColumns(); c++) {
				for(int r = 0; r < model.getRows(); r++) {
					SolderedPin sp = model.getSolderPinAt(r, c);
					String type = "";
					if(sp instanceof SolderedRegister) {
						type = Integer.toString(((SolderedRegister)sp).getSolderedGatePinNumber());
					} else if(sp instanceof SolderedControl) {
						type = ((SolderedControl)sp).getControlStatus() ? "T" : "F";
					} else if(sp instanceof SpacerPin) {
						type = "_";
					}
					SolderedGate next  = sp.getSolderedGate();
					if(next != sg) {
						sg = next;
						solderID++;
					}
					
					String firstCharName = sg.getGateModelFormalName().substring(0, 1);
					
					debugS[c][r] = firstCharName + type + "," + solderID;
					
					largestSpaces[c] = largestSpaces[c] > debugS[c][r].length() ? largestSpaces[c] : debugS[c][r].length();
				}
			}
			
			for(int r = 0; r < model.getRows(); r++) {
				for(int c = 0; c < model.getColumns(); c++) {
					commandResponse.print(debugS[c][r]);
					String offsetFix = "  ";
					for(int i = debugS[c][r].length(); i < largestSpaces[c]; i++)
						offsetFix += " ";
					commandResponse.print(offsetFix);
				}
				commandResponse.println("");
			}
			
			break;
		}
		
		return null;
	}
	
	private static boolean assertExists(String gateModel, GateModel gm, PrintStream commandResponse) {
		if(gm == null) {
			commandResponse.println("Gate \"" + gateModel +  "\" does not exist");
			return false;
		} else {
			return true;
		}
	}
	
	private static CircuitBoardView getFocusedCircuitBoardView(MainScene ms, PrintStream commandResponse) {
		AppTab t = ms.getCenterFocusedTab();
		if(t == null) {
			commandResponse.printErrln("No circuit board is opened and focused");
			return null;
		}
		
		AppFXMLComponent component = t.getAppFXMLComponent();
		
		if(!(component instanceof CircuitBoardView)) {
			commandResponse.printErrln("No circuit board is opened and focused");
			return null;
		}
			
		return (CircuitBoardView) component;
	}
	
}
