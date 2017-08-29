package cop5556sp17;

import static cop5556sp17.Scanner.Kind.AND;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.ASSIGN;
import static cop5556sp17.Scanner.Kind.BARARROW;
import static cop5556sp17.Scanner.Kind.COMMA;
import static cop5556sp17.Scanner.Kind.DIV;
import static cop5556sp17.Scanner.Kind.EOF;
import static cop5556sp17.Scanner.Kind.EQUAL;
import static cop5556sp17.Scanner.Kind.GE;
import static cop5556sp17.Scanner.Kind.GT;
import static cop5556sp17.Scanner.Kind.IDENT;
import static cop5556sp17.Scanner.Kind.KW_BOOLEAN;
import static cop5556sp17.Scanner.Kind.KW_FILE;
import static cop5556sp17.Scanner.Kind.KW_FRAME;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_IF;
import static cop5556sp17.Scanner.Kind.KW_IMAGE;
import static cop5556sp17.Scanner.Kind.KW_INTEGER;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SCALE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_URL;
import static cop5556sp17.Scanner.Kind.KW_WHILE;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.LBRACE;
import static cop5556sp17.Scanner.Kind.LE;
import static cop5556sp17.Scanner.Kind.LPAREN;
import static cop5556sp17.Scanner.Kind.LT;
import static cop5556sp17.Scanner.Kind.MINUS;
import static cop5556sp17.Scanner.Kind.MOD;
import static cop5556sp17.Scanner.Kind.NOTEQUAL;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_SLEEP;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.OR;
import static cop5556sp17.Scanner.Kind.PLUS;
import static cop5556sp17.Scanner.Kind.RBRACE;
import static cop5556sp17.Scanner.Kind.RPAREN;
import static cop5556sp17.Scanner.Kind.SEMI;
import static cop5556sp17.Scanner.Kind.TIMES;

import java.util.List;
import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input. You
	 * will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}

	/**
	 * Useful during development to ensure unimplemented routines are not
	 * accidentally called during development. Delete it when the Parser is
	 * finished.
	 *
	 */
	@SuppressWarnings("serial")
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner. Check for EOF (i.e. no
	 * trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	Program parse() throws SyntaxException {
		Program p = null;
		p = program();
		matchEOF();
		return p;
	}

	Expression expression() throws SyntaxException {
		// TODO
		// expression::= term (relOp term)*
		Expression exp0;
		Expression exp1;

		/* Store the first token */
		Token firstToken = t;
		exp0 = term();
		while (relOp(t.kind)) {
			Token operator = t;
			consume();
			exp1 = term();
			exp0 = new BinaryExpression(firstToken, exp0, operator, exp1);
		}
		return exp0;
	}

	Expression term() throws SyntaxException {
		// TODO
		// term ::= elem (weakOp elem)*s

		Expression exp0;
		Expression exp1;

		Token firstToken = t;
		exp0 = elem();
		while (weakOp(t.kind)) {
			Token operator = t;
			consume();
			exp1 = elem();
			exp0 = new BinaryExpression(firstToken, exp0, operator, exp1);
		}
		return exp0;
	}

	Expression elem() throws SyntaxException {
		// TODO
		// elem::= factor (strongOP factor)*
		Expression exp0 = null;
		Expression exp1 = null;
		Token firstToken = t;
		exp0 = factor();
		while (strongOp(t.kind)) {
			Token operator = t;
			consume();
			exp1 = factor();
			exp0 = new BinaryExpression(firstToken, exp0, operator, exp1);
		}
		return exp0;
	}

	private boolean strongOp(Kind kind) throws SyntaxException {
		// Returns true if token is (*, /, &, % )
		if (kind == TIMES || kind == DIV || kind == AND || kind == MOD) {
			return true;
		} else
			return false;
	}

	private boolean weakOp(Kind kind) throws SyntaxException {
		// Returns true if token is (*, /, &, % )
		if (kind == PLUS || kind == MINUS || kind == OR) {
			return true;
		} else
			return false;
	}

	private boolean relOp(Kind kind) throws SyntaxException {
		// Returns true if token is (*, /, &, % )
		if (kind == LT || kind == LE || kind == GT || kind == GE || kind == EQUAL || kind == NOTEQUAL) {
			return true;
		} else
			return false;
	}

	Expression factor() throws SyntaxException {
		Expression exp = null;
		Token firstToken = t;
		Kind kind = firstToken.kind;
		switch (kind) {
		case IDENT: {
			exp = new IdentExpression(firstToken);
			consume();
		}
			break;
		case INT_LIT: {
			exp = new IntLitExpression(firstToken);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			exp = new BooleanLitExpression(firstToken);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			// screenWidth and screenHeight are constant expression
			exp = new ConstantExpression(firstToken);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			exp = expression();
			match(RPAREN);
		}
			break;
		default:
			// you will want to provide a more useful error message
			throw new SyntaxException("illegal character " + t.getText() + "at position " + t.pos);
		}
		return exp;
	}

	Block block() throws SyntaxException {
		// block ::= {(dec |statement)*}
		Token firstToken = null;
		int i = 0;
		int j = 0;
		ArrayList<Dec> decList = new ArrayList<Dec>();
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		firstToken = t;
		match(LBRACE);

		while (scanner.tokenNum < scanner.tokens.size() && t.kind != RBRACE) {
			if (t.kind == KW_INTEGER || t.kind == KW_BOOLEAN || t.kind == KW_IMAGE || t.kind == KW_FRAME) {
				decList.add(i++, dec());
			} else
				statementList.add(j++, statement());
		}
		match(RBRACE);

		return new Block(firstToken, decList, statementList);
	}

	Program program() throws SyntaxException {
		// program ::= IDENT block
		// program ::= IDENT param_dec ( , param_dec )* block

		Token firstToken = t;
		Block b = null;
		ArrayList<ParamDec> pdList = new ArrayList<ParamDec>();
		int i = 0;

		match(IDENT);
		if (t.kind == LBRACE)
			b = block();
		else {
			pdList.add(i++, paramDec());
			while (t.kind == COMMA) {
				consume();
				pdList.add(i++, paramDec());
			}
			
			b = block();
		}
		return new Program(firstToken, pdList, b);
	}

	ParamDec paramDec() throws SyntaxException {
		// TODO
		// paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN) IDENT

		Token firstToken = t;
		match(KW_URL, KW_FILE, KW_INTEGER, KW_BOOLEAN);
		Token ident = t;
		match(IDENT);

		return new ParamDec(firstToken, ident);

	}

	Dec dec() throws SyntaxException {
		// dec ::= (KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME) IDENT
		Token firstToken = t;
		match(KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME);
		Token ident = t;
		match(IDENT);
		return new Dec(firstToken, ident);
	}

	Statement statement() throws SyntaxException {
		// TODO
		// statement ::= OP_SLEEP expression ; | whileStatement | ifStatement |
		// chain ; | assign ;

		Statement st = null;
		if (t.kind == OP_SLEEP) {
			Token firstToken = t;
			consume();
			Expression exp = expression();
			match(SEMI);
			st = new SleepStatement(firstToken, exp);
		} else if (t.kind == KW_WHILE) {
			st = whileStatement();
		} else if (t.kind == KW_IF)
			st = ifStatement();
		else if (scanner.peek().kind == ASSIGN) {
			st = assign();
			match(SEMI);
		} else {
			st = chain();
			match(SEMI);
		}

		return st;
	}

	AssignmentStatement assign() throws SyntaxException {
		// assign ::= IDENT ASSIGN expression
		IdentLValue ilvalue = null;
		Expression exp = null;

		Token firstToken = t;
		try {
			ilvalue = new IdentLValue(firstToken);
			match(IDENT);
			match(ASSIGN);
			exp = expression();
		} catch (Exception e) {
			throw new SyntaxException("Not accepted assignment expression at pos " + t.pos);
		}

		return new AssignmentStatement(firstToken, ilvalue, exp);
	}

	WhileStatement whileStatement() throws SyntaxException {
		// whileStatement ::= KW_WHILE (expression) block
		Expression exp = null;
		Block b = null;

		Token firstToken = t;
		match(KW_WHILE);
		match(LPAREN);
		exp = expression();
		match(RPAREN);

		b = block();

		return new WhileStatement(firstToken, exp, b);
	}

	IfStatement ifStatement() throws SyntaxException {
		// ifStatement ::= KW_IF ( expression ) block
		Expression exp = null;
		Token firstToken = t;
		Block b = null;
		match(KW_IF);
		match(LPAREN);
		exp = expression();
		match(RPAREN);

		b = block();

		return new IfStatement(firstToken, exp, b);
	}

	Chain chain() throws SyntaxException {
		// chain ::= chainElem arrowOp chainElem (arrowOp chainElem)*
		BinaryChain binChain = null;
		ChainElem chain = null;
		Token firstToken = t;
		chain = chainElem();
		if (arrowOp()) {
			Token arrow = t;
			consume();
			ChainElem ce = chainElem();
			binChain = new BinaryChain(firstToken, chain, arrow, ce);
			while (arrowOp()) {
				arrow = t;
				consume();
				ce = chainElem();
				binChain = new BinaryChain(firstToken, binChain, arrow, ce);
			}
		} else
			throw new SyntaxException("Expected ARROW/BARARROW, found " + t.getText() + " at pos " + t.pos);

		return binChain;
	}

	boolean arrowOp() throws SyntaxException {
		if (t.kind == ARROW || t.kind == BARARROW) {
			return true;
		} else
			return false;
	}

	ChainElem chainElem() throws SyntaxException {
		// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg

		ChainElem chainElem = null;

		if (t.kind == IDENT)
			chainElem = identChain();
		else if (t.kind == OP_BLUR || t.kind == OP_GRAY | t.kind == OP_CONVOLVE)
			chainElem = flChain();
		else if (t.kind == KW_SHOW || t.kind == KW_HIDE || t.kind == KW_MOVE || t.kind == KW_XLOC || t.kind == KW_YLOC)
			chainElem = frChain();
		else if (t.kind == OP_WIDTH || t.kind == OP_HEIGHT || t.kind == KW_SCALE)
			chainElem = ImageChain();
		else
			throw new SyntaxException(t.getLinePos().toString() + ": Illegal char " + t.getText());

		return chainElem;
	}

	/*
	 * IdentChain ::= ident FilterOpChain ::= filterOp Tuple FrameOpChain ::=
	 * frameOp Tuple ImageOpChain ::= imageOp Tuple BinaryChain ::= Chain (arrow
	 * | bararrow) ChainElem
	 */

	IdentChain identChain() throws SyntaxException {
		Token firstToken = t;
		consume();
		return new IdentChain(firstToken);
	}

	FilterOpChain flChain() throws SyntaxException {
		Token firstToken = t;
		consume();
		Tuple argument = arg();
		return new FilterOpChain(firstToken, argument);
	}

	FrameOpChain frChain() throws SyntaxException {
		Token firstToken = t;
		consume();
		Tuple argument = arg();
		return new FrameOpChain(firstToken, argument);
	}

	ImageOpChain ImageChain() throws SyntaxException {
		Token firstToken = t;
		consume();
		Tuple argument = arg();
		return new ImageOpChain(firstToken, argument);
	}

	// TODO pay attention to this ***************************************
	/*
	 * BinaryChain binChain () throws SyntaxException { Token firstToken = t;
	 * Chain c = chain (); Token arrow = t; match (ARROW, BARARROW); ChainElem
	 * cm = chainElem (); return new BinaryChain (firstToken, c, arrow, cm); }
	 */

	Tuple arg() throws SyntaxException {
		// arg ::= E\ (expression (, expression)* )
		List<Expression> expList = new ArrayList<Expression>();
		int i = 0;
		Token firstToken = t;

		if (t.kind == LPAREN) {
			// Assign left paren to first Token
			consume();
			expList.add(i++, expression());
			while (t.kind == COMMA) {
				consume();
				// Add expressions to ArrayList
				expList.add(i++, expression());
			}
			match(RPAREN);
		}

		return new Tuple(firstToken, expList);
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.kind == kind) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy

		boolean matched = false;

		for (Kind k : kinds) {
			if (t.kind == k)
				matched = true;
		}
		if (matched)
			return consume();
		else
			throw new SyntaxException("Unexpected symbol " + t.getText());

	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
