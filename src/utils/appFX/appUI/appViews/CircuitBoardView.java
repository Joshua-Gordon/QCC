package appFX.appUI.appViews;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;

import appFX.appUI.GateIcon;
import appFX.appUI.LatexNode;
import appFX.appUI.MainScene;
import appFX.appUI.ParameterPrompt;
import appFX.appUI.appViews.AppView.ViewListener;
import appFX.appUI.appViews.gateChooser.AbstractGateChooser;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.UserDefinitions.DefinitionEvaluatorException;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
import appFX.framework.solderedGates.SolderedGate;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import utils.customCollections.ImmutableArray;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

public class CircuitBoardView extends AppView implements Initializable, ViewListener {
	
	private static final int GRID_SIZE = 50;
	
	public ScrollPane description, container;
	public GridPane circuitBoardPane;
	public TextField name, symbol;
	public HBox parameters;
	public CheckBox grid;
	
	private final CircuitBoardModel circuitBoard;
	private final Project project;
	private boolean initialized = false;
	private final SolderRegionTool solderRegion;
	
	public static void openCircuitBoard(String circuitBoardName) {
		AppStatus status = AppStatus.get();
		CircuitBoardView circuitBoardView = new CircuitBoardView(status.getFocusedProject(), circuitBoardName);
		status.getMainScene().addView(circuitBoardView);
	}
	
	private CircuitBoardView(Project project, String circuitBoard) {
		super("CircuitBoardView.fxml", circuitBoard, Layout.CENTER);
		this.circuitBoard = (CircuitBoardModel) project.getCircuitBoardModels().get(circuitBoard);
		this.project = project;
		this.circuitBoard.setRenderEventHandler(new CircuitBoardEventHandler());
		this.solderRegion = new SolderRegionTool();
	}
	
	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		Project p = AppStatus.get().getFocusedProject();
		if(initialized && p.getCircuitBoardModels() == source) {
			if(methodName.equals("put")) {
				if(((GateModel)args[0]).getFormalName().equals(getName())) {
					setViewListener(null);
					closeView();
					return true;
				}
			} else if (methodName.equals("replace") || methodName.equals("remove")){
				if(args[0].equals(getName())) {
					setViewListener(null);
					closeView();
					return true;
				}
			}
		}
		return false;
	}
	
	public void toggleGrid(ActionEvent ae) {
		if(grid.isSelected())
			showGrid();
		else
			hideGrid();
		
	}
	
	private void showGrid() {
		Set<Node> nodes = circuitBoardPane.lookupAll(".cb_grid");
		for(Node n : nodes) {
			n.setStyle("-fx-border-color: #BDBDBD ;\n" + 
					   "-fx-border-width: 1 ; \n" + 
					   "-fx-border-style: segments(5, 5, 5, 5)  line-cap round ;");
		} 
	}
	
	private void hideGrid() {
		Set<Node> nodes = circuitBoardPane.lookupAll(".cb_grid");
		for(Node n : nodes)
			n.setStyle("");
	}
	
	
	
	public void editAsNew(ActionEvent ae) {
		AppCommand.doAction(AppCommand.EDIT_AS_NEW_GATE, circuitBoard.getFormalName());
	}
	
	public void editProperties(ActionEvent ae) {
		AppCommand.doAction(AppCommand.EDIT_GATE, circuitBoard.getFormalName());
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialized = true;
		setViewListener(this);
		addToReceiveEventListener();
		
		name.setText(circuitBoard.getName());
		name.setEditable(false);
		
		symbol.setText(circuitBoard.getSymbol());
		symbol.setEditable(false);
		
		description.setContent(new LatexNode(circuitBoard.getDescription()));
		
		ImmutableArray<String> args = circuitBoard.getArguments();
		if(!args.isEmpty()) {
			String parametersLatex = "\\(" + args.get(0) + "\\)";
			for(int i = 1; i < args.size(); i++)
				parametersLatex += ", \\(" + args.get(i) + "\\)";
			
			parameters.getChildren().add(new LatexNode(parametersLatex, .7f));
		}
		
		circuitBoardPane.setOnMouseExited((e) -> {
			solderRegion.showTool(false);
		});
		

		AppStatus.get().getMainScene().addToolButtonListener(solderRegion.toolChanged);
		AbstractGateChooser.addToggleListener(solderRegion.gateModelChanged);
		
		rerenderCircuitBoard();
	}
	
	public CircuitBoardModel getCircuitBoardModel() {
		return circuitBoard;
	}
	
	
	@Override
	public void viewChanged(boolean wasAdded) {
		if(!wasAdded) {
			AppStatus.get().getMainScene().removeToolButtonListener(solderRegion.toolChanged);
			AbstractGateChooser.removeToggleListener(solderRegion.gateModelChanged);
			removeEventListener();
		}
	}
	
	private class CircuitBoardEventHandler implements ReceivedEvent {
		
		@Override
		public boolean receive(Object source, String methodName, Object... args) {
			rerenderCircuitBoard();
			return false;
		}
	}
	
	private class SolderRegionTool extends Region implements EventHandler<MouseEvent>{
		private final ChangeListener<Toggle> toolChanged, gateModelChanged;
		private Integer[] regs;
		private ArrayList<NumberRegion> regDisps;
		private int currentReg = -1;
		private int lastReg = -1;
		private int column = -1;
		
		public SolderRegionTool() {
			setStyle("-fx-background-color: #BDBDBD66");
			setOnMouseClicked(this);
			
			
			toolChanged = (o, oldV, newV) -> {
				MainScene ms = AppStatus.get().getMainScene();
				if(oldV != null && ms.isSolderButton((ToggleButton) oldV))
					showTool(false);
			};
			
			gateModelChanged = (o, oldV, newV) -> {
				restart();
			};
		}
		
		public void showTool(boolean show) {
			setManaged(show);
			setVisible(show);
		}
		
		
		public void toolAction() {
			
			if(column != -1 && column != getColumn())
				restart();
			column = getColumn();
			
			
			GateModel gm = getSelectedModel();
			if(gm != null) { 
				
				if(regs == null) {
					regs = new Integer[gm.getNumberOfRegisters()];
					regDisps = new ArrayList<NumberRegion>(gm.getNumberOfRegisters() - 1);
					lastReg = 1;
					currentReg = 0;
				}
				
				NumberRegion numberRegion = new NumberRegion(); 
				regDisps.add(numberRegion);
				circuitBoardPane.getChildren().add(numberRegion);
				
				regs[currentReg] = getRow();
				currentReg = lastReg;
				lastReg++;
				
				if(lastReg == regs.length + 1) {
					ImmutableArray<String> args = gm.getArguments();
					if(args.size() > 0) {
						ParameterPrompt pp = new ParameterPrompt(project, circuitBoard, gm.getFormalName(), regs, column);
						pp.showAndWait();
					} else {
						try {
							circuitBoard.placeGate(gm.getFormalName(), column, regs);
						} catch (DefinitionEvaluatorException e) {
							e.printStackTrace();
						}
					}
					restart();
				}
			}
			
		}
		
		private int getRow() {
			return GridPane.getRowIndex(this);
		}
		
		private int getColumn () {
			return GridPane.getColumnIndex(this) - 1;
		}
		
		public void restart() {
			currentReg = -1;
			column = -1;
			lastReg = -1;
			
			if(regDisps != null) {
				for(NumberRegion nr : regDisps)
					circuitBoardPane.getChildren().remove(nr);
			}
			
			regDisps = null;
			regs = null;
		}
		
		public GateModel getSelectedModel() {
			return AbstractGateChooser.getSelected();
		}

		@Override
		public void handle(MouseEvent event) {
			toolAction();
		}
		
		
		private class NumberRegion extends BorderPane {
			private int reg = currentReg;
			private Label label;
			
			private NumberRegion() {
				label = new Label(Integer.toString(reg));
				setCenter(label);
				setStyle("-fx-background-color: #BDBDBD;");
				GridPane.setConstraints(this, getColumn() + 1, getRow());
				
				setOnMouseClicked((e) -> {
					regs[currentReg] = GridPane.getRowIndex(this);
					int temp = currentReg;
					currentReg = reg;
					reg = temp;
					label.setText(Integer.toString(reg));
				});
			}
		}
	}
	
	
//	Render Methods:
	
	
	public void rerenderCircuitBoard() {
		
		ObservableList<Node> nodes = circuitBoardPane.getChildren();
		nodes.clear();

		ObservableList<RowConstraints> rows = circuitBoardPane.getRowConstraints();
		
		for(int i = 0; i < circuitBoard.getRows(); i++) {
			LatexNode ln = new LatexNode("\\( \\lvert \\psi_{" + i +  "}\\rangle \\)");
			GridPane.setConstraints(ln, 0, i, 1, 1, HPos.CENTER, VPos.CENTER);
			ln.setPadding(new Insets(0, 5, 0, 5));
			
			rows.add(new RowConstraints(GridPane.USE_PREF_SIZE, GridPane.USE_COMPUTED_SIZE, GridPane.USE_PREF_SIZE));
			
			circuitBoardPane.getChildren().add(ln);
		}
		
		
		ObservableList<ColumnConstraints> columns = circuitBoardPane.getColumnConstraints();
		for(int i = 0; i < circuitBoard.getColumns(); i++)
			columns.add(new ColumnConstraints(GridPane.USE_PREF_SIZE, GridPane.USE_COMPUTED_SIZE, GridPane.USE_PREF_SIZE));
		
		
		for(RawExportableGateData data : circuitBoard) {
			SolderedGate sg = data.getSolderedGate();
			
			int column = data.getColumn();
			
			if(sg.isIdentity()) {
				Hashtable<Integer, Integer> regs = data.getRegisters();
				Node n = makeIdentityAt(regs.get(0), column);
				nodes.add(n);
				continue;
			}
			
			
			
			
			
			
			LinkedList<Control> controls = data.getControls();
			
			int start = data.getGateRowSpaceStart();
			int startBody = data.getGateRowBodyStart();
			int endBody = data.getGateRowBodyEnd();
			int end = data.getGateRowSpaceEnd();

			
			for(int i = start; i < startBody; i++) {
				Control c = controls.peek();
				if(c != null && c.getRegister() == i) {
					nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
					controls.pop();
				} else {
					nodes.add(makeIdentityWithLineAt(i, column));
				}
			}
			
			
			
			
			
			
			
			GateModel gm = project.getGateModel(sg.getGateModelFormalName());
			
			String symbol;
			if(gm == null) {
				symbol = sg.getGateModelFormalName();
				for(int i = startBody; i < endBody + 1; i++)
					nodes.add(makeIdentityAt(i, column));
//				nodes.add(makeRegularGateBody(data, symbol));
				addRegularGateBody(nodes, data, symbol);
			} else if(gm.isPreset()) {
				symbol = gm.getName();
				PresetGateModel pgm = (PresetGateModel) gm;
				
				switch(pgm.getPresetGateType()) {
				case CNOT:
					
					for(int i = startBody; i < endBody + 1; i++) {
						Control c = controls.peek();
						if(c != null && c.getRegister() == i) {
							nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
							controls.pop();
						} else if (data.getRegisters().get(0) == i) {
							nodes.add(makeControlAt(i, column, true, getDispType(start, end, i)));
						} else if (data.getRegisters().get(1) == i) {
							nodes.add(makeCNOTHead(i, column, getDispType(startBody, end, i)));
						} else {
							nodes.add(makeIdentityWithLineAt(i, column));
						}
					}
					
					
					break;
				case MEASUREMENT:
					
					nodes.add(makeMeasurementGate(startBody, column));
					
					break;
				case SWAP:
					
					for(int i = startBody; i < endBody + 1; i++) {
						Control c = controls.peek();
						if(c != null && c.getRegister() == i) {
							nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
							controls.pop();
						} else if (data.getRegisters().get(0) == i) {
							nodes.add(makeSwapHead(i, column, getDispType(start, end, i)));
						} else if (data.getRegisters().get(1) == i) {
							nodes.add(makeSwapHead(i, column, getDispType(start, end, i)));
						} else {
							nodes.add(makeIdentityWithLineAt(i, column));
						}
					}
					
					
					break;
				case TOFFOLI:
					
					for(int i = startBody; i < endBody + 1; i++) {
						Control c = controls.peek();
						if(c != null && c.getRegister() == i) {
							nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
							controls.pop();
						} else if (data.getRegisters().get(0) == i) {
							nodes.add(makeControlAt(i, column, true, getDispType(start, end, i)));
						} else if (data.getRegisters().get(1) == i) {
							nodes.add(makeControlAt(i, column, true, getDispType(start, end, i)));
						} else if (data.getRegisters().get(2) == i) {
							nodes.add(makeCNOTHead(i, column, getDispType(start, end, i)));
						} else {
							nodes.add(makeIdentityWithLineAt(i, column));
						}
					}
					
					break;
				default:
					for(int i = startBody; i < endBody + 1; i++)
						nodes.add(makeIdentityAt(i, column));
//					nodes.add(makeRegularGateBody(data, gm.getSymbol()));
					addRegularGateBody(nodes, data, gm.getSymbol());
					break;
				}
				
			
				
				
			} else {
				symbol = gm.getSymbol();
				for(int i = startBody; i < endBody + 1; i++)
					nodes.add(makeIdentityAt(i, column));
//				nodes.add(makeRegularGateBody(data, symbol));
				addRegularGateBody(nodes, data, symbol);
			}
			
			
			
			
			
			
			
			for(int i = endBody + 1; i < end; i++) {
				Control c = controls.peek();
				if(c != null && c.getRegister() == i) {
					nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
					controls.pop();
				} else {
					nodes.add(makeIdentityWithLineAt(i, column));
				}
			}
			
			
			
		}
		
		nodes.add(solderRegion);
		solderRegion.showTool(false);
		
		if(grid.isSelected())
			showGrid();
	}
	
	private int getDispType(int start, int end, int current) {
		if(current == start) return 1;
		else if (current == end) return -1;
		else return 0;
	}
	
	public void addRegularGateBody(ObservableList<Node> nodes, RawExportableGateData data, String symbol) {
		BorderPane bp = new BorderPane();
		
		bp.setMinSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);
		bp.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		bp.setMaxSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);
		
		bp.setStyle("-fx-background-color: #FFFFFF;\n "
				+ "-fx-border-color: #000000; \n"
				+ "-fx-border-width: 1; ");
		
		HBox box = new HBox();
		box.setAlignment(Pos.CENTER);
		Label l = new Label(symbol);
		l.setStyle("-fx-font-family: 'Vast Shadow'; \n" 
				 + "-fx-font-size: 20;");
		
		box.getChildren().add(l);
		
		ImmutableArray<String> paramLatex = data.getSolderedGate().getParameterSet().getLatexRepresentations();
		
		if(!paramLatex.isEmpty()) {
			String paramString = "\\( ( " + paramLatex.get(0);
			
			for(int i = 1; i < paramLatex.size(); i++)
				paramString += " , " + paramLatex.get(i);
			
			paramString += " ) \\)";
			LatexNode lv = new LatexNode(paramString, 1f, "#00000000", "#000000");
			box.getChildren().add(lv);
		}
		
		box.setPadding(new Insets(5));
		bp.setCenter(box);
		
		
		Hashtable<Integer, Integer> regs = data.getRegisters();
		
		if(regs.size() > 1) {
			Pane ap = new Pane();
			
//			
			Node start   = nodes.get(data.getGateRowBodyStart());
			for(int i = 0; i < regs.size(); i++) {
				int reg = regs.get(i);
				//int gridRow = reg - data.getGateRowBodyStart();
				Label label = new Label(Integer.toString(i));
				Node toMatch = nodes.get(reg);
				label.layoutYProperty().bind(toMatch.layoutYProperty().subtract(start.layoutYProperty()));
//				GridPane.setConstraints(label, 0, gridRow, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
				ap.getChildren().add(label);
			}
			bp.setLeft(ap);
			BorderPane.setMargin(ap, new Insets(GRID_SIZE / 4, 5, GRID_SIZE / 4, 5));
		}
		GridPane.setConstraints(bp, data.getColumn() + 1, data.getGateRowBodyStart(), 1, data.getGateRowBodyEnd() - data.getGateRowBodyStart() + 1);
		GridPane.setHalignment(bp, HPos.CENTER);
		GridPane.setVgrow(bp, Priority.ALWAYS);
		GridPane.setMargin(bp, new Insets(5, 5, 5, 5));
		
		
		
		nodes.add(bp);
	}
	
	
//	public Node makeRegularGateBody(RawExportableGateData data, String symbol) {
//		BorderPane bp = new BorderPane();
//		
//		bp.setMinSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);
//		bp.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
//		bp.setMaxSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);
//		
//		bp.setStyle("-fx-background-color: #FFFFFF;\n "
//				+ "-fx-border-color: #000000; \n"
//				+ "-fx-border-width: 1; ");
//		
//		HBox box = new HBox();
//		box.setAlignment(Pos.CENTER);
//		Label l = new Label(symbol);
//		l.setStyle("-fx-font-family: 'Vast Shadow'; \n" 
//				 + "-fx-font-size: 20;");
//		
//		box.getChildren().add(l);
//		
//		ImmutableArray<String> paramLatex = data.getSolderedGate().getParameterSet().getLatexRepresentations();
//		
//		if(!paramLatex.isEmpty()) {
//			String paramString = "\\( ( " + paramLatex.get(0);
//			
//			for(int i = 1; i < paramLatex.size(); i++)
//				paramString += " , " + paramLatex.get(i);
//			
//			paramString += " ) \\)";
//			LatexNode lv = new LatexNode(paramString, .6f, "#00000000", "#000000");
//			box.getChildren().add(lv);
//		}
//		
//		box.setPadding(new Insets(5));
//		bp.setCenter(box);
//		
//		
//		Hashtable<Integer, Integer> regs = data.getRegisters();
//		LinkedList<Integer> underNeaths = data.getUnderneathIdentityGates();
//		
//		if(regs.size() > 1) {
//			GridPane pane = new GridPane();
//			
//			for(int reg : underNeaths) {
//				int gridRow = reg - data.getGateRowBodyStart();
//				Region r = new Region();
//				GridPane.setConstraints(r, 0, gridRow, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//				pane.getChildren().add(r);
//			}
//			
//			for(int i = 0; i < regs.size(); i++) {
//				int reg = regs.get(i);
//				int gridRow = reg - data.getGateRowBodyStart();
//				Label label = new Label(Integer.toString(reg));
//				GridPane.setConstraints(label, 0, gridRow, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//				pane.getChildren().add(label);
//			}
//			
//			
//			bp.setLeft(pane);
//		}
//		GridPane.setConstraints(bp, data.getColumn() + 1, data.getGateRowBodyStart(), 1, data.getGateRowBodyEnd() - data.getGateRowBodyStart() + 1);
//		GridPane.setHalignment(bp, HPos.CENTER);
//		return bp;
//	}
	
	
	public Node makeMeasurementGate(int row, int column) {
		BorderPane r = new BorderPane();
		r.getStyleClass().add("cb_grid");
		
		r.setMinSize(AnchorPane.USE_PREF_SIZE, AnchorPane.USE_PREF_SIZE);
		r.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line l = new Line();
		
		r.getChildren().add(l);
		l.endXProperty().bind(r.widthProperty().subtract(1));
		l.startYProperty().bind(r.heightProperty().divide(2));
		l.endYProperty().bind(r.heightProperty().divide(2));
		
		ImageView view = new ImageView(GateIcon.MEASUREMENT);
		r.setCenter(view);
		
		GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
		
		setOnSolderHover(r, row, column);
		
		return r;
	}
	
	
	public Node makeIdentityAt(int row, int column) {
		AnchorPane r = new AnchorPane();
		r.getStyleClass().add("cb_grid");
		
		r.setMinSize(GRID_SIZE, GRID_SIZE);
		r.setPrefSize(GRID_SIZE, GRID_SIZE);
		r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line l = new Line();
		
		r.getChildren().add(l);
		l.endXProperty().bind(r.widthProperty().subtract(1));
		l.startYProperty().bind(r.heightProperty().divide(2));
		l.endYProperty().bind(r.heightProperty().divide(2));
		
		GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
		
		setOnSolderHover(r, row, column);
		
		return r;
	}
	
	public Node makeIdentityWithLineAt(int row, int column) {
		AnchorPane r = new AnchorPane();
		r.getStyleClass().add("cb_grid");
		
		r.setMinSize(GRID_SIZE, GRID_SIZE);
		r.setPrefSize(GRID_SIZE, GRID_SIZE);
		r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line l = new Line();
		
		r.getChildren().add(l);
		l.endXProperty().bind(r.widthProperty().subtract(1));
		l.startYProperty().bind(r.heightProperty().divide(2));
		l.endYProperty().bind(r.heightProperty().divide(2));

		Line v = new Line();
		r.getChildren().add(v);
		v.endYProperty().bind(r.heightProperty().subtract(1));
		v.startXProperty().bind(r.widthProperty().divide(2));
		v.endXProperty().bind(r.widthProperty().divide(2));
		
		
		GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
		
		setOnSolderHover(r, row, column);
		
		return r;
	}
	
	public Node makeSwapHead(int row, int column, int position) {
		AnchorPane r = new AnchorPane();
		r.getStyleClass().add("cb_grid");
		
		r.setMinSize(GRID_SIZE, GRID_SIZE);
		r.setPrefSize(GRID_SIZE, GRID_SIZE);
		r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line h = new Line();
		Line v = new Line();
		
		
		
		Line first = new Line();
		Line second = new Line();
		
		
		
		r.getChildren().add(h);
		h.endXProperty().bind(r.widthProperty().subtract(1));
		h.startYProperty().bind(r.heightProperty().divide(2));
		h.endYProperty().bind(r.heightProperty().divide(2));
		
		r.getChildren().add(v);
		
		if(position < 0) {
			v.endYProperty().bind(r.heightProperty().divide(2));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		} else if(position == 0) {
			v.endYProperty().bind(r.heightProperty().subtract(1));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		} else if(position > 0) {
			v.startYProperty().bind(r.heightProperty().divide(2));
			v.endYProperty().bind(r.heightProperty().subtract(1));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		}
		
		first.startXProperty().bind(r.widthProperty().divide(2).subtract(10));
		first.endXProperty().bind(r.widthProperty().divide(2).add(10));
		first.startYProperty().bind(r.heightProperty().divide(2).subtract(10));
		first.endYProperty().bind(r.heightProperty().divide(2).add(10));
		
		second.startXProperty().bind(r.widthProperty().divide(2).add(10));
		second.endXProperty().bind(r.widthProperty().divide(2).subtract(10));
		second.startYProperty().bind(r.heightProperty().divide(2).subtract(10));
		second.endYProperty().bind(r.heightProperty().divide(2).add(10));
		
		r.getChildren().add(first);
		r.getChildren().add(second);
		
		
		GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
		
		setOnSolderHover(r, row, column);
		
		return r;
	}
	
	public Node makeCNOTHead(int row, int column, int position) {
		AnchorPane r = new AnchorPane();
		r.getStyleClass().add("cb_grid");
		
		r.setMinSize(GRID_SIZE, GRID_SIZE);
		r.setPrefSize(GRID_SIZE, GRID_SIZE);
		r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line h = new Line();
		Line v = new Line();
		
		final int radius = 8;
		
		Circle c;
		
		c = new Circle(radius, Color.TRANSPARENT);
		
		c.setStroke(Color.BLACK);
		
		
		
		r.getChildren().add(h);
		h.endXProperty().bind(r.widthProperty().subtract(1));
		h.startYProperty().bind(r.heightProperty().divide(2));
		h.endYProperty().bind(r.heightProperty().divide(2));
		
		r.getChildren().add(v);
		
		if(position < 0) {
			v.endYProperty().bind(r.heightProperty().divide(2).add(radius));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		} else if(position == 0) {
			v.endYProperty().bind(r.heightProperty().subtract(1));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		} else if(position > 0) {
			v.startYProperty().bind(r.heightProperty().divide(2).subtract(radius));
			v.endYProperty().bind(r.heightProperty().subtract(1));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		}
		
		r.getChildren().add(c);
		c.centerXProperty().bind(r.widthProperty().divide(2));
		c.centerYProperty().bind(r.heightProperty().divide(2));
		
		
		GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
		

		setOnSolderHover(r, row, column);
		
		return r;
	}
	
	
	public Node makeControlAt(int row, int column, boolean controlType, int position) {
		AnchorPane r = new AnchorPane();
		r.getStyleClass().add("cb_grid");
		
		r.setMinSize(GRID_SIZE, GRID_SIZE);
		r.setPrefSize(GRID_SIZE, GRID_SIZE);
		r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line h = new Line();
		Line v = new Line();
		
		
		
		Circle c;
		
		if(controlType) 
			c = new Circle(3, Color.BLACK);
		else
			c = new Circle(3, Color.WHITE);
		
		c.setStroke(Color.BLACK);
		
		
		
		r.getChildren().add(h);
		h.endXProperty().bind(r.widthProperty().subtract(1));
		h.startYProperty().bind(r.heightProperty().divide(2));
		h.endYProperty().bind(r.heightProperty().divide(2));
		
		r.getChildren().add(v);
		
		if(position < 0) {
			v.endYProperty().bind(r.heightProperty().divide(2));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		} else if(position == 0) {
			v.endYProperty().bind(r.heightProperty().subtract(1));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		} else if(position > 0) {
			v.startYProperty().bind(r.heightProperty().divide(2));
			v.endYProperty().bind(r.heightProperty().subtract(1));
			v.startXProperty().bind(r.widthProperty().divide(2));
			v.endXProperty().bind(r.widthProperty().divide(2));
		}
		
		r.getChildren().add(c);
		c.centerXProperty().bind(r.widthProperty().divide(2));
		c.centerYProperty().bind(r.heightProperty().divide(2));
		
		
		
		GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
		
		setOnSolderHover(r, row, column);
		
		return r;
	}
	
	
	private void setOnSolderHover(Node n, int row, int column) {
		
		SolderMouseHover eventHandler = new SolderMouseHover(row, column);
		n.setOnMouseEntered(eventHandler);
	}
	
	
	
	
	
	
	private class SolderMouseHover implements EventHandler<MouseEvent> {

		private final int row, column;
		
		public SolderMouseHover(int row, int column) {
			this.row = row;
			this.column = column;
		}
			
		@Override
		public void handle(MouseEvent event) {
			MainScene ms = AppStatus.get().getMainScene();
			if(ms.isSolderButtonSelected()) {
				if(!solderRegion.isManaged())
					solderRegion.showTool(true);
				GridPane.setConstraints(solderRegion, column + 1, row);
			}
		}
	}
	
	
}
