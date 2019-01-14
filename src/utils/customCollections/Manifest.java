package utils.customCollections;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

public class Manifest <T> implements Serializable {
	private static final long serialVersionUID = -5048176347320317395L;
	
	private final Hashtable<T, ManifestObject> elements;
	
	public Manifest() {
		this.elements = new Hashtable<>();
	}
	
	private Manifest(Hashtable<T, ManifestObject> elements) {
		this.elements = elements;
	}
	
	public ManifestObject add(T element) {
		ManifestObject mo = elements.get(element);
		if(mo == null) {
			mo = new ManifestObject(element);
			elements.put(element, mo);
		} else {
			mo.ocurrances++;
		}
		return mo;
	}
	
	public void replace(T oldValue, T newValue) {
		ManifestObject mo = elements.remove(oldValue);
		
		if(mo != null) {
			mo.element = newValue;
			elements.put(newValue, mo);
		}
	}
	
	public void remove(T element) {
		ManifestObject mo = elements.get(element);
		
		if(mo != null) {
			if(mo.ocurrances == 0)
				elements.remove(element);
			else
				mo.ocurrances--;
		}
	}
	
	public int getOccurrences(T element) {
		ManifestObject mo = elements.get(element);
		if(mo != null)
			return mo.ocurrances + 1;
		return 0;
	}
	
	public boolean contains (T element) {
		return elements.containsKey(element);
	}
	
	public Set<T> getElements () {
		return elements.keySet();
	}
	
	public Manifest<T> deepCopy() {
		Hashtable<T, ManifestObject> temp = new Hashtable<>();
		
		for(T key : elements.keySet()) {
			ManifestObject mo = elements.get(key);
			temp.put(key, mo.clone());
		}
		
		return new Manifest<T>(temp);
	}
	
	public class ManifestObject implements Serializable {
		private static final long serialVersionUID = -4119245424558455962L;
		
		private T element;
		private int ocurrances;
		
		private ManifestObject (T element) {
			this(element, 0);
		}
		
		private ManifestObject(T element, int occurances) {
			this.element = element;
			this.ocurrances = occurances;
		}
		
		public T getObject () {
			return element;
		}
		
		public ManifestObject clone() {
			return new ManifestObject(element, ocurrances);
		}
	}
}
