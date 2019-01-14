package appFX.appPreferences;

import java.util.prefs.Preferences;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;

/**
 * All preferences for the application are handled in this class.
 * @author quantumresearch
 *
 */
public interface AppPreferences {
	
	Preferences preferences = Preferences.userNodeForPackage(AppPreferences.class);
	
	
	
	
	
	
	
	/**
	 * All string preferences for the application are created as an enum type here.
	 * @author quantumresearch
	 *
	 */
	static enum Strings{
		// File IO
		PREVIOUS_PROJ_URL(""),
		
		// Run & Debug Variables
		PYQUIL_INTERP_LOC("python"),
		QASM_INTERP_LOC("python"),
		
		;
		
		
		
		
		
		
		
		
		private final String defaultValue;
		
		Strings(String defaultValue){
			this.defaultValue = defaultValue;
		}
		
		public String get() {
			return preferences.node("strings").get(name(), defaultValue);
		}
		
		public void set(String value) {
			preferences.node("strings").put(name(), value);
		}
		
		public void resetToDefault() {
			preferences.node("strings").remove(name());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * All integer preferences for the application are created as an enum type here.
	 * @author quantumresearch
	 *
	 */
	static enum Integers{
		// GUI Variables
		WINDOW_WIDTH(700),
		WINDOW_HEIGHT(600),
		
		;
		
		
		
		
		
		
		
		
		private final int defaultValue;
		
		Integers(int defaultValue){
			this.defaultValue = defaultValue;
		}
		
		public int get() {
			return preferences.node("integers").getInt(name(), defaultValue);
		}
		
		public void set(int value) {
			preferences.node("integers").putInt(name(), value);
		}
		
		public void resetToDefault() {
			preferences.node("integers").remove(name());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * All boolean preferences for the application are created as an enum type here.
	 * @author quantumresearch
	 *
	 */
	static enum Booleans{
		// Opened Views
		PROJECT_HEIRARCHY_OPEN(true),
		CONSOLE_OPEN(true),
		PRESET_GATES_OPEN(true),
		CUSTOM_GATES_OPEN(true),
		CIRCUITBOARDS_OPEN(true),
		CUSTOM_ORACLES_OPEN(true),
		
		;
		
		
		
		
		
		
		
		
		
		private final boolean defaultValue;
		
		Booleans(boolean defaultValue){
			this.defaultValue = defaultValue;
		}
		
		public boolean get() {
			return preferences.node("booleans").getBoolean(name(), defaultValue);
		}
		
		public void set(boolean value) {
			preferences.node("booleans").putBoolean(name(), value);
		}
		
		public void resetToDefault() {
			preferences.node("booleans").remove(name());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * All float preferences for the application are created as an enum type here.
	 * @author quantumresearch
	 *
	 */
	static enum Floats{
		
		
		
		;
		
		
		
		
		
		
		
		
		
		
		
		private final float defaultValue;
		
		Floats(float defaultValue){
			this.defaultValue = defaultValue;
		}
		
		public float get() {
			return preferences.node("floats").getFloat(name(), defaultValue);
		}
		
		public void set(float value) {
			preferences.node("floats").putFloat(name(), value);
		}
		
		public void resetToDefault() {
			preferences.node("floats").remove(name());
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * All float preferences for the application are created as an enum type here.
	 * @author quantumresearch
	 *
	 */
	static enum Doubles{
		// GUI Variables
		LEFT_DIVIDER_POSITION(.3f),
		RIGHT_DIVIDER_POSITION(.7f),
		BOTTOM_DIVIDER_POSITION(.6f),
		
		
		;
		
		
		
		
		
		
		
		
		
		
		
		private final double defaultValue;
		
		Doubles(double defaultValue){
			this.defaultValue = defaultValue;
		}
		
		public double get() {
			return preferences.node("doubles").getDouble(name(), defaultValue);
		}
		
		public void set(double value) {
			preferences.node("doubles").putDouble(name(), value);
		}
		
		public void resetToDefault() {
			preferences.node("doubles").remove(name());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * All keyboard shortcut preferences for the application are created as an enum type here.
	 * @author quantumresearch
	 *
	 */
	static enum KeyShortCuts {
		OPEN_PROJECT(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN)),
		SAVE_PROJECT_AS(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)),
		SAVE(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)),
		EXPORT_QUIL(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN)),
		EXPORT_QASM(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)),
		EXPORT_QUIPPER(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)),
		PREFERENCES(new KeyCodeCombination(KeyCode.F1, KeyCombination.CONTROL_DOWN)),
		MAKE_CUSTOM_GATE(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN)),
		ADD_ROW(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN)),
		ADD_COLUMN(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN)),
		REMOVE_LAST_ROW(new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN)),
		REMOVE_LAST_COLUMN(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN)),
		RUN_QUIL(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN)),
		RUN_QASM(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN)),
		RUN_SIMULATION(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN)),
		
		
		;
		
		
		
		
		
		
		
		
		
		
		
		private final KeyCodeCombination defaultValue;
		
		KeyShortCuts(KeyCodeCombination defaultValue){
			this.defaultValue = defaultValue;
		}
		
		public KeyCodeCombination get() {
			Preferences keyCombo = preferences.node("keyCombo");

			String code = keyCombo.node("code").get(name(), defaultValue.getCode().name());
			String shift = keyCombo.node("shift").get(name(), defaultValue.getShift().name());
			String control = keyCombo.node("control").get(name(), defaultValue.getControl().name());
			String alt = keyCombo.node("alt").get(name(), defaultValue.getAlt().name());
			String meta = keyCombo.node("meta").get(name(), defaultValue.getMeta().name());
			String shortcut = keyCombo.node("shortcut").get(name(), defaultValue.getShortcut().name());
			
			return new KeyCodeCombination(KeyCode.valueOf(code), 
											ModifierValue.valueOf(shift), 
											ModifierValue.valueOf(control), 
											ModifierValue.valueOf(alt), 
											ModifierValue.valueOf(meta), 
											ModifierValue.valueOf(shortcut));
		}
		
		public void set(KeyCodeCombination value) {
			Preferences keyCombo = preferences.node("keyCombo");
			
			keyCombo.node("code").put(name(), value.getCode().name());
			keyCombo.node("shift").put(name(), value.getShift().name());
			keyCombo.node("control").put(name(), value.getControl().name());
			keyCombo.node("alt").put(name(), value.getAlt().name());
			keyCombo.node("meta").put(name(), value.getMeta().name());
			keyCombo.node("shortcut").put(name(), value.getShortcut().name());
		}
		
		public void resetToDefault() {
			Preferences keyCombo = preferences.node("keyCombo");
			
			keyCombo.node("code").remove(name());
			keyCombo.node("shift").remove(name());
			keyCombo.node("control").remove(name());
			keyCombo.node("alt").remove(name());
			keyCombo.node("meta").remove(name());
			keyCombo.node("shortcut").remove(name());
		}
	}
	
}
