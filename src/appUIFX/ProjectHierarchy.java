package appUIFX;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.AppStatus;
import framework2FX.Project;
import framework2FX.gateModels.AbstractGateModel;
import framework2FX.gateModels.DefaultGateModel;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ProjectHierarchy extends AppView implements Initializable {
	
	public TreeView<String> projectView;
	private TreeItem<String> root, topLevel, subCircuits, customGates, customOracles, defaultGates;
	
	public ProjectHierarchy() {
		super("ProjectHierarchy.fxml", "Project Hierarchy", Layout.LEFT);
	}
	
	public void setFocusedProject(Project project) {
		root = new TreeItem<>(project.getProjectName());
		topLevel = new TreeItem<>("Top Level Board");
		subCircuits = new TreeItem<String>("Sub-Circuits");
		customGates = new TreeItem<String>("Custom Gates");
		customOracles = new TreeItem<String>("Custom Oracles");
		defaultGates = new TreeItem<String>("Default Gates");
		
		String topLevelName = project.getTopLevelCircuitName();
		boolean hasTopLevel = topLevelName != null;
		
		if(hasTopLevel)
			topLevel.getChildren().add(new TreeItem<>(topLevelName));
			
		for(String name : project.getSubCircuits().getKeyIterable())
			if(!hasTopLevel || !name.equals(topLevelName))
				subCircuits.getChildren().add(new TreeItem<String>(name));
		
		for(AbstractGateModel sgm : project.getCustomGates())
			customGates.getChildren().add(new TreeItem<String>(sgm.getName()));
		
		for(AbstractGateModel agm : project.getCustomOracles())
			customOracles.getChildren().add(new TreeItem<String>(agm.getName()));
		
		for(DefaultGateModel dg : DefaultGateModel.values())
			defaultGates.getChildren().add(new TreeItem<String>(dg.getModel().getName()));
		
		
		root.getChildren().add(topLevel);
		root.getChildren().add(subCircuits);
		root.getChildren().add(customGates);
		root.getChildren().add(customOracles);
		root.getChildren().add(defaultGates);
		
		projectView.setRoot(root);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Project p = AppStatus.get().getFocusedProject();
		if( p != null )
			setFocusedProject(p);
	}

	@Override
	public void receive(Object source, String methodName, Object... args) {
		if(source instanceof AppStatus && methodName.equals("setFocusedProject"))
			setFocusedProject( (Project) args[0] );
		if(source instanceof Project && methodName.equals("setProjectFileLocation"))
			root.setValue(Project.getProjectNameFromURI((URI) args[0]));
	}
	
	
}
