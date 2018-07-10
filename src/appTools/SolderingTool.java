package appTools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import appUI.CircuitBoardRenderContext;
import appUI.Window;
import framework.AbstractGate;
import framework.AbstractGate.GateType;
import framework.SolderedGate;
import framework.SolderedRegister;
import utils.ResourceLoader;

public class SolderingTool extends Tool{
	
	private Point prevHoveredGrid = null;
	private int columnPixelSelection;
	private Stage stage = Stage.NO_SELECTION;
	private static final int GATE_PIXEL_SIZE = CircuitBoardRenderContext.GATE_PIXEL_SIZE;
	
	private AbstractGate selectedGate;
	private BufferedImage grayedOut;
	
	private int selectedColumn;
	private int columnPixelPosition;
	private int[] registers;
	private int currentGateRegister;
	private int lastGateRegisterEdited;
	private int firstLocalRegister, lastLocalRegister;
	
	private static enum Stage{
		NO_SELECTION, SELECTED_MULTIQUBITGATE
	}
	
	public SolderingTool(Window window, ImageIcon icon) {
		super("Solder Gate Tool", window, icon);
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		setGridLocation(p);
		
		switch(stage) {
		case NO_SELECTION:
			if(window.getGateChooser().isSelected()) {
				AbstractGate gate = window.getGateChooser().getSelectedGate();
				if(gate.isMultiQubitGate())
					startMultiQubitSelection(p, gate);
				else
					solderSingleQubit(p, gate);
			}
			break;
		case SELECTED_MULTIQUBITGATE:
			if(p.x == selectedColumn)
				selectMultiQubitRegister(p);
			break;
		}
	}
	
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		setGridLocation(p);
		
		switch(stage) {
		case NO_SELECTION:
			if((prevHoveredGrid == null || p.distance(prevHoveredGrid) != 0) && window.getGateChooser().isSelected())
				drawGateSelection(p);
			break;
		case SELECTED_MULTIQUBITGATE:
			if((prevHoveredGrid == null || p.distance(prevHoveredGrid) != 0) && p.x == selectedColumn)
				drawMultiQubitSelection(p);
			else if(p.x != selectedColumn)
				window.getRenderContext().paintBaseImageWithOverlay(grayedOut);
			break;
		}
		prevHoveredGrid = p;
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyChar());
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			stage = Stage.NO_SELECTION;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println(e.getKeyChar());
	}
	
	
	
	
	
	private void selectMultiQubitRegister(Point p) {
		int previouslySelected = scanSelected(p.y);
		int previousGateRegister = currentGateRegister;
		
		registers[currentGateRegister] = p.y;
		if(previouslySelected == -1)
			currentGateRegister = ++lastGateRegisterEdited;
		else
			currentGateRegister = previouslySelected;
		
		if(p.y < registers[firstLocalRegister])
			firstLocalRegister = previousGateRegister;
		if(p.y > registers[lastLocalRegister])
			lastLocalRegister = previousGateRegister;
		
		if(currentGateRegister == registers.length) {
			SolderedGate sg = new SolderedGate(selectedGate, firstLocalRegister, lastLocalRegister);
			
			
			window.getSelectedBoard().detachAllGatesWithinRange(registers[firstLocalRegister], registers[lastLocalRegister], selectedColumn);
			
			int row;
			for(int i = 0; i < registers.length; i++) {
				row = registers[i];
				window.getSelectedBoard().setSolderedRegister(selectedColumn, row, new SolderedRegister(sg, i));
			}
			
			window.getSelectedBoard().setUnsaved();
			window.getRenderContext().paintRerenderedBaseImageOnly();
			stage = Stage.NO_SELECTION;
			
		}else {
			Graphics2D g2d = (Graphics2D) grayedOut.getGraphics();
			g2d.setColor(Color.BLUE);
			int columnWidth = window.getRenderContext().getColumnWidth(selectedColumn) * GATE_PIXEL_SIZE;
			g2d.fillRect(columnPixelPosition, p.y * GATE_PIXEL_SIZE, columnWidth, GATE_PIXEL_SIZE);
			g2d.setColor(Color.WHITE);
			g2d.setFont(ResourceLoader.MPLUS.deriveFont(24));
			CircuitBoardRenderContext.drawCenteredString(g2d, Integer.toString(previousGateRegister), 
					columnPixelPosition, p.y * GATE_PIXEL_SIZE, columnWidth - 1, GATE_PIXEL_SIZE - 1);
			window.getRenderContext().paintBaseImageWithOverlay(grayedOut);
		}
	}
	
	private void drawMultiQubitSelection(Point p) {
		BufferedImage bi = window.getRenderContext().getOverlay();
		Graphics2D g2d = (Graphics2D)bi.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawImageOnImage(g2d, bi, grayedOut);
		
		g2d.setColor(Color.RED);
		int columnWidth =	window.getRenderContext().getColumnWidth(selectedColumn) * GATE_PIXEL_SIZE;
		g2d.drawRect(columnPixelPosition, p.y * GATE_PIXEL_SIZE, columnWidth - 1, GATE_PIXEL_SIZE - 1);
		g2d.setFont(ResourceLoader.MPLUS.deriveFont(16));
		CircuitBoardRenderContext.drawCenteredString(g2d, Integer.toString(currentGateRegister),
				columnPixelPosition, p.y * GATE_PIXEL_SIZE, columnWidth - 1, GATE_PIXEL_SIZE - 1);
		
		g2d.dispose();
		window.getRenderContext().paintBaseImageWithOverlay(bi);
	}
	
	private void drawGateSelection(Point p) {
		BufferedImage bi = window.getRenderContext().getOverlay();
		Graphics2D g2d = (Graphics2D)bi.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.RED);
		
		int columnPosition = columnPixelSelection;
		int columnWidth = window.getRenderContext().getColumnWidth(p.x) * GATE_PIXEL_SIZE;
		if(window.getGateChooser().getSelectedGate().isMultiQubitGate())
			g2d.drawRect(columnPosition, 0, columnWidth - 1, bi.getHeight() - 1);
		else
			g2d.drawRect(columnPosition, p.y * GATE_PIXEL_SIZE, columnWidth - 1, GATE_PIXEL_SIZE - 1);
		
		
		g2d.dispose();
		window.getRenderContext().paintBaseImageWithOverlay(bi);
	}
	
	private void solderSingleQubit(Point p, AbstractGate gate) {
		SolderedGate sg = new SolderedGate(gate, 0, 0);
		SolderedRegister sr = new SolderedRegister(sg, 0);
		
		window.getSelectedBoard().detachAllGatesWithinRange(p.y, p.y, p.x);
		
		window.getSelectedBoard().setSolderedRegister(p.x, p.y, sr);
		window.getRenderContext().paintRerenderedBaseImageOnly();
		window.getSelectedBoard().setUnsaved();
	}
	
	private void startMultiQubitSelection(Point p, AbstractGate gate) {
		
		
		selectedGate = gate;
		stage = Stage.SELECTED_MULTIQUBITGATE;
		
		
		currentGateRegister = 0;
		registers = new int[gate.getNumberOfRegisters()];
		lastGateRegisterEdited = 0;
		selectedColumn = p.x;
		columnPixelPosition = columnPixelSelection;
		firstLocalRegister = 0;
		lastLocalRegister = 0;
		
		grayedOut = window.getRenderContext().getOverlay();
		Graphics2D g2d = (Graphics2D)grayedOut.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(240, 240, 240, 180));
		
		int xOffset = columnPixelPosition + window.getRenderContext().getColumnWidth(selectedColumn) * GATE_PIXEL_SIZE;
		g2d.fillRect(0, 0, columnPixelPosition, grayedOut.getHeight());
		g2d.fillRect(xOffset, 0, grayedOut.getWidth() - xOffset, grayedOut.getHeight());
		
		g2d.dispose();
		prevHoveredGrid = null;
		drawMultiQubitSelection(p);
	}
	
	private int scanSelected(int boardRegister) {
		int y = -1;
		while(++y < lastGateRegisterEdited)
			if(boardRegister == registers[y])
				return y;
		return -1;
	}
	
	
	
	@Override
	public void mouseExited(MouseEvent e) {
		if(stage == Stage.NO_SELECTION) {
			BufferedImage bi = window.getRenderContext().getOverlay();
			window.getRenderContext().paintBaseImageWithOverlay(bi);
		}else {
			window.getRenderContext().paintBaseImageWithOverlay(grayedOut);
		}
		prevHoveredGrid = null;
	}
	
	
	private void drawImageOnImage(Graphics2D g2d, BufferedImage bottom, BufferedImage top) {
		g2d.drawImage(top, 0, 0, top.getWidth(), top.getHeight(), null);
	}
	
	
	
	
	
	public void setGridLocation(Point mouseCoords) {
		int[] params = window.getRenderContext().getGridColumnPosition(mouseCoords.x);
		mouseCoords.x = params[0];
		columnPixelSelection = params[1];
		mouseCoords.y = (int) (mouseCoords.getY() / GATE_PIXEL_SIZE);
	}

	@Override
	public void onSelected() {
		prevHoveredGrid = null;
		stage = Stage.NO_SELECTION;
	}
	
	
	@Override
	public void onUnselected() {
		window.getRenderContext().removeOverlay();
	}
}
