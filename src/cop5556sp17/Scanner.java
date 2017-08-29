package cop5556sp17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}

	}
	
	/* States for DFA*/
	public static enum State {
		START, AFTER_EQ, IN_DIGIT, GOTEQUAL, IN_IDENT, AFTER_OR, AFTER_MINUS, COMMENT,
		AFTER_DIV, AFTER_OR_MINUS, AFTER_NOT, AFTER_LT, AFTER_GT, COM_STAR, ERROR
	}; 
	
	
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			if (kind.getText().length() ==0)
				return chars.substring(pos, pos+length);
			else
				return kind.getText();
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			int index = 0;
			int ret = Arrays.binarySearch(lineNo.toArray(), pos);
			if (ret < 0){
				/* t.pos is greater than all the elements it will return -(lineNo.length + 1)
				 * We have to get 1 previous element, subtracting 2 from the returned value.
				 */
				index = Math.abs(ret) - 2;
			}
			else
				index = ret;
			
			LinePos lp = new LinePos (index, ((lineNo.get(index) == 0)? pos : (pos - lineNo.get(index)-1)));
			return lp;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			
			try {
				return Integer.parseInt(chars.substring(pos, pos+length));
			}
			catch (Exception e) {
				throw new NumberFormatException ("Token not integer at position " +pos);
			}
		}
		
		@Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		 

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }
		
	}

	 


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		keywords = new HashMap<String, Kind>();
		lineNo = new ArrayList<Integer>();
		lineNo.add(0);
		addKeywords ();
	}

	private void addKeywords (){
		keywords.put("integer", Kind.KW_INTEGER);
		keywords.put("boolean", Kind.KW_BOOLEAN);
		keywords.put("image", Kind.KW_IMAGE);
		keywords.put("url", Kind.KW_URL);
		keywords.put("file", Kind.KW_FILE);
		keywords.put("frame", Kind.KW_FRAME);
		keywords.put("while", Kind.KW_WHILE);
		keywords.put("if", Kind.KW_IF);
		keywords.put("screenheight", Kind.KW_SCREENHEIGHT);
		keywords.put("screenwidth", Kind.KW_SCREENWIDTH);
		keywords.put("blur",Kind.OP_BLUR);
		keywords.put ("gray", Kind.OP_GRAY);
		keywords.put ("convolve", Kind.OP_CONVOLVE);
		keywords.put ("width", Kind.OP_WIDTH);
		keywords.put ("height", Kind.OP_HEIGHT);
		keywords.put ("xloc", Kind.KW_XLOC);
		keywords.put ("yloc", Kind.KW_YLOC);
		keywords.put ("hide", Kind.KW_HIDE);
		keywords.put ("show", Kind.KW_SHOW);
		keywords.put ("move", Kind.KW_MOVE);
		keywords.put ("sleep", Kind.OP_SLEEP);
		keywords.put ("scale", Kind.KW_SCALE);
		keywords.put("true", Kind.KW_TRUE);
		keywords.put("false", Kind.KW_FALSE);
	}
	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		
	    int length = chars.length();
	    State state = State.START;
	    int startPos = 0;
	    int ch;
	    while (pos <= length) /* '=' symbol has been removed*/{
	        ch = pos < length ? chars.charAt(pos) : -1;
	        switch (state) {
	            case START: {
	                pos = skipWhiteSpace(pos);
	                ch = pos < length ? chars.charAt(pos) : -1;
	                startPos = pos;
	                switch (ch) { 
	                    case -1	: {tokens.add(new Token(Kind.EOF, pos, 0)); pos++;}  break;
	                    case '+': {tokens.add(new Token(Kind.PLUS, startPos, 1));pos++;} break;
	                    case '*': {tokens.add(new Token(Kind.TIMES, startPos, 1));pos++;} break;
	                    case '=': {state = State.AFTER_EQ;pos++;}break;
	                    case '0': {tokens.add(new Token(Kind.INT_LIT,startPos, 1));pos++;}break;
	                    case ';': {tokens.add(new Token (Kind.SEMI, startPos, 1)); pos++;}break;
	                    case ',': {tokens.add(new Token (Kind.COMMA, startPos, 1)); pos++;}break;
	                    case '(': {tokens.add(new Token (Kind.LPAREN, startPos, 1)); pos++;}break;
	                    case ')': {tokens.add(new Token (Kind.RPAREN, startPos, 1)); pos++;}break;
	                    case '{': {tokens.add(new Token (Kind.LBRACE, startPos, 1)); pos++;}break;
	                    case '}': {tokens.add(new Token (Kind.RBRACE, startPos, 1)); pos++;}break;
	                    case '&': {tokens.add(new Token(Kind.AND, startPos, 1));pos++;} break;
	                    case '%': {tokens.add(new Token(Kind.MOD, startPos, 1));pos++;} break;
	                    case '/': {state = State.AFTER_DIV; pos++;} break;                    
	                    case '|': {state = State.AFTER_OR; pos++;} break;
	                    case '-': {state = State.AFTER_MINUS; pos++;} break;
	                    case '!': {state = State.AFTER_NOT; pos++;} break;
	                    case '<': {state = State.AFTER_LT; pos++;} break;
	                    case '>': {state = State.AFTER_GT; pos++;} break;
	                    
	                    default: {
	                        if (Character.isDigit(ch)) {state = State.IN_DIGIT;pos++;} 
	                        else if (Character.isJavaIdentifierStart(ch)) {
	                             state = State.IN_IDENT;pos++;
	                         } 
	                         else {throw new IllegalCharException(
	                                    "illegal char " +ch+" at pos "+pos);
	                         }
	                      }
	                }

	            }  break;
	            
	            case AFTER_DIV: {
	            	if (ch == '*'){ state = State.COMMENT; pos++;}
	            	else{
	            		tokens.add (new Token(Kind.DIV, startPos, pos-startPos));
	            		state = State.START;
	            	}
	            } break;
	            
	            case AFTER_OR: {
	            	if (ch == '-'){ state = State.AFTER_OR_MINUS; pos++;}
	            	else{
	            		tokens.add(new Token (Kind.OR, startPos, pos-startPos));
	            		state = State.START;
	            	}	            		
	            }  break;
	            
	            case AFTER_OR_MINUS: {
	            	if (ch == '>'){
	            		tokens.add(new Token (Kind.BARARROW, startPos, pos-startPos +1));
		            	pos++;
	            	}
	            	else {
	            		tokens.add(new Token (Kind.OR, startPos, 1));
	            		tokens.add(new Token (Kind.MINUS, startPos+1, 1));
	            	}
	            	state = State.START;
	    		}  break;
	    		
	            case AFTER_MINUS: {
	            	if (ch == '>') {
	            		tokens.add(new Token (Kind.ARROW, startPos, pos-startPos+1));
	            		pos++;
	            	}
	            	else
	            		tokens.add (new Token (Kind.MINUS, startPos, pos-startPos));
	            	
            		state = State.START;
	            		
	            }  break;
	            
	            case AFTER_NOT: {
	            	if (ch == '='){
	            		tokens.add(new Token (Kind.NOTEQUAL, startPos, pos-startPos+1));
	            		pos++;
	            	}
	            	else
	            		tokens.add(new Token (Kind.NOT, startPos, 1));
	            	
	            	state = State.START;
	            } break;
	            
	            case AFTER_LT: {
	            	if (ch == '=') {
	            		tokens.add(new Token (Kind.LE, startPos, pos - startPos+1));
	            		pos++;
	            	}
	            	else if (ch == '-'){
	            		tokens.add(new Token (Kind.ASSIGN, startPos, pos-startPos +1));
	            		pos++;
	            	}
	            	else
	            		tokens.add(new Token (Kind.LT, startPos, 1));

	            	state = State.START;
	            		
	            }  break;
	            
	            case AFTER_GT: {
	            	if (ch == '='){
	            		tokens.add(new Token (Kind.GE, startPos, pos-startPos+1));
	            		pos++;
	            	}
	            	else
	            		tokens.add(new Token (Kind.GT, startPos, 1));
	            	
	            	state = State.START;
	            }  break;

	            case IN_DIGIT: {
	                while (pos < length && Character.isDigit(chars.charAt(pos)))
	                    pos++;
	              
	                tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
	                state = State.START;
	                try {
	                	Integer.parseInt(chars.substring(startPos, pos));
	                }
	                catch (Exception e){
	                 	  throw new IllegalNumberException(
	                    	  "Out of bound Integer at pos " +startPos);  
	                }
	            }  break;
	            
	            case IN_IDENT: {
	            	while (pos<length && Character.isJavaIdentifierPart(chars.charAt(pos)))
	            		pos++;
	            	String s = chars.substring(startPos, pos);

	            	// Check if the scanned identifier is a keyword
	            	if (keywords.containsKey(s))
	            		tokens.add(new Token (keywords.get(s), startPos, pos - startPos));
	            	else
	            		tokens.add(new Token (Kind.IDENT, startPos, pos-startPos));

	            	state = State.START;
	            }  break;
	            
	            case AFTER_EQ: {
	            	if (ch == '='){tokens.add(new Token(Kind.EQUAL, startPos, ++pos - startPos));
	            	state = State.START;
	            	}
	            	else {
	            	throw new IllegalCharException(
                            "illegal char " +ch+" at pos "+pos);
	            	}
	            }  break;	            
	            
	            case COMMENT:  {    	
	            	pos = skipWhiteSpace (pos);
	            	int cha = (pos < length) ? chars.charAt(pos) : -1;
	            	switch (cha){
	            	case '*':
	            		state = State.COM_STAR;
	            		break;
	            	case -1:
	            		throw new IllegalCharException ("Comment starting from position " +startPos
	            				+ "not closed and End of file reached ");
	            	};
	            	
            		pos++;
	            	
	        	}  break;
	        	
	            case COM_STAR: {
	            	if (ch == '/'){
	            		pos++;
	            		state = State.START;
	            	}
	            	else {
	            		state = State.COMMENT;
	            	}
	            }  break;
	            	
	            default:
	            	assert false;
	        }
	    }
		return this;  
	}



	public int skipWhiteSpace(int pos) {
		while ((pos < chars.length()) /*&& (chars.charAt(pos) != '\n' */&& Character.isWhitespace(chars.charAt(pos))) {
			if (chars.charAt(pos) == '\n')
				lineNo.add(pos);
			pos++;
		}
		return pos;
	}



	final ArrayList<Token> tokens;
	final ArrayList<Integer> lineNo;
	final HashMap<String, Kind> keywords;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		return t.getLinePos();
	}


}
