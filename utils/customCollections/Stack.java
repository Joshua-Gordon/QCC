package utils.customCollections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a Stack Object
 * @author Massimiliano Cutugno
 *
 * @param <T>
 */
public class Stack <T> implements Collection <T>{
	
	private ListNode<T> top = null;
	private ListNode<T> mark = null;
	
	private int size = 0;
	private int markIndex = 0;
	
	public void push (T element) {
		top = new ListNode<>(element, top);
		size++;
	}
	
	public T peak () {
		if(size == 0) throw new EmptyCollectionException();
		return top.element;
	}
	
	public T pop () {
		if(size == 0) throw new EmptyCollectionException();
		if(top == mark)
			unmark();
		T element = top.element;
		top = top.next;
		size --;
		return element;
	}
	
	
	/**
	 * Stores the last added node at the current state of this collection.
	 * Any method using this marked position does not require iterating to this
	 * position, and therfore it very effiecient to mark important places within
	 * this collection if at any time one would desire to go to this state. Only
	 * one node can be marked at one time; if {@link #mark()} is called twice, 
	 * the last marked position is replaced by the last added element to this
	 * collection. If this marked node is removed, then this collection is
	 * {@link #unmark()}.
	 * 
	 */
	public void mark() {
		if(size == 0) throw new EmptyCollectionException();
		mark = top;
		markIndex = size - 1;
	}
	
	/**
	 * Unmarks the last marked node in this collection. If collection is not marked,
	 * nothing is done.
	 * 
	 * @see {@link #mark}
	 */
	public void unmark () {
		mark = null;
		markIndex = 0;
	}
	
	/**
	 * Checks if this collection is marked
	 * @see {@link #mark}
	 * 
	 * @return if this collection is marked or not
	 */
	public boolean isMarked () {
		return mark != null;
	}
	
	/**
	 * @see {@link #mark}
	 * @return if this collection is not marked, then 0 is returned.
	 * otherwise the index of the marked node is returned.
	 */
	public int getMarkedIndex () {
		return markIndex;
	}
	
	public void stackAtMark () {
		if(size == 0) throw new EmptyCollectionException();
		top = mark;
		size = markIndex + 1;
		unmark();
	}
	
	public void removeAllAfterMark () {
		mark.next = null;
		size -= markIndex;
		markIndex = 0; 
	}
	
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		if (o != null) {
			for(T element : this)
				if (element.equals(o))
					return true;
		} else {
			for(T element : this)
				if (element == null)
					return true;
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new Itr();
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[size];
		int i = 0;
		for(T element : this)
			array[i++] = element;
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] a) {
		if (a.length < size)
            a = (E[]) Arrays.copyOf(a, size, a.getClass());
		
		int i = 0;
		for (T element : this)
			a[i++] = (E) element;
		
        if (a.length > size)
            a[size] = null;
        return a;
	}

	@Override
	public boolean add(T e) {
		push(e);
		return true;
	}
	
	@Override
	public boolean remove(Object o) {
		Iterator<T> iterator = this.iterator();
		if (o == null) {
			while(iterator.hasNext()) {
				if (iterator.next() == null) {
					iterator.remove();
					break;
				}
			}
		} else {
			while(iterator.hasNext()) {
				if (iterator.next().equals(o)) {
					iterator.remove();
					break;
				}
			}
		}
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean containsAll = true;
		boolean containsOne;
		for(Object elementX : c) {
			containsOne = false;
			for(T elementY : this) {
				if (elementX.equals(elementY)) {
					containsOne = true;
					break;
				}
			}
			if((containsAll &= containsOne) == false)
				break;
		}
		return containsAll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(Collection<? extends T> c) {
		for(Object element : c)
			push((T) element);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Iterator<T> iterator = this.iterator();
		for (Object o : c)
			if (o == null)
				while(iterator.hasNext())
					if (iterator.next() == null)
						iterator.remove();
			else
				while(iterator.hasNext())
					if (iterator.next().equals(o))
						iterator.remove();
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Iterator<T> iterator = this.iterator();
		T elementX;
		boolean withinCollection;
		while(iterator.hasNext()) {
			elementX = iterator.next();
			withinCollection = false;
			for(Object elementY : c) {
				if (c == null) {
					if (elementX == null) {
						withinCollection = true;
						break;
					}
				} else {
					if (elementX.equals(elementY)) {
						withinCollection = true;
						break;
					}
				}
			}
			if(!withinCollection)
				iterator.remove();
		}
		return false;
	}

	@Override
	public void clear() {
		top = null;
		size = 0;
		unmark();
	}
	
	@Override
	public String toString() {
		String s = "}";
		
		Iterator<T> iterator = iterator();
		
		if(iterator.hasNext()) {
			s = iterator.next().toString() + s;
			while(iterator.hasNext())
				s = iterator.next().toString() + ", " + s;
		}

		s = "{" + s;
		return s;
	}
	
	private class Itr implements Iterator<T> {
		
		private ListNode<T> previous = null;
		private ListNode<T> current = null;
		private ListNode<T> next = top;
		private int index = size;
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public T next() {
			index --;
			previous = current;
			current = next;
			next = next.next;
			return current.element;
		}
		
		@Override
		public void remove () {
			if(current == null) 
				throw new IndexOutOfBoundsException();
			if (previous == null)
				top = next;
			else
				previous.next = next;
			if (current == mark)
				unmark();
			if(index < markIndex)
				markIndex --;
			size --;
		}
	}
	
}
