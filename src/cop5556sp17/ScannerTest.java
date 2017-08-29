package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	// Keyword test
	@Test
	public void testkeyword () throws IllegalCharException, IllegalNumberException {
		String input = "screenwidth==Ident$_1234";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_SCREENWIDTH, token.kind);
		assertEquals(0, token.pos);
		assertEquals(11, token.length);
		assertEquals ("screenwidth", token.getText());
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(EQUAL, token2.kind);
		assertEquals(11, token2.pos);
		assertEquals(2, token2.length);
		assertEquals ("==", token2.getText());
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(IDENT, token3.kind);
		assertEquals(13, token3.pos);
		assertEquals(11, token3.length);
		assertEquals ("Ident$_1234", token3.getText());
		
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token4.kind);
	}
	
	@Test
	public void testOperator () throws IllegalCharException, IllegalNumberException {
		String input = "|- |-> \n 0 abc123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(OR, token.kind);
		assertEquals(0, token.pos);
		assertEquals(1, token.length);
		assertEquals ("|", token.getText());
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(MINUS, token2.kind);
		assertEquals(1, token2.pos);
		assertEquals(1, token2.length);
		assertEquals ("-", token2.getText());
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(BARARROW, token3.kind);
		assertEquals(3, token3.pos);
		assertEquals(3, token3.length);
		assertEquals ("|->", token3.getText());
		assertEquals(0, scanner.getLinePos(token3).line);
		assertEquals(3, scanner.getLinePos(token3).posInLine);
		
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(INT_LIT, token4.kind);
		assertEquals(9, token4.pos);
		assertEquals(1, token4.length);
		assertEquals ("0", token4.getText());
		assertEquals(1, scanner.getLinePos(token4).line);
		assertEquals(1, scanner.getLinePos(token4).posInLine);
		
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(IDENT, token5.kind);
		assertEquals(11, token5.pos);
		assertEquals(6, token5.length);
		assertEquals("abc123", token5.getText());
		assertEquals(1, scanner.getLinePos(token5).line);
		assertEquals(3, scanner.getLinePos(token5).posInLine);
		
		Scanner.Token token6 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token6.kind);
	}
	
	@Test
	public void testOperator2 () throws IllegalCharException, IllegalNumberException {
		String input = "|-> -> -";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(BARARROW, token.kind);
		assertEquals(0, token.pos);
		assertEquals(3, token.length);
		assertEquals ("|->", token.getText());
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(ARROW, token2.kind);
		assertEquals(4, token2.pos);
		assertEquals(2, token2.length);
		assertEquals ("->", token2.getText());
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(MINUS, token3.kind);
		assertEquals(7, token3.pos);
		assertEquals(1, token3.length);
		assertEquals ("-", token3.getText());
		
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token4.kind);
	}
		
	@Test
	public void testOperator3 () throws IllegalCharException, IllegalNumberException {
		String input = "| & == != < > <= >= + - * / % ! -> |-> <-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(OR, token.kind);
		assertEquals(0, token.pos);
		assertEquals(1, token.length);
		assertEquals ("|", token.getText());
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(AND, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(1, token2.length);
		assertEquals ("&", token2.getText());
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(EQUAL, token3.kind);
		assertEquals(4, token3.pos);
		assertEquals(2, token3.length);
		assertEquals ("==", token3.getText());
		
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(NOTEQUAL, token4.kind);
		assertEquals(7, token4.pos);
		assertEquals(2, token4.length);
		assertEquals ("!=", token4.getText());
		
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(LT, token5.kind);
		assertEquals(10, token5.pos);
		assertEquals(1, token5.length);
		assertEquals ("<", token5.getText());
		
		Scanner.Token token6 = scanner.nextToken();
		assertEquals(GT, token6.kind);
		assertEquals(12, token6.pos);
		assertEquals(1, token6.length);
		assertEquals (">", token6.getText());
		
		Scanner.Token token7 = scanner.nextToken();
		assertEquals(LE, token7.kind);
		assertEquals(14, token7.pos);
		assertEquals(2, token7.length);
		assertEquals ("<=", token7.getText());
		
		Scanner.Token token8 = scanner.nextToken();
		assertEquals(GE, token8.kind);
		assertEquals(17, token8.pos);
		assertEquals(2, token8.length);
		assertEquals (">=", token8.getText());
		
		Scanner.Token token9 = scanner.nextToken();
		assertEquals(PLUS, token9.kind);
		assertEquals(20, token9.pos);
		assertEquals(1, token9.length);
		assertEquals ("+", token9.getText());
		
		Scanner.Token token10 = scanner.nextToken();
		assertEquals(MINUS, token10.kind);
		assertEquals(22, token10.pos);
		assertEquals(1, token10.length);
		assertEquals ("-", token10.getText());
		
		Scanner.Token token11 = scanner.nextToken();
		assertEquals(TIMES, token11.kind);
		assertEquals(24, token11.pos);
		assertEquals(1, token11.length);
		assertEquals ("*", token11.getText());
		
		Scanner.Token token12 = scanner.nextToken();
		assertEquals(DIV, token12.kind);
		assertEquals(26, token12.pos);
		assertEquals(1, token12.length);
		assertEquals ("/", token12.getText());
		
		Scanner.Token token13 = scanner.nextToken();
		assertEquals(MOD, token13.kind);
		assertEquals(28, token13.pos);
		assertEquals(1, token13.length);
		assertEquals ("%", token13.getText());
		
		Scanner.Token token14 = scanner.nextToken();
		assertEquals(NOT, token14.kind);
		assertEquals(30, token14.pos);
		assertEquals(1, token14.length);
		assertEquals ("!", token14.getText());
		
		Scanner.Token token15 = scanner.nextToken();
		assertEquals(ARROW, token15.kind);
		assertEquals(32, token15.pos);
		assertEquals(2, token15.length);
		assertEquals ("->", token15.getText());
		
		Scanner.Token token16 = scanner.nextToken();
		assertEquals(BARARROW, token16.kind);
		assertEquals(35, token16.pos);
		assertEquals(3, token16.length);
		assertEquals ("|->", token16.getText());
		
		Scanner.Token token17 = scanner.nextToken();
		assertEquals(ASSIGN, token17.kind);
		assertEquals(39, token17.pos);
		assertEquals(2, token17.length);
		assertEquals ("<-", token17.getText());
		
		Scanner.Token token18 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token18.kind);
	}
	
	@Test
	public void testident () throws IllegalCharException, IllegalNumberException {
		String input = "bcd abcde";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(0, token.pos);
		assertEquals(input.substring(0, input.indexOf(" ")).length(), token.length);
		assertEquals (input.substring(0, input.indexOf(" ")), token.getText());
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(IDENT, token2.kind);
		assertEquals(input.indexOf(" ") + 1, token2.pos);
		assertEquals(input.substring(input.indexOf(" ") + 1, input.length()).length(), token2.length);
		assertEquals (input.substring(input.indexOf(" ")+1), token2.getText());
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();
	}

	/*
	 * test unclosed comment "/**/ /****\n"
	 */
	
	@Test
	public void testComment () throws IllegalCharException, IllegalNumberException {
		String input = "/**/ a /****\n";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test
	public void testComment2 () throws IllegalCharException, IllegalNumberException {
		String input = "/* /* * /*/ \nabc + 2;\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		
		assertEquals (5, scanner.tokens.size());
		Scanner.Token token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(13, token.pos);
		assertEquals(3, token.length);
		assertEquals ("abc", token.getText());
		assertEquals(1, scanner.getLinePos(token).line);
		assertEquals(0, scanner.getLinePos(token).posInLine);
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(PLUS, token2.kind);
		assertEquals(17, token2.pos);
		assertEquals(1, token2.length);
		assertEquals ("+", token2.getText());
		assertEquals(1, scanner.getLinePos(token2).line);
		assertEquals(4, scanner.getLinePos(token2).posInLine);
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(INT_LIT, token3.kind);
		assertEquals(19, token3.pos);
		assertEquals(1, token3.length);
		assertEquals ("2", token3.getText());
		assertEquals (2, token3.intVal());
		assertEquals(1, scanner.getLinePos(token3).line);
		assertEquals(6, scanner.getLinePos(token3).posInLine);
		
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(SEMI, token4.kind);
		assertEquals(20, token4.pos);
		assertEquals(1, token4.length);
		assertEquals (";", token4.getText());
		assertEquals(1, scanner.getLinePos(token4).line);
		assertEquals(7, scanner.getLinePos(token4).posInLine);
		
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token5.kind);
		assertEquals(2, scanner.getLinePos(token5).line);
		assertEquals(0, scanner.getLinePos(token5).posInLine);
		assertEquals("eof", token5.getText());
	}
	
	/* Multiple lines in input*/
	
	@Test
	public void multiLine () throws IllegalCharException, IllegalNumberException {
		String input = "/abc\n\nab\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(DIV, token.kind);
		assertEquals(0, token.pos);
		assertEquals(1, token.length);
		assertEquals ("/", token.getText());
		assertEquals(0, scanner.getLinePos(token).line);
		assertEquals(0, scanner.getLinePos(token).posInLine);
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(IDENT, token2.kind);
		assertEquals(1, token2.pos);
		assertEquals(3, token2.length);
		assertEquals ("abc", token2.getText());
		assertEquals(0, scanner.getLinePos(token2).line);
		assertEquals(1, scanner.getLinePos(token2).posInLine);
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(IDENT, token3.kind);
		assertEquals(6, token3.pos);
		assertEquals(2, token3.length);
		assertEquals ("ab", token3.getText());
		assertEquals(2, scanner.getLinePos(token3).line);
		assertEquals(0, scanner.getLinePos(token3).posInLine);

		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token4.kind);
		assertEquals(3, scanner.getLinePos(token4).line);
		assertEquals(0, scanner.getLinePos(token4).posInLine);
	}
	
	@Test
	public void testComment3 () throws IllegalCharException, IllegalNumberException {
		String input = "/* */*/ \nabc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		
		assertEquals (4, scanner.tokens.size());
		Scanner.Token token = scanner.nextToken();
		assertEquals(TIMES, token.kind);
		assertEquals(5, token.pos);
		assertEquals(1, token.length);
		assertEquals ("*", token.getText());
		assertEquals(0, scanner.getLinePos(token).line);
		assertEquals(5, scanner.getLinePos(token).posInLine);
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(DIV, token2.kind);
		assertEquals(6, token2.pos);
		assertEquals(1, token2.length);
		assertEquals ("/", token2.getText());
		assertEquals(0, scanner.getLinePos(token2).line);
		assertEquals(6, scanner.getLinePos(token2).posInLine);
		
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(IDENT, token3.kind);
		assertEquals(9, token3.pos);
		assertEquals(3, token3.length);
		assertEquals ("abc", token3.getText());
		assertEquals(1, token3.getLinePos().line);
		assertEquals(0, token3.getLinePos().posInLine);
		
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token4.kind);
		assertEquals(1, scanner.getLinePos(token4).line);
		assertEquals(3, scanner.getLinePos(token4).posInLine);
	}
	
	@Test
	public void testSingleEqual() throws IllegalCharException, IllegalNumberException{
		String input = "abc!==ab";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test
	public void testintVal() throws IllegalCharException, IllegalNumberException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		thrown.expect(NumberFormatException.class);
		token.intVal();
	}
}