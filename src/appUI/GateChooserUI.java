package appUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.Hashtable;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import framework.AbstractGate;
import framework.CircuitBoard;
import framework.DefaultGate;

@SuppressWarnings("serial")
public class GateChooserUI extends AbstractAppViewUI implements ListSelectionListener{
	private JTabbedPane tabPane;
	private Window w;
	private Hashtable<String, JList<AbstractGate>> lists = new Hashtable<>();
	
	private static final String DEFAULT_GATES = "Default Gates";
	private static final String CUSTOM_GATES = "Custom Gates";
	private static final String CUSTOM_ORACLES = "Custom Oracles";
	private AbstractGate selectedGate;
	
	public GateChooserUI(Window w) {
		super("Gates");
		this.w = w;
		setBackground(Color.LIGHT_GRAY);
		setLayout(new BorderLayout());
		tabPane = new JTabbedPane();
		tabPane.add(DEFAULT_GATES, makeListTab(DEFAULT_GATES));
		tabPane.add(CUSTOM_GATES, makeListTab(CUSTOM_GATES));
		tabPane.add(CUSTOM_ORACLES, makeListTab(CUSTOM_ORACLES));
		add(tabPane, BorderLayout.CENTER);
		lists.get(DEFAULT_GATES).setModel(DefaultGate.DEFAULT_GATES);
	}
	
	public void updateListModels() {
		CircuitBoard cb = w.getSelectedBoard();
		lists.get(CUSTOM_GATES).setModel(cb.getCustomGates());
		lists.get(CUSTOM_ORACLES).setModel(cb.getCustomOracles());
	}
	
	private JPanel makeListTab(String listName) {
		JPanel panel = new JPanel(new BorderLayout());
		JList<AbstractGate> list = new JList<>();
		lists.put(listName, list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		list.setCellRenderer(new ListCellRenderer<AbstractGate>() {
			private final Color color = new Color(255, 200, 200);
			@Override
			public Component getListCellRendererComponent(JList<? extends AbstractGate> list, AbstractGate value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JPanel panel = new JPanel(new GridBagLayout());
				if(isSelected) {
					panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				}
				if(cellHasFocus) {
					panel.setOpaque(true);
					panel.setBackground(color);
				}else {
					panel.setOpaque(false);
				}
				JLabel label = new JLabel(value.getIcon());
				label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				panel.add(label, gbc);
				return panel;
			}
		});
		panel.setLayout(new BorderLayout());
		JScrollPane pane = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(pane);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return panel;
	}
	
	@Override
	public void changeVisibility(boolean visible) {
		setVisible(visible);
		JSplitPane splitPane = w.getGateChooserSplitPane();
		if(visible) {
			splitPane.setDividerLocation(-100);
		}
		splitPane.setEnabled(visible);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			JList<AbstractGate> list = (JList<AbstractGate>)e.getSource();
			selectedGate = list.getSelectedValue();
		}
	}
	
	public AbstractGate getGate() {
		return selectedGate;
	}
	
	public boolean isSelected() {
		return selectedGate != null;
	}
	

}
