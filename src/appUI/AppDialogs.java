package appUI;
import java.awt.Component;
import java.io.File;

import javax.swing.JOptionPane;

public class AppDialogs {
	
	
	public static String formatMessage(String message){
		final String BEHIND = "<html><div style='text-align: center;'>";
		final String INFRONT = "</div></html>";
		return BEHIND + message.replace("\n","<br>") + INFRONT;
	}
	
	
//	Generic Messages
	
	public static int warningMsg(Component parent, String title, String message, String option1, String option2){
		String[] options = {option1, option2};
		return JOptionPane.showOptionDialog(
				parent, formatMessage(message), title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null, options, options[1]);
	}
	
	public static int errorMsg(Component parent, String title, String message, String option1, String option2){
		String[] options = {option1, option2};
		return JOptionPane.showOptionDialog(
				parent, formatMessage(message), title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null, options, options[1]);
	}
	
	public static void errorMsg(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, 
				formatMessage(message),
				"Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
//	Specific Messages
	
	
	
	public static int restoreToDefaults(Component parent, String titlePrefs) {
		return warningMsg(parent, "Restore To Defaults", 
				"Are you sure you want to\n"
				+ "restore all " + titlePrefs + "\n"
						+ "back to defaults values?", "Yes, Restore All", "Cancel");
	}
	
	public static int changePreferences(Component parent) {
		return warningMsg(parent, "Apply changes", 
				"Are you sure you want to\n"
				+ "Apply these changes?", "Yes", "Cancel");
	}
	
	public static void lengthNotValid(Component parent, String length) {
		errorMsg(parent, "\"" + length + "\" is not a valid length.");
	}
	
	public static void keyCodeUsed(Component parent) {
		errorMsg(parent, "This keycode is already used");
	}
	
	public static void keyCodeNotValid(Component parent) {
		errorMsg(parent, "This keycode is not valid");
	}
	
	public static void errorIO(Component parent) {
		errorMsg(parent, "IOException");
	}
	
	public static void errorProg(Component parent){
		errorMsg(parent, "Programic Error");
	}
	
	public static void couldNotExport(Component parent) {
		errorMsg(parent, "Could not export File");
	}
	
	public static void couldNotOpenFile(Component parent) {
		errorMsg(parent, "Could not open File");
	}
	
	public static void couldNotSaveFile(Component parent) {
		errorMsg(parent, "Could not save File");
	}
	
	public static void couldNotRemoveRow(Component parent) {
		errorMsg(parent, "Rows cannot be less than 1 unit");
	}
	
	public static void couldNotRemoveColumn(Component parent) {
		errorMsg(parent, "Columns cannot be less than 1 unit");
	}
	
	public static void fileIsntValid(Component parent, File file) {
		errorMsg(parent, "The file \"" + file.getName() + "\" is not valid.\n"
				+ "File Path: " + file.getAbsolutePath());
	}
	
	public static void fileExtIsntValid(Component parent, File file, String extention) {
		errorMsg(parent, "The file \"" + file.getName() + "\" must have\n\""
				+ extention + "\" as a file extention.");
	}
	
	public static int fileReplacePrompt(Component parent, File file){
		return warningMsg(parent, "File exists", "The file \"" + file.getName() + "\" already exists.\n"
				+ "Do you want to replace this file?",
				"Yes, replace file", "Cancel");
	}
	
//	public static int openFileWithoutSaving(Component parent, String fileName) {
//		return warningMsg(parent, "File is not saved", "The file \"" + fileName + "\" is not saved.\n"
//				+ "Do you want continue without saving?",
//				"Yes", "Cancel");
//	}
	
	public static int continueWithoutSaving(Component parent, String fileName) {
		return warningMsg(parent, "File is not saved", "The file \"" + fileName + "\" is not saved.\n"
				+ "Do you want continue without saving?",
				"Continue Without Saving", "Save File");
	}
	
	public static int closeWithoutSaving(Component parent, String fileName) {
		return warningMsg(parent, "File is not saved", "The file \"" + fileName + "\" is not saved.\n"
				+ "Do you want close without saving?",
				"Close", "Save File");
	}
	
}
