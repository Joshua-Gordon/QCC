package utils.customCollections;

import java.util.Iterator;

public class CommandParameterList implements Iterable<String>{
	private String[] parameters;
	
	public CommandParameterList (String ... parameters) {
		this.parameters = parameters;
	}
	
	public String get(int index) {
		if( index >= parameters.length )
			throw new ParameterOutOfBoundsException(index);
		return parameters[index];
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
	public Iterator<String> iterator() {
		return new CommandIterator();
	}
	
	
	private class CommandIterator implements Iterator<String> {
		int index = 0;
		
		@Override
		public boolean hasNext() {
			return index != parameters.length;
		}

		@Override
		public String next() {
			return parameters[index++];
		}

	}
}
