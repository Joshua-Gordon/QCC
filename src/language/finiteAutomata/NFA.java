package language.finiteAutomata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import language.compiler.Token;
import language.finiteAutomata.DFA.AcceptTable;
import language.finiteAutomata.DFA.InputAlphabet;
import language.finiteAutomata.DFA.StateTransitionTable;
import language.finiteAutomata.DFA.TransitionMap;


/**
 * 
 * This class handles all NFA (Nondeterminant Finite Automata) related functionality. Moreover,
 * {@link NFA} is a class that creates {@link NFA} objects that supports Tokenization (as each accepting state contains a
 * reference to a Token). This class is supported by the language.compiler library.
 * <p>
 * <b> NOTE: </b> Unlike the class {@link DFA}, the transition table when used in a {@link NFA} maps a 
 * character input to multiple values. Each of these values do <b>NOT</b> represent the next state of the 
 * NFA, but it <b>DOES</b> represent how many indexes away the next state is from the current state.
 * 
 * 
 * @author Massimiliano Cutugno
 *
 */
public class NFA implements Cloneable{
	
	public static final char EPSILION = '\u0000';
	
	
	public static final char[] DIGIT_CHARS = new char [] {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
	};
	
	public static final char[] WORD_CHARS = new char [] {
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
		'_'
	};
	
	public static final char[] WHITE_SPACE_CHARS = new char[] {
		' ', '\u0009', '\n', '\u000B', '\u000C', '\r', '\u001C', '\u001D', '\u001E', '\u001F'
	};
	
	private int startState;
	private InputAlphabet inputAlphaBet;
	private StateTransitionTable<HashSet<Integer>> transitions;
	private AcceptTable acceptingStates;
	
	
	/**
	 * Creates an NFA that does not accept anything
	 * 
	 * @return a newly created {@link NFA} that does not accept any character sequence (including epsilon)
	 */
	public static NFA acceptNone() {
		StateTransitionTable<HashSet<Integer>> table = new StateTransitionTable<>();
		table.add(new TransitionMap<>());
		return new NFA(0, new InputAlphabet(), table, new AcceptTable());
	}
	
	/**
	 * Creates a {@link NFA} that only accepts an empty string
	 * 
	 * @param token the accepting token associated with the accepting state of this {@link NFA}
	 * @return a newly created {@link NFA} that does not accept any character sequence except for a empty string
	 */
	public static NFA acceptStart(Token token) {
		StateTransitionTable<HashSet<Integer>> table = new StateTransitionTable<>();
		table.add(new TransitionMap<>());
		AcceptTable accept = new AcceptTable();
		accept.put(0, token);
		return new NFA(0, new InputAlphabet(), table, accept);
	}
	
	
	/**
	 * Creates a {@link NFA} that either accepts a single white space character
	 * or accepts anything other than a single white space character
	 * 
	 * @param token the accepting token associated with the accepting state of this {@link NFA}
	 * @param accept flag that determines to accept specified characters or accept anything but specified characters
	 * @return a newly created {@link NFA} that accepts any white space character character except 
	 * if {@code accept == true} otherwise it will accept any character that isn't a white space.
	 */
	public static NFA acceptWhiteSpace (Token acceptingToken, boolean accept) {
		if(accept)
			return acceptCharacters(WHITE_SPACE_CHARS, acceptingToken);
		else 
			return acceptEverythingBut(WHITE_SPACE_CHARS, acceptingToken);
	}
	
	/**
	 * Creates a {@link NFA} that accepts a accepts a single word character
	 * or accepts anything other than a single word character
	 * 
	 * @param token the accepting token associated with the accepting state of this {@link NFA}
	 * @param accept flag that determines to accept specified characters or accept anything but specified characters
	 * @return a newly created {@link NFA} that accepts any word character character except 
	 * if {@code accept == true} otherwise it will accept any character that isn't a word character.
	 */
	public static NFA acceptWord (Token acceptingToken, boolean accept) {
		if(accept)
			return acceptCharacters(WORD_CHARS, acceptingToken);
		else 
			return acceptEverythingBut(WORD_CHARS, acceptingToken);
	}
	
	/**
	 * Creates a {@link NFA} that accepts a single digit character
	 * or accepts anything other than a single digit character
	 * 
	 * @param token the accepting token associated with the accepting state of this {@link NFA}
	 * @param accept flag that determines to accept specified characters or accept anything but specified characters
	 * @return a newly created {@link NFA} that accepts any digit character character except 
	 * if {@code accept == true} otherwise it will accept any character that isn't a digit.
	 */
	public static NFA acceptDigit (Token acceptingToken, boolean accept) {
		if(accept)
			return acceptCharacters(DIGIT_CHARS, acceptingToken);
		else 
			return acceptEverythingBut(DIGIT_CHARS, acceptingToken);
	}
	
	/**
	 * Creates an array containing all character within the span of
	 * one character to another
	 * 
	 * @param first the beginning char in the range of characters (inclusive)
	 * @param last the ending char in the range of characters (inclusive)
	 * @return the array containing all character within the span of
	 * one character {@code first} to {@code last}
	 */
	public static char[] getCharactersInRange (char first, char last) {
		if(last < first)
			throw new IllegalArgumentException("char first must be <= char last");
		
		char[] list = new char[last-first + 1];
		int i = 0;
		for(char c = first; c <= last; c++)
			list[i++] = c;
		return list;
	}
	
	/**
	 * Creates an NFA that accepts any single character that is not specified in the given list of characters
	 * 
	 * @param list the list of characters to be avoided
	 * @param acceptingToken the accepting token corresponding to the accepting state of this {@link NFA}
	 * @return The NFA that accepts any character not in {@code list} with the accepting token {@code acceptingToken}
	 */
	public static NFA acceptEverythingBut(char[] list, Token acceptingToken) {
		int startState = 0;
		InputAlphabet alphabet = new InputAlphabet();
		StateTransitionTable<HashSet<Integer>> transitionTable = new StateTransitionTable<>();
		TransitionMap< HashSet<Integer>> table = new TransitionMap<>();
		AcceptTable acceptTable = new AcceptTable();
		
		// garbage state
		HashSet<Integer> nextStates = new HashSet<>();
		nextStates.add(2);
		
		for(char c : list) {
			alphabet.add(c);
			table.put(c, nextStates);
		}
		
		// accepting state
		nextStates = new HashSet<>();
		nextStates.add(1);
		
		table.put(DFA.OTHER_CHARS, nextStates);
		transitionTable.add(table);
		transitionTable.add(new TransitionMap<>());
		transitionTable.add(new TransitionMap<>());
		acceptTable.put(1, acceptingToken);
		
		return new NFA(startState, alphabet, transitionTable, acceptTable);
	}
	
	
	/**
	 * Creates an NFA that only accepts any one of the specified list of characters
	 * 
	 * @param list the list of characters to be accepted
	 * @param acceptingToken the accepting token corresponding to the accepting state of this {@link NFA}
	 * @return The NFA that accepts any one of the characters in {@code list} with the accepting token {@code acceptingToken}
	 */
	public static NFA acceptCharacters (char[] list, Token acceptingToken) {
		int startState = 0;
		InputAlphabet alphabet = new InputAlphabet();
		StateTransitionTable<HashSet<Integer>> transitionTable = new StateTransitionTable<>();
		TransitionMap <HashSet<Integer>> table = new TransitionMap<>();
		AcceptTable acceptTable = new AcceptTable();
		
		HashSet<Integer> nextStates;
		for(char c : list) {
			alphabet.add(c);
			nextStates = new HashSet<>();
			nextStates.add(1);
			table.put(c, nextStates);
		}
		transitionTable.add(table);
		transitionTable.add(new TransitionMap<>());
		acceptTable.put(1, acceptingToken);
		
		return new NFA(startState, alphabet, transitionTable, acceptTable);
	}
	
	
	
	/**
	 * Creates an NFA that only accepts one of the specified list of characters in {@code listToAccept}
	 * or accepts any one of the characters not presented in {@code listToAcceptEverythingBut}.
	 * If a character is presented in both lists, then it will be accepted by this NFA.
	 * 
	 * @param listToAccept the list of characters to be accepted
	 * @param listToAcceptEverythingBut the list of characters to be avoided
	 * @param acceptingToken the accepting token corresponding to the accepting state of this {@link NFA}
	 * @return The NFA that accepts one of the characters in {@code list} 
	 *  or accepts any one of the characters not presented in {@code listToAcceptEverythingBut}
	 *  with the accepting token {@code acceptingToken}
	 */
	public static NFA acceptCharsAndEverythingButChars (HashSet<Character> listToAccept, 
			HashSet<Character> listToAcceptEverythingBut, Token acceptingToken) {
		
		int startState = 0;
		InputAlphabet alphabet = new InputAlphabet();
		StateTransitionTable<HashSet<Integer>> transitionTable = new StateTransitionTable<>();
		TransitionMap <HashSet<Integer>> table = new TransitionMap<>();
		AcceptTable acceptTable = new AcceptTable();
		
		boolean everythingButIsEmpty = listToAcceptEverythingBut.isEmpty();
		
		HashSet<Integer> nextStates;
		for(char c : listToAccept) {
			listToAcceptEverythingBut.remove(c);
			alphabet.add(c);
			nextStates = new HashSet<>();
			nextStates.add(1);
			table.put(c, nextStates);
		}
		for(char c : listToAcceptEverythingBut) {
			alphabet.add(c);
			nextStates = new HashSet<>();
			nextStates.add(2);
			table.put(c, nextStates);
		}
		
		if(!everythingButIsEmpty) {
			nextStates = new HashSet<>();
			nextStates.add(1);
			table.put(DFA.OTHER_CHARS, nextStates);
		}
		
		transitionTable.add(table);
		transitionTable.add(new TransitionMap<>());
		transitionTable.add(new TransitionMap<>());
		acceptTable.put(1, acceptingToken);
		
		return new NFA(startState, alphabet, transitionTable, acceptTable);
	}
	
	
	
	
	
	
	
	/**
	 * Creates a NFA with a given starting state, inputAlphabet, transition table, and all the accepting states. 
	 * <p>
	 * <b> NOTE: </b> Unlike the class {@link DFA}, the transition table maps a character input to multiple values. Each of these
	 * values do <b>NOT</b> represent the next state of the NFA, but it <b>DOES</b> represent how many indexes away the next state is from the 
	 * current state.
	 * 
	 * @param startState
	 * @param inputAlphaBet
	 * @param transitions
	 * @param acceptingStates
	 */
	public NFA (int startState, InputAlphabet inputAlphaBet, StateTransitionTable<HashSet<Integer>> transitions, AcceptTable acceptingStates) {
		this.startState = startState;
		this.inputAlphaBet = inputAlphaBet;
		this.transitions = transitions;
		this.acceptingStates = acceptingStates;
	}

	
	/**
	 * Changes this {@link NFA} to a {@link NFA} concatenated with itself an arbitrary amount of times {@code numTimes}
	 * 
	 * @param numTimes number of times of concatenation
	 * @return this {@link NFA} after concatenation
	 */
	public NFA mult(int numTimes) {
		if(numTimes < 0)
			throw new IllegalArgumentException("The argument must not be less than 0");
		
		if(numTimes == 0) {
			NFA nfa = acceptStart(getAnyToken());
			this.startState = nfa.startState;
			this.inputAlphaBet = nfa.inputAlphaBet;
			this.transitions = nfa.transitions;
			this.acceptingStates = nfa.acceptingStates;
			
			return this;
		}
			
		NFA copy = clone();
		for(int i = 1; i < numTimes; i++)
			concat(copy);
		return this;
	}
	
	/**
	 * Changes this {@link NFA} to a {@link NFA} that is concatenated with itself at least a arbitrary amount of times
	 * 
	 * @param numTimes the least amount of times this NFA should concatenate with itself
	 * @return this {@link NFA} after concatenation
	 */
	public NFA multAtLeast(int numTimes) {
		if(numTimes < 0)
			throw new IllegalArgumentException("The argument must not be less than 0");
		if(numTimes == 0)
			return star();
		
		NFA copy = clone();
		for(int i = 1; i < numTimes; i++)
			concat(copy);
		concat(copy.star());
		return this;
	}
	
	
	/**
	 * Changes this {@link NFA} to a {@link NFA} that can be concatenated with itself specified by a range of repetitions
	 * 
	 * @param min the minimum amount of times this NFA can concatenate itself with
	 * @param max the maximum amount of times this NFA can concatenate itself with
	 * @return this {@link NFA} after concatenation
	 */
	public NFA multAnywhereFrom(int min, int max) {
		if(max < min)
			throw new IllegalArgumentException("The argument max must not be less than min");
		
		NFA copy = clone();
		
		mult(min);
		
		for(int i = min; i < max; i++)
			optionallyConcat(copy, false);
		
		return this;
	}
	
	
	/**
	 * Changes this {@link NFA} to a {@link NFA} concatenated with a deep copy of another {@link NFA} {@code other}
	 * 
	 * @param other {@link NFA} to concatenate to this {@link NFA}
	 * @return this {@link NFA} after concatenation
	 */
	public NFA concat(NFA other) {
		return optionallyConcat(other, true);
	}
	
	/**
	 * If {@code mustOnlyConcat == true}, then it changes this {@link NFA} to a {@link NFA} concatenated 
	 * with a deep copy of another {@link NFA} {@code other}. Otherwise, it will change this {@link NFA} to
	 * a {@link NFA} that accepts both the original {@link NFA} and the original {@link NFA} concatenated with
	 * another specified {@link NFA} {@code other}.
	 * 
	 * @param other {@link NFA} to concatenate to this {@link NFA}
	 * @param mustOnlyConcat a flag that allows a {@link NFA} that accepts concatenation or 
	 * an {@link NFA} allows concatenation to be optional
	 * @return this {@link NFA} after concatenation
	 */
	public NFA optionallyConcat(NFA other, boolean mustOnlyConcat) {
		
		inputAlphaBet.addAll(other.inputAlphaBet);
		
		Integer numStates = transitions.size();
		
		other.transitions.copyInto(transitions);
		
		TransitionMap<HashSet<Integer>> transition;
		HashSet<Integer> nextStates;
		
		for(Integer i : acceptingStates.keySet()) {
			transition = transitions.get(i);
			if(transition.inDomain(EPSILION)) {
				nextStates = transition.map(EPSILION);
				nextStates.add(numStates + other.startState - i);
			} else {
				nextStates = new HashSet<>();
				nextStates.add(numStates + other.startState - i);
				transition.put(EPSILION, nextStates);
			}
		}
		
		if(mustOnlyConcat)
			acceptingStates = new AcceptTable();
		
		for(Integer i : other.acceptingStates.keySet())
			acceptingStates.put(i + numStates, other.acceptingStates.get(i));
		
		return this;
	}
	
	
	
	
	/**
	 * Changes this {@link NFA} to another {@link NFA} unioned with a deep copy of another {@link NFA} {@code other}
	 * 
	 * @param other {@link NFA} to union to this {@link NFA}
	 * @return this {@link NFA} after appling union
	 */
	public NFA union (NFA other) {
		inputAlphaBet.addAll(other.inputAlphaBet);
		
		
		Integer numStates = transitions.size();
		Integer numStatesOther = other.transitions.size();
		
		other.transitions.copyInto(transitions);
		for(Integer i : other.acceptingStates.keySet())
			acceptingStates.put(i + numStates, other.acceptingStates.get(i));
		
		
		// Adding a new start state with epsilon transitions
		TransitionMap<HashSet<Integer>> firstTransition = new TransitionMap<>();
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

	/**
	 * Changes this {@link NFA} to the star operation of this {@link NFA}
	 * 
	 * @return this {@link NFA} after appling star operation
	 */
	public NFA star () {
		
		TransitionMap<HashSet<Integer>> transition;
		HashSet<Integer> nextStates;
		
		// adding all epsilon transitions to the start state
		for (Integer i : acceptingStates.keySet()) {
			transition = transitions.get(i);
			if (transition.inDomain(EPSILION)) {
				nextStates = transition.map(EPSILION);
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
		
		transition = new TransitionMap<>();
		nextStates = new HashSet<>();
		nextStates.add(startState - numStates);
		transition.put(EPSILION, nextStates);
		transitions.add(transition);
		
		// making new start state 
		startState = numStates;
		
		return this;
	}
	
	
	/**
	 * @return a newly created {@link DFA} object that is converted from this {@link NFA}
	 */
	public DFA convertToDFA () {
		StateTransitionTable<Integer> dfaTransitions = new StateTransitionTable<>();
		AcceptTable dfaAcceptingStates = new AcceptTable();
		
		Hashtable<DFAStateID, Integer> dfaIDToStatesMap = new Hashtable<>();
		DFAStateID startDFAStateID = new DFAStateID();
		Token acceptingToken = getAllEpsilonTransitionStates(startState, startDFAStateID);
		
		dfaIDToStatesMap.put(startDFAStateID, 0);
		if(acceptingToken != null)
			dfaAcceptingStates.put(0, acceptingToken);
		
		fillDFAStateTransitions(startDFAStateID, dfaTransitions, dfaAcceptingStates, dfaIDToStatesMap);
		
		return new DFA(inputAlphaBet, dfaTransitions, dfaAcceptingStates);
	}
	
	
	/**
	 * Fills all the {@link DFA} transitions for the specified DFA state {@code dfaStateID}. This also
	 * recursively fills all the transitions of the new DFA states created during this call;
	 * 
	 * @param dfaStateID the DFA state that needs its transitions filled in the transition table
	 * @param dfaTransitions the DFA transition table (to be filled) that contains all mappings for each state and input char
	 * @param dfaAcceptingStates the list of DFA accepting
	 * @param dfaIDToStatesMap The mapping between all DFA state IDs to their corresponding DFA state "integer" representation
	 */
	private void fillDFAStateTransitions (
			DFAStateID dfaStateID, 
			StateTransitionTable<Integer> dfaTransitions,	AcceptTable dfaAcceptingStates, 
			Hashtable<DFAStateID, Integer> dfaIDToStatesMap) {
		
		
		TransitionMap <Integer> transition = new TransitionMap<>();
		dfaTransitions.add(transition);
		
		inputAlphaBet.forEachWithOthers((c) -> {
			DFAStateID nextDFAStateID = new DFAStateID();
			Token tempToken = getNextDFAState(dfaStateID, c, nextDFAStateID);
			Integer nextDFAState;
			if(!nextDFAStateID.isEmpty()) {
				if(dfaIDToStatesMap.containsKey(nextDFAStateID)) {
					nextDFAState = dfaIDToStatesMap.get(nextDFAStateID);
				} else {
					nextDFAState = dfaIDToStatesMap.size();
					dfaIDToStatesMap.put(nextDFAStateID, nextDFAState);
					if(tempToken != null) // here may reside problem
						dfaAcceptingStates.put(nextDFAState, tempToken);
					fillDFAStateTransitions(nextDFAStateID, dfaTransitions, dfaAcceptingStates, dfaIDToStatesMap);
				}
				transition.put(c, nextDFAState);
			}
		});
		
	}
	
	
	
	/**
	 * Fills list of the all possible next states (and all their multiple epsilon transition states) 
	 * {@code nextDFAStateID} within this NFA from a list of initial states {@code dfaStateID} using
	 * a transition of a input char {@code inputChar}.
	 * @param dfaStateID list of NFA initial states
	 * @param inputChar
	 * @param nextDFAStateID list of added NFA states after mapping
	 * @return if none of the NFA states added to {@code nextDFAStateID} are accepting, then null is returned,
	 * otherwise the last NFA state's corresponding accepting Token is returned
	 */
	private Token getNextDFAState (DFAStateID dfaStateID, Character inputChar, DFAStateID nextDFAStateID) {
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
	
	
	/**
	 * Adds all the states resulting by one or many epsilon transitions from the specified nfa state {@code nfaState} 
	 * to the list {@code dfaStatesCollected} 
	 * 
	 * @param nfaState the nfa state which all of it's epsilon transitions to be added to the nfa state list
	 * @param dfaStatesCollected list of nfa states
	 * @return if the nfa state is already contained within {@code dfaStatesCollected}, then null is returned.
	 * if any of the states have been added to the list are accepting, then the
	 * last seen accepting state's corresponding token is
	 * is returned, otherwise it will return null.
	 */
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
	
	
	/** 
	 * @param state the initial state
	 * @param inputChar the transition input character
	 * @return a list of transitions state steps given a state and a input character
	 */
	private HashSet<Integer> getNextStates (Integer state, Character inputChar) {
		return transitions.get(state).map(inputChar);
	}
	
	/**
	 * @param state the NFA state to check
	 * @return null if specified state is not accepting, otherwise it will return the corresponding
	 * token
	 */
	private Token getAcceptingToken (Integer state) {
		return acceptingStates.get(state);
	}
	
	
	/**
	 * @return Grabs any accepting token associated with this {@link NFA}. If none
	 * exist, then a {@code DFA.NONE} will be returned;
	 */
	public Token getAnyToken() {
		Collection<Token> tokens = acceptingStates.values();
		if(!tokens.isEmpty())
			return tokens.iterator().next();
		else 
			return Token.NONE;
	}
	
	@Override
	public NFA clone () {
		AcceptTable at = new AcceptTable();
		StateTransitionTable<HashSet<Integer>> tt = new StateTransitionTable<>();
		InputAlphabet ia = new InputAlphabet();
		ia.addAll(inputAlphaBet);
		transitions.copyInto(tt);
		acceptingStates.forEach((c, t)-> {
			at.put(c, t);
		});
		return new NFA(startState, ia, tt, at);
	}
	
	
	/**
	 * Contains a list of NFA states that is used to represent a unique DFA state.
	 * This is only used when NFA is converted to a DFA
	 * 
	 * @author Massimiliano Cutugno
	 *
	 */
	@SuppressWarnings("serial")
	public static class DFAStateID extends HashSet<Integer> {}
	
}
