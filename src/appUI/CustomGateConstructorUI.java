package appUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import appUI.GateMatrixEditable.MatrixFormatException;
import mathLib.Complex;
import mathLib.Matrix;

@SuppressWarnings("serial")
public class CustomGateConstructorUI extends JDialog implements ChangeListener, ActionListener{
	private Window w;
	
	private JLabel[] labels = new JLabel[6];
	private JTextField[] textFields = new JTextField[1];
	private JScrollPane textArea;
	private JSpinner spinner = new JSpinner();
	private JButton[] buttons = new JButton[1];
	private JRadioButton[] radioButtons = new JRadioButton[2];
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JScrollPane fullMatrix;
	private JScrollPane kroneckerMatrix;
	private JPanel blank;
	private ArrayList<Matrix<Complex>> outputMatrixes = null;
	
	public CustomGateConstructorUI(JFrame parent) {
		super(parent);
		
		setTitle("Custom Gate");
		setSize(new Dimension(500, 700));
		setLocationRelativeTo(parent);
		setModal(true);
		
		labels[0] = new JLabel("  " + "Custom Gate");
		labels[0].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		labels[0].setBackground(Color.LIGHT_GRAY);
		labels[0].setOpaque(true);
		labels[0].setMinimumSize(new Dimension(100,  30));
		labels[0].setPreferredSize(new Dimension(100,  30));
		labels[1] = new JLabel("Gate Name: ");
		labels[2] = new JLabel("Number of Qubits: ");
		labels[3] = new JLabel("Matrix Representation: ");
		labels[4] = new JLabel("Description: ");
		textFields[0] = new JTextField();
		textFields[0].setMinimumSize(new Dimension(150, 20));
		textFields[0].setPreferredSize(new Dimension(150, 20));
		SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 8, 1);
		spinner.setModel(spinnerModel);
		spinner.addChangeListener(this);
		spinner.setMinimumSize(new Dimension(50, 20));
		spinner.setPreferredSize(new Dimension(50, 20));
		radioButtons[0] = new JRadioButton("Full Matrix Representation");
		radioButtons[0].addActionListener(this);
		radioButtons[1] = new JRadioButton("Kronecker Matrix Representation");
		radioButtons[1].addActionListener(this);
		buttonGroup = new ButtonGroup();
		buttonGroup.add(radioButtons[0]);
		buttonGroup.add(radioButtons[1]);
		textArea = new JScrollPane(new JTextArea());
		textArea.setMinimumSize(new Dimension(100, 70));
		textArea.setPreferredSize(new Dimension(100, 70));
		buttons[0] = new JButton("Create Gate");
		buttons[0].addActionListener(this);
		
		fullMatrix = new JScrollPane(new GateMatrixEditable(1, false));
		kroneckerMatrix = new JScrollPane(new GateMatrixEditable(1, true));
		fullMatrix.setMinimumSize(new Dimension(100, 70));
		fullMatrix.setPreferredSize(new Dimension(100, 70));
		kroneckerMatrix.setMinimumSize(new Dimension(100, 70));
		kroneckerMatrix.setPreferredSize(new Dimension(100, 70));
		fullMatrix.setVisible(false);
		kroneckerMatrix.setVisible(false);
		blank = new JPanel();
		blank.setPreferredSize(new Dimension(10, 10));
		blank.setMinimumSize(new Dimension(10, 10));
		
		initLayout();
	}
	
	private void initLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets basic = new Insets(2, 4, 2, 4);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 18, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(labels[0], gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = basic;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		add(labels[1], gbc);
		gbc.gridx++;
		add(textFields[0], gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(2, 4, 18, 2);
		add(labels[2], gbc);
		gbc.gridx++;
		add(spinner, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.insets = basic;
		add(labels[3], gbc);
		gbc.gridy++;
		add(radioButtons[0], gbc);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(fullMatrix, gbc);
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(radioButtons[1], gbc);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(kroneckerMatrix, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weighty = .2;
		add(blank, gbc);
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(labels[4], gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = .1;
		add(textArea, gbc);
		gbc.weighty = 0;
		gbc.gridx = 1;
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		add(buttons[0], gbc);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		((GateMatrixEditable)fullMatrix.getViewport().getView()).changeSize(Integer.parseInt(spinner.getValue().toString()));
		((GateMatrixEditable)kroneckerMatrix.getViewport().getView()).changeSize(Integer.parseInt(spinner.getValue().toString()));
		revalidate();
		repaint();
	}
	
	private GateMatrixEditable getSelectedMatrixEditable() {
		if(radioButtons[0].isSelected())
			return (GateMatrixEditable) fullMatrix.getViewport().getView();
		else if(radioButtons[1].isSelected())
			return (GateMatrixEditable) kroneckerMatrix.getViewport().getView();
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Create Gate")) {
			String temp = "";
			String param = textFields[0].getText();
			if(param == null || param.equals(""))
				temp += labels[1].getText();
			if(buttonGroup.getSelection().isSelected())
				temp += (temp.isEmpty()? "": ", ") + labels[3].getText();
			if(!temp.isEmpty()) {
				AppDialogs.paramsMissing(this, temp);
				return;
			}
			
			ArrayList<Matrix<Complex>> matrixes;
			try{
				matrixes = getSelectedMatrixEditable().getMatrixes();
			}catch(MatrixFormatException mfe) {
				if(mfe.isKroneckerFormat()) {
					AppDialogs.notValidComplexNumber(this, mfe.getValue(), mfe.getMatrixNumber(), mfe.getRow(), mfe.getColumn());
				}else {
					AppDialogs.notValidComplexNumber(this, mfe.getValue(), mfe.getRow(), mfe.getColumn());
				}
				return;
			}
			outputMatrixes = matrixes;
			dispose();
			
		} else {
			if(radioButtons[0].isSelected()) {
				fullMatrix.setVisible(true);
				kroneckerMatrix.setVisible(false);
			}else{
				fullMatrix.setVisible(false);
				kroneckerMatrix.setVisible(true);
			}
			revalidate();
			repaint();
		}
	}
	
	public ArrayList<Matrix<Complex>> getCustomMatrix(){
		return outputMatrixes;
	}
	
}
