package appTools;

import javax.swing.ImageIcon;

import appUI.CircuitBoardRenderContext;
import appUI.Window;
import framework.AbstractGate;
import framework.Main;
import framework.SolderedGate;
import framework.SolderedRegister;
import mathLib.Complex;
import mathLib.Matrix;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class EditTool extends Tool{

	public EditTool(Window w, ImageIcon icon) {
		super("Edit Gate Tool", w, icon);
	}

	@Override
	public void onSelected() {
		
	}

	@Override
	public void onUnselected() {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		setGridLocation(p);
		SolderedGate selected = window.getSelectedBoard().getSolderedGate(p.x,p.y);
		if(selected.getAbstractGate().getType().equals(AbstractGate.GateType.I)) {
			return;
		}
		addControl(selected,p.x,0,true);
		System.out.println("Added");
	}

	/**
	 * Allows for putting controls onto gates by editing the matrix
	 * @param sg The gate to be controlled
	 * @param col The column of the gate
	 * @param register The location of the control qubit in the column
	 * @param c Whether or not it is a true control or a false control
	 * @return A new gate that is equivalent to ag but controlled by the register
	 */
	public static void addControl(SolderedGate sg, int col,int register, boolean c) {
		Matrix<Complex> oldMat = sg.getAbstractGate().getMatrix();
		Matrix<Complex> newMat = new Matrix<Complex>(Complex.ISQRT2(),oldMat.getColumns()<<1,oldMat.getRows()<<1).identity();
		if(c) {
			for(int y = oldMat.getRows(); y < newMat.getRows(); ++y) {
				for(int x = oldMat.getColumns(); x < newMat.getColumns(); ++x) {
					newMat.r(oldMat.v(x-oldMat.getColumns(),y-oldMat.getRows()),x,y);
				}
			}
		} else {
			for(int y = 0; y < oldMat.getRows(); ++y) {
				for(int x = 0; x < oldMat.getColumns(); ++x) {
					newMat.r(oldMat.v(x,y),x,y);
				}
			}
		}
		sg.getAbstractGate().setMatrix(newMat);
		sg.getAbstractGate().setType(AbstractGate.GateType.CUSTOM);
		sg.getAbstractGate().setName("C"+sg.getAbstractGate().getName());
		ArrayList<SolderedRegister> oldRegs = sg.getRegisters();
		SolderedRegister control = new SolderedRegister(sg,oldRegs.size());
		Main.getWindow().getSelectedBoard().setSolderedRegister(col,register,control);
	}


	public void setGridLocation(Point mouseCoords) {
		int[] params = window.getRenderContext().getGridColumnPosition(mouseCoords.x);
		mouseCoords.x = params[0];
		mouseCoords.y = (int) (mouseCoords.getY() / CircuitBoardRenderContext.GATE_PIXEL_SIZE);
	}
}
