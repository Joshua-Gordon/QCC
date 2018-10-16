package language.finiteAutomata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import language.compiler.Token;
import utils.customCollections.Pair;
import utils.customCollections.Queue;

public class DFA {
	private final TransitionTable<Integer> transitions;
	private final AcceptTable acceptingStates;
	private final InputAlphabet inputAlphabet;
	
	/**
	 * Creates a DFA with a start state which is always 0
	 * @param transitions
	 * @param acceptingStates
	 */
	public DFA (InputAlphabet inputAlphabet, TransitionTable<Integer> transitions, AcceptTable acceptingStates) {
		this.inputAlphabet = inputAlphabet;
		this.transitions = transitions;
		this.acceptingStates = acceptingStates;
	}
	
	
	
	public boolean accepts(BufferedReader br) throws IOException {
		Integer currentState = 0;
		int c;
		
		while((c = br.read()) != -1) {
			currentState = getNextState(currentState, (char) c);
			if(currentState == null) return false;
		}
		
		return isAccepting(currentState);
	}
	
	public Pair<Token, String> getNextToken (PushbackReader pr) throws IOException {
		Integer lastAcceptingState = null;
		Integer lastAcceptingIndex = 0;
		Integer currentState = 0;
		LinkedList<Character> outputChars = new LinkedList<>();
		
		
		int c;
		
		while((c = pr.read()) != -1) {
			outputChars.offerFirst((char)c);
			currentState = getNextState(currentState, (char) c);
			if(currentState == null) break;
			
			if (isAccepting(currentState)) {
				lastAcceptingState = currentState;
				lastAcceptingIndex = outputChars.size();
			}
		}
		
		Iterator<Character> iterator = outputChars.iterator();
		int size = outputChars.size() - lastAcceptingIndex;
		char [] giveBack = new char[size];
		
		for(int i = size - 1; i > -1 ; i --)
			giveBack[i] = iterator.next();
		
		pr.unread(giveBack);
		
		if(lastAcceptingState == null) return null;
		
		char[] stringChars = new char[lastAcceptingIndex];
		for(int i = lastAcceptingIndex - 1; i > -1; i --)
			stringChars[i] = iterator.next();
		
		Token lastAcceptingToken = getAcceptingToken(lastAcceptingState);
		return new Pair<Token, String>(lastAcceptingToken, new String(stringChars));
	}
	
	public Integer getNextState (Integer state, char inputChar) {
		if(inputAlphabet.contains(inputChar))
			return transitions.get(state).get(inputChar);
		else
			if(inputAlphabet.contains(NFA.OTHER_CHARS))
				return transitions.get(state).get(NFA.OTHER_CHARS);
			else
				return null;
	}
	
	public Boolean isAccepting (Integer state) {
		return acceptingStates.containsKey(state);
	}
	
	public Token getAcceptingToken (Integer state) {
		return acceptingStates.get(state);
	}
	
	@SuppressWarnings("serial")
	public static class InputAlphabet extends HashSet<Character> {}
	@SuppressWarnings("serial")
	public static class TransitionTable <T> extends ArrayList<Hashtable<Character, T>> {}
	@SuppressWarnings("serial")
	public static class AcceptTable extends Hashtable<Integer, Token>{}
}
