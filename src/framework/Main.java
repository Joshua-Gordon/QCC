package framework;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import Simulator.InternalExecutor;
import Simulator.MixedState;
import Simulator.Qubit;
import appPreferencesFX.AppPreferences;
import appUI.Window;
import appUIFX.AppFileIO;
import appUIFX.MainScene;
import framework2FX.AppStatus;
import framework2FX.Project;
import javafx.application.Application;
import javafx.stage.Stage;

import mathLib.*;
import mathLib.Vector;
import testLib.*;
import testLib.BaseGraph;
import java.util.*;
import java.io.*;


public class Main extends Application implements AppPreferences {
	
	private static Window window;
	
    public static void main(String[] args) {
    	/* toggle flags: debug mode or not */
    	boolean normalMode = true;

    	boolean javaFX_GUI = true;
    	
    	boolean debugMode = false;
    	boolean debugSimulatorMode = false;
    	
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
    		ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(DefaultGate.LangType.QUIL,"res/test.quil");
    		window.getSelectedBoard().setGates(gates);
    		CircuitBoard cb = window.getSelectedBoard();
    		System.out.println(cb.getRows());
    		MixedState ms = InternalExecutor.createMixedState(cb);
    		System.out.println(ms.getDensityMatrix());
    	}
    	
    	if ( debugSimulatorMode ) {
    		ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(DefaultGate.LangType.QASM,"res//test2.qasm");
			Main.getWindow().getSelectedBoard().setGates(gates);
			for(int i = 0; i < 10; ++i) {
				int result = InternalExecutor.simulate(window.getSelectedBoard());
				System.out.println(result);
			}
		}
    }
    
    
    
    
    
    
    
    
    public static Window getWindow() {
    	return window;
    }
	

    @Override
	public void start(Stage primaryStage) throws Exception {    	
    	MainScene mainScene = new MainScene();
    	AppStatus.setAppStatus(primaryStage, mainScene);
    	
    	mainScene.loadNewScene(primaryStage, 1000, 600);
    	primaryStage.setTitle("QCC");
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
