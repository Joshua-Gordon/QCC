package utils.customCollections.eventTracableCollections;

import java.io.Serializable;
import java.util.Iterator;

public class EventIterator <T> implements Iterator<T>, Serializable{
	private static final long serialVersionUID = 2748512940555104382L;
	
	private Iterator<T> iterator;
	
	public EventIterator(Iterable<T> iterable) {
		this.iterator = iterable.iterator();
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	public T next() {
		return iterator.next();
	}

}
