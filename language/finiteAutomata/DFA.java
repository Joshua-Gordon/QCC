package language.finiteAutomata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import language.compiler.Token;


/**
 * 
 * This class handles all DFA (Determinant Finite Automata) related functionality. Moreover,
 * {@link DFA} is a class that creates {@link DFA} objects that supports Tokenization (as each accepting state contains a
 * reference to a Token). This class is supported by the language.compiler library.
 * <p>
 * <b> NOTE: </b> Unlike the class {@link NFA}, the transition table when used in a {@link DFA} maps a 
 * character input to a single value. This value do <b>DOES</b> represent the next state of the NFA, 
 * but it <b>DOES NOT</b> represent how many indexes away the next state is from the current state.
 * 
 * @author Massimiliano Cutugno
 *
 */
public class DFA {
	public static final Character OTHER_CHARS = null;
	
	private final StateTransitionTable<Integer> transitions;
	private final AcceptTable acceptingStates;
	private final InputAlphabet inputAlphabet;
	
	/**
	 * Creates a DFA with a start state which is always 0, a {@link StateTransitionTable}, and a {@link AcceptTable}
	 * @param transitions 
	 * @param acceptingStates
	 */
	public DFA (InputAlphabet inputAlphabet, StateTransitionTable<Integer> transitions, AcceptTable acceptingStates) {
		this.inputAlphabet = inputAlphabet;
		this.transitions = transitions;
		this.acceptingStates = acceptingStates;
	}
	
	/**
	 * Checks if string (in its entirety) is accepted by this {@link DFA}
	 * @param s the string to be tested
	 * @return whether or not a string is accepting
	 */
	public boolean accepts(String s) {
		try {
			return accepts(new BufferedReader(new StringReader(s)));
		} catch (IOException e) {
			return false;
		}
	}
	
	
	/**
	 * Checks if {@link BufferedReader} input (in its entirety) is accepted by this {@link DFA}
	 * @param br the stream of chracters to be tested
	 * @return whether or not a string is accepting
	 */
	public boolean accepts(BufferedReader br) throws IOException {
		Integer currentState = 0;
		int c;
		
		while((c = br.read()) != -1) {
			currentState = getNextState(currentState, (char) c);
			if(currentState == null) return false;
		}
		
		return isAccepting(currentState);
	}
	
	
	/**
	 * Returns the next state of this {@link DFA} given some state within the {@link DFA} and input character
	 * @param state the specified input state
	 * @param inputChar the input character
	 * @return the next state
	 */
	public Integer getNextState (Integer state, char inputChar) {
		return transitions.get(state).map(inputChar);
	}
	
	/**
	 * Determines whether a state is accepting or not within the {@link DFA}
	 * 
	 * @param state
	 * @return whether or not the specified state is accepting within the {@link DFA}
	 */
	public Boolean isAccepting (Integer state) {
		return acceptingStates.containsKey(state);
	}
	
	/**
	 * Gets a accepting token
	 * 
	 * @param state
	 * @return if this state is accepting, then the corresponding Token is returned, otherwise null is returned
	 */
	public Token getAcceptingToken (Integer state) {
		return acceptingStates.get(state);
	}
	
	
	/**
	 * 
	 * This class is designed to represent the input alphabet for either a {@link NFA} or {@link DFA} object
	 * 
	 * @author Massimiliano Cutugno
	 *
	 */
	@SuppressWarnings("serial")
	public static class InputAlphabet extends HashSet<Character> {
		
		/**
		 * extends definition of the forEach function, but also includes
		 * an iteration that is specified with every character except the
		 * ones already within the set
		 * @param consumer
		 */
		public void forEachWithOthers(Consumer<Character> consumer) {
			forEach(consumer);
			consumer.accept(OTHER_CHARS);
		}
		
		
	}
	
	
	/**
	 * This class creates objects that represent State Transition Tables for {@link NFA}s and {@link DFA}s
	 * Each index in this array is analogous to a single state within the {@link NFA} or {@link DFA}.
	 * Therefore the size of this transition table is the number of states within the {@link NFA} or {@link DFA}.
	 * 
	 * <p>
	 * <b> NOTE: </b> Unlike the class {@link NFA}, the transition table when used in a {@link DFA} maps a 
	 * character input to a single value. This value do <b>DOES</b> represent the next state of the NFA, 
	 * but it <b>DOES NOT</b> represent how many indexes away the next state is from the current state.
	 * <p>
	 * <b> ALSO NOTE: </b> Unlike the class {@link DFA}, the transition table when used in a {@link NFA} maps a 
 	 * character input to multiple values. Each of these values do <b>NOT</b> represent the next state of the 
 	 * NFA, but it <b>DOES</b> represent how many indexes away the next state is from the current state.
	 * 
	 * @author Massimiliano Cutugno
	 *
	 * @param <T>
	 */
	@SuppressWarnings("serial")
	public static class StateTransitionTable <T> extends ArrayList<TransitionMap<T>> {
		
		/**
		 * This copies all the elements within this table into another specified table
		 * @param table
		 */
		public void copyInto(StateTransitionTable<T> table) {
			for(TransitionMap<T> transition : this) {
				TransitionMap<T> temp = new TransitionMap<>();
				transition.forEach((c, t) -> {
					temp.put(c, clone(t));
				});
				table.add(temp);
			}
		}
		
		@SuppressWarnings("unchecked")
		public T clone(T elem) {
			if(elem instanceof HashSet<?>) {
				return (T) ((HashSet<?>)elem).clone();
			} else {
				return elem;
			}
		}
		
	}
	
	/**
	 * This class creates objects that represent all the character transitions for a single state
	 * within a {@link NFA}s and {@link DFA}s
	 * 
	 * @author Massimiliano Cutugno
	 *
	 * @param <T>
	 */
	public static class TransitionMap <T> {
		private Hashtable<Character, T> map;
		private T otherChars;
		
		/**
		 * Creates a new {@link TransitionMap}
		 */
		public TransitionMap () {
			this.map = new Hashtable<>();
			this.otherChars = null;
		}
		
		/**
		 * @see {@link Hashtable#forEach()}
		 * @param biConsumer
		 */
		public void forEach(BiConsumer<Character, T> biConsumer) {
			map.forEach(biConsumer);
			biConsumer.accept(OTHER_CHARS, otherChars);
		}
		
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void addOtherToOutput(T output) {
			if(otherChars instanceof HashSet<?>)
				((HashSet) output).addAll((HashSet) otherChars);
		}
		
		/**
		 * Puts a transition into this {@link TransitionMap}
		 * @param cIn the character used for the mapping.
		 * Use {@link NFA#EPSILION} to specify a epsilon transition or 
		 * {@link #OTHER_CHARS} to specify any other character transition that is not
		 * concretely mapped
		 * @param output the state or states mapped out of this transition
		 */
		public void put(Character cIn, T output) {
			if(cIn == OTHER_CHARS) {
				otherChars = output;
			} else if(cIn == NFA.EPSILION) {
				map.put(cIn, output);
			} else {
				if(otherChars != null)
					addOtherToOutput(output);			
				map.put(cIn, output);
			}
		}
		
		/**
		 * maps a character to a state or a list of states
		 * @param cIn the character to use for mapping. 
		 * Use {@link NFA#EPSILION} to specify a epsilon transition or 
		 * {@link #OTHER_CHARS} to specify any other character transition that is not
		 * concretely mapped
		 * @return the state or list of states mapped out
		 */
		public T map(Character cIn) {
			if(cIn == OTHER_CHARS)
				return otherChars;
			
			if(cIn == NFA.EPSILION)
				return map.get(cIn);
			
			if(map.containsKey(cIn))
				return map.get(cIn);
			else
				return otherChars;
		}
		
		/**
		 * Checks if this character has a mapping in this {@link TransitionMap}
		 * 
		 * @param cIn the character to check.
		 * Use {@link NFA#EPSILION} to specify a epsilon transition or 
		 * {@link #OTHER_CHARS} to specify any other character transition that is not
		 * concretely mapped
		 * @return whether or not this 
		 */
		public boolean inDomain(Character cIn) {
			if(cIn == OTHER_CHARS)
				return otherChars == null;
			
			if(cIn == NFA.EPSILION)
				return map.containsKey(cIn);

			if(map.containsKey(cIn))
				return true;
			else
				return otherChars == null;
		}
		
		
	}
	
	/**
	 * 
	 * This class creates objects which store the accepting states with their corresponding
	 * accepting tokens in a {@link DFA} or a {@link NFA}
	 * 
	 * @author Massimiliano Cutugno
	 *
	 */
	@SuppressWarnings("serial")
	public static class AcceptTable extends Hashtable<Integer, Token>{
		
		/**
		 * This adds a state to the accepting table without specifying a accepting Token.
		 * @param acceptState the state to add
		 */
		public void add(Integer acceptState) {
			put(acceptState, Token.NONE);
		}
		
	}
}
