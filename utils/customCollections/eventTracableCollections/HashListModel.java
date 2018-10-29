package utils.customCollections.eventTracableCollections;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.function.BiConsumer;

import javax.swing.AbstractListModel;

public class HashListModel <T, E> extends AbstractListModel<E>{
	private static final long serialVersionUID = -7123014423888876242L;
	
	private Hashtable<T, E> hashTable = new Hashtable<>();
	
	@Override
	public int getSize() {
		return hashTable.size();
	}

	@Override
	public E getElementAt(int index) {
		Iterator<E> it = hashTable.values().iterator();
        for(int i = 0; i < index; ++i)
            it.next();
        return it.next();
	}
	
	public E get(T key) {
		return hashTable.get(key);
	}
	
	public void put(T key, E value) {
		hashTable.put(key, value);
	}
	
	public void forEach(BiConsumer<? super T, ? super E> action) {
		hashTable.forEach(action);
	}
	
	public Collection<E> getValues() {
		return hashTable.values();
	}
	
	public Collection<T> getKeys(){
		return hashTable.keySet();
	}
}
