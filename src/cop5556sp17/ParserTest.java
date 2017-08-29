package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import static cop5556sp17.Scanner.Kind.*;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  true * screenheight ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  true * * screenheight ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect (SyntaxException.class);
        parser.elem();
	}

	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc123%false|false*true>5";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.expression();
	}
	
	// Revisit this
	@Test
	public void testExpression2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc123%false|false true>5";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.expression();
        assertEquals (KW_TRUE, parser.t.kind);
	}
	
	@Test
	public void testChainElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "yloc (a+b)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.chainElem();
	}
	
	@Test
	public void testChainElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "yloc    ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.chainElem();
        assertEquals (EOF, parser.t.kind);
	}
	
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc |->abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.chain();
	}
	
	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc<-2";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.assign();
	}
	
	@Test
	public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc<-2;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.statement();
	}
	
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image abc123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.dec();
	}
	
	@Test
	public void testDec2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image height";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
        parser.dec();
	}
	
	@Test
	public void testDec3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image abc123;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.dec();
	}
	@Test
	public void testblock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer abc \n while (abc <5){image e \n } abc<- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.block();
	}
	
	@Test
	public void testblock2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{integer abc \n while (abc <5){image e \n } abc<- 3;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect (SyntaxException.class);
        parser.block();
	}
	
	@Test
	public void testblock3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{abc|->blur;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.block();
        assertEquals (EOF, parser.t.kind);
	}
	@Test
	public void testblock4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{{}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
        parser.block();
	}
	
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main integer check {boolean abc \n abc<- 3;\n image pic sleep 5;\n frame frame1\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.parse();
        assertEquals (EOF,parser.t.kind);
	}
	
	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main integer check {boolean abc \n abc<- 3;\n image pic sleep 5;\n} frame frame1\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testProgram3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sum {image abc123;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
        parser.parse();
	}
	
	@Test
	public void testProgram4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "main integer abc { boolean fyi\n if (hyt==true){ abc<-2;}\n if (hyt ==false){abc <- fyi;}\n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
        parser.parse();
        assertEquals (EOF, parser.t.kind);
	}
	
	@Test
	public void testProgram5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "    ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
        parser.parse();
	}
}
