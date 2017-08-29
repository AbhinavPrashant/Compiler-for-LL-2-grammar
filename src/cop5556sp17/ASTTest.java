package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.*;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}



	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}


	@Test
	public void testBooleanLit() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "false + screenwidth";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BooleanLitExpression.class, be.getE0().getClass());
		assertEquals(ConstantExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep 5;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(SleepStatement.class, ast.getClass());
		assertEquals(OP_SLEEP, ast.firstToken.kind);
	}
	
	@Test
	public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while (1) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
		assertEquals(KW_WHILE, ast.firstToken.kind);
	}
	
	@Test
	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if (1) {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(IfStatement.class, ast.getClass());
		assertEquals(KW_IF, ast.firstToken.kind);
	}
	
	@Test
	public void testStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc <- 32;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(AssignmentStatement.class, ast.getClass());
		assertEquals(IDENT, ast.firstToken.kind);
		AssignmentStatement assign = (AssignmentStatement) ast;
		assertEquals(IDENT, assign.var.firstToken.kind);
		assertEquals(IntLitExpression.class, assign.e.getClass());
		
	}
	
	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(2, abc)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		assertEquals(LPAREN, ast.firstToken.kind);
		Tuple tuple = (Tuple) ast;
		assertEquals(IntLitExpression.class, tuple.getExprList().get(0).getClass());
		assertEquals(IdentExpression.class, tuple.getExprList().get(1).getClass());
	}
	
	@Test
	public void testchain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc -> blur |-> hide |-> height";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
		assertEquals(IDENT, ast.firstToken.kind);
		BinaryChain c = (BinaryChain) ast;
		assertEquals(ImageOpChain.class, c.getE1().getClass());
		assertEquals(BARARROW, c.getArrow().kind);
		assertEquals (FrameOpChain.class, ((BinaryChain)c.getE0()).getE1().getClass());
		BinaryChain bnext = (BinaryChain)c.getE0();
		assertEquals (FilterOpChain.class, ((BinaryChain)bnext.getE0()).getE1().getClass());
		BinaryChain bnext2 = (BinaryChain)c.getE0();
		assertEquals (IdentChain.class, ((BinaryChain)bnext2.getE0()).getE0().getClass());
		assertEquals (ARROW, ((BinaryChain)bnext2.getE0()).getArrow().kind);
	}
	
	@Test
	public void testImageOpStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "tos url u,\n integer x\n{integer y image i u -> i; i -> height -> x; frame f i -> scale (x) -> f;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();

	}
	
	@Test
	public void testProg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		
		String input = "p url u1 {}";
		Scanner scanner = new Scanner (input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void testProg2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		
		String input = "tos integer x\n{image i frame f i -> scale (x) -> f;}";
		Scanner scanner = new Scanner (input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();
		;

	}
	
	@Test
	public void testProg3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		
		String input = "p url u1, url u2, file f1, file f2, integer i {}";
		Scanner scanner = new Scanner (input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void testAssign() throws Exception{
		String input = "p integer y, boolean x {\n y <- 2+5;\n x <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.parse();
	}
}
