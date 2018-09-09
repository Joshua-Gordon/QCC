package utils;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;

public class EventHashTable <S, T> extends Notifier implements Serializable {
	private static final long serialVersionUID = 5065741284350025195L;
	
	private final Hashtable<S, T> table;
	
	public EventHashTable(Notifier receiver) {
		table = new Hashtable<>();
		setReceiver(receiver);
	}
	
	public int size() {
		return table.size();
	}
	
	public T get(S key) {
		return table.get(key);
	}
	
	public boolean containsKey(S key) {
		return table.containsKey(key);
	}
	
	public Iterable<S> getKeyIterable() {
		return new Iterable<S>() {	
			@Override
			public Iterator<S> iterator() {
				return new EventIterator<S>(table.keySet());
			}
		};
	}
	
	public Iterable<T> getValueIterable(){
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new EventIterator<T>(table.values());
			}
		};
	}
	
	public boolean contains(T obj) {
		return table.contains(obj);
	}
	
	public boolean isEmpty() {
		return table.isEmpty();
	}
	
	
	public void put(S key, T value) {
		sendChange(this, "put", key, value);
		table.put(key, value);
	}
	
	public void remove(S key) {
		sendChange(this, "remove", key);
		table.remove(key);
	}
	
	public void clear() {
		sendChange(this, "clear");
		table.clear();
	}

}
