package appSW.appUI.documentFilters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ComplexFilter extends DocumentFilter{
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
			throws BadLocationException {
		if(isAllowedString(string))
			super.insertString(fb, offset, string, attr);
	}
	
	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);
	}
	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		if(isAllowedString(text))
			super.replace(fb, offset, length, text, attrs);
	}
	
	private boolean isAllowedString(String s) {
		for(int i = 0; i < s.length(); i++)
			if(!isAllowedChar(s.charAt(i)))
				return false;
		return true;
	}
	
	
	private boolean isAllowedChar(char c) {
		switch(c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '.':
		case '+':
		case '-':
		case 'i':
			return true;
		}
		return false;
	}
}
