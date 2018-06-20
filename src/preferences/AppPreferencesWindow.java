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
	
	public static final int WIDTH = 500;
	public static final int HEIGHT = 300;
	final static DefaultListModel<AbstractPreferenceView> PREFERENCES_VIEWS = new DefaultListModel<>();
		
	private final JList<AbstractPreferenceView> PREFERENCE_PANELS;
	
	
	static {
//		Added Views Here:
		add(new PythonPreferencesView());
		add(new ActionCommandPreferencesView());
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
			preferenceView.add(app, app.toString());
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
				AbstractPreferenceView selectedView = app.getModel().getElementAt(index);
				CardLayout layout = (CardLayout)preferenceView.getLayout();
				layout.show(preferenceView, selectedView.toString());
			}
		});
		return app;
	}
	
	
	
	
	
	private static void add(AbstractPreferenceView apv) {
		PREFERENCES_VIEWS.addElement(apv);
	}
}
