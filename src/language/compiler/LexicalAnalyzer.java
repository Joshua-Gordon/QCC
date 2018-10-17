package language.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import language.finiteAutomata.DFA;
import utils.customCollections.Pair;
import utils.customCollections.Queue;
import utils.customCollections.Queue.QueueIterator;

public class LexicalAnalyzer {
	private final DFA dfa;
	
	LexicalAnalyzer (DFA dfa) {
		this.dfa = dfa;
	}
	
	@SafeVarargs
	public LexicalAnalyzer (Pair<Token, String> ... tokenRegexPair) {
		
		// to be implemented
		
		dfa = null;
	}
	
	
	
	public Stream<Pair<Token, String>> getTokenStream (BufferedReader br) {
		return Stream.generate( new Supplier<Pair<Token, String>>() {
			
			private Queue<Character> buffer = new Queue<>();
			
			@SuppressWarnings("rawtypes")
			@Override
			public Pair<Token, String> get() {

				Integer currentState = 0;
				Integer lastAcceptingState = null;
				char c;
				
				boolean contRead = true;
				
				
				
				// start reading from buffered chars from last invocation of getNextToken() and traverse through DFA
				QueueIterator iterator = buffer.iterator();
				while(iterator.hasNext()) {
					c = (char) iterator.next();
					
					currentState = dfa.getNextState(currentState, (char) c);
					if(currentState == null) {
						contRead = false;
						break;
					}
					
					if (dfa.isAccepting(currentState)) {
						lastAcceptingState = currentState;
						iterator.mark();
					}
				}
				
				
				
				// start reading chars from BufferedReader and traverse through DFA
				int ci;
				if(contRead) {
					try {
						while((ci = br.read()) != -1) {
							
							buffer.enqueue((char) ci);
							currentState = dfa.getNextState(currentState, (char) ci);
							if(currentState == null) break;
							
							if (dfa.isAccepting(currentState)) {
								lastAcceptingState = currentState;
								buffer.mark();
							}
						}
					} catch (IOException e) {
						throw new LexicalAnaylizerIOException(e.getMessage());
					}
				}
				
				
				
				// if no states were ever accepted 
				if (!buffer.isMarked())
					if(buffer.isEmpty())
						// end of the stream
						return null;
					else
						// the remaining char sequence does not satisfied any
						// of the patterns of the specified tokens
						throw new LexemeNotRecognizedException();
				
				
				
				// all the characters that weren't used in the accepting string 
				// must be used for next invocation of getNextToken()
				Queue<Character> backToBufferChars = buffer.splitAtMark();	
				
				
				
				// load all accepting chars into a string
				char[] acceptingChars = new char[buffer.size()];
				int i = 0;
				for(char accept : buffer)
					acceptingChars[i++] = accept;
				String s = new String (acceptingChars);
				
				
				
				
				// buffer for next invocation of getNextToken()
				buffer = backToBufferChars;
				
				return new Pair<>(dfa.getAcceptingToken(lastAcceptingState), s);
			}
		});
	}
	
	@SuppressWarnings("serial")
	public static class LexemeNotRecognizedException extends RuntimeException {
		
		public LexemeNotRecognizedException () {
			super ("No token could recognize the remaining char sequence");
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class LexicalAnaylizerIOException extends RuntimeException {
		public LexicalAnaylizerIOException(String message) {
			super(message);
		}
	}
}
