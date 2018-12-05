package appUIFX;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import appPreferencesFX.AppPreferences;
import appUIFX.TabView.ViewListener;
import framework2FX.CircuitBoard;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainScene extends AppFXMLComponent implements Initializable, AppPreferences{

	public SplitPane verticalSplitPane, horizontalSplitPane;
	public BorderPane leftBorderPane, bottomBorderPane, rightBorderPane;
	public TabPane leftTabPane, centerTabPane, rightTabPane, bottomTabPane;
	public ToggleButton selectTool, solderTool, editTool, addColumnTool, removeColumnTool, addRowTool, removeRowTool;
	public MenuBar menuBar;
	
	private double[] cachedDividerPositions;
	
	
	public MainScene() {
		super("MainScene.fxml");
		this.cachedDividerPositions = new double[] { Doubles.LEFT_DIVIDER_POSITION.get(),
				Doubles.BOTTOM_DIVIDER_POSITION.get(), Doubles.RIGHT_DIVIDER_POSITION.get()};
	}
	
	
	public void addCircuitBoardView(CircuitBoardView circuitBoardView) {
		addView(centerTabPane, circuitBoardView.getTab((Stage) menuBar.getScene().getWindow()));
	}
	
	
	
	public void addView(TabView singleView) {
		AppView view = singleView.getView();
		
		Stage stage = menuBar.getScene() == null ? null : (Stage) menuBar.getScene().getWindow();
		Tab tab = view.getTab(stage);
		
		tab.setOnClosed((event) -> {
			ViewListener viewListener = singleView.getViewListener();
			if(viewListener != null)
				viewListener.viewChanged(false);
		});
		
		boolean added;
		switch(view.getLayout()) {
		case LEFT:
			added = addView(leftTabPane, tab);
			break;
		case CENTER:
			added = addView(centerTabPane, tab);
			break;
		case RIGHT:
			added = addView(rightTabPane, tab);
			break;
		case BOTTOM:
		default:
			added = addView(bottomTabPane, tab);
			break;
		}
		
		if(added) {
			ViewListener listener = singleView.getViewListener();
			if(listener != null)
				listener.viewChanged(true);
		}
	}
	
	
	
	
	
	private boolean addView(TabPane tabPane, Tab tab) {
		
		Iterator<Tab> i = tabPane.getTabs().iterator();
		
		Tab t;
		while(i.hasNext()) {
			t = i.next();
			if(t.getText().equals(tab.getText()))
				return false;
		}
		
		tabPane.getTabs().add(tab);
		return true;
	}
	
	
	
	
	
	public void removeView(TabView singleView) {
		AppView view = singleView.getView();
		
		boolean removed;
		
		switch(singleView.getView().getLayout()) {
		case LEFT:
			removed = removeView(leftTabPane, view);
			break;
		case CENTER:
			removed = removeView(centerTabPane, view);
			break;
		case RIGHT:
			removed = removeView(rightTabPane, view);
			break;
		case BOTTOM:
		default:
			removed = removeView(bottomTabPane, view);
			break;
		}
		
		if(removed) {
			ViewListener listener = singleView.getViewListener();
			if(listener != null)
				listener.viewChanged(false);
		}
	}
	
	
	private boolean removeView(TabPane tabPane, AppView view) {
		String viewName = view.getName();
		
		ObservableList<Tab> tabs = tabPane.getTabs();
		Iterator<Tab> i = tabs.iterator();
		
		Tab t;
		int ii = 0;
		while(i.hasNext()) {
			t = i.next();
			if(t.getText().equals(viewName)) {
				tabs.remove(ii);
				return true;
			}
			ii++;
		}
		
		return false;
	}
	
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeTools();
		AppMenuBar.initializeMenuBar(this);
		initializeViews();
	}
	
	
	
	
	
	
	private void initializeTools() {
		ToggleGroup tools  = new ToggleGroup();
		selectTool.setToggleGroup(tools);
		solderTool.setToggleGroup(tools);
		editTool.setToggleGroup(tools);
		addColumnTool.setToggleGroup(tools);
		removeColumnTool.setToggleGroup(tools);
		addRowTool.setToggleGroup(tools);
		removeRowTool.setToggleGroup(tools);
	}
	
	
	
	
	
	
	private void initializeViews() {		
		if(Booleans.CONSOLE_OPEN.get())
			addView(TabView.CONSOLE);
		if(Booleans.PRESET_GATES_OPEN.get())
			addView(TabView.PRESET_GATES_VIEW);
		if(Booleans.CUSTOM_GATES_OPEN.get())
			addView(TabView.CUSTOM_GATES_VIEW);
		if(Booleans.PROJECT_HEIRARCHY_OPEN.get())
			addView(TabView.PROJECT_HIERARCHY);
		
		intializeTabPanes();
	}
	
	
	
	
	
	private void intializeTabPanes() {
		linkDividerAndTabPane(leftTabPane, 0);
		linkDividerAndTabPane(bottomTabPane, 1);
		linkDividerAndTabPane(rightTabPane, 2);
	}
	
	
	
	
	
	private void linkDividerAndTabPane(TabPane tabPane, int cachedDividerPositionIndex) {
		if(cachedDividerPositionIndex > 2 || cachedDividerPositionIndex < 0)
			throw new IllegalArgumentException("cachedDividerPositionIndex must be between 0 and 2 (inclusive)");
		
		ObservableList<Tab> tabs = tabPane.getTabs();
		SplitPane splitPane;
		BorderPane borderPane;
		int dividerIndex = cachedDividerPositionIndex > 1 ? 1 : 0;
		int dividerClosePosition = cachedDividerPositionIndex > 0? 1 : 0;
		
		switch(cachedDividerPositionIndex) {
		case 0:
			borderPane = leftBorderPane;
			splitPane = horizontalSplitPane;
			break;
		case 1:
			borderPane = bottomBorderPane;
			splitPane = verticalSplitPane;
			break;
		case 2:
		default:
			borderPane = rightBorderPane;
			splitPane = horizontalSplitPane;
			break;
		}
		
		
		
		tabs.addListener(new ListChangeListener<Tab>() {
			@Override
			public void onChanged(Change<? extends Tab> c) {
				while(c.next()) {
					
					if(c.wasRemoved() && c.getList().isEmpty()) {
						cachedDividerPositions[cachedDividerPositionIndex] = splitPane.getDividers().get(dividerIndex).getPosition();
						borderPane.setMaxWidth(0);
						borderPane.setMaxHeight(0);
						borderPane.setVisible(false);
						borderPane.setManaged(false);
						
					} else if(c.wasAdded() && c.getAddedSize() == c.getList().size()){
						borderPane.setMaxWidth(BorderPane.USE_COMPUTED_SIZE);
						borderPane.setMaxHeight(BorderPane.USE_COMPUTED_SIZE);
						borderPane.setVisible(true);
						borderPane.setManaged(true);
						
						splitPane.setDividerPosition(dividerIndex, cachedDividerPositions[cachedDividerPositionIndex]);
					}
				}
			}
		});
		
		
		
		if(tabPane.getTabs().isEmpty()) {
			splitPane.setDividerPosition(dividerIndex, dividerClosePosition);
			borderPane.setMaxWidth(0);
			borderPane.setMaxHeight(0);
			borderPane.setVisible(false);
			borderPane.setManaged(false);
			
		}else {
			borderPane.setMaxWidth(BorderPane.USE_COMPUTED_SIZE);
			borderPane.setMaxHeight(BorderPane.USE_COMPUTED_SIZE);
			borderPane.setVisible(true);
			borderPane.setManaged(true);
			
			splitPane.setDividerPosition(dividerIndex, cachedDividerPositions[cachedDividerPositionIndex]);
		}
	}
}
