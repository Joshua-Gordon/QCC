package utils.customCollections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class OrderedSet <T> implements Collection<T>, Serializable {
	private static final long serialVersionUID = 767801037222203057L;
	
	private final HashSet<T> set;
	private final ArrayList<T> list;
	
	private OrderedSet(HashSet<T> set, ArrayList<T> list) {
		this.set = set;
		this.list = list;
	}
	
	public OrderedSet() {
		this(new HashSet<>(), new ArrayList<>());
	}
	
	@SuppressWarnings("unchecked")
	public OrderedSet (T ... values) {
		this();
		addAll(values);
	}
	
	public OrderedSet(Collection<T> c) {
		this();
		addAll(c);
	}
	
	@SuppressWarnings("unchecked")
	public boolean addAll(T ... values) {
		boolean containsValue = false;
		for(T value : values) {
			if(!set.add(value)) {
				list.add(value);
			} else {
				containsValue = true;
			}
		}
		return containsValue;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return new OrdIter();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <E> E[] toArray(E[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(T e) {
		if(!set.add(e)) {
			list.add(e);
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if(!set.remove(o)) {
			list.remove(o);
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean hasChanged = false;
		for(T e : c)
			hasChanged |= add(e);
		return hasChanged;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for(Object e : c)
			hasChanged |= remove(e);
		return hasChanged;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean hasChanged = false;
		Iterator<T> elements = list.iterator();
		while(elements.hasNext()) {
			Object e = elements.next();
			if(!c.contains(e)) {
				set.remove(e);
				elements.remove();
				hasChanged = true;
			}
		} 
		return hasChanged;
	}

	@Override
	public void clear() {
		set.clear();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OrderedSet<T> clone() {
		return new OrderedSet(set.clone(), list.clone());
	}
	
	private class OrdIter implements Iterator<T> {
		private Iterator<T> iter = list.iterator();
		private T lastValue;
		private int index = -1;
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public T next() {
			index++;
			return lastValue = iterator().next();
		}
		
		@Override
		public void remove() {
			set.remove(lastValue);
			list.remove(index);
		}
		
	}
}
