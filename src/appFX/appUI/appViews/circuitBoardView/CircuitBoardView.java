package appFX.appUI.appViews.circuitBoardView;

import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;

import appFX.appUI.GateIcon;
import appFX.appUI.LatexNode;
import appFX.appUI.appViews.AppView;
import appFX.appUI.appViews.AppView.ViewListener;
import appFX.appUI.appViews.gateChooser.AbstractGateChooser;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
import appFX.framework.solderedGates.SolderedGate;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import utils.customCollections.ImmutableArray;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;


public class CircuitBoardView extends AppView implements Initializable, ViewListener, EventHandler<MouseEvent> {
	
	private static final int GRID_SIZE = 50;
	
	public ScrollPane description, container;
	public GridPane circuitBoardPane;
	public TextField name, symbol;
	public HBox parameters;
	public CheckBox grid;
	
	private final CircuitBoardModel circuitBoard;
	private final Project project;
	private boolean initialized = false;
	private final SelectCursor cursor;
	private Node[] topNodes;
	
	public static void openCircuitBoard(String circuitBoardName) {
		AppStatus status = AppStatus.get();
		if(!status.getMainScene().containsView(circuitBoardName, Layout.CENTER)) {
			CircuitBoardView circuitBoardView = new CircuitBoardView(status.getFocusedProject(), circuitBoardName);
			status.getMainScene().addView(circuitBoardView);
		}
	}
	
	private CircuitBoardView(Project project, String circuitBoard) {
		super("CircuitBoardView.fxml", circuitBoard, Layout.CENTER);
		this.circuitBoard = (CircuitBoardModel) project.getCircuitBoardModels().get(circuitBoard);
		this.project = project;
		this.circuitBoard.setRenderEventHandler(new CircuitBoardEventHandler());
		this.cursor = new SelectCursor(this);
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
			cursor.hideCursor();
		});
		
		circuitBoardPane.setOnMouseEntered((e) -> {
			cursor.showTool();
		});
		
		
		circuitBoardPane.addEventFilter(MouseEvent.MOUSE_MOVED, this);

		AppStatus.get().getMainScene().addToolButtonListener(cursor.getToolChangedListener());
		AbstractGateChooser.addToggleListener(cursor.getModelChangedListener());
		
		rerenderCircuitBoard();
	}
	
	public CircuitBoardModel getCircuitBoardModel() {
		return circuitBoard;
	}
	
	
	@Override
	public void viewChanged(boolean wasAdded) {
		if(!wasAdded) {
			AppStatus.get().getMainScene().removeToolButtonListener(cursor.getToolChangedListener());
			AbstractGateChooser.removeToggleListener(cursor.getModelChangedListener());
			removeEventListener();
		}
	}
	
	private class CircuitBoardEventHandler implements ReceivedEvent {
		
		@Override
		public boolean receive(Object source, String methodName, Object... args) {
			cursor.getCurrentTool().reset();
			rerenderCircuitBoard();
			return false;
		}
	}
	
	
	
//	Render Methods:
	
	
	public void rerenderCircuitBoard() {
		
		
		
		ObservableList<Node> nodes = circuitBoardPane.getChildren();
		nodes.clear();
		ObservableList<RowConstraints> rows = circuitBoardPane.getRowConstraints();
		topNodes = new Node[circuitBoard.getColumns()];
		
		for(int i = 0; i < circuitBoard.getRows(); i++) {
			LatexNode ln = new LatexNode("\\( \\lvert \\psi_{" + i +  "}\\rangle \\)");
			ln.setPadding(new Insets(0, 5, 0, 5));
			BorderPane bp = new BorderPane();
			bp.setCenter(ln);
			GridPane.setConstraints(bp, 0, i, 1, 1, HPos.CENTER, VPos.CENTER);
			circuitBoardPane.getChildren().add(bp);			
			
			rows.add(new RowConstraints(GridPane.USE_PREF_SIZE, GridPane.USE_COMPUTED_SIZE, GridPane.USE_PREF_SIZE));
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
				topNodes[column] = n;
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
				drawBehindIdentiyGates(nodes, column, start, startBody, endBody, end);
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
					topNodes[column] = nodes.get(nodes.size() - 1);
					
					break;
				case MEASUREMENT:
					
					nodes.add(makeMeasurementGate(startBody, column));
					topNodes[column] = nodes.get(nodes.size() - 1);
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
					topNodes[column] = nodes.get(nodes.size() - 1);
					
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
					topNodes[column] = nodes.get(nodes.size() - 1);
					break;
				default:
					drawBehindIdentiyGates(nodes, column, start, startBody, endBody, end);
					addRegularGateBody(nodes, data, gm.getSymbol());
					break;
				}
				
				
			} else {
				symbol = gm.getSymbol();
				drawBehindIdentiyGates(nodes, column, start, startBody, endBody, end);
				addRegularGateBody(nodes, data, symbol);
			}
			
			
			
			for(int i = endBody + 1; i < end + 1; i++) {
				Control c = controls.peek();
				if(c != null && c.getRegister() == i) {
					nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
					controls.pop();
				} else {
					nodes.add(makeIdentityWithLineAt(i, column));
				}
			}
		}			
		
		cursor.addToNodeList(nodes);
		
		if(grid.isSelected())
			showGrid();
		
		
	}
	
	
	private void drawBehindIdentiyGates(ObservableList<Node> nodes, int column, int start, int startBody, int endBody, int end) {
		int controlTop = startBody - start > 0 ? -1 : 0;
		int controlBot = end - endBody > 0 ? 1 : 0;
		if(endBody - startBody == 0) {
			int value = controlTop == -1 && controlBot == 1 ? -2 : controlTop + controlBot;
			nodes.add(makeIdentityWithControlOut(startBody, column, value));
		} else {
			nodes.add(makeIdentityWithControlOut(startBody, column, controlTop));
			for(int i = startBody + 1; i < endBody; i++)
				nodes.add(makeIdentityAt(i, column));
			nodes.add(makeIdentityWithControlOut(endBody, column, controlBot));
		}
		topNodes[column] = nodes.get(nodes.size() - 1);
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
			
			LinkedList<Control> controls = data.getControls();
			int bodyEnd = data.getGateRowBodyEnd();
			Control c = controls.peek();
			while(c != null && c.getRegister() < bodyEnd) {
				controls.pop();
				
				Circle circ;
				
				if(c.getControlStatus() == Control.CONTROL_TRUE) 
					circ = new Circle(3, Color.BLACK);
				else
					circ = new Circle(3, Color.WHITE);
				circ.setStroke(Color.BLACK);
				
				Node toMatch = nodes.get(c.getRegister());
				circ.centerYProperty().bind(toMatch.layoutYProperty().subtract(start.layoutYProperty()).add(6));
				circ.setCenterX(5);
				
				ap.getChildren().add(circ);
				c = controls.peek();
			}
		}
		
		GridPane.setConstraints(bp, data.getColumn() + 1, data.getGateRowBodyStart(), 1, data.getGateRowBodyEnd() - data.getGateRowBodyStart() + 1);
		GridPane.setHalignment(bp, HPos.CENTER);
		GridPane.setVgrow(bp, Priority.ALWAYS);
//		GridPane.setMargin(bp, new Insets(5, 5, 5, 5));
		
		
		
		nodes.add(bp);
	}
	
	
	
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
		
		return r;
	}
	
	public Node makeIdentityWithControlOut(int row, int column, int type) {
		if(type == 0)
			return makeIdentityAt(row, column);
		else if(type == -2) 
			return makeIdentityWithLineAt(row, column);
		else {
			AnchorPane r = new AnchorPane();
			r.getStyleClass().add("cb_grid");
			
			r.setMinSize(GRID_SIZE, GRID_SIZE);
			r.setPrefSize(GRID_SIZE, GRID_SIZE);
			r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
			
			Line h = new Line();
			Line v = new Line();
			
			r.getChildren().add(h);
			h.endXProperty().bind(r.widthProperty().subtract(1));
			h.startYProperty().bind(r.heightProperty().divide(2));
			h.endYProperty().bind(r.heightProperty().divide(2));
			
			r.getChildren().add(v);
			
			if(type < 0) {
				v.endYProperty().bind(r.heightProperty().divide(2));
				v.startXProperty().bind(r.widthProperty().divide(2));
				v.endXProperty().bind(r.widthProperty().divide(2));
			} else if(type > 0) {
				v.startYProperty().bind(r.heightProperty().divide(2));
				v.endYProperty().bind(r.heightProperty().subtract(1));
				v.startXProperty().bind(r.widthProperty().divide(2));
				v.endXProperty().bind(r.widthProperty().divide(2));
			}
			
			
			GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
			
			return r;
		}
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
		
		return r;
	}
	

	public Project getProject() {
		return project;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
			int row = 0, column = 0;
			
			for(int i = 0; i < circuitBoard.getRows(); i++) {
				Parent p = (Parent) circuitBoardPane.getChildren().get(i);
				if(event.getY() > p.getLayoutY())
					row = i;
				else break; 
			}
			
			for(int i = 0; i < circuitBoard.getColumns(); i++) {
				Parent p = (Parent) topNodes[i];
				if(event.getX() > p.getLayoutX())
					column = i;
				else break; 
			}
			
			if(cursor.getColumn() != column)
				cursor.setColumn(column);
			if(cursor.getRow() != row)
				cursor.setRow(row);
		}
	}

	
	
}
