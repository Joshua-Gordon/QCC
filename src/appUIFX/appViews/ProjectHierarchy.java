package appUIFX.appViews;

import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import appUIFX.AppFileIO;
import appUIFX.MainScene;
import framework2FX.AppCommand;
import framework2FX.AppStatus;
import framework2FX.Project;
import framework2FX.gateModels.CircuitBoard;
import framework2FX.gateModels.DefaultModel;
import framework2FX.gateModels.GateModel;
import framework2FX.gateModels.OracleModel;
import framework2FX.gateModels.PresetGateType;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import utils.customCollections.Pair;

public class ProjectHierarchy extends AppView implements Initializable, EventHandler<MouseEvent> {
	
	public TreeView<String> projectView;
	private TreeItem<String> root, topLevel, subCircuits, customGates, customOracles, presetGates;
	private boolean initialized = false;
	private ContextMenu cm = new ContextMenu();
	
	public ProjectHierarchy() {
		super("ProjectHierarchy.fxml", "Project Hierarchy", Layout.LEFT);
	}
	
	public void setFocusedProject(Project project) {
		root = new TreeItem<>(project.getProjectName() + AppFileIO.QUANTUM_PROJECT_EXTENSION);
		topLevel = new TreeItem<>("Top Level Board");
		subCircuits = new TreeItem<>("Sub-Circuits");
		customGates = new TreeItem<>("Custom Gates");
		customOracles = new TreeItem<>("Custom Oracles");
		presetGates = new TreeItem<>("Preset Gates");
		
		String topLevelName = project.getTopLevelCircuitName();
		boolean hasTopLevel = topLevelName != null;
		
		if(topLevelName != null) {
			topLevel.getChildren().add(new TreeItem<>(topLevelName));
		}
			
		for(String name : project.getSubCircuits().getGateNameIterable())
			if(!hasTopLevel || !name.equals(topLevelName))
				subCircuits.getChildren().add(new TreeItem<String>(name));
		
		for(String name : project.getCustomGates().getGateNameIterable())
			customGates.getChildren().add(new TreeItem<String>(name));
			
		for(String name: project.getCustomOracles().getGateNameIterable())
			customOracles.getChildren().add(new TreeItem<String>(name));
		
		for(PresetGateType dg : PresetGateType.values())
			presetGates.getChildren().add(new TreeItem<String>(dg.getModel().getFormalName()));
		
		
		root.getChildren().add(topLevel);
		root.getChildren().add(subCircuits);
		root.getChildren().add(customGates);
		root.getChildren().add(customOracles);
		root.getChildren().add(presetGates);
		
		projectView.setRoot(root);
		cm.hide();
	}
	
	@Override
	public void handle(MouseEvent event) {
		TreeItem<String> item = projectView.getSelectionModel().getSelectedItem();
		
		if(item != null && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
			cm.hide();
			
			TreeItem<String> parent = item.getParent();
			
			if(parent == topLevel || parent == subCircuits || parent == presetGates
					|| parent == customGates || parent == customOracles)
				AppCommand.doAction(AppCommand.OPEN_GATE, item.getValue());
			
		} else if (item != null && event.getButton().equals(MouseButton.SECONDARY) && event.getClickCount() == 1) {
			cm.hide();
			cm = new CustomContextMenu(item);
			cm.show(projectView, event.getScreenX(), event.getScreenY());
		} else {
			cm.hide();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialized = true;
		Project p = AppStatus.get().getFocusedProject();
		if( p != null )
			setFocusedProject(p);

		projectView.setOnMouseClicked(this);
	}

	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		AppStatus status = AppStatus.get();
		Project p = status.getFocusedProject();
		
		if(source instanceof AppStatus && methodName.equals("setFocusedProject") && initialized) {
			setFocusedProject( (Project) args[0] );
		} else if(source instanceof Project && methodName.equals("setProjectFileLocation") && initialized) {
			root.setValue(Project.getProjectNameFromURI((URI) args[0]));
		} else if(source instanceof Project && methodName.equals("setTopLevelCircuitName") && initialized) {
			topLevel.getChildren().clear();
			if(args[0] != null) {
				topLevel.getChildren().add(new TreeItem<String>(args[0].toString()));
				Iterator<TreeItem<String>> iter = subCircuits.getChildren().iterator();
				while(iter.hasNext())
					if(iter.next().getValue().equals(args[0]))
						iter.remove();
			}
			
			String previous = p.getTopLevelCircuitName();
			if(previous != null && p.getSubCircuits().containsGateModel(previous))
				subCircuits.getChildren().add(new TreeItem<String>(previous.toString()));
			
		} else 	if(p != null && initialized && (source == p.getCustomGates() || source == p.getSubCircuits() || source == p.getCustomOracles())) {
			Pair<TreeItem<String>, String> list = getListFromSource(p, source);
			if(methodName.equals("put")) {
				GateModel replacement = (GateModel) args[0];
				removeSolderableByName(list.first(), replacement.getFormalName());
				addSolderable(list.first(), replacement);
			} else if(methodName.equals("replace")) {
				String name = (String) args[0];
				GateModel replacement = (GateModel) args[1];
				removeSolderableByName(list.first(), name + "." + list.second());
				addSolderable(list.first(), replacement);
			} else 	if(methodName.equals("remove")) {
				String name = (String) args[0];
				removeSolderableByName(list.first(), name + "." + list.second());
			}
		}
		cm.hide();
		
		return false;
	}
	
	private Pair<TreeItem<String>, String> getListFromSource(Project p, Object source) {
		if(source == p.getCustomGates()) {
			return new Pair<>(customGates, DefaultModel.GATE_MODEL_EXTENSION);
		} else if (source == p.getSubCircuits()) {
			return new Pair<>(subCircuits, CircuitBoard.CIRCUIT_BOARD_EXTENSION);
		} else if(source == p.getCustomOracles()) {
			return new Pair<>(customOracles, OracleModel.ORACLE_MODEL_EXTENSION);
		}
		return null;
	}

	private void removeSolderableByName(TreeItem<String> treeItem, String name) {
		Iterator<TreeItem<String>> iter = treeItem.getChildren().iterator();
		while(iter.hasNext()) {
			TreeItem<String> next= iter.next();
			
			if(next.getValue().equals(name)) {
				iter.remove();
				return;
			}
		}
	}
	
	private void addSolderable(TreeItem<String> treeItem, GateModel solderable) {
		treeItem.getChildren().add(new TreeItem<String>(solderable.getFormalName()));
	}
	
	@SuppressWarnings("unused")
	private class CustomContextMenu extends ContextMenu {
		
		private CustomContextMenu(TreeItem<String> selected) {
			TreeItem<String> parent = selected.getParent();
			
			AppStatus as = AppStatus.get();
			
			MainScene ms = as.getMainScene();
			Project p = as.getFocusedProject();
			
			ObservableList<MenuItem> elements = getItems();
			
			if (parent == topLevel || parent == subCircuits || parent == presetGates || parent == customGates || parent == customOracles) {

				GateModel gm = (GateModel) AppCommand.doAction(AppCommand.GET_GATE, selected.getValue());
				
				if(parent == topLevel) {
					MenuItem removeFromTopLevel = new MenuItem("Remove From Top-Level");
					removeFromTopLevel.setOnAction((e) -> AppCommand.doAction(AppCommand.REMOVE_TOP_LEVEL));
					elements.add(removeFromTopLevel);
					elements.add(new SeparatorMenuItem());
					
				} else if(gm instanceof CircuitBoard) {
					MenuItem mkTopLevel = new MenuItem("Set as Top-Level");
					mkTopLevel.setOnAction((e) -> AppCommand.doAction(AppCommand.SET_AS_TOP_LEVEL, selected.getValue()));
					elements.add(mkTopLevel);
					elements.add(new SeparatorMenuItem());
				}
				
				
				MenuItem open = new MenuItem("Open");
				MenuItem editAsNew = new MenuItem("Edit as New");
				
				open.setOnAction((e) -> AppCommand.doAction(AppCommand.OPEN_GATE, selected.getValue()));
				elements.add(open);
				if(!gm.isPreset()) {
					MenuItem edit = new MenuItem("Edit");
					edit.setOnAction((e) -> AppCommand.doAction(AppCommand.EDIT_GATE, selected.getValue()));
					elements.add(edit);
				}
				editAsNew.setOnAction((e) -> AppCommand.doAction(AppCommand.EDIT_AS_NEW_GATE, selected.getValue())); 
				elements.add(editAsNew);
				if(!gm.isPreset()) {
					MenuItem remove = new MenuItem("Remove");
					remove.setOnAction((e) -> AppCommand.doAction(AppCommand.REMOVE_GATE, selected.getValue()));
					elements.add(remove);
				}
			} else if (selected == subCircuits) {
				MenuItem open = new MenuItem("Add Untitled Circuit Board");
				open.setOnAction((e) -> AppCommand.doAction(AppCommand.ADD_UNTITLED_CIRCUIT_BOARD));
				elements.add(open);
			} else if (selected == customGates) {
				MenuItem open = new MenuItem("Create Gate");
				open.setOnAction((e) -> AppCommand.doAction(AppCommand.CREATE_DEFAULT_GATE));
				elements.add(open);
			} else if (selected == customOracles) {
				MenuItem open = new MenuItem("Create Oracle");
				open.setOnAction((e) -> AppCommand.doAction(AppCommand.CREATE_ORACLE_GATE));
				elements.add(open);
			}
		}
		
		
	}
	
}
