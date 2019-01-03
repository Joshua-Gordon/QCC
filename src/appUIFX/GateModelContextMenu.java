package appUIFX;

import java.util.Optional;

import appUIFX.AppAlerts.AcceptInputRunnable;
import appUIFX.AppAlerts.Prompt;
import framework2FX.AppCommand;
import framework2FX.AppStatus;
import framework2FX.Project;
import framework2FX.gateModels.CircuitBoardModel;
import framework2FX.gateModels.GateModel;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class GateModelContextMenu extends ContextMenu {
	
	public GateModelContextMenu(Project p, GateModel gm) {
		
		ObservableList<MenuItem> elements = getItems();
		
		addToElements(gm, elements, p);
	}
	
	public static void addToElements(GateModel gm, ObservableList<MenuItem> elements, Project p) {
		String gateModel = gm.getFormalName();
		
		if(p != null && gm instanceof CircuitBoardModel) {
			String topLevel = p.getTopLevelCircuitName();
			
			if(topLevel != null && topLevel.equals(gateModel)) {
				MenuItem removeFromTopLevel = new MenuItem("Remove From Top-Level");
				removeFromTopLevel.setOnAction((e) -> AppCommand.doAction(AppCommand.REMOVE_TOP_LEVEL));
				elements.add(removeFromTopLevel);
				elements.add(new SeparatorMenuItem());
				
			} else {
				MenuItem mkTopLevel = new MenuItem("Set as Top-Level");
				mkTopLevel.setOnAction((e) -> AppCommand.doAction(AppCommand.SET_AS_TOP_LEVEL, gateModel));
				elements.add(mkTopLevel);
				elements.add(new SeparatorMenuItem());
			}
		}
		
		
		MenuItem open = new MenuItem("Open");
		MenuItem editAsNew = new MenuItem("Edit as New");
		
		open.setOnAction((e) -> AppCommand.doAction(AppCommand.OPEN_GATE, gateModel));
		elements.add(open);
		if(!gm.isPreset()) {
			MenuItem rename = new MenuItem("Rename");
			rename.setOnAction((e) -> {
				Prompt prompt = new Prompt(AppStatus.get().getPrimaryStage(), 
						"Rename", "Rename \"" + gm.getName() + "\"", gm.getName(), 250, 120);
				
				AcceptInputRunnable runnable = (s) -> {
					if(!s.matches(GateModel.NAME_REGEX)) {
						AppAlerts.showMessage(prompt.getPromptWindow(), "Inproper name scheme", 
								GateModel.INPROPER_NAME_SCHEME_MSG, AlertType.ERROR);
						return false;
					}
					
					if(s.matches(gm.getName()))
						return true;
					
					String formalName = s + "." + gm.getExtString();
					
					GateModel gmOld = AppStatus.get().getFocusedProject().getGateModel(formalName);
					
					if(gmOld == null)
						return true;
					
					if(gmOld.isPreset()) {
						AppAlerts.showMessage(prompt.getPromptWindow(), "Gate Model is preset", 
								"Gate \"" + s +  "\" is a preset gate and cannot be renamed", AlertType.ERROR);
						return false;
					}
					
					Optional<ButtonType> options = AppAlerts.showMessage(prompt.getPromptWindow(), "Override Gate Model " + formalName, 
							"A Gate Model with the name \"" + formalName + "\" already exists, "
									+ "do you want to override this gate model?"
									+ " All instances of the previous implementation of \""
									+ formalName + "\" in this project will be removed.", AlertType.WARNING);
						
					return options.get() == ButtonType.OK;
					
				};
				
				prompt.setAcceptRunnable(runnable);
				
				Optional<ButtonType> options = prompt.showAndWait();
				
				if(options.get() == ButtonType.APPLY)
					AppCommand.doAction(AppCommand.RENAME_GATE, gateModel, prompt.getReply() + "." +  gm.getExtString());
			});
			elements.add(rename);
			MenuItem edit = new MenuItem("Edit");
			edit.setOnAction((e) -> AppCommand.doAction(AppCommand.EDIT_GATE, gateModel));
			elements.add(edit);
		}
		editAsNew.setOnAction((e) -> AppCommand.doAction(AppCommand.EDIT_AS_NEW_GATE, gateModel));
		elements.add(editAsNew);
		if(!gm.isPreset()) {
			MenuItem remove = new MenuItem("Remove");
			remove.setOnAction((e) -> AppCommand.doAction(AppCommand.REMOVE_GATE, gateModel));
			elements.add(remove);
		}
	}
}
