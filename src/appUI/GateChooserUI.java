package appUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

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
	private ArrayList<JList<AbstractGate>> lists = new ArrayList<>();
	
	private static final String DEFAULT_GATES_NAME = "Default Gates";
	private static final String CUSTOM_GATES_NAME = "Custom Gates";
	private static final String CUSTOM_ORACLES_NAME = "Custom Oracles";
	
	public static final int DEFAULT_GATES = 0;
	public static final int CUSTOM_GATES = 1;
	public static final int CUSTOM_ORACLES = 2;
	
	private AbstractGate selectedGate;
	private boolean isChanging = false;
	
	public GateChooserUI(Window w) {
		super("Gates");
		this.w = w;
		setBackground(Color.LIGHT_GRAY);
		setLayout(new BorderLayout());
		tabPane = new JTabbedPane();
		tabPane.add(DEFAULT_GATES_NAME, makeListTab());
		tabPane.add(CUSTOM_GATES_NAME, makeListTab());
		tabPane.add(CUSTOM_ORACLES_NAME, makeListTab());
		add(tabPane, BorderLayout.CENTER);
		lists.get(DEFAULT_GATES).setModel(DefaultGate.GATE_MAP);
	}
	
	public void updateListModels() {
		CircuitBoard cb = w.getSelectedBoard();
		lists.get(CUSTOM_GATES).setModel(cb.getCustomGates());
		lists.get(CUSTOM_ORACLES).setModel(cb.getCustomOracles());
	}
	
	private JPanel makeListTab() {
		JPanel panel = new JPanel(new BorderLayout());
		JList<AbstractGate> list = new JList<>();
		lists.add(list);
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
		splitPane.setEnabled(visible);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting() && !isChanging) {
			isChanging = true;
			JList<AbstractGate> list = (JList<AbstractGate>) e.getSource();
			selectedGate = list.getSelectedValue();
			for(int i = 0; i < lists.size(); i++) {
				if(!list.equals(lists.get(i))) {
					lists.get(i).clearSelection();
				}
			}
			isChanging = false;
		}
	}
	
	public AbstractGate getSelectedGate() {
		return selectedGate;
	}
	
	public boolean isSelected() {
		return selectedGate != null;
	}
	

}
