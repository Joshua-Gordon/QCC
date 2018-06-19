package preferences;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;

public final class AppPreferences {
	
	final static DefaultListModel<AbstractPreferenceView> PREFERENCES_VIEWS = new DefaultListModel<>();
	
	private static final HashMap<String, HashMap<String, Object>> DEFAULT_VALUES = new HashMap<>();
	private static final String ERROR_MESSAGE = "Preference of type \"TYPE\" with name \"NAME\" does not exist.";
	
	private static HashMap<String, Object> currentPrefType = null;
	
	static {
//		Added Views
		add(new PythonPreferencesView());
		add(new ActionCommandPreferencesView());
		
//		Added Default Values for Preferences
		setType("File IO");
		add("Previous File Location", "");
		
		setType("Python");
		add("Interpreter Location", "python");
		
		setType("Action Commands");
		add("Open Circuit",    		KeyEvent.VK_O);
		add("Save Circuit as", 		KeyEvent.VK_A);
		add("Save",			   		KeyEvent.VK_S);
		add("QUIL",			   		KeyEvent.VK_ENTER);
		add("QASM", 		   		KeyEvent.VK_Q);
		add("Quipper",         		KeyEvent.VK_P);
		add("Preferences", 	   		KeyEvent.VK_F1);
		add("Hadamard", 	   		KeyEvent.VK_H);
		add("I", 	   		   		KeyEvent.VK_I);
		add("X", 	   		   		KeyEvent.VK_X);
		add("Y", 	   				KeyEvent.VK_Y);
		add("Z", 	   				KeyEvent.VK_Z);
		add("Measure", 	   			KeyEvent.VK_M);
		add("CNot", 	   			KeyEvent.VK_C);
		add("Swap", 	   			KeyEvent.VK_W);
		add("Add Row", 	   			KeyEvent.VK_DOWN);
		add("Add Column", 	   		KeyEvent.VK_RIGHT);
		add("Remove Last Row", 		KeyEvent.VK_UP);
		add("Remove Last Column", 	KeyEvent.VK_LEFT);
		add("Run QUIL", 	   		KeyEvent.VK_R);
		add("Run QASM", 	   		KeyEvent.VK_S);
	}
	
	
	
	
	public static void putInt(String type, String name, Integer value) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		if(value != null) {
			prefs.putInt(name, value);
		}else {
			prefs.remove(name);
		}
	}
	
	
	public static void put(String type, String name, String value) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		if(value != null) {
			prefs.put(name, value);
		}else {
			prefs.remove(name);
		}
	}
	
	public static void putBoolean(String type, String name, Boolean value) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		if(value != null) {
			prefs.putBoolean(name, value);
		}else {
			prefs.remove(name);
		}
	}
	
	public static void putFloat(String type, String name, Float value) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		if(value != null) {
			prefs.putFloat(name, value);
		}else {
			prefs.remove(name);
		}
	}
	
	
	
	public static Boolean getBoolean(String type, String name) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return null;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		return prefs.getBoolean(name, (Boolean) DEFAULT_VALUES.get(type).get(name));
	}
	
	public static Float getFloat(String type, String name) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return null;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		return prefs.getFloat(name, (Float) DEFAULT_VALUES.get(type).get(name));
	}
	
	public static Integer getInt(String type, String name) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return null;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		return prefs.getInt(name, (Integer) DEFAULT_VALUES.get(type).get(name));
	}
	
	
	public static String get(String type, String name) {
		if(!prefExists(type, name)) {
			try {
				final String message = ERROR_MESSAGE.replace("TYPE", type).replace("NAME", name);
				throw new NullPointerException(message);
			}catch(NullPointerException exception) {
				exception.printStackTrace();
			}
			return null;
		}
		Preferences prefs = Preferences.userRoot().node(type);
		return prefs.get(name, (String) DEFAULT_VALUES.get(type).get(name));
	}
	
	
	private static void setType(String prefType) {
		if(!DEFAULT_VALUES.containsKey(prefType)) 
			DEFAULT_VALUES.put(prefType, new HashMap<>());
		currentPrefType = DEFAULT_VALUES.get(prefType);
	}
	
	private static void add(AbstractPreferenceView apv) {
		PREFERENCES_VIEWS.addElement(apv);
	}
	
	private static void add(String prefName, Object defaultValue) {
		currentPrefType.put(prefName, defaultValue);
	}
	
	
	
	public static boolean prefExists(String type, String name) {
		return DEFAULT_VALUES.containsKey(type) && 
				DEFAULT_VALUES.get(type).containsKey(name);
	}
	
	
	
}
