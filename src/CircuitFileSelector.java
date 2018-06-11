import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class CircuitFileSelector {
	
	public static final String CIRCUIT_BOARD_EXT;
	public static final String CIRCUIT_BOARD_DES;
	
	static {
		CIRCUIT_BOARD_EXT = ".qcir";
		CIRCUIT_BOARD_DES = "Quantum Circuit Boards (*" + CIRCUIT_BOARD_EXT + ")";
	}
	
	
	public static void selectBoardFromFileSystem() {
		try{
			Main.cb = open();
			Main.render();
		}catch(IOException e) {
			AppDialogs.errorIO(Main.w.getFrame());
			AppDialogs.couldNotOpenFile(Main.w.getFrame());
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			AppDialogs.errorProg(Main.w.getFrame());
			AppDialogs.couldNotOpenFile(Main.w.getFrame());
			e.printStackTrace();
		}
	}
	
	public static void saveBoardToFileSystem() {
		try {
			URI location = saveAs();
			Main.cb.setFileLocation(location);
			Main.w.setTitle(new File(location).getName());
		} catch (IOException e) {
			AppDialogs.errorIO(Main.w.getFrame());
			AppDialogs.couldNotSaveFile(Main.w.getFrame());
			e.printStackTrace();
		}
	}
	
	public static void saveBoard() {
		if(Main.cb.getFileLocation() != null) {
			saveBoardToFileSystem();
		}else {
			try {
				save(Main.cb);
			} catch (IOException e) {
				AppDialogs.errorIO(Main.w.getFrame());
				AppDialogs.couldNotSaveFile(Main.w.getFrame());
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	private static CircuitBoard open() throws IOException, ClassNotFoundException {
		CircuitBoard fetchedBoard =  Main.cb;
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new CircuitBoardFilter());
		fileChooser.setDialogTitle("Select Circuit Board File");
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		final int option = fileChooser.showDialog(Main.w.getFrame(), "Open");
		
		if(option == JFileChooser.APPROVE_OPTION) {
			File choosenFile = fileChooser.getSelectedFile();
			if(choosenFile.exists() && fileChooser.accept(choosenFile)) {
				FileInputStream fis = new FileInputStream(choosenFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				fetchedBoard = (CircuitBoard) ois.readObject();
				fis.close();
				
				fetchedBoard.setFileLocation(choosenFile.toURI());
			}else {
				AppDialogs.fileIsntValid(Main.w.getFrame(), choosenFile);
				fetchedBoard = open();
			}
		}
		
		return fetchedBoard;
	}
	
	
	
	private static URI saveAs() throws IOException {
		URI fetchedURI = Main.cb.getFileLocation();
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new CircuitBoardFilter());
		fileChooser.setDialogTitle("Save Circuit Board File");
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		final int option1 = fileChooser.showDialog(Main.w.getFrame(), "Save");
		
		if(option1 == JFileChooser.APPROVE_OPTION) {
			File choosenFile = fileChooser.getSelectedFile();
			if(choosenFile.exists() && choosenFile.isDirectory()) {
				AppDialogs.fileIsntValid(Main.w.getFrame(), choosenFile);
				fetchedURI = saveAs();
			}else{
				int option2 = 0;
				if(choosenFile.exists()) {
					option2 = AppDialogs.fileReplacePrompt(Main.w.getFrame(), choosenFile);
				}
				if(option2 == 0) {
					FileOutputStream fos = new FileOutputStream(choosenFile);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(Main.cb);
					fos.close();
				}else {
					fetchedURI = saveAs();
				}
			}
		}
		
		return fetchedURI;
	}
	
	
	
	public static void save(CircuitBoard cb) throws IOException {
		File file = new File(cb.getFileLocation());
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(cb);
		fos.close();
	}
	
	
	
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
	
}
