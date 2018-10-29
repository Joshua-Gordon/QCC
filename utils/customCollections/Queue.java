package utils.customCollections;

import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Iterator;


/**
 * Represents a Queue object
 * @author Massimiliano Cutugno
 *
 * @param <T>
 */
public class Queue<T> implements Collection<T> {
	private ListNode<T> top = null;
	private ListNode<T> mark = null;
	private ListNode<T> bot = null;
	
	private int size = 0;
	private int markIndex = -1;
	
	public void enqueue (T element) {
		if(top == null) {
			top = new ListNode<>(element, null);
			bot = top;
		} else {
			bot.next = new ListNode<>(element, null);
			bot = bot.next;
			if(isMarked())
				markIndex ++;
		}
		size++;
	}
	
	public T dequeue () {
		if (top == null)
			throw new EmptyStackException();
		if(top == mark)
			unmark();
		if(top == bot)
			bot = null;
		
		T element = top.element;
		top = top.next;
		size--;
		
		return element;
	}
	
	public T peak () {
		if(size == 0) throw new EmptyStackException();
		return top.element;
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
		if (size == 0)
			throw new EmptyStackException();
		mark = bot;
		markIndex = 0;
	}
	
	/**
	 * Unmarks the last marked node in this collection. If collection is not marked,
	 * nothing is done.
	 * 
	 * @see {@link #mark}
	 */
	public void unmark () {
		mark = null;
		markIndex = -1;
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
	
	/**
	 * if this queue is marked, then all the elements that have been added since the marking
	 * are remove from this queue instantly.
	 * @return null if this queue is not marked, otherwise, it returns a queue of all the elements that
	 * have been added since the queue was last marked.
	 */
	public Queue<T> splitAtMark () {
		if(!isMarked())
			return null;
		
		Queue<T> queue = new Queue<>();
		queue.top = mark.next;
		queue.bot = bot;
		queue.size = markIndex;
		
		bot = mark;
		bot.next = null;
		size -= markIndex;
		unmark();
		
		return queue;
	}
	
	
	public void queueAtMark (boolean markAtStartNotEnd) {
		if(!isMarked())
			return;
		
		if(markAtStartNotEnd) {
			top = mark;
			size = markIndex + 1;
			unmark();
		} else {
			mark.next = null;
			bot = mark;
			size -= markIndex;
			unmark();
		}
	}
	
	public void removeAllAfterMark () {
		mark.next = null;
		size -= markIndex;
		markIndex = 0; 
	}
	
	public void append (Queue<T> queue) {
		if(this == queue)
			throw new QueueAppendException();
		if (isEmpty()) {
			size = queue.size;
			top = queue.top;
			bot = queue.bot;
		} else {
			size += queue.size;
			if(isMarked())
				markIndex += queue.size;
			bot.next = queue.top;
			bot = queue.bot;
		}
		queue.clear();
	}
	
	public void prepend (Queue<T> queue) {
		if(this == queue)
			throw new QueueAppendException();
		if (queue.isEmpty())
			return;
		size += queue.size;
		queue.bot.next = top;
		top = queue.top;
		queue.clear();
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
	public QueueIterator iterator() {
		return new QueueIterator();
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
		enqueue(e);
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
			enqueue((T) element);
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
		bot = null;
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
	
	public class QueueIterator implements Iterator<T> {
		
		private ListNode<T> previous = null;
		private ListNode<T> current = null;
		private ListNode<T> next = top;
		private int index = size;
		
		@Override
		public boolean hasNext() {
			return next != null;
		}
		
		public void mark() {
			if(current == null) 
				throw new IndexOutOfBoundsException();
			mark = current;
			markIndex = index;
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
			if (previous == null) {
				dequeue();
			} else {
				if (current == bot)
					bot = previous;
				previous.next = next;
				
				if (current == mark)
					unmark();
				if(index < markIndex)
					markIndex --;
				size --;
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class QueueAppendException extends RuntimeException {
		
		public QueueAppendException () {
			super("Cannot append or prepend a queue to itself");
		}
		
	}
}
