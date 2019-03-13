package appSW.appUI;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import appSW.framework.CircuitBoard;
import appSW.framework.DefaultGate;
import appSW.framework.SolderedRegister;
import appSW.framework.Translator;
import appSW.preferences.AppPreferences;
import utils.AppDialogs;

/**
 * Methods that include exporting and importing files for a {@link CircuitBoard} project should 
 * all be included in this class
 * 
 * @author quantumresearch
 *
 */
public class CircuitBoardSelector{
	
    public static final String UNSAVED_FILE_NAME = "Untitled";
	public static final String CIRCUIT_BOARD_EXT;
	public static final String CIRCUIT_BOARD_DES;
	public static final String PNG_EXT;
	public static final String PNG_DES;
	
	private Window window;
	
	static {
		PNG_EXT = ".png";
		PNG_DES = "Portable Network Graphic PNG (*" + PNG_EXT + ")";
		CIRCUIT_BOARD_EXT = ".qcir";
		CIRCUIT_BOARD_DES = "Quantum Circuit Boards (*" + CIRCUIT_BOARD_EXT + ")";
	}
	
	
	
	/**
	 * Creates a File Selector Graphical Interface 
	 * @param window 
	 * Sets the JFrame of this window to "modal" when using FileSelector methods that use JDialogs
	 */
	public CircuitBoardSelector(Window window) {
		this.window = window;
	}
	
	/**
	 * Using The Applications Preferences, The last CircuitBoard (When this application was last closed)<br>
	 * worked on will be returned through this method.<br>
	 * <p>
	 * If no previous CircuitBoard has been worked on, or the CircuitBoard that was last opened<br>
	 * has been refactored / deleted, then a new "Default" CircuitBoard will be returned.
	 * @return
	 * Loaded CircuitBoard
	 */
	public CircuitBoard loadPreviousCircuitBoard() {
    	CircuitBoard board = null;
    	String url = AppPreferences.get("File IO", "Previous File Location");
        File file = new File(url);
        if(url != "" && file.exists()) {
        	board = openFile(file);
        	if(board == null)
        		return CircuitBoard.getDefaultCircuitBoard();
        }else {
        	return CircuitBoard.getDefaultCircuitBoard();
        }
        return board;
    }
	
	/**
	 * Some attributes of CircuitBoard are transient (not stored when serialized).<br>
	 * This method seeks to recreate all lost transient attributes.<br>
	 * This is used after a CircuitBoard has been un-serialized.
	 * 
	 * @param cb
	 * CircuitBoard (cannot be null)
	 */
	private static void prepareBoard(CircuitBoard cb) {
    	for(int i = 0; i < cb.getCustomGates().size(); i++)
    		cb.getCustomGates().getElementAt(i).loadIcon();
    	for(int i = 0; i < cb.getCustomOracles().size(); i++)
    		cb.getCustomOracles().getElementAt(i).loadIcon();
    }
	
	
	/**
	 * Creates a prompt to export the specified CircuitBoard.
	 * The prompt opens up a FileChooser that shows the "focusedDirectory" first
	 * @param cb 
	 * The circuitBoard to be saved
	 * @param focusedDirectory 
	 * The focused Directory upon opening (can be set to null to open up the users home directory)
	 */
	public void exportPNG(CircuitBoard cb, File focusedDirectory){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new PNGBoardFilter());
		fileChooser.setDialogTitle("Export To PNG");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(focusedDirectory);
		
		final int option1 = fileChooser.showDialog(window.getFrame(), "Export");
		
		if(option1 == JFileChooser.APPROVE_OPTION) {
			File choosenFile = fileChooser.getSelectedFile();
			if(choosenFile.exists() && choosenFile.isDirectory()) {
				AppDialogs.fileIsntValid(window.getFrame(), choosenFile);
				exportPNG(cb, choosenFile.getParentFile());
			}else if(!choosenFile.getName().endsWith(PNG_EXT)) {
				AppDialogs.fileExtIsntValid(window.getFrame(), choosenFile, PNG_EXT);
				exportPNG(cb, choosenFile.getParentFile());
			}else {
				int option2 = 0;
				if(choosenFile.exists()) {
					option2 = AppDialogs.fileReplacePrompt(window.getFrame(), choosenFile);
				}
				if(option2 == 0) {
					BufferedImage picture = window.getRenderContext().renderBaseImage(false);
					try {
						ImageIO.write(picture, "png", choosenFile);
					} catch (IOException e) {
						AppDialogs.couldNotExport(window.getFrame());
						e.printStackTrace();
					}
				}else {
					exportPNG(cb, choosenFile.getParentFile());
				}
			}
		}
	}
	
//	returns "0" if operation is canceled, "1" if continued with or without saving, "2" if file needs to be saved
	
	/**
	 * Checks if the specified CircuitBoard has been edited after last save. <br>
	 * If no changes have been made since then, the method will return 1. <br>
	 * If changes have been made, a "Continue without saving" Prompt opens up. <br>
	 * If the user selects the "Continue without Save" option, the prompt disposes and the method<br>
	 * will return 1. <br>
	 * If the user selects the "Save" option, the prompt disposes and the method will return 2. <br>
	 * If the user selects any "Cancel" option, the prompt disposes and the method will return 0.
	 * <p>
	 * This method is primarily used when some sort of action upon the specified circuitBoard could <br>
	 * compromised cached changes of the CircuitBoard before stored to the hard drive. <br>
	 * <p>
	 * When the method returns 0, the operations in play after this method is called should be canceled <br>
	 * to avoid reasonable doubt of the users intentions <br>
	 * 
	 * When the method return 1, the operations in play after this method has been called should continue<br>
	 * normally <br>
	 * 
	 * When the method returns 2, the operations in play after this method has been called should continue<br>
	 * <b>After</b> the "save" method has been called and return true
	 * 
	 * 
	 * @param cb 
	 * The CircuitBoard to be checked
	 * @return int
	 */
	public int warnIfBoardIsEdited(CircuitBoard cb) {
		int option = 1;
		if(cb.hasBeenEdited()) {
			String notSavedFileName;
			if(cb.getFileLocation() != null)
				notSavedFileName = new File(cb.getFileLocation()).getName();
			else
				notSavedFileName = UNSAVED_FILE_NAME;
			option += AppDialogs.continueWithoutSaving(window.getFrame(), notSavedFileName);
		}
		return option;
	}
	
	
	/**
	 * Creates a new untitled CircuitBoard<br>
	 * Will create a prompt "Continue without saving" to user if previousBoard<br>
	 * has not been saved
	 * <p>
	 * If prompt is canceled, previousBoard is returned
	 * <p>
	 * If previousBoard is null, a new untitled CircuitBoard will return without prompt
	 * 
	 * @param previousBoard 
	 * The Previous Board to be checked
	 * @return CircuitBoard
	 */
	public CircuitBoard createNewBoard(CircuitBoard previousBoard) {
		CircuitBoard tempBoard = previousBoard;
		
		final int option = previousBoard == null? 1 : warnIfBoardIsEdited(previousBoard);
	   	
		if(option > 0) {
	   		boolean followThrough = true;
	   		if(option == 2)
	   			followThrough = saveBoard(previousBoard);
	   		if(followThrough)
	   			tempBoard = CircuitBoard.getDefaultCircuitBoard();
	   	}
    	return tempBoard;
	}
	
	/**
	 * Creates an user interface to select a CircuitBoard from the file system.<br>
	 * Prompts the user "Continue without saving" if the previousBoard needs to be saved<br>
	 * before opening user interface.<br>
	 * If prompt is canceled or no CircuitBoard has been selected, then the previousBoard will<br>
	 * be returned<br>
	 * <p>
	 * If previousBoard is null, then method will skip "Continue without saving" prompt.<br>
	 * However (if previousBoard is null), if user chooses no CircuitBoard to be selected,<br>
	 * then the method will return null
	 * 
	 * @param previousBoard
	 * Previous Board to be checked
	 * @return
	 * Selected CircuitBoard
	 */
	public CircuitBoard selectBoardFromFileSystem(CircuitBoard previousBoard) {
		CircuitBoard tempBoard = previousBoard;
		final int option = previousBoard == null? 1 : warnIfBoardIsEdited(previousBoard);
		if(option > 0) {
			boolean followThrough = true;
			if(option == 2) 
				followThrough = saveBoard(previousBoard);
			if(followThrough) {
				try{
					tempBoard = open(previousBoard, null);
				}catch(IOException e) {
					AppDialogs.errorIO(window.getFrame());
					AppDialogs.couldNotOpenFile(window.getFrame());
					e.printStackTrace();
				}catch(ClassNotFoundException e) {
					AppDialogs.errorProg(window.getFrame());
					AppDialogs.couldNotOpenFile(window.getFrame());
					e.printStackTrace();
				}
			}
		}
		return tempBoard;
	}
	
	/**
	 * 
	 * Saves Specified CircuitBoard to FileSystem by opening up a FileChooser User Interface
	 * 
	 * @param cb 
	 * CircuitBoard to be saved
	 * @return
	 * Whether or not the specified CircuitBoard has been successfully saved
	 */
	public boolean saveBoardToFileSystem(CircuitBoard cb) {
		URI location = null;
		try {
			location = saveAs(cb, null);
			if(location != null)
				cb.setFileLocation(location);
		} catch (IOException e) {
			AppDialogs.errorIO(window.getFrame());
			AppDialogs.couldNotSaveFile(window.getFrame());
			e.printStackTrace();
		}
		return location != null;
	}
	
	
	/**
	 * Overrides CircuitBoard's last saved File with a new save.<br>
	 * If the CircuitBoard hasn't been saved previously, then the saveBoardToFileSystem()<br>
	 * Method will be called on the specified CircuitBoard.
	 * 
	 * 
	 * @param cb
	 * CircuitBoard to be saved
	 * @return
	 * Whether or not the specified CircuitBoard has been successfully saved
	 */
	public boolean saveBoard(CircuitBoard cb) {
		if(cb.getFileLocation() == null) {
			return saveBoardToFileSystem(cb);
		}else {
			try {
				save(cb);
				return true;
			} catch (IOException e) {
				AppDialogs.errorIO(window.getFrame());
				AppDialogs.couldNotSaveFile(window.getFrame());
				e.printStackTrace();
			}
			return false;
		}
	}
	
	/**
	 * This method is use to programmically choose a Saved CircuitBoard file from the FileSystem.<br>
	 * If file doesn't exist or isn't a CircuitBoard file, then null is returned
	 * @param circuitBoardFile
	 * File of the CircuitBoard
	 * @return
	 * The CircuitBoard received from the file system
	 */
	public CircuitBoard openFile(File circuitBoardFile) {
		CircuitBoard board = null;
		try {
			FileInputStream fis = new FileInputStream(circuitBoardFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			board = (CircuitBoard) ois.readObject();
			fis.close();
			board.setFileLocation(circuitBoardFile.toURI());
        	prepareBoard(board);
		}catch(IOException e) {
			AppDialogs.errorIO(window.getFrame());
			AppDialogs.couldNotOpenFile(window.getFrame());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			AppDialogs.errorProg(window.getFrame());
			AppDialogs.couldNotOpenFile(window.getFrame());
			e.printStackTrace();
		}
		return board;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Opens a prompt that allows the user to select a CircuitBoard file from a file system
	 * @param previousBoard
	 * @param focusedDirectory
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private CircuitBoard open(CircuitBoard previousBoard, File focusedDirectory) throws IOException, ClassNotFoundException {
		CircuitBoard fetchedBoard =  previousBoard;
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new CircuitBoardFilter());
		fileChooser.setDialogTitle("Select Circuit Board File");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(focusedDirectory);
		
		final int option = fileChooser.showDialog(window.getFrame(), "Open");
		
		if(option == JFileChooser.APPROVE_OPTION) {
			File choosenFile = fileChooser.getSelectedFile();
			if(choosenFile.exists() && fileChooser.accept(choosenFile)) {
				FileInputStream fis = new FileInputStream(choosenFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				fetchedBoard = (CircuitBoard) ois.readObject();
				fis.close();
				
				fetchedBoard.setFileLocation(choosenFile.toURI());
				prepareBoard(fetchedBoard);
			}else {
				AppDialogs.fileIsntValid(window.getFrame(), choosenFile);
				fetchedBoard = open(previousBoard, choosenFile.getParentFile());
			}
		}
		
		return fetchedBoard;
	}
	
	
	
	
	/**
	 * Opens a Prompt that allows the user to save a CircuitBoard File
	 * @param boardToSave
	 * @param focusedDirectory
	 * @return
	 * @throws IOException
	 */
	private URI saveAs(CircuitBoard boardToSave, File focusedDirectory) throws IOException {
		URI fetchedURI = null;
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new CircuitBoardFilter());
		fileChooser.setDialogTitle("Save Circuit Board File");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(focusedDirectory);
		
		final int option1 = fileChooser.showDialog(window.getFrame(), "Save");
		
		if(option1 == JFileChooser.APPROVE_OPTION) {
			File choosenFile = fileChooser.getSelectedFile();
			if(choosenFile.exists() && choosenFile.isDirectory()) {
				AppDialogs.fileIsntValid(window.getFrame(), choosenFile);
				fetchedURI = saveAs(boardToSave, choosenFile.getParentFile());
			}else if(!choosenFile.getName().endsWith(CIRCUIT_BOARD_EXT)) {
				AppDialogs.fileExtIsntValid(window.getFrame(), choosenFile, CIRCUIT_BOARD_EXT);
				fetchedURI = saveAs(boardToSave, choosenFile.getParentFile());
			}else {
				int option2 = 0;
				if(choosenFile.exists()) {
					option2 = AppDialogs.fileReplacePrompt(window.getFrame(), choosenFile);
				}
				if(option2 == 0) {
					FileOutputStream fos = new FileOutputStream(choosenFile);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(boardToSave);
					fos.close();
					fetchedURI = choosenFile.toURI();
					boardToSave.setSaved();
				}else {
					fetchedURI = saveAs(boardToSave, choosenFile.getParentFile());
				}
			}
		}
		
		return fetchedURI;
	}
	
	
	
	/**
	 * Saves a Specified CircuitBoard File
	 * @param cb
	 * @throws IOException
	 */
	private void save(CircuitBoard cb) throws IOException {
		File file = new File(cb.getFileLocation());
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(cb);
		fos.close();
		cb.setSaved();
	}
	
	
	public void loadProgram(DefaultGate.LangType lt) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setMultiSelectionEnabled(false);
		jfc.showOpenDialog(null);
		String path = jfc.getSelectedFile().getAbsolutePath();
		ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(lt,path);
		window.getSelectedBoard().setGates(gates);
		window.getRenderContext().paintRerenderedBaseImageOnly();
	}
	
	
	
	/**
	 * Class that creates a CircuitBoard Filter for File Chooser Prompts
	 * @author quantumresearch
	 *
	 */
	private static class CircuitBoardFilter extends FileFilter{
		@Override
		public boolean accept(File f) {
			if(f.isDirectory()) return true;
			return f.getName().toLowerCase().endsWith(CIRCUIT_BOARD_EXT);
		}
		@Override
		public String getDescription() {
			return CIRCUIT_BOARD_DES;
		}
	}
	
	/**
	 * Class that creates PNG Filter for FileChooser Prompts
	 * @author quantumresearch
	 *
	 */
	private static class PNGBoardFilter extends FileFilter{
		@Override
		public boolean accept(File f) {
			if(f.isDirectory()) return true;
			return f.getName().toLowerCase().endsWith(PNG_EXT);
		}
		@Override
		public String getDescription() {
			return PNG_DES;
		}
	}
	
}
