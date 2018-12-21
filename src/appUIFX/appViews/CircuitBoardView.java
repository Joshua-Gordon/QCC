package appUIFX.appViews;

import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ResourceBundle;

import appUIFX.GateIcon;
import appUIFX.LatexNode;
import framework2FX.AppStatus;
import framework2FX.Project;
import framework2FX.exportGates.Control;
import framework2FX.exportGates.RawExportableGateData;
import framework2FX.gateModels.CircuitBoard;
import framework2FX.gateModels.GateModel;
import framework2FX.gateModels.GateModelFactory.PresetGateModel;
import framework2FX.solderedGates.SolderedGate;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class CircuitBoardView extends AppView implements Initializable{
	
	public static final int REFRESH_CANVAS_UNIT = 200;
	
	public ScrollPane container;
	public GridPane circuitBoardPane;
	
	private String circuitBoard;
	
	
	public static void openCircuitBoard(String circuitBoardName) {
		CircuitBoardView cbv = new CircuitBoardView(circuitBoardName);
		AppStatus.get().getMainScene().addView(cbv);
	}
	
	
	private CircuitBoardView(String circuitBoardName) {
		super("CircuitBoardView.fxml", circuitBoardName, Layout.CENTER);
		this.circuitBoard = circuitBoardName;
	}
	
	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		
		
		return false;
	}
	
	public void showCircuitBoard(String circuitBoardName) {
		Project p = AppStatus.get().getFocusedProject();
		
		CircuitBoard cb = (CircuitBoard) p.getGateModel(circuitBoardName);
		
		ObservableList<Node> nodes = circuitBoardPane.getChildren();
		
		nodes.clear();
		
		for(int i = 0; i < cb.getRows(); i++) {
			LatexNode ln = new LatexNode("\\( \\lvert \\psi_" + i +  "\\rangle \\)");
			GridPane.setConstraints(ln, 0, i, 1, 1, HPos.CENTER, VPos.CENTER);
			ln.setPadding(new Insets(0, 5, 0, 5));
			
			circuitBoardPane.getChildren().add(ln);
		}
		
		
		
		
		for(RawExportableGateData data : cb) {
			SolderedGate sg = data.getSolderedGate();
			
			int column = data.getColumn();
			
			if(sg.isIdentity()) {
				Hashtable<Integer, Integer> regs = data.getRegisters();
				nodes.add(makeIdentityAt(regs.get(0), column));
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
			
			
			
			
			
			
			
			GateModel gm = p.getGateModel(sg.getGateModelFormalName());
			
			String symbol;
			if(gm == null) {
				symbol = sg.getGateModelFormalName();
				nodes.add(makeRegularGateBody(data, symbol));
			} else if(gm.isPreset()) {
				symbol = gm.getName();
				PresetGateModel pgm = (PresetGateModel) gm;
				
				switch(pgm.getPresetGateType()) {
				case CNOT:
					
					for(int i = startBody; i < endBody; i++) {
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
					
					for(int i = startBody; i < endBody; i++) {
						Control c = controls.peek();
						if(c != null && c.getRegister() == i) {
							nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
							controls.pop();
						} else if (data.getRegisters().get(0) == i) {
							nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
						} else if (data.getRegisters().get(1) == i) {
							nodes.add(makeSwapHead(i, column, getDispType(start, end, i)));
						} else {
							nodes.add(makeIdentityWithLineAt(i, column));
						}
					}
					
					
					break;
				case TOFFOLI:
					
					for(int i = startBody; i < endBody; i++) {
						Control c = controls.peek();
						if(c != null && c.getRegister() == i) {
							nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
							controls.pop();
						} else if (data.getRegisters().get(0) == i) {
							nodes.add(makeControlAt(i, column, true, getDispType(start, end, i)));
						} else if (data.getRegisters().get(1) == i) {
							nodes.add(makeSwapHead(i, column, getDispType(start, end, i)));
						} else if (data.getRegisters().get(2) == i) {
							nodes.add(makeSwapHead(i, column, getDispType(start, end, i)));
						} else {
							nodes.add(makeIdentityWithLineAt(i, column));
						}
					}
					
					break;
				default:
					nodes.add(makeRegularGateBody(data, symbol));
					break;
				}
				
			
				
				
			} else {
				symbol = gm.getName();
				nodes.add(makeRegularGateBody(data, symbol));
			}
			
			
			
			
			
			
			
			for(int i = endBody; i < end; i++) {
				Control c = controls.peek();
				if(c != null && c.getRegister() == i) {
					nodes.add(makeControlAt(i, column, c.getControlStatus(), getDispType(start, end, i)));
					controls.pop();
				} else {
					nodes.add(makeIdentityWithLineAt(i, column));
				}
			}
			
			
			
		}
		
		
	}
	
	private int getDispType(int start, int end, int current) {
		if(current == start) return 1;
		else if (current == end) return -1;
		else return 0;
	}
	
	
	private Node makeRegularGateBody(RawExportableGateData data, String symbol) {
		return null;
	}
	
	
	private Node makeMeasurementGate(int row, int column) {
		BorderPane r = new BorderPane();
		
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
	
	
	private Node makeIdentityAt(int row, int column) {
		AnchorPane r = new AnchorPane();
		
		r.setMinSize(40, 40);
		r.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		r.setMaxSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
		
		Line l = new Line();
		
		r.getChildren().add(l);
		l.endXProperty().bind(r.widthProperty().subtract(1));
		l.startYProperty().bind(r.heightProperty().divide(2));
		l.endYProperty().bind(r.heightProperty().divide(2));
		
		GridPane.setConstraints(r, column + 1, row, 1, 1, HPos.CENTER, VPos.CENTER);
		return r;
	}
	
	private Node makeIdentityWithLineAt(int row, int column) {
		AnchorPane r = new AnchorPane();
		
		r.setMinSize(40, 40);
		r.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
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
	
	private Node makeSwapHead(int row, int column, int position) {
		AnchorPane r = new AnchorPane();
		
		r.setMinSize(40, 40);
		r.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
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
	
	private Node makeCNOTHead(int row, int column, int position) {
		AnchorPane r = new AnchorPane();
		
		r.setMinSize(40, 40);
		r.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
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
	
	
	private Node makeControlAt(int row, int column, boolean controlType, int position) {
		AnchorPane r = new AnchorPane();
		
		r.setMinSize(40, 40);
		r.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
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
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showCircuitBoard(circuitBoard);
	}

}
