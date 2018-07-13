package appUI;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.AbstractDocument;

import appUI.customBorders.MatrixBorder;
import documentFilters.ComplexFilter;
import mathLib.Complex;
import mathLib.Matrix;

@SuppressWarnings("serial")
public class GateMatrixEditable extends JPanel{
	
	private int numQubits;
	private static final String TENSOR = "<html><div style='font-size: large;'>⊗<div></html>";
	private ArrayList<MatrixEditable> matrixEditables = new ArrayList<MatrixEditable>();
	private ArrayList<JLabel> tensorLabels = new ArrayList<>();
	private GridBagConstraints gbc;
	private boolean kroneckerFormat;
	
	public GateMatrixEditable(int numQubits, boolean kroneckerFormat) {
		this.numQubits = numQubits;
		this.kroneckerFormat = kroneckerFormat;
		if(numQubits <= 0)
			throw new IndexOutOfBoundsException("The Number of Qubits within a Matrix must be a Natural Number");
		
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 3, 0, 3);
		gbc.gridx = 0;
		gbc.gridy = 0;
		matrixEditables.add(new MatrixEditable(kroneckerFormat? 2 : getMatSize()));
		add(matrixEditables.get(0), gbc);
		
		if(kroneckerFormat) {
			JLabel tensorLabel;
			MatrixEditable me;
			for(int i = 1; i < numQubits; i++) {
				gbc.gridx++;
				tensorLabel = new JLabel(TENSOR);
				add(tensorLabel, gbc);
				tensorLabels.add(tensorLabel);
				gbc.gridx++;
				me = new MatrixEditable(2);
				add(me, gbc);
				matrixEditables.add(me);
			}
		}
	}
	
	public void changeSize(int numQubits) {
		if(numQubits <= 0)
			throw new IndexOutOfBoundsException("The Number of Qubits within a Matrix must be a Natural Number");
		
		this.numQubits = numQubits;
		
		if(kroneckerFormat) {
			JLabel tensorLabel;
			MatrixEditable me;
			
			if(numQubits > matrixEditables.size()) {	
				gbc.gridx = (matrixEditables.size() - 1) * 2;
				
				for(int i = matrixEditables.size(); i < numQubits; i++) {
					gbc.gridx++;
					tensorLabel = new JLabel(TENSOR);
					add(tensorLabel, gbc);
					tensorLabels.add(tensorLabel);
					gbc.gridx++;
					me = new MatrixEditable(2);
					add(me, gbc);
					matrixEditables.add(me);
				}
				
			}else if(numQubits < matrixEditables.size()) {
				
				for(int i = matrixEditables.size() - 1; i >= numQubits; i--) {
					me = matrixEditables.get(i);
					remove(me);
					matrixEditables.remove(i);
					tensorLabel = tensorLabels.get(i - 1);
					remove(tensorLabel);
					tensorLabels.remove(i - 1);
				}
			}
		}else{
			matrixEditables.get(0).changeSize(getMatSize());
		}
	}
	
	private int getMatSize() {
		return 1 << numQubits ;
	}
	
	public ArrayList<Matrix<Complex>> getMatrixes() throws MatrixFormatException{
		ArrayList<Matrix<Complex>> mats = new ArrayList<>();
		for(int i = 0; i < matrixEditables.size(); i++)
			mats.add(matrixEditables.get(i).getMatrix(i, kroneckerFormat));
		return mats;
	}
	
	private static class MatrixEditable extends JPanel{
		private DefaultTableModel model;
		
		private MatrixEditable(int size) {
			setBorder(new MatrixBorder());
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = 0;
			
			model = new DefaultTableModel(size, size);
			JTable table = new JTable(model);
			table.setDefaultEditor(Object.class, new ComplexCellEditor());
			table.setRowHeight(30);
			table.getFillsViewportHeight();
			table.setCellSelectionEnabled(false);
			
			DefaultTableCellRenderer render = new DefaultTableCellRenderer();
			render.setHorizontalAlignment(SwingConstants.CENTER);
			table.setDefaultRenderer(Object.class, render);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			for(int i = 0; i < size; i++)
				table.getColumnModel().getColumn(i).setCellRenderer(render);
			add(table, gbc);
		}
		private void changeSize(int size) {
			model.setRowCount(size);
			model.setColumnCount(size);
		}
		
		private Matrix<Complex> getMatrix(int matrixNumber, boolean kroneckerFormat) throws MatrixFormatException{
			int rows = model.getRowCount();
			int columns = model.getColumnCount();
			String value;
			Matrix<Complex> mat = new Matrix<>(rows, columns, Complex.ONE().mkArray(rows * columns));
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < columns; j++) {
					if(model.getValueAt(i, j) == null)
						value = "0";
					else
						value = model.getValueAt(i, j).toString();
					try {
						mat.r(Complex.parseComplex(value), i, j);
					}catch(NumberFormatException e) {
						throw new MatrixFormatException(value, matrixNumber, i, j, kroneckerFormat);
					}
				}
			}
			return mat;
		}
		
	}
	
	private static class ComplexCellEditor extends AbstractCellEditor implements TableCellEditor{
		private JTextField field = new JTextField();
		
		public ComplexCellEditor() {
			super();
			field.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			((AbstractDocument) field.getDocument()).setDocumentFilter(new ComplexFilter());
		}
		
		@Override
		public Object getCellEditorValue() {
			String text = field.getText();
			field.setText("");
			return text;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			
			if(value != null ) {
				String s = (String) value;
				field.setText(s);
			}
			return field;
		}
		
	}
	
	public static class MatrixFormatException extends NumberFormatException{
		private String value;
		private int row, column, matrixNumber;
		private boolean kroneckerFormat;
		
		public MatrixFormatException(String value, int matrixNumber, int row, int column, boolean kroneckerFormat) {
			this.kroneckerFormat = kroneckerFormat;
			this.matrixNumber = matrixNumber;
			this.value = value;
			this.row = row;
			this.column = column;
		}
		
		public int getMatrixNumber() {
			return matrixNumber;
		}
		
		public int getRow() {
			return row;
		}
		
		public int getColumn() {
			return column;
		}
		
		public String getValue() {
			return value;
		}
		
		public boolean isKroneckerFormat() {
			return kroneckerFormat;
		}
	}
	
}
