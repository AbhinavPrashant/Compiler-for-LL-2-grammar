package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	int slotNumber = 1;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		int argIndex = 0;
		for (ParamDec dec : params) {
			dec.visit(this, argIndex);
			argIndex++;
		}
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		// TODO visit the local variables
		for (Dec dec : program.getB().getDecs()) {
			mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypename().getJVMTypeDesc(), null, dec.getStart(),
					dec.getEnd(), dec.getSlotNumber());
		}
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method

		cw.visitEnd();// end of class

		// generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		Expression exp = assignStatement.getE();
		exp.visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {

		Chain chain = binaryChain.getE0();
		chain.visit(this, "left");

		if (chain.getTypeName() == TypeName.URL) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL",
					PLPRuntimeImageIO.readFromURLSig, false);
		}
		if (chain.getTypeName() == TypeName.FILE) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile",
					PLPRuntimeImageIO.readFromFileDesc, false);
		}

		ChainElem chainElem = binaryChain.getE1();

		if (chainElem instanceof FilterOpChain) {
			if (binaryChain.getArrow().kind == Kind.BARARROW && chainElem.getFirstToken().kind == Kind.OP_GRAY)
				mv.visitInsn(DUP);
			else
				mv.visitInsn(ACONST_NULL);
		}

		chainElem.visit(this, "right");

		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {

		Expression exp0 = binaryExpression.getE0();
		exp0.visit(this, arg);
		Expression exp1 = binaryExpression.getE1();
		exp1.visit(this, arg);

		Kind operator = binaryExpression.getOp().kind;

		int opCode = -1;

		switch (operator) {

		case PLUS:
			if (exp0.getType() == TypeName.IMAGE) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			} else
				mv.visitInsn(IADD);
			break;

		case MINUS:
			if (exp0.getType() == TypeName.IMAGE)
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			else
				mv.visitInsn(ISUB);
			break;

		case TIMES:
			if (exp0.getType() == TypeName.IMAGE && exp1.getType() == TypeName.INTEGER)
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);

			else if (exp0.getType() == TypeName.INTEGER && exp1.getType() == TypeName.IMAGE) {
				mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);

			} else
				mv.visitInsn(IMUL);
			break;

		case DIV:
			if (exp0.getType() == TypeName.IMAGE && exp1.getType() == TypeName.INTEGER)
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);

			else
				mv.visitInsn(IDIV);
			break;

		case GT:
			opCode = IF_ICMPLE;
			break;

		case GE:
			opCode = IF_ICMPLT;
			break;

		case LT:
			opCode = IF_ICMPGE;
			break;

		case LE:
			opCode = IF_ICMPGT;
			break;

		case EQUAL:
			if (exp0.getType() == TypeName.BOOLEAN || exp0.getType() == TypeName.INTEGER)
				opCode = IF_ICMPNE;
			else
				opCode = IF_ACMPNE;
			break;

		case NOTEQUAL:
			if (exp0.getType() == TypeName.BOOLEAN || exp0.getType() == TypeName.INTEGER)
				opCode = IF_ICMPEQ;
			else
				opCode = IF_ACMPEQ;
			break;

		case AND:
			mv.visitInsn(IAND);
			break;

		case OR:
			mv.visitInsn(IOR);
			break;

		case MOD:
			if (exp0.getType() == TypeName.IMAGE && exp1.getType() == TypeName.INTEGER)
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);

			else
				mv.visitInsn(IREM);
			break;

		default:
			break;

		}

		// check on this
		if (operator == GT || operator == GE || operator == LT || operator == LE || operator == EQUAL
				|| operator == NOTEQUAL) {

			Label condOp = new Label();
			mv.visitJumpInsn(opCode, condOp);
			mv.visitLdcInsn(1);
			Label condOp2 = new Label();
			mv.visitJumpInsn(GOTO, condOp2);
			mv.visitLabel(condOp);
			mv.visitLdcInsn(0);
			mv.visitLabel(condOp2);
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {

		Label blockStart = new Label();
		Label blockEnd = new Label();
		mv.visitLabel(blockStart);

		for (Dec dec : block.getDecs()) {
			dec.setStart(blockStart);
			dec.setEnd(blockEnd);
			dec.visit(this, arg);
		}

		for (Statement statement : block.getStatements()) {
			statement.visit(this, arg);

			// statement was a binary chain so it would have left value on top
			// of stack so popping it
			if (statement instanceof BinaryChain)
				mv.visitInsn(POP);
		}
		mv.visitLabel(blockEnd);

		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {

		if (booleanLitExpression.getValue())
			mv.visitLdcInsn(1);
		else
			mv.visitLdcInsn(0);

		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {

		Kind expKind = constantExpression.getFirstToken().kind;

		if (expKind == KW_SCREENWIDTH) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth",
					PLPRuntimeFrame.getScreenWidthSig, false);
		} else if (expKind == KW_SCREENHEIGHT) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight",
					PLPRuntimeFrame.getScreenHeightSig, false);
		}

		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {

		declaration.setSlotNumber(slotNumber++);

		if (declaration.getTypename() == TypeName.FRAME || declaration.getTypename() == TypeName.IMAGE) {
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlotNumber());
		}

		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {

		Kind filterOp = filterOpChain.getFirstToken().kind;

		if (filterOp == OP_BLUR) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);

		} else if (filterOp == OP_CONVOLVE) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig,
					false);

		} else if (filterOp == OP_GRAY) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
		}

		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {

		Kind operator = frameOpChain.getFirstToken().kind;

		if (operator == KW_SHOW)
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc,
					false);
		else if (operator == KW_HIDE)
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc,
					false);
		else if (operator == KW_XLOC)
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc,
					false);

		else if (operator == KW_YLOC)
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc,
					false);

		else if (operator == KW_MOVE) {

			frameOpChain.getArg().visit(this, arg);

			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc,
					false);
		}

		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {

		String side = (String) arg;

		if (side == "left") {
			if (identChain.getDec() instanceof ParamDec) {
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(),
						identChain.getTypeName().getJVMTypeDesc());
				
			} else if (identChain.getDec() instanceof Dec) {
				TypeName typeName = identChain.getTypeName();
				
				if (typeName == TypeName.IMAGE || typeName == TypeName.FRAME)
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlotNumber());
				else
					mv.visitVarInsn(ILOAD, identChain.getDec().getSlotNumber());
			}
			
		} else { // It is on right
			TypeName identType = identChain.getTypeName();

			if (identType == TypeName.INTEGER) {
				// since top of stack will be stored in the variable but we need
				// it on top of the stack for further binary chain operation
				mv.visitInsn(DUP);

				if (identChain.getDec() instanceof ParamDec) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, identChain.getFirstToken().getText(),
							identChain.getTypeName().getJVMTypeDesc());

				} else if (identChain.getDec() instanceof Dec) {
					mv.visitVarInsn(ISTORE, identChain.getDec().getSlotNumber());
				}
			}

			else if (identType == TypeName.IMAGE) {
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, identChain.getDec().getSlotNumber());
			}

			else if (identType == TypeName.FILE) { 
				// mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(),
						identChain.getTypeName().getJVMTypeDesc());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc,
						false);
			}

			else if (identType == TypeName.FRAME) {
				/*
				 * invoke createOrSet method to set the image on the top of the
				 * stack. Method takes 2 arguments: image and frame pushing frame
				 * on top of stack
				 */
				mv.visitVarInsn(ALOAD, identChain.getDec().getSlotNumber());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame",
						PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, identChain.getDec().getSlotNumber());
			}
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {

		// Load ident expression on top of the stack
		// Class instance with PUTFIELD and local variables with ILOAD
		if (identExpression.getDec() instanceof ParamDec) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.getFirstToken().getText(),
					identExpression.getType().getJVMTypeDesc());

		} else if (identExpression.getDec() instanceof Dec) {
			TypeName typeName = identExpression.getDec().getTypename();

			if (typeName == IMAGE || typeName == FRAME) {
				mv.visitVarInsn(ALOAD, identExpression.getDec().getSlotNumber());

			} else
				mv.visitVarInsn(ILOAD, identExpression.getDec().getSlotNumber());
		}

		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {

		if (identX.getDec() instanceof ParamDec) {

			mv.visitVarInsn(ALOAD, 0);

			/*
			 * when we are visiting assignment statement and we have loaded
			 * result of right hand side on top of stack and while trying to
			 * assign this value to class instance we need to swap top two
			 * elements of the stack. Top of stack should have result of right
			 * hand side and object of class below it whereas here its opposite
			 */
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, identX.getFirstToken().getText(),
					identX.getDec().getTypename().getJVMTypeDesc());

		} else if (identX.getDec() instanceof Dec) {

			if (identX.getDec().getTypename() == TypeName.IMAGE) {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage",
						PLPRuntimeImageOps.copyImageSig, false);

				// Calling copy image will return a new Buffered image on top of
				// the stack. Store this value in the image variable
				mv.visitVarInsn(ASTORE, identX.getDec().getSlotNumber());

			} else if (identX.getDec().getTypename() == TypeName.FRAME) {
				mv.visitVarInsn(ASTORE, identX.getDec().getSlotNumber());

			} else
				mv.visitVarInsn(ISTORE, identX.getDec().getSlotNumber());
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {

		Expression exp = ifStatement.getE();
		exp.visit(this, arg);

		/*
		 * Create a label AFTER which will allow the program to skip block if
		 * top of stack value is 0
		 */
		Label AFTER = new Label();
		/* Jump if top of stack is 0 i.e. expression has evaluated to false */
		mv.visitJumpInsn(IFEQ, AFTER);

		ifStatement.getB().visit(this, arg);

		mv.visitLabel(AFTER);

		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {

		Kind imageOp = imageOpChain.getFirstToken().kind;

		if (imageOp == OP_HEIGHT)
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight", "()I", false);
		else if (imageOp == OP_WIDTH)
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", "()I", false);

		else if (imageOp == KW_SCALE) {

			imageOpChain.getArg().visit(this, arg);

			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {

		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {

		String fieldName = paramDec.getIdent().getText();
		TypeName type = paramDec.getTypename();
		String fieldType = type.getJVMTypeDesc();

		if (type == TypeName.INTEGER) {
			cw.visitField(ACC_PUBLIC, fieldName, fieldType, null, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn((int) arg);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldType);

		} else if (type == TypeName.BOOLEAN) {
			cw.visitField(ACC_PUBLIC, fieldName, fieldType, null, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn((int) arg);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldType);

		} else if (type == URL) {
			cw.visitField(ACC_PUBLIC, fieldName, fieldType, null, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn((int) arg);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldType);

		} else if (type == TypeName.FILE) { // This should be file type
			cw.visitField(ACC_PUBLIC, fieldName, fieldType, null, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn((int) arg);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldType);
		}

		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {

		sleepStatement.getE().visit(this, arg);

		mv.visitInsn(I2L);

		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {

		for (Expression exp : tuple.getExprList())
			exp.visit(this, arg);

		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {

		// Evaluate the expression first
		Label GUARD = new Label();
		mv.visitJumpInsn(GOTO, GUARD);

		// Body of the loop. visit only when expression evaluates to true, i.e.
		// top of stack is 1
		Label BODY = new Label();
		mv.visitLabel(BODY);

		whileStatement.getB().visit(this, arg);

		mv.visitLabel(GUARD);
		// evaluate the expression and put result on top of stack
		whileStatement.getE().visit(this, arg);
		// if top of stack is 1 jump to body of the label
		mv.visitJumpInsn(IFNE, BODY);

		return null;
	}
}
