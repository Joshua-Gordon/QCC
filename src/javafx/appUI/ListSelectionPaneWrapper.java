package javafx.appUI;

import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public abstract class ListSelectionPaneWrapper implements Iterable<Node> {
	private Pane p;
	private int elementSelected = -1;
	
	public ListSelectionPaneWrapper(Pane p) {
		this.p = p;
	}
	
	public void addElementToEnd(Node n) {
		if(whenElementIsAdded(p.getChildren().size() - 1, n))
			p.getChildren().add(new ListElement(n));
	}
	
	public void addElement(int index, Node n) {
		if(whenElementIsAdded(index, n))
			p.getChildren().add(index, new ListElement(n));
	}
	
	public void removeElement(int index) {
		ListElement n = (ListElement) p.getChildren().get(index);
		if(whenElementIsRemoved(index, n.getContent()))
			p.getChildren().remove(index);
	}
	
	public void clear() {
		if(whenCleared())
			p.getChildren().clear();
	}
	
	public int size() {
		return p.getChildren().size();
	}
	
	public Node getElement(int index) {
		return ((ListElement) p.getChildren().get(index)).getContent();
	}
	
	protected abstract boolean whenElementIsRemoved(int index, Node n);
	protected abstract boolean whenElementIsAdded(int index, Node n);
	protected abstract boolean whenElementMoves(int indexFirst, int indexNext);
	protected abstract boolean whenCleared();
	
	
	private class ListElement extends HBox {
		
		private ListElement (Node n) {
			
			Button button = new Button("x");
			
			getChildren().addAll(n, button);
			
			button.setOnAction(new ListRemovedEventHandler(this));
			
			setSpacing(5);
			setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(n, Priority.ALWAYS);
			setPadding(new Insets(5, 5, 5, 5));
			addEventFilter(MouseEvent.DRAG_DETECTED, new ListSelectedEventHandler(this));
			addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, new ListDroppedEventHandler(this));
			
			addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, (e) -> {
				int index = p.getChildren().indexOf(this);
				if(index != elementSelected) {
					if(index < elementSelected)
						setStyle("-fx-border-width: 2 0 0 0; -fx-border-color: #00FF00;");
					else
						setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: #00FF00;");
				}
			});
			
			addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED, (e) -> {
				setStyle("-fx-border-insets: 0 0 0 0; -fx-border-color: #00000000;");
			});
			
		}
		
		private Node getContent() {
			return getChildren().get(0);
		}
	}
	
	
	private class ListRemovedEventHandler implements EventHandler<ActionEvent> {
		
		private final ListElement le;
		
		public ListRemovedEventHandler (ListElement le) {
			this.le = le;
		}
		
		@Override
		public void handle(ActionEvent event) {
			ObservableList<Node> listElements = p.getChildren();
			int index = listElements.indexOf(le);
			if(whenElementIsRemoved(index, le.getContent()))
				listElements.remove(le);
		}
		
	}
	
	private class ListSelectedEventHandler implements EventHandler<MouseEvent> {
		
		private final ListElement le;
		
		public ListSelectedEventHandler (ListElement le) {
			this.le = le;
		}
		
		@Override
		public void handle(MouseEvent event) {
			ListElement dragging = le;
			elementSelected = p.getChildren().indexOf(dragging);
			dragging.startFullDrag();
		}
		
	}
	
	private class ListDroppedEventHandler implements EventHandler<MouseEvent> {
		
		private final ListElement le;
		
		public ListDroppedEventHandler (ListElement le) {
			this.le = le;
		}
		
		@Override
		public void handle(MouseEvent event) {
			ObservableList<Node> listElements = p.getChildren();
			
			int insertIndex = listElements.indexOf(le);
			if(insertIndex == elementSelected || elementSelected == -1) {
				elementSelected = -1;
				return;
			}
			
			if(whenElementMoves(elementSelected, insertIndex)) {
				Node n = listElements.remove(elementSelected);
				listElements.add(insertIndex, n);
			}
		}
	}
	
	@Override
	public Iterator<Node> iterator() {
		return new ListIterator();
	}
	
	private class ListIterator implements Iterator<Node> {
		private final Iterator<Node> iter = p.getChildren().iterator();
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Node next() {
			return ((ListElement) iter.next()).getContent();
		}
	}
}
