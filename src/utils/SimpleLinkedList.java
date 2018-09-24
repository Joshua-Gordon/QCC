package utils;



import java.util.Iterator;

public class SimpleLinkedList <T> implements Iterable<T>{
	private SimpleLinkedList<T> next;
	private T element;
	
	
	public SimpleLinkedList () {
		this.next = null;
		this.element = null;
	}
	
	public SimpleLinkedList (T element) {
		this(new SimpleLinkedList<T>(), element);
	}
	
	public SimpleLinkedList (SimpleLinkedList<T> next) {
		this.next = next.next;
	}
	
	private SimpleLinkedList (SimpleLinkedList<T> next, T first) {
		this.next = next;
		this.element = first;
	}
	
	
	public void add(T element) {
		next = new SimpleLinkedList<>(next, element);
	}
	
	public T get() {
		if(next == null) throw new IndexOutOfBoundsException("Linkedlist ends here");
		return next.element;
	}
	
	public SimpleLinkedList<T> splitNext(){
		return next;
	}
	
	public void set(T value) {
		if(next == null) throw new IndexOutOfBoundsException("Linkedlist ends here");
		this.next.element = value;
	}
	
	public void pop() {
		if(next != null)
			next = next.next;
	}
	
	public static class LinkedIterator<T> implements Iterator<T> {	
		private SimpleLinkedList<T> current;
		
		private LinkedIterator (SimpleLinkedList<T> start) {
			this.current = start;
		}
		
		@Override
		public boolean hasNext() {
			return current.next != null;
		}

		@Override
		public T next() {
			current = current.next;
			return current.element;
		}
		
		public SimpleLinkedList<T> getCurrentLinkedList() {
			return current;
		}
	}

	@Override
	public LinkedIterator<T> iterator() {
		return new LinkedIterator<T>(this);
	}
	
	@Override
	public String toString() {
		if(next == null)
			return "{ }";
		
		String s = next.element.toString() + "}";
		
		for(T element : this.splitNext())
			s = element.toString() + ", " + s;
		
		return "{" + s;
	}
	
}
