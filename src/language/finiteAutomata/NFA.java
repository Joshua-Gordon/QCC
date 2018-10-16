package language.finiteAutomata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import language.compiler.Token;
import language.finiteAutomata.DFA.AcceptTable;
import language.finiteAutomata.DFA.TransitionTable;
import utils.customCollections.Pair;
import language.finiteAutomata.DFA.InputAlphabet;

public class NFA {
	public static final char EPSILION = '\u0000';
	public static final char OTHER_CHARS = (char) -1;
	
	private static final char[] NUMBER_CHARS = new char [] {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
	};
	
	private static final char[] WORD_CHARS = new char [] {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
		'_'
	};
	
	private static final char[] WHITE_SPACE_CHARS = new char[] {
		' ', '\u0009', '\n', '\u000B', '\u000C', '\r', '\u001C', '\u001D', '\u001E', '\u001F'
	};
	
	private int startState;
	private InputAlphabet inputAlphaBet;
	private TransitionTable<HashSet<Integer>> transitions;
	private AcceptTable acceptingStates;
	
	public static NFA acceptSpace (Token acceptingToken) {
		return acceptCharacters(WHITE_SPACE_CHARS, acceptingToken);
	}
	
	public static NFA acceptWord (Token acceptingToken) {
		return acceptCharacters(WORD_CHARS, acceptingToken);
	}
	
	public static NFA acceptNumbers (Token acceptingToken) {
		return acceptCharacters(NUMBER_CHARS, acceptingToken);
	}
	
	public static NFA acceptEverythingBut(char[] list, Token acceptingToken) {
		int startState = 0;
		InputAlphabet alphabet = new InputAlphabet();
		TransitionTable<HashSet<Integer>> transitionTable = new TransitionTable<>();
		Hashtable<Character, HashSet<Integer>> table = new Hashtable<>();
		AcceptTable acceptTable = new AcceptTable();
		
		for(char c : list)
			alphabet.add(c);
		
		alphabet.add(OTHER_CHARS);
		HashSet<Integer> nextStates = new HashSet<>();
		
		nextStates.add(1);
		table.put(OTHER_CHARS, nextStates);
		transitionTable.add(table);
		transitionTable.add(new Hashtable<>());
		acceptTable.put(1, acceptingToken);
		
		return new NFA(startState, alphabet, transitionTable, acceptTable);
	}
	
	public static NFA acceptCharacters (char[] list, Token acceptingToken) {
		int startState = 0;
		InputAlphabet alphabet = new InputAlphabet();
		TransitionTable<HashSet<Integer>> transitionTable = new TransitionTable<>();
		Hashtable<Character, HashSet<Integer>> table = new Hashtable<>();
		AcceptTable acceptTable = new AcceptTable();
		
		HashSet<Integer> nextStates;
		for(char c : list) {
			alphabet.add(c);
			nextStates = new HashSet<>();
			nextStates.add(1);
			table.put(c, nextStates);
		}
		transitionTable.add(table);
		transitionTable.add(new Hashtable<>());
		acceptTable.put(1, acceptingToken);
		
		return new NFA(startState, alphabet, transitionTable, acceptTable);
	}
	
	
	
	
	
	
	
	
	
	public NFA (int startState, InputAlphabet inputAlphaBet, TransitionTable<HashSet<Integer>> transitions, AcceptTable acceptingStates) {
		this.startState = startState;
		this.inputAlphaBet = inputAlphaBet;
		this.transitions = transitions;
		this.acceptingStates = acceptingStates;
	}
	
	public NFA concat(NFA other) {
		inputAlphaBet.addAll(other.inputAlphaBet);
		
		Integer numStates = transitions.size();
		
		transitions.addAll(other.transitions);
		
		Hashtable<Character, HashSet<Integer>> transition;
		HashSet<Integer> nextStates;
		
		for(Integer i : acceptingStates.keySet()) {
			transition = transitions.get(i);
			if(transition.containsKey(EPSILION)) {
				nextStates = transition.get(EPSILION);
				nextStates.add(numStates + other.startState - i);
			} else {
				nextStates = new HashSet<>();
				nextStates.add(numStates + other.startState - i);
				transition.put(EPSILION, nextStates);
			}
		}
		
		acceptingStates = new AcceptTable();
		
		for(Integer i : other.acceptingStates.keySet())
			acceptingStates.put(i + numStates, other.acceptingStates.get(i));
		
		return this;
	}
	
	public NFA union (NFA other) {
		inputAlphaBet.addAll(other.inputAlphaBet);
		
		
		Integer numStates = transitions.size();
		Integer numStatesOther = other.transitions.size();
		
		transitions.addAll(other.transitions);
		for(Integer i : other.acceptingStates.keySet())
			acceptingStates.put(i + numStates, other.acceptingStates.get(i));
		
		
		// Adding a new start state with epsilon transitions
		Hashtable<Character, HashSet<Integer>> firstTransition = new Hashtable<>();
		HashSet<Integer> newStates = new HashSet<>();
		
		Integer firstStartState = startState - (numStates + numStatesOther);
		Integer secondStartState = other.startState - numStatesOther;
		
		newStates.add(firstStartState);
		newStates.add(secondStartState);
		firstTransition.put(EPSILION, newStates);
		transitions.add(firstTransition);
		
		// The new start state
		startState = numStates + numStatesOther;
		
		return this;
	}
	
	public NFA star () {
		
		Hashtable<Character, HashSet<Integer>> transition;
		HashSet<Integer> nextStates;
		
		// adding all epsilon transitions to the start state
		for (Integer i : acceptingStates.keySet()) {
			transition = transitions.get(i);
			if (transition.containsKey(EPSILION)) {
				nextStates = transition.get(EPSILION);
				nextStates.add(startState - i);
			} else {
				nextStates = new HashSet<>();
				nextStates.add(startState - i);
				transition.put(EPSILION, nextStates);
			}
		}
		
		// making new start state with epsilon transitions
		Integer numStates = transitions.size();
		
		Token token = acceptingStates.isEmpty()? null : acceptingStates.values().iterator().next();
		acceptingStates.put(numStates, token);
		
		transition = new Hashtable<>();
		nextStates = new HashSet<>();
		nextStates.add(startState - numStates);
		transition.put(EPSILION, nextStates);
		transitions.add(transition);
		
		// making new start state 
		startState = numStates;
		
		return this;
	}
	
	public DFA converToDFA () {
		TransitionTable<Integer> dfaTransitions = new TransitionTable<>();
		AcceptTable dfaAcceptingStates = new AcceptTable();
		
		Hashtable<DFAStateID, Integer> dfaIDToStatesMap = new Hashtable<>();
		DFAStateID startDFAStateID = new DFAStateID();
		Token acceptingToken = getAllEpsilonTransitionStates(startState, startDFAStateID);
		
		dfaIDToStatesMap.put(startDFAStateID, 0);
		if(acceptingToken != null)
			dfaAcceptingStates.put(0, acceptingToken);
		
		fillDFAStateTransitions(0, startDFAStateID, dfaTransitions, dfaAcceptingStates, dfaIDToStatesMap);
		
		return new DFA(inputAlphaBet, dfaTransitions, dfaAcceptingStates);
	}
	
	private void fillDFAStateTransitions (
			Integer dfaState, DFAStateID dfaStateID, 
			TransitionTable<Integer> dfaTransitions,	AcceptTable dfaAcceptingStates, 
			Hashtable<DFAStateID, Integer> dfaIDToStatesMap) {
		
		Token tempToken;
		DFAStateID nextDFAStateID;
		Integer nextDFAState;
		
		Hashtable<Character, Integer> transition = new Hashtable<>();
		dfaTransitions.add(transition);
		
		for(char c : inputAlphaBet) {
			nextDFAStateID = new DFAStateID();
			tempToken = getNextDFAState(dfaStateID, c, nextDFAStateID);
			if(!nextDFAStateID.isEmpty()) {
				if(dfaIDToStatesMap.containsKey(nextDFAStateID)) {
					nextDFAState = dfaIDToStatesMap.get(nextDFAStateID);
				} else {
					nextDFAState = dfaIDToStatesMap.size();
					dfaIDToStatesMap.put(nextDFAStateID, nextDFAState);
					if(tempToken != null) // here may reside problem
						dfaAcceptingStates.put(nextDFAState, tempToken);
					fillDFAStateTransitions(nextDFAState, nextDFAStateID, dfaTransitions, dfaAcceptingStates, dfaIDToStatesMap);
				}
				transition.put(c, nextDFAState);
			}
		}
		
	}
	
	
	private Token getNextDFAState (DFAStateID dfaStateID, char inputChar, DFAStateID nextDFAStateID) {
		HashSet<Integer> nextStates;
		Token acceptingToken = null;
		Token tempToken;
		
		for(Integer state : dfaStateID) {
			nextStates = getNextStates(state, inputChar);
			if(nextStates != null) {
				for(Integer stateE : nextStates) {
					tempToken = getAllEpsilonTransitionStates(state + stateE, nextDFAStateID);
					if (tempToken != null)
						acceptingToken = tempToken;
				}
			}
		}
		
		return acceptingToken;
	}
	
	private Token getAllEpsilonTransitionStates(Integer nfaState, DFAStateID dfaStatesCollected){
		if (dfaStatesCollected.contains(nfaState))
			return null;
		
		dfaStatesCollected.add(nfaState);
		
		Token acceptingToken = getAcceptingToken(nfaState);
		
		HashSet<Integer> nextEpsilionStates = getNextStates(nfaState, EPSILION);
		
		if(nextEpsilionStates == null)
			return acceptingToken;
		
		Token currentAcceptingAttr;
		
		for (Integer state : nextEpsilionStates) {
			currentAcceptingAttr = getAllEpsilonTransitionStates(nfaState + state, dfaStatesCollected);
			if(currentAcceptingAttr != null)
				acceptingToken = currentAcceptingAttr;
		}
		
		return acceptingToken;
	}
	
	private HashSet<Integer> getNextStates (Integer state, char inputChar) {
		return transitions.get(state).get(inputChar);
	}
	
	private Token getAcceptingToken (Integer state) {
		return acceptingStates.get(state);
	}
	
	@SuppressWarnings("serial")
	public static class DFAStateID extends HashSet<Integer> {}
	
}
