package utils.customCollections;

import java.util.Iterator;

public class CommandParameterList implements Iterable<Object>{
	private Object[] parameters;
	
	public CommandParameterList (Object ... parameters) {
		this.parameters = parameters;
	}
	
	public Object get(int index) {
		if( index >= parameters.length )
			throw new ParameterOutOfBoundsException(index);
		return parameters[index];
	}
	
	public String getString(int index) {
		return get(index).toString();
	}
	
	@SuppressWarnings("serial")
	public class ParameterOutOfBoundsException extends RuntimeException {
		private ParameterOutOfBoundsException (int index) {
			super ("Command requires argument number \"" + (index + 1) + "\"") ;
		}
		
	}
	
	public int size() {
		return parameters.length;
	}

	@Override
	public Iterator<Object> iterator() {
		return new CommandIterator();
	}
	
	public Iterable<String> stringIterable() {
		return new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return new CommandStringIterator() ;
			}
		};
	}
	
	
	private class CommandIterator implements Iterator<Object> {
		int index = 0;
		
		@Override
		public boolean hasNext() {
			return index != parameters.length;
		}

		@Override
		public Object next() {
			return parameters[index++];
		}

	}
	
	private class CommandStringIterator implements Iterator<String> {
		int index = 0;
		
		@Override
		public boolean hasNext() {
			return index != parameters.length;
		}

		@Override
		public String next() {
			return parameters[index++].toString();
		}

	}
	
}
