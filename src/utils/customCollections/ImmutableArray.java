package utils.customCollections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public class ImmutableArray <T> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = 3565001439834778170L;
	
	private final Object[] array;
	
	public ImmutableArray(Collection<T> collection) {
		this.array = new Object[collection.size()];
		int i = 0;
		for(T element : collection)
			array[i++] = element;
	}
	
	@SafeVarargs
	public ImmutableArray (T ... array) {
		this.array = new Object[array.length];
		int i = 0;
		for(T value : array)
			this.array[i++] = value;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T) array[index];
	}
	
	public int size () {
		return array.length;
	}

	public boolean isEmpty () {
		return size() == 0;
	}
	
	@SuppressWarnings("unchecked")
	public T[] toArray(T[] elements) {
		if(elements.length != array.length)
			throw new IllegalArgumentException("input array must be the same size of this array");
		
		int i = 0;
		for(Object o : array)
			elements[i++] = (T) o;
		
		return elements;
	}
	
	@Override
	public Iterator<T> iterator() {
		
		return new Iterator<T>() {
			private int index = -1;
			
			@Override
			public boolean hasNext() {
				return index + 1 != array.length;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				return (T) array[++index];
			}
		};
	}
}
