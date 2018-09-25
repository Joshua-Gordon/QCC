package mathLib.expression;

import utils.SimpleLinkedList;
import utils.SimpleLinkedList.LinkedIterator;

public abstract class Node {
	
	public static enum NodeType {
		OP, FUNC, VAR, CONST;
	}
	
	public abstract NodeType getNodeType();
	public abstract Node duplicate();
	
	
	protected SimpleLinkedList<Node> duplicateNodes(SimpleLinkedList<Node> toCopy){
		SimpleLinkedList<Node> copy = new SimpleLinkedList<>();
		LinkedIterator<Node> iterator = toCopy.iterator();
		
		SimpleLinkedList<Node> cur = copy;
		while(iterator.hasNext()) {
			cur.add(iterator.next().duplicate());
			cur = cur.splitNext();
		}
		
		return copy;
	}
}
