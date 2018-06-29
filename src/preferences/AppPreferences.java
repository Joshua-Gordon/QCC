package preferences;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public final class AppPreferences {
	
	private static final HashMap<String, HashMap<String, Object>> DEFAULT_VALUES = new HashMap<>();
	private static final String ERROR_MESSAGE = "Preference of type \"TYPE\" with name \"NAME\" does not exist.";
	
	private static HashMap<String, Object> currentPrefType = null;
	
	
	
	/*
	 * To Josh or whomever this may concern,
	 * 
	 *  --All of the Application's Preferences Options are created Here (under the static "block")--
	 *  
	 *  (Do the following under the static "block")-->
	 *  		To create a preference option, First use the setType() method to give your
	 *  		preference a group name... This is so preferences are organized according
	 * 			to a "group name" or a "type name"
	 *  
	 *  		Then, Under the setType() Method used, you can give a list of preference names
	 *  		with their corresponding default values using the add() method
	 *  <--(Do the following under the static "block")
	 *  
	 *  
	 *  
	 *  Thats it! your preference option is created!
	 *  
	 *  
	 *  
	 *  However, the preference option is only set to its default value.
	 *  To put a value into the preference option, use any of the following methods:
	 *  	put()	      
	 *  	putInt()
	 *  	putFloat()
	 *  	putBoolean()
	 *  
	 *  Use these methods throughout any of the classes when a preference option needs
	 *  to be changed. To change the preference back to its default value, put null as
	 *  a parameter.
	 *  
	 *  To access a preference use any of the following methods:
	 *  	get()	      
	 *  	getInt()
	 *  	getFloat()
	 *  	getBoolean()
	 *  
	 *  
	 *  Note : 
	 *  Most of the user defined preferences should be set by a user within 
	 *  the Preference user-interface Window (aka. AppPreferenceWindow.java)  
	 *  To create user-interface within this window for your newly created preferences
	 *  Go to AppPreferenceWindow.java File and follow the comments there.
	 *  
	 *  Best,
	 *  -Max
	 */
	static {
		//deleteAllPreferences();
		
		
		
//		All Created Preferences with their Default Values
		
		setType("File IO");
		add("Previous File Location", "");
		
		setType("Opened Views");
		add("Console", false);
		
		setType("PyQuil");
		add("Interpreter Location", "python");

		setType("QASM");
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
		add("Custom",				KeyEvent.VK_T);
		add("Add Row", 	   			KeyEvent.VK_DOWN);
		add("Add Column", 	   		KeyEvent.VK_RIGHT);
		add("Remove Last Row", 		KeyEvent.VK_UP);
		add("Remove Last Column", 	KeyEvent.VK_LEFT);
		add("Run QUIL", 	   		KeyEvent.VK_R);
		add("Run QASM", 	   		KeyEvent.VK_N); //Conflicts with Save, we will discuss this on Tuesday
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
	
	
	private static void add(String prefName, Object defaultValue) {
		currentPrefType.put(prefName, defaultValue);
	}
	
	
	
	public static boolean prefExists(String type, String name) {
		return DEFAULT_VALUES.containsKey(type) && 
				DEFAULT_VALUES.get(type).containsKey(name);
	}
	
	

	/**
	 * 
	 * This method should be used <b> only </b> <br>
	 * used to clear all preferences related to this application
	 * in case a preferences previous preference had been refactored or
	 * removed.
	 * 
	 */
	public static void deleteAllPreferences() {
		Preferences prefs = Preferences.userRoot();
		try {
			String[] children = prefs.childrenNames();
			if(children != null) {
				Preferences prefType;
				for(String child : children) {
					prefType = prefs.node(child);
					prefType.removeNode();
					prefType.flush();
				}
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
