package language.finiteAutomata;

import java.util.HashSet;

import language.compiler.Token;


/**
 * This class is a static class that handles parsing a regex expressions into NFA's
 * <p>
 * <b>SUPPORTS:</b> <br>
 * 
 * <pre>
 * .		any character except newline
 * \w\d\s		word, digit, whitespace
 * \W\D\S		not word, digit, whitespace
 * [abc]		any of a, b, or c
 * [^abc]		not a, b, or c
 * [a-g]		character between a & g
 * \.\*\\		escaped special characters
 * \t\n\r		tab, linefeed, carriage return
 * (abc)		capture group
 * a*a+a?		0 or more, 1 or more, 0 or 1
 * a{5}a{2,}	exactly five, two or more
 * a{1,3}		between one & three
 * ab|cd		match ab or cd
 * </pre>
 * 
 * 
 * <p>
 * <b>DOES NOT SUPPORT:</b> <br>
 * 
 * <pre>
 * \1		backreference to group #1
 * (?:abc)	non-capturing group
 * (?=abc)	positive lookahead
 * (?!abc)	negative lookahead
 * a+?a{2,}?	match as few as possible
 * ^abc$		start / end of the string
 * \b\B		word, not-word boundary
 * </pre>
 * @author Massimiliano Cutugno
 *
 */
public final class RegularExpression {
	
	/**
	 * Parses regular expressions into a {@link NFA}
	 * @param regex the regular expression
	 * @param accepting the accepting token associated with the accepting state of the returned {@link NFA}
	 * @return the equivalent {@link NFA} of this regex
	 */
	public static NFA regexToNFA (String regex, Token accepting) {
		return RegexParser.parse(regex, accepting);
	}
	
	
	/**
	 * Checks if the given string is accepted by the regex
	 * @param string the string to be checked
	 * @param regex the regular expression
	 * @return whether or not the given string is accepted by the regular expression
	 */
	public static boolean matchesRegex(String string, String regex) {
		NFA nfa = regexToNFA(regex, Token.NONE);
		DFA dfa = nfa.convertToDFA();
		return dfa.accepts(string);
	}
	
//	A template context free grammer to use for the regex parser
	
//	starting -> expr | epsilon
	
//	expr -> group | group expr
//	group -> ( expr ) | classN | classA | any | list | escape | opt | star | plus | union | concat
//	classA -> [ letterList ]
//	classN -> [ ^ letterList ]
//	letterList -> any | escape | specialHybrid | list | any - any | letterList letterList
//	any -> "any character except . \ "
//	anyHybrid -> any | 
//	list -> \w | \d | \s | \W | \D | \S | .
// 	escape -> \ special | \ .
//	special -> \ | specialHybrid
//	specialHybrid = + | * | ( | ) | '|' | { | } 
//	opt -> group ?
//	star -> group *
//	plus -> group +
//	union -> group | group
//	multiplicity -> group {Number} | group {Number,} | group {Number, Number}
//	Number -> "is any positive Number or zero"
	
	
	
	// one pass parser to compile regex expression into NFA
	private static final class RegexParser {
		private int index = 0;
		private final String regex;
		private char lookAhead;
		private final Token accepting;
		private int exprRecur = 0;
		
		private static NFA parse(String regex, Token accepting) {
			RegexParser p = new RegexParser(regex, accepting);
			return p.runAndReturnSematics();
		}
		
		private RegexParser (String regex, Token accepting) {
			this.regex = regex;
			this.accepting = accepting;
		}
		
		private NFA runAndReturnSematics() {
			NFA nfa = NFA.acceptStart(accepting);
			
			
			
			// starting nonterminal
			if(peak() == null)
				return nfa;
			nfa.concat(expr());
			
			// Makes sure that an equal number of '(' symbols match the number of ')' symbols
			if(exprRecur != 0)
				throw new RegexParseException();
			
			return nfa;
		}
		
		private void match(char symbol) {
			if(!getNext() || lookAhead != symbol)
				throw new RegexParseException();
		}
		
		private NFA expr() {
			
			NFAData nfaData = new NFAData();
			
			while (getNext()) {
				if (lookAhead == '(') {
					exprRecur ++;
					nfaData.setCurrentNFA(expr());
				} else if (lookAhead == ')') {
					if(exprRecur > 0) {
						exprRecur --;
						return nfaData.getTotalNFA();
					} else {
						throw new RegexParseException();
					}
				} else if (lookAhead == '[') {
					nfaData.setCurrentNFA(charClass());
				} else if (lookAhead == '*') {
					checkNFANull(nfaData.getCurrentNFA());
					nfaData.getCurrentNFA().star();
				} else if (lookAhead == '+') {
					checkNFANull(nfaData.getCurrentNFA());
					nfaData.getCurrentNFA().multAtLeast(1);
				} else if (lookAhead == '?') {
					checkNFANull(nfaData.getCurrentNFA());
					nfaData.getCurrentNFA().multAnywhereFrom(0, 1);
				} else if (lookAhead == '{') {
					multiplicity(nfaData.getCurrentNFA());
				} else if (lookAhead == '.') {
					nfaData.setCurrentNFA(NFA.acceptEverythingBut(new char[]{'\n'}, accepting));
				} else if (lookAhead == '\\') { 
					nfaData.setCurrentNFA(escape());
				} else if (lookAhead == '|') {
					return nfaData.getTotalNFA().union(expr());
				} else {
					nfaData.setCurrentNFA(NFA.acceptCharacters(new char[]{lookAhead}, accepting));
				}
			}
			return nfaData.getTotalNFA();
		}
		
		
		
		// changes NFA to be reoccuring some amount of times
		private void multiplicity(NFA currentNFA) {
			checkNFANull(currentNFA);
			
			int first = number();
			
			if(necessaryPeak() == ',') {
				getNext();
				if(necessaryPeak() == '}')
					currentNFA.multAtLeast(first);
				else
					currentNFA.multAnywhereFrom(first, number());
			} else {
				currentNFA.mult(first);
			}
			match('}');
		}
		
		
		
		// gets positive integer
		private int number() {
			if(!getNext() || Character.isDigit(lookAhead))
				throw new RegexParseException();
			
			int number = Character.getNumericValue(lookAhead);
			
			Character peak = peak();
			while (peak != null && Character.isDigit(peak)) {
				number *= 10;
				number += Character.getNumericValue(number);
				getNext();
			}
			return number;
		}

		
		
		
		// gets escaped character or default character class
		private NFA escape() {
			if(!getNext())
				throw new RegexParseException();
			switch (lookAhead) {
			case 'w':
				return NFA.acceptWord(accepting, true);
			case 'W':
				return NFA.acceptWord(accepting, false);
			case 'd':
				return NFA.acceptDigit(accepting, true);
			case 'D':
				return NFA.acceptDigit(accepting, false);
			case 's':
				return NFA.acceptWhiteSpace(accepting, true);
			case 'S':
				return NFA.acceptWhiteSpace(accepting, false);
			default:
				return NFA.acceptCharacters(new char[]{lookAhead}, accepting);
			}
		}
		
		// creates an NFA for a character class 
		private NFA charClass() {
			boolean acceptNotReject = true;
			if(necessaryPeak() == '^') {
				acceptNotReject = false;
				getNext();
			}
			
			HashSet<Character> acceptingCharSet = new HashSet<>();
			HashSet<Character> avoidingCharSet = new HashSet<>();
			
			while(necessaryPeak() != ']') {
				getNext();
				
				if(fillIfInBuiltCharClass(acceptingCharSet, avoidingCharSet))
					continue;
				
				// detects ranges
				if(necessaryPeak() == '-') {
					char start = lookAhead;
					getNext();
					if(necessaryPeak() != ']') {
						getNext();
						
						if(fillIfInBuiltCharClass(acceptingCharSet, avoidingCharSet)) {
							acceptingCharSet.add(start);
							acceptingCharSet.add('-');
						} else {
							// create character range
							if(lookAhead < start)
								throw new RegexParseException();
							else
								for(char c = start; c <= lookAhead; c++)
									acceptingCharSet.add(c);
						}
					} else {
						acceptingCharSet.add(start);
						acceptingCharSet.add('-');
					}
				} else {
					acceptingCharSet.add(lookAhead);
				}
			}
			match(']');
			
			if(avoidingCharSet.isEmpty() && acceptingCharSet.isEmpty())
				throw new RegexParseException();
			
			if(acceptNotReject)
				return NFA.acceptCharsAndEverythingButChars(acceptingCharSet, avoidingCharSet, accepting);
			else
				return NFA.acceptCharsAndEverythingButChars(avoidingCharSet, acceptingCharSet, accepting);

		}
		
		private boolean fillIfInBuiltCharClass(HashSet<Character> acceptingCharSet, HashSet<Character> avoidingCharSet) {
			if (lookAhead == '\\') {
				if(!getNext())
					throw new RegexParseException();
				
				switch (lookAhead) {
				case 'w':
					for(char c : NFA.WORD_CHARS)
						acceptingCharSet.add(c);
					return true;
				case 'W':
					for(char c : NFA.WORD_CHARS)
						avoidingCharSet.add(c);
					return true;
				case 'd':
					for(char c : NFA.DIGIT_CHARS)
						acceptingCharSet.add(c);
					return true;
				case 'D':
					for(char c : NFA.DIGIT_CHARS)
						avoidingCharSet.add(c);
					return true;
				case 's':
					for(char c : NFA.WHITE_SPACE_CHARS)
						acceptingCharSet.add(c);
					return true;
				case 'S':
					for(char c : NFA.WHITE_SPACE_CHARS)
						avoidingCharSet.add(c);
					return true;
				}
			}
			return false;
		}
		
		
		
		
		private void checkNFANull(NFA nfa) {
			if(nfa == null)
				throw new RegexParseException();
		}
		
		
		
		
		private boolean getNext () {
			if(index == regex.length())
				return false;
			lookAhead = regex.charAt(index++);
			return true;
		}
		
		
		
		
		
		private Character necessaryPeak() {
			Character peak = peak();
			if(peak == null)
				throw new RegexParseException();
			return peak;
		}
		
		
		
		
		private Character peak() {
			if(index == regex.length())
				return null;
			return regex.charAt(index);	
		}
		
		
		
		
		private class NFAData {
			private NFA nfaAccum = null;
			private NFA currentNFA = null;
			
			void setCurrentNFA(NFA currentNFA) {
				safelyConcatNFA();
				this.currentNFA = currentNFA;
			}
			
			NFA getCurrentNFA() {
				return currentNFA;
			}
			
			NFA getTotalNFA () {
				checkNFANull(currentNFA);
				safelyConcatNFA();
				return nfaAccum;
			}
			
			private void safelyConcatNFA() {
				if(currentNFA == null)
					nfaAccum = null;
				
				if(nfaAccum == null) 
					nfaAccum = currentNFA;
				else
					nfaAccum = nfaAccum.concat(currentNFA);
			}
			
		}
		
		@SuppressWarnings("serial")
		public class RegexParseException extends RuntimeException {
			public RegexParseException () {
				super ("Could not parse regex, error ocurred at index " + 
						index + " in \"" + regex + "\"");
			}
		}
		
	}
	
}
