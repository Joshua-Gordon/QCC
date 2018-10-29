package utils.customCollections.eventTracableCollections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class EventArrayList <T> extends Notifier implements Iterable<T>, Serializable{
	private static final long serialVersionUID = -4916749353160666682L;
	
	private ArrayList<T> list;
	
	public EventArrayList(Notifier receiver) {
		list = new ArrayList<>();
		setReceiver(receiver);
	}
	
	public int size() {
		return list.size();
	}
	
	public T get(int i) {
		return list.get(i);
	}
	
	public boolean contains(T obj) {
		return list.contains(obj);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	
	public void set(int index, T value) {
		sendChange(this, "set", index, value);
		list.set(index, value);
	}
	
	public void remove(int index) {
		sendChange(this, "remove", index);
		list.remove(index);
	}
	
	public void add(T value) {
		sendChange(this, "add", value);
		list.add(value);
	}
	
	public void add(int index, T value) {
		sendChange(this, "add", index, value);
		list.add(index, value);
	}
	
	public void clear() {
		sendChange(this, "clear");
		list.clear();
	}

	@Override
	public Iterator<T> iterator() {
		return new EventIterator<T>(list);
	}

	
	
	
}
