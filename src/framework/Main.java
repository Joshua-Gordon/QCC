package framework;

import java.util.ArrayList;
import java.util.Random;

import Simulator.*;
import appPreferencesFX.AppPreferences;
import appUI.Window;
import appUIFX.AppFileIO;
import appUIFX.MainScene;
import framework2FX.AppStatus;
import framework2FX.Project;
import javafx.application.Application;
import javafx.stage.Stage;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.Vector;
import mathLib.compile.UnitaryDecomp;
import utils.customCollections.CollectionUtils;



public class Main extends Application implements AppPreferences {
	
	private static Window window;
	
    public static void main(String[] args) {
    	/* toggle flags: debug mode or not */
    	boolean normalMode = false;
    	
    	boolean javaFX_GUI = false;
    	
    	boolean debugMode = false;
    	boolean debugSimulatorMode = false;
    	boolean debugMixedStateMode = true;
    	
    	if ( normalMode ) {
    		if( javaFX_GUI ) {
    			launch(args);
    		} else {
	        	DefaultGate.loadGates();
	    		window = new Window();
	    		window.setVisible(true);
    		}
    	}
    	
    	if ( debugMode ) {
    		
    	}
    	
    	if ( debugSimulatorMode ) {
    		ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(DefaultGate.LangType.QASM,"res//test2.qasm");
			Main.getWindow().getSelectedBoard().setGates(gates);
			for(int i = 0; i < 10; ++i) {
				int result = InternalExecutor.simulate(window.getSelectedBoard());
				System.out.println(result);
			}
		}

		if(debugMixedStateMode) {
			Vector<Complex> v = UnitaryDecomp.getRandomTwoVector(); //Reusing code
			Matrix<Complex> vm = v.outerProduct(v); //You should replace this matrix and the other one with the kraus operators you actually want
			Random r = new Random();
			vm = vm.mult(new Complex(r.nextDouble(),r.nextDouble())); //Random scaling
			Matrix<Complex> wm = Matrix.identity(Complex.ZERO(),vm.getRows()).sub(vm); //Make it sum to identity
			Operation o = new Operation(vm,wm); //Create the operation
			POVM measure = new POVM(o);			//and the POVM wrapping it
			Vector<Complex> c = Qubit.ZERO();
			Matrix<Complex> test = c.outerProduct(Qubit.ONE()); //Create density matrix for input, should be |0><1|
			Matrix<Complex> res = measure.measure(test); //Run POVM
			System.out.println(res);
		}
    }
    
    
    public static Window getWindow() {
    	return window;
    }
	

    @Override
	public void start(Stage primaryStage) throws Exception {
    	MainScene mainScene = new MainScene();
    	AppStatus.initiateAppStatus(primaryStage, mainScene);
    	
    	mainScene.loadNewScene(primaryStage, 1000, 600);
    	primaryStage.setTitle("QuaCC");
    	primaryStage.show();
    	
    	loadProject();
	}
    
    
    
    private void loadProject() {
    	Project project = AppFileIO.loadPreviouslyClosedProject();
    	if(project == null)
    		project = Project.createNewTemplateProject();
    	AppStatus.get().setFocusedProject(project);
    }
    
    
    /*
     * Note, the following code is needed to run the output program
     * from pyquil.parser import parse_program
     * from pyquil.api import QVMConnection
     * qvm = QVMConnection()
     * p = parse_program("whatever this java code outputs")
     * qvm.wavefunction(p).amplitudes
     */

    /*
     * Alternatively, to use the QASM output, this code will work:
     * import qiskit
     * qp = qiskit.QuantumProgram()
     * name = "test"
     * qp.load_qasm_file("test.qasm",name=name)
     * ret = qp.execute([name])
     * print(ret.get_counts(name))
     */

}
