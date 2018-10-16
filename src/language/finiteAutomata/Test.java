package language.finiteAutomata;

import java.util.HashSet;
import java.util.Hashtable;

import language.compiler.Token;
import language.finiteAutomata.DFA.AcceptTable;
import language.finiteAutomata.DFA.InputAlphabet;

public class Test {
	
	
	public static void main(String[] args) {
		
		
		
//		NFA nfa = NFA.acceptWord(new Token("")).concat(NFA.acceptSpace(new Token("")));
//		DFA dfa = nfa.converToDFA();
//		
//		String s = "a b  ssd  sd";
//		
//		try(
//			PushbackReader pr = new PushbackReader(new StringReader(s), 40);
//		){
//			Pair<Token, String> next;
//			
//			while ((next = dfa.getNextToken(pr)) != null)
//				System.out.println(next.second());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		NFATransitionTable tt = new NFATransitionTable();
//		InputAlphabet ia = new InputAlphabet();
//		AcceptTable at = new AcceptTable();
//		Integer startState = 0;
//		
//		addToAlphaBet(ia, new char[]{'a', 'b'});
//		tt.add(getTable(new char[]{'a'}, new int[][]{{1}}));
//		tt.add(getTable(new char[]{NFA.EPSILION}, new int[][]{{1}}));
//		tt.add(getTable(new char[]{'b'}, new int[][]{{1}}));
//		tt.add(getTable(new char[]{NFA.EPSILION}, new int[][]{{-1}}));
//		addAcceptingStates(at, new int[] {1, 3}, new Token(null));
//		
//		NFA nfa = new NFA(startState, ia, tt, at);
//		DFA dfa = nfa.converToDFA();
//		
//		String s = "abbbbaabbbbb";
//		
//		try(
//			PushbackReader pr = new PushbackReader(new StringReader(s));
//		){
//			System.out.println(dfa.getNextToken(pr).second());
//			System.out.println(dfa.getNextToken(pr).second());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void addAcceptingStates(AcceptTable at, int[] states, Token token) {
		for(int i = 0; i < states.length; i++)
			at.put(states[i], token);
	}
	
	public static void addToAlphaBet (InputAlphabet ia, char[] values) {
		for(char c : values)
			ia.add(c);
	}
	
	public static Hashtable<Character, HashSet<Integer>> getTable (char[] inputChars, int[][] states) {
		Hashtable<Character, HashSet<Integer>> table = new Hashtable<>();
		HashSet<Integer> temp;
		int i = 0;
		for(char c : inputChars) {
			temp = new HashSet<>();
			for(int j = 0; j <  states[i].length; j++)
				temp.add(states[i][j]);
			
			table.put(c, temp);
			i++;
		}
		return table;
	}
	
}
