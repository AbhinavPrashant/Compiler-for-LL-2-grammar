package cop5556sp17;

import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		
		Chain chain = binaryChain.getE0();
		TypeName chainType = (TypeName) chain.visit(this, arg);
		ChainElem chainElem = binaryChain.getE1();
		TypeName chainElemType = (TypeName) chainElem.visit(this, arg);
		Kind operator = binaryChain.getArrow().kind;
		
		// Arrow operator manipulation
		if (operator == ARROW){
			
			// 1st element is of type url and second is image, return type image
			if (chainType == URL){
				if (chainElemType == IMAGE ){
					binaryChain.setTypeName(IMAGE);
				}
				else
					throw new TypeCheckException(chainElem.getFirstToken().getLinePos().toString() +
							": Invalid operand type " + chainElemType.name() + "to url " + operator.getText());		
			}
			
			/* 1st element is type file and 2nd is of type image, return type is image*/
			else if (chainType == FILE){
				if (chainElemType == IMAGE ){
					binaryChain.setTypeName(IMAGE);
				}
				else
					throw new TypeCheckException(chainElem.getFirstToken().getLinePos().toString() +
							": Invalid operand type " + chainElemType.name() + "to file -> ");
			}
			
			/* 1st element type is frame */
			else if (chainType == FRAME){
				//2nd element is frame operator i.e xloc and yloc, chain type will be integer	
				if (chainElem instanceof FrameOpChain){	
					Kind firstToken = chainElem.getFirstToken().kind;
					if (firstToken == KW_XLOC|| firstToken == KW_YLOC){
						binaryChain.setTypeName(TypeName.INTEGER);
					}
					// 2nd element is show, hide or move, result will be of frame type
					else if (firstToken == KW_SHOW || firstToken == KW_HIDE || firstToken == KW_MOVE){
						binaryChain.setTypeName(TypeName.FRAME);
					}
				}
				else
					throw new TypeCheckException(chainElem.getFirstToken().getLinePos().toString() +
							" Type mismatch: Invalid 2nd operand to -> " + chainElemType.toString());
			}
			
			/* 1st element is image type */
			else if (chainType == IMAGE){
				
				if (chainElem instanceof ImageOpChain){
					Kind firstToken = chainElem.getFirstToken().kind;
					if (firstToken == OP_WIDTH || firstToken == OP_HEIGHT)
						binaryChain.setTypeName(INTEGER);
					else if (firstToken == Kind.KW_SCALE)
						binaryChain.setTypeName(IMAGE);
				}
				else if (chainElemType == FRAME){
					binaryChain.setTypeName(FRAME);
				}
				else if (chainElemType == FILE){
					binaryChain.setTypeName(NONE);
				}
				else if (chainElem instanceof FilterOpChain){
					Kind firstToken = chainElem.getFirstToken().kind;
					if (firstToken == OP_GRAY || firstToken == OP_BLUR || firstToken == OP_CONVOLVE)
						binaryChain.setTypeName(IMAGE);
				}
				else if (chainElem instanceof IdentChain && chainElem.getTypeName() == IMAGE){
					binaryChain.setTypeName(IMAGE);
				}
				else
					throw new TypeCheckException(chainElem.getFirstToken().getLinePos().toString() +
							": Invalid operation image -> " + chainElemType.toString() + ". Type inconsistent");
			}
			
			else if (chainType == INTEGER){
				// 2nd operand to arrow is of integer type
				if (chainElem instanceof IdentChain && chainElem.getTypeName() == INTEGER)
					binaryChain.setTypeName(INTEGER);
				else
					throw new TypeCheckException(chainElem.getFirstToken().getLinePos().toString() +
							": Invalid operation integer -> " + chainElemType.toString() + ". Expected integer");
					
			}
			
			else
				throw new TypeCheckException(chain.getFirstToken().getLinePos().toString() +
						": Invalid " + chainType.toString() + " chain type to arrow");
		}
		
		// Bar arrow operator. only image |-> filterOperator is defined. Return type is image
		else if (operator == BARARROW){
			// 1st element is of type image
			if (chainType == IMAGE){
				Kind firstToken = chainElem.firstToken.kind;
				if (chainElem instanceof FilterOpChain){
					if (firstToken == OP_GRAY || firstToken == OP_BLUR || firstToken == OP_CONVOLVE){
						binaryChain.setTypeName(IMAGE);
					}
				}
				else
					throw new TypeCheckException(chainElem.getFirstToken().getLinePos().toString() +
							" Type mismatch: Invalid 2nd operand " + chainElemType.toString() +
							" to operator |->. Expected filter operator");
			}
			else 
				throw new TypeCheckException(chain.getFirstToken().getLinePos().toString() +
							" Type mismatch: Invalid types " + chainType.toString() + ", " +
								chainElemType.toString()+ " to |-> operator");
		}
		
		return binaryChain.getTypeName();
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {

		TypeName e0TypeName, e1TypeName;
		
		Expression exp0 = binaryExpression.getE0();
		e0TypeName = (TypeName)exp0.visit(this, arg);
		Token op = binaryExpression.getOp();
		Expression exp1 = binaryExpression.getE1();
		e1TypeName = (TypeName) exp1.visit(this, arg);
		
		if (isKind(op, EQUAL, NOTEQUAL)){
			// if lvalue and rvalue type is not same; throw error
			if (e0TypeName != e1TypeName)
				throw new TypeCheckException(op.getLinePos().toString() + ": Operation " + op.kind.text + 
						" undefined. Arguments don't match");
			else
				binaryExpression.setType(BOOLEAN);
		}
		else if (e0TypeName == INTEGER){
			// If 2nd expression is integer type
			if  (e1TypeName == INTEGER){
				// plus minus times and divide result -> integer
				if (isKind(op, PLUS, MINUS, TIMES, DIV, AND, OR, MOD)){
					binaryExpression.setType(INTEGER);
				}
				// LT, GT, LE, GE result ->boolean
				else if (isKind (op, LT, GT, LE, GE)){
					binaryExpression.setType(BOOLEAN);
				}
				else
					throw new TypeCheckException(op.getLinePos().toString() +": Operation " + op.kind.text 
							+ " undefined for Integer");
			}
			
			// if 2nd expression is of type image
			else if (e1TypeName == IMAGE){
				
				// Only times, (div and mod)** is valid operation and result will be of type Image
				if (isKind(op, TIMES))
					binaryExpression.setType(IMAGE);
				else
					throw new TypeCheckException(op.getLinePos().toString() +": Operation " + op.kind.text 
							+ " undefined for Integer and Image");
			}
			// 2nd argument is neither integer nor image
			else
				throw new TypeCheckException(op.getLinePos().toString() + ": Second argument type is " + e1TypeName.name() +
							". Expected Integer or Image type to Integer");
		}
		
		// 1st expression is image type
		else if (e0TypeName == IMAGE) {
		
			// 2nd expression is image type
			if (e1TypeName == IMAGE){
				// only plus and minus operations are valid
				if (isKind (op, PLUS, MINUS)){
					binaryExpression.setType(IMAGE);
				}
				else
					throw new TypeCheckException(op.getLinePos().toString() + ": Operation " + op.kind.text + 
								" invalid for Image");
			}
			// 2nd expression is integer type
			else if (e1TypeName == INTEGER){
				// only times, (div and mod)** operation is valid between image and integer
				if (isKind (op, TIMES, DIV, MOD)){
					binaryExpression.setType(IMAGE);
				}
				else
					throw new TypeCheckException(op.getLinePos().toString() + ": Operation " +  op.kind.text+
								 " undefined for Image and Integer");

			}
			else
				throw new TypeCheckException(op.getLinePos().toString() + ": Second argument type is " + e1TypeName.name() +
							". Expected Image or Integer type to Image");			
		}
		
		//1st expression is boolean type 
		else if (e0TypeName == BOOLEAN){
			
			// 2nd argument must be boolean
			if (e1TypeName == BOOLEAN){
				if (isKind(op, LT, GT, LE, GE, AND, OR)){
					binaryExpression.setType(BOOLEAN);
				}
				else
					throw new TypeCheckException(op.getLinePos().toString() + ": Operation " + op.kind.text + 
								" undefined for boolean");
			}
			else
				throw new TypeCheckException(op.getLinePos().toString() + ": Second argument type is " + e1TypeName.name() +
						". Expected boolean type to boolean");
		}
		// None of the above conditions matches
		else
			throw new TypeCheckException (op.getLinePos().toString() +": Operation undefined. Expression 1 type " +
						e0TypeName.name() + " Expression 2 type " + e1TypeName.name() + " with operation " + op.kind.getText());
		
		return binaryExpression.getType();
	}

	// check whether operator is one of the type as specified in opKind
	private boolean isKind(Token operator, Kind...opKind) {
		
		for (Kind op: opKind){
			if (op == operator.kind)
				return true;
		}
		return false;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {

		symtab.enterScope();
		for (Dec dec: block.getDecs())
			dec.visit(this, arg);

		for (Statement st: block.getStatements())
			st.visit(this, arg);
		
		symtab.leaveScope();
		
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		
		booleanLitExpression.setType(BOOLEAN);
	
		return booleanLitExpression.getType();
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {

		int argLength = (int)filterOpChain.getArg().visit(this, arg);
		if (argLength != 0){
			throw new TypeCheckException(filterOpChain.getArg().getFirstToken().getLinePos().toString() +
					": No arguments expected to " + filterOpChain.getFirstToken().getText());
		}
		filterOpChain.setTypeName(IMAGE);
		return filterOpChain.getTypeName();
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {

		Token frameOpKind = frameOpChain.getFirstToken();
		int argLength;
		
		if (frameOpKind.kind == KW_HIDE || frameOpKind.kind == KW_SHOW){
			argLength = (int) frameOpChain.getArg().visit(this, arg);
			if (argLength != 0){
				throw new TypeCheckException( frameOpKind.getLinePos().toString() + ": " +
							frameOpKind.getText() +	" does not expects arguments");
			}
			frameOpChain.setTypeName(NONE);
		}
		else if (frameOpKind.kind == KW_XLOC || frameOpKind.kind == KW_YLOC){
			argLength = (int) frameOpChain.getArg().visit(this, arg);
			if (argLength != 0){
				throw new TypeCheckException(frameOpKind.getLinePos().toString() + ": " +
							frameOpKind.getText() +	" does not expects arguments");
			}
			frameOpChain.setTypeName(INTEGER);
		}
		else if (frameOpKind.kind == KW_MOVE){
			argLength = (int) frameOpChain.getArg().visit(this, arg);
			if (argLength != 2){
				throw new TypeCheckException(frameOpKind.getLinePos().toString() + ": " +
							frameOpKind.getText() +	" expects 2 arguments");
			}
			frameOpChain.setTypeName(NONE);
		}
		else
			throw new Exception("******* PARSER EXCEPTION *******");
		
		return frameOpChain.getTypeName();
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {

		Token ident = identChain.firstToken;
		Dec dec = symtab.lookup(ident.getText());
		if (dec != null){
			identChain.setDec(dec);
			identChain.setTypeName(Type.getTypeName(dec.firstToken));
		}
		else
			throw new TypeCheckException(ident.getLinePos().toString() + ": Variable " + ident.getText() +
											" not declared");
		// Return the type of ident chain
		return identChain.getTypeName();
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		
		Token ident = identExpression.firstToken;
		Dec dec = symtab.lookup(ident.getText());
		if (dec != null){
			identExpression.setDec(dec);
			identExpression.setType(Type.getTypeName(identExpression.getDec().getType()));
		}
		else
			throw new TypeCheckException(ident.getLinePos().toString() + ": Variable " + ident.getText() + 
											" not declared");
		// Return the type of expression
		return identExpression.getType();
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {

		Expression exp = ifStatement.getE();
		Block b = ifStatement.getB();
		
		TypeName typeName = (TypeName) exp.visit(this, arg);
		if (! typeName.isType(BOOLEAN)){
			throw new TypeCheckException(exp.getFirstToken().getLinePos().toString() +
							": Invalid Expression to if statement. Expected boolean");
		}
		
		b.visit(this, arg);
		
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		
		intLitExpression.setType(INTEGER);
		return intLitExpression.getType();
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {

		Expression exp = sleepStatement.getE();
		TypeName typeName = (TypeName) exp.visit(this, arg);
		if (typeName != INTEGER)
			throw new TypeCheckException(exp.getFirstToken().getLinePos().toString() +
							": Invalid Sleep Statement. Expected Integer Expression");
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {

		Expression exp = whileStatement.getE();
		Block b = whileStatement.getB();
		
		TypeName typeName = (TypeName) exp.visit(this, arg);
		if (! typeName.isType(BOOLEAN)){
			throw new TypeCheckException(exp.getFirstToken().getLinePos().toString() +
							": Invalid Expression to while statement. Expected boolean");
		}
		
		b.visit(this, arg);
		
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {

		Token ident = declaration.getIdent();
		if (! symtab.insert(ident.getText(), declaration)){
			throw new TypeCheckException(ident.getLinePos().toString() +": Variable " + ident.getText() + 
							" is already declared");
		}
		// set type name for declaration
		declaration.setTypename(Type.getTypeName(declaration.getFirstToken()));
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		
		for (Dec dec: program.getParams()){
			dec.visit(this, arg);
		}
		
		program.getB().visit(this, arg);
		
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		
		IdentLValue lvalue = assignStatement.getVar();
		TypeName lvalueType = (TypeName) lvalue.visit(this, arg);
		Expression exp = assignStatement.getE();
		TypeName expressionType = (TypeName) exp.visit(this, arg);
		
		if (lvalueType != expressionType){
			throw new TypeCheckException(lvalue.getFirstToken().getLinePos().toString() +
					" Type mismatch: Cannot assign " + expressionType.toString() +
					" to " +lvalue.firstToken.getText());
		}
		
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {

		Dec dec = symtab.lookup(identX.getText());
		if (dec == null){
			throw new TypeCheckException(identX.firstToken.getLinePos().toString() + ": Variable "
					+ identX.getText() + " not declared");
		}
		
		identX.setDec(dec);
		
		return Type.getTypeName(dec.getType());
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		
		Token ident = paramDec.getIdent();
		if (! symtab.insert(ident.getText(), paramDec)){
			throw new TypeCheckException(ident.getLinePos().toString() + ": Parameter " +
							ident.getText() + " is already declared");
		}
		// Set type for param
		paramDec.setTypename(Type.getTypeName(paramDec.getFirstToken()));
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		
		constantExpression.setType(INTEGER);
		
		return constantExpression.getType();
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
	
		Token imageOpKind = imageOpChain.getFirstToken();
		int argLength;
		
		if (imageOpKind.kind == OP_WIDTH || imageOpKind.kind == OP_HEIGHT){
			argLength = (int) imageOpChain.getArg().visit(this, arg);
			if (argLength != 0){
				throw new TypeCheckException(imageOpKind.getLinePos().toString() + ": Operator " +
								imageOpKind.getText() +	" does not expects arguments");
			}
			imageOpChain.setTypeName(INTEGER);
		}
		else if (imageOpKind.kind == KW_SCALE){
			argLength = (int) imageOpChain.getArg().visit(this, arg);
			if (argLength != 1){
				throw new TypeCheckException(imageOpKind.getLinePos().toString() + ": Operator " +
								imageOpKind.getText() + " expects 1 argument");
			}
			imageOpChain.setTypeName(IMAGE);
		}
		
		return imageOpChain.getTypeName();
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {

		int length =0;
		for (Expression exp: tuple.getExprList()){
			TypeName typeName = (TypeName) exp.visit(this, arg);
			length++;
			if (typeName != INTEGER){
				throw new TypeCheckException(exp.getFirstToken().getLinePos().toString() +
								": Expression is invalid. Expected Integer Expression");
			}
		}
		return length;
	}
}
