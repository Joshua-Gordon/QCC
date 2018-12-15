package utils.customCollections;

import java.util.Hashtable;
import java.util.Set;

public class Manifest <T> {
	private final Hashtable<T, Integer> elements = new Hashtable<>();
	
	public void add(T element) {
		if(contains(element))
			elements.put(element, elements.get(element) + 1);
		else
			elements.put(element, 0);
	}
	
	public void remove(T element) {
		if(contains(element)) {
			int value = elements.get(element);
			
			if(value == 0)
				elements.remove(element);
			else
				elements.put(element, value - 1);
		} else {
			throw new RuntimeException("Element " + element.toString() + " is not already in this manifest");
		}
	}
	
	public boolean contains (T element) {
		return elements.containsKey(element);
	}
	
	public Set<T> getElements () {
		return elements.keySet();
	}
}
