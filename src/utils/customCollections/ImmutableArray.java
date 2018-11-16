package utils.customCollections;

import java.io.Serializable;
import java.util.Iterator;

public class ImmutableArray <T> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = 3565001439834778170L;
	
	private final T[] array;
	
	public ImmutableArray (T ... array) {
		this.array = array;
	}
	
	public T get(int index) {
		return array[index];
	}
	
	public int size () {
		return array.length;
	}

	public boolean isEmpty () {
		return size() == 0;
	}
	
	@Override
	public Iterator<T> iterator() {
		
		return new Iterator<T>() {
			private int index = -1;
			
			@Override
			public boolean hasNext() {
				return index + 1 != array.length;
			}
			
			@Override
			public T next() {
				return array[++index];
			}
		};
	}
}
