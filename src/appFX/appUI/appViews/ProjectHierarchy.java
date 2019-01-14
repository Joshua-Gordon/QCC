package appFX.appUI.appViews;

import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import appFX.appUI.AppFileIO;
import appFX.appUI.GateModelContextMenu;
import appFX.appUI.MainScene;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.BasicModel;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.OracleModel;
import appFX.framework.gateModels.PresetGateType;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
			
		for(String name : project.getCircuitBoardModels().getGateNameIterable())
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
		
		if(initialized) {
			if(source instanceof AppStatus && methodName.equals("setFocusedProject")) {
				setFocusedProject( (Project) args[0] );
			} else if(source instanceof Project && methodName.equals("setProjectFileLocation")) {
				root.setValue(Project.getProjectNameFromURI((URI) args[0]));
			} else if(source instanceof Project && methodName.equals("setTopLevelCircuitName")) {
				topLevel.getChildren().clear();
				if(args[0] != null) {
					topLevel.getChildren().add(new TreeItem<String>(args[0].toString()));
					Iterator<TreeItem<String>> iter = subCircuits.getChildren().iterator();
					while(iter.hasNext())
						if(iter.next().getValue().equals(args[0]))
							iter.remove();
				}
				
				String previous = p.getTopLevelCircuitName();
				if(previous != null && p.getCircuitBoardModels().containsGateModel(previous))
					subCircuits.getChildren().add(new TreeItem<String>(previous.toString()));
				
			} else 	if(p != null &&(source == p.getCustomGates() || source == p.getCircuitBoardModels() || source == p.getCustomOracles())) {
				Pair<TreeItem<String>, String> list = getListFromSource(p, source);
				
				
				if(methodName.equals("put")) {
					GateModel replacement = (GateModel) args[0];
					removeSolderableByName(list.first(), replacement.getFormalName());
					addSolderable(list.first(), replacement);
				} else if(methodName.equals("replace")) {
					String name = (String) args[0];
					GateModel replacement = (GateModel) args[1];
					removeSolderableByName(list.first(), name);
					removeSolderableByName(list.first(), replacement.getFormalName());
					addSolderable(list.first(), replacement);
				} else 	if(methodName.equals("remove")) {
					String name = (String) args[0];
					removeSolderableByName(list.first(), name);
				}
			}
			cm.hide();
		}
		
		return false;
	}
	
	private Pair<TreeItem<String>, String> getListFromSource(Project p, Object source) {
		if(source == p.getCustomGates()) {
			return new Pair<>(customGates, BasicModel.GATE_MODEL_EXTENSION);
		} else if (source == p.getCircuitBoardModels()) {
			return new Pair<>(subCircuits, CircuitBoardModel.CIRCUIT_BOARD_EXTENSION);
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
				GateModel gm = p.getGateModel(selected.getValue());
				GateModelContextMenu.addToElements(gm, elements, p);
				
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
