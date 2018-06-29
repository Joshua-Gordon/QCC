package preferences;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


@SuppressWarnings("serial")
public class AppPreferencesWindow extends JDialog {
	
	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;
	final static DefaultListModel<AbstractPreferenceView> PREFERENCES_VIEWS = new DefaultListModel<>();
		
	private final JList<AbstractPreferenceView> PREFERENCE_PANELS;
	
	/*
	 * 
	 * To Josh or whomever this may concern,
	 * 
	 * 
	 * 
	 * --All Preference User-Interfaces are added Here--
	 * 
	 * This class, AppPreferencesWindow, creates Preference user-interface
	 * Window that allows the user to change preferences to his/her desire
	 * 
	 * To create a tab for your preference within this window,
	 * create a new class that extends AbstractPreferenceView,
	 * then added the unimplemented methods to your class.
	 * 
	 * In the constructor of your newly created class,
	 * add the super() method with the name of your preference view's
	 * name as it's argument (ie. super("My Preference View"));
	 * 
	 * Finally, use the add() method within the static "block" below 
	 * with an instance of your newly created class as a parameter.
	 * 
	 * This will add your view to the window!
	 * 
	 * 
	 * However, There is no UI elements within the (aka. a blank view)
	 * To added UI elements to your view, use the getContent() method within your
	 * newly created class. This method returns the JPanel where the elements will be
	 * shown when your view is opened. With this JPanel, added Swing UI elements until
	 * you get the desired look of your preference view.
	 * 
	 * The importPreferences() method is called every time The Preference Window is opened
	 * In this method, the AppPreferences.get() Methods should be called to
	 * load the preference values into your Swing UI elements.
	 * 
	 * The applyChanges() and restoreToDefaults() methods are called when the UI buttons
	 * "Apply Changes" and "Restore To Defaults" are pressed on the Preference Window.
	 * These methods should use the AppPreferences.put() methods to load the Swing UI elements
	 * selected Preferences to change the current AppPreferences.
	 * 
	 * After this is done, The your Preference View should be functioning to your desire
	 * 
	 * 
	 * 
	 * Best,
	 * Max 
	 * 
	 */
	static {
//		Added Views Here:
		add(new ActionCommandPreferencesView());
		add(new PyQuilPreferencesView());
		add(new QASMPreferencesView());
	}
	
	
	
	
	
	public AppPreferencesWindow(JFrame parent){
		super(parent);
		setModal(true);
		setSize(new Dimension(WIDTH,HEIGHT));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
		setTitle("Preferences");
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		JPanel preferenceView = new JPanel(new CardLayout());
		PREFERENCE_PANELS = mkJList(preferenceView);
		PREFERENCE_PANELS.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		AbstractPreferenceView app;
		for(int i = 0; i < PREFERENCE_PANELS.getModel().getSize(); i++) {
			app = PREFERENCE_PANELS.getModel().getElementAt(i);
			app.importPreferences();
			preferenceView.add(app.getViewToRender(), app.toString());
		}
		
		JScrollPane pane = new JScrollPane(PREFERENCE_PANELS);
		container.add(pane, BorderLayout.WEST);
		container.add(preferenceView, BorderLayout.CENTER);
		
		if(PREFERENCE_PANELS.getModel().getSize() != 0) {
			PREFERENCE_PANELS.setSelectedIndex(0);
		}
	}
	
	
	
	
	private JList<AbstractPreferenceView> mkJList(JPanel preferenceView) {
		
		JList<AbstractPreferenceView> app = new JList<>(PREFERENCES_VIEWS);
		app.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		app.setLayoutOrientation(JList.VERTICAL);
		app.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = app.getSelectedIndex();
				if(index != -1) {
					AbstractPreferenceView selectedView = app.getModel().getElementAt(index);
					CardLayout layout = (CardLayout)preferenceView.getLayout();
					layout.show(preferenceView, selectedView.toString());
				}else if(app.getModel().getSize() > 0){
					app.setSelectedIndex(0);
					CardLayout layout = (CardLayout)preferenceView.getLayout();
					layout.show(preferenceView, app.getModel().getElementAt(0).toString());
				}
			}
		});
		return app;
	}
	
	
	
	
	
	private static void add(AbstractPreferenceView apv) {
		PREFERENCES_VIEWS.addElement(apv);
	}
}
