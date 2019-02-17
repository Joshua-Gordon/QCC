package appFX.appUI;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import appFX.appPreferences.AppPreferences;
import appFX.appUI.appViews.AppView;
import appFX.appUI.appViews.AppView.Layout;
import appFX.appUI.appViews.AppView.ViewListener;
import appFX.appUI.appViews.ConcreteTabView;
import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.framework.AppStatus;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import main.Main;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

public class MainScene extends AppFXMLComponent implements Initializable, AppPreferences, ReceivedEvent {

	public SplitPane verticalSplitPane, horizontalSplitPane;
	public BorderPane leftBorderPane, bottomBorderPane, rightBorderPane;
	public TabPane leftTabPane, centerTabPane, rightTabPane, bottomTabPane;
	public ToggleButton selectTool, solderTool, editTool, controlTool, controlNotTool, addColumnTool, removeColumnTool, addRowTool, removeRowTool;
	public MenuBar menuBar;
	public Label appNameLabel;
	
	private ToggleGroup tools;
	
	private double[] cachedDividerPositions;
	private boolean initialized = false;
	
	public MainScene() {
		super("MainScene.fxml");
		this.cachedDividerPositions = new double[] { Doubles.LEFT_DIVIDER_POSITION.get(),
				Doubles.BOTTOM_DIVIDER_POSITION.get(), Doubles.RIGHT_DIVIDER_POSITION.get()};
	}
	
	
	public void addCircuitBoardView(CircuitBoardView circuitBoardView) {
		addView(centerTabPane, circuitBoardView.getTab());
	}
	
	
	public void addView(ConcreteTabView tabView) {
		addView(tabView.getView());
	}
	
	
	public boolean containsView(String tabName, Layout tabLayout) {
		switch(tabLayout) {
		case LEFT:
			return containsTab(tabName, leftTabPane);
		case CENTER:
			return containsTab(tabName, centerTabPane);
		case RIGHT:
			return containsTab(tabName, rightTabPane);
		case BOTTOM:
		default:
			return containsTab(tabName, bottomTabPane);
		}
	}
	
	private boolean containsTab(String name, TabPane tabPane) {
		Iterator<Tab> i = tabPane.getTabs().iterator();
		Tab t;
		while(i.hasNext()) {
			t = i.next();
			if(t.getText().equals(name))
				return true;
		}
		return false;
	}
	
	
	public void addView(AppView view) {
		
		Tab tab = view.getTab();
		
		ViewListener viewListener = view.getViewListener();
		
		tab.setOnClosed((event) -> {
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
			if(viewListener != null)
				viewListener.viewChanged(true);
		}
	}
	
	
	
	
	
	private boolean addView(TabPane tabPane, Tab tab) {
		
		Iterator<Tab> i = tabPane.getTabs().iterator();
		
		Tab t;
		while(i.hasNext()) {
			t = i.next();
			if(t.getText().equals(tab.getText())) {
				tabPane.getSelectionModel().select(t);
				return false;
			}
		}
		
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
		return true;
	}
	
	
	
	
	
	public void removeView(ConcreteTabView singleView) {
		removeView(singleView.getView());
	}
	
	public void removeView (AppView view ) {
//		boolean removed;
		
		switch(view.getLayout()) {
		case LEFT:
//			removed = removeView(leftTabPane, view);
			removeView(leftTabPane, view);
			break;
		case CENTER:
//			removed = removeView(centerTabPane, view);
			removeView(centerTabPane, view);
			break;
		case RIGHT:
//			removed = removeView(rightTabPane, view);
			removeView(rightTabPane, view);
			break;
		case BOTTOM:
		default:
//			removed = removeView(bottomTabPane, view);
			removeView(bottomTabPane, view);
			break;
		}
		
//		if(removed) {
//			ViewListener listener = view.getViewListener();
//			if(listener != null)
//				listener.viewChanged(false);
//		}
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
	
	
	public AppTab getCenterFocusedTab() {
		AppTab t = (AppTab) centerTabPane.getSelectionModel().getSelectedItem();
		if(t == null)
			return null;
		return t;
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialized = true;
		initializeTools();
		AppMenuBar.initializeMenuBar(this);
		initializeViews();
		appNameLabel.setText(Main.APP_NAME);
	}
	
	
	
	
	
	
	private void initializeTools() {
		tools  = new ToggleGroup();
		selectTool.setToggleGroup(tools);
		solderTool.setToggleGroup(tools);
		editTool.setToggleGroup(tools);
		controlTool.setToggleGroup(tools);
		controlNotTool.setToggleGroup(tools);
		addColumnTool.setToggleGroup(tools);
		removeColumnTool.setToggleGroup(tools);
		addRowTool.setToggleGroup(tools);
		removeRowTool.setToggleGroup(tools);
	}
	
	public ToggleButton getSelectedTool() {
		return (ToggleButton) tools.getSelectedToggle();
	}
	
	public boolean isSolderButton(ToggleButton tb) {
		return tb == solderTool;
	}
	
	
	public boolean isSolderButtonSelected() {
		return tools.getSelectedToggle() == solderTool;
	}
	
	
	public void addToolButtonListener(ChangeListener<? super Toggle> listener) {
		tools.selectedToggleProperty().addListener(listener);
	}
	
	public void removeToolButtonListener(ChangeListener<? super Toggle> listener) {
		tools.selectedToggleProperty().removeListener(listener);
	}
	
	
	private void initializeViews() {
		for(ConcreteTabView ctv :  ConcreteTabView.values())
			if(ctv.wasOpen().get())
				addView(ctv);
		
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


	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		if(source == AppStatus.get() && methodName.equals("setFocusedProject") && initialized)
			centerTabPane.getTabs().clear();
		
		return false;
	}
}
