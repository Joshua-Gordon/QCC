package utils.customCollections;


/**
 * 
 * Class instances are used to make methods able to output 2 values
 * instead of the usual 1.
 * 
 * @author Massimiliano Cutugno
 *
 * @param <T>
 * @param <E>
 */
public class Pair <T, E> {
	private T first;
	private E second;
	
	public Pair(T first, E second) {
		setBoth(first, second);
	}
	
	public Pair () {
		this (null, null);
	}
	
	
	/**
	 * 
	 * @return first element
	 */
	public T first () {
		return first;
	}
	
	/**
	 * 
	 * @return second element
	 */
	public E second () {
		return second;
	}
	
	
	public void setFirst (T first) {
		this.first = first;
	}
	
	public void setSecond (E second) {
		this.second = second;
	}
	
	public void setBoth (T first, E second) {
		this.first = first;
		this.second = second;
	}
}
