
package cop5556sp17;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;

public class CodeGenVisitorTest_3 {

	static final boolean doPrint = true;

	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	boolean devel = false;
	boolean grade = true;
	private String copyImage = "copyImage";
	private String cosFrame = "createOrSetFrame";
	private String showImage = "showImage";
	private String setImage = "createOrSetFrameshowImage"; // this is potentially a bug in the provided code
	private String getX = "getX";
	private String getY = "getY";
	private String moveFrame = "moveFrame";
	private String scale = "scale";
	private String blur = "blurOp";
	private String gray = "grayOp";
	private String convolve = "convolve";
	private String iadd = "add";
	private String isub = "sub";
	private String imul = "mul";
	private String idiv = "div";
	private String imod = "mod";
	private String hideImage = "hideImage";
	private String getScreenWidth = "getScreenWidth";
	private String getScreenHeight = "getScreenHeight";
	
	
	
	@Before
	public void initLog(){
		if (devel || grade) 
			PLPRuntimeLog.initLog();
	}

	@After
	public void printLog(){
		System.out.println(PLPRuntimeLog.getString());
	}

	public String getRFURL(String url){
		return "readFromURL("+url+")";
	}
	public String getCLURL(String url){
		return "getURL("+url+")";
	}
	
	public String getRFFile(String file){
		return "readFromFile("+file+")";
	}
	
	@Test
	public void initializeLocalVarsToNull() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog1";
		String input = progname + " { " 
				+ "frame f image i frame f2 image i2 }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
//		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[0];
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
//		assertEquals("", PLPRuntimeLog.getString());
	}

	@Test
	public void initializeCmdLineArgs() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog2";
		String input = progname + " url u, file f{ " 
				+ "}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
//		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image.png","file.jpg"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		assertEquals(getCLURL("http://127.0.0.1/image.png"), PLPRuntimeLog.getString());
	}

	@Test
	public void testSleep() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog3";
		String input = progname + " { "
				+ "integer a "
				+ "integer b "
				+ "a<-5;"
				+ "b<-6;"
				+ "sleep a; "
				+ "sleep a+b; "
				+ "sleep 5; " 
				+ "}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
//		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[0];
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		assertEquals("56", PLPRuntimeLog.getString());
	}

	@Test
	public void testFrame1() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		initLog();
		String progname = "prog4";
		String input = progname + " file f,url u,file f2{ "
				+ "image a "
				+ "image b "
				+ "f->a; "
				+ "u->b; "
				+ "a<-b; "
				+ "frame myfrm "
				+ "a->myfrm; "
				+ "a->f2; "
				+ "f->a->myfrm; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
//		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"image1.png","http://127.0.0.1/image1.png","image2.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		//printLog();
		assertEquals(getCLURL("http://127.0.0.1/image1.png")+getRFFile("image1.png")
				+ getRFURL("http://127.0.0.1/image1.png")
				+ copyImage 
				+ cosFrame
				+ writeFile("image2.png")
				+ getRFFile("image1.png")
				+ setImage
				, PLPRuntimeLog.getString());
	}
	
	private String writeFile(String string) {
		return "write("+string+")";
	}

	@Test
	public void testFrame2() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog5";
		String input = progname + " file f{ "
				+ "image a "
				+ "frame myfrm "
				+ "f->a->myfrm; "
				+ "myfrm->show; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
//		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		assertEquals( getRFFile("image1.png")
				+ cosFrame 
				+ showImage
				, PLPRuntimeLog.getString());
	}
	
	@Test
	public void testFrameFromFile() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog6";
		String input = progname + " file f{ "
				+ "image a "
				+ "frame myfrm "
				+ "f->a; "
				+ "a->myfrm; "
				+ "myfrm->show; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
//		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
//		Thread.sleep(5000);
		assertEquals(getRFFile("image1.png") + cosFrame + showImage, PLPRuntimeLog.getString());
	}
	@Test
	public void testFrameFromURL() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog6";
		String input = progname + " url u{ "
				+ "image a "
				+ "frame myfrm "
				+ "u->a; "
				+ "a->myfrm; "
				+ "myfrm->show;"
				+ "integer x "
				+ "myfrm->xloc->x;"
				+ "integer y "
				+ "y<-x;"
				+ "myfrm->yloc->x;"
				+ "y<-x;"
				+ "sleep 2000;"
				+ "myfrm->move(10,10);"
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
//		Thread.sleep(5000);
		assertEquals(getCLURL("http://127.0.0.1/image1.png")+
				getRFURL("http://127.0.0.1/image1.png")
				+ cosFrame
				+ showImage
				+ getX + "587"
				+ getY + "269"
				+ moveFrame, PLPRuntimeLog.getString())	
		;
	}
	@Test
	//TODO: scale will not work unless stored
	public void testScale() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog7";
		String input = progname + " url u{ "
				+ "image a "
				+ "image b "
				+ "u->a; "
				+ "a->scale(2)->b;"
				+ "frame myfrm "
				+ "b->myfrm; "
				+ "sleep 1000; "
				+ "myfrm->show;"
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
//		Thread.sleep(2000);
		assertEquals( getCLURL("http://127.0.0.1/image1.png")+getRFURL("http://127.0.0.1/image1.png")
				+ scale 
				+ cosFrame 
				+ showImage 
				, PLPRuntimeLog.getString());
	}

	@Test
	public void testHeightWidth() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog7";
		String input = progname + " url u{ "
				+ "image a "
				+ "u->a; "
				+ "integer x "
				+ "integer y "
				+ "a->width->x;"
				+ "y<-x;"
				+ "a->height->x;"
				+ "y<-x;"
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	//	Thread.sleep(5000);
		assertEquals(getCLURL("http://127.0.0.1/image1.png")+ getRFURL("http://127.0.0.1/image1.png") + "225225", PLPRuntimeLog.getString());
	}

	@Test
	public void testBlur() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog9";
		String input = progname + " url u{ "
				+ "image a "
				+ "image b "
				+ "u->a; "
				+ "a->blur->b; "
				+ "frame f "
				+ "b->f;"
				+ "f->show; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		//Thread.sleep(5000);
		assertEquals( getCLURL("http://127.0.0.1/image1.png")+getRFURL("http://127.0.0.1/image1.png")
				+ blur
				+ cosFrame
				+ showImage
				, PLPRuntimeLog.getString());
	}

	@Test
	public void testGray() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog9";
		String input = progname + " url u{ "
				+ "image a "
				+ "image b "
				+ "u->a; "
				+ "a->gray->b; "
				+ "frame f "
				+ "b->f;"
				+ "f->show; "
				+ "sleep 1000; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
//		Thread.sleep(1000);;
		assertEquals(getCLURL("http://127.0.0.1/image1.png")+ getRFURL("http://127.0.0.1/image1.png")
				+ gray 
				+ cosFrame
				+ showImage
				, PLPRuntimeLog.getString());
	}
	
	@Test
	public void testConvolve() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog9";
		String input = progname + " url u{ "
				+ "image a "
				+ "image b "
				+ "u->a; "
				+ "a->convolve->b; "
				+ "frame f "
				+ "b->f;"
				+ "f->show; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image1.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		Thread.sleep(2000);
		assertEquals(getCLURL("http://127.0.0.1/image1.png")+ getRFURL("http://127.0.0.1/image1.png")
				+ convolve 
				+ cosFrame
				+ showImage
				, PLPRuntimeLog.getString());
	}

	
	@Test
	public void testImageOps1() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog9";
		String input = progname + " file f1, file f2{ "
				+ "image a "
				+ "image b "
				+ "f1->a; "
				+ "f2->b; "
				+ "image c "
				+ "c<-a+b;"
				+ "c<-a-b;"
				+ "c<-a*2;"
				+ "c<-a/2;"
				+ "c<-a%2;"
				+ "frame f "
				+ "c->f;"
				+ "f->show; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"image1.png","image2.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		//Thread.sleep(2000);
		assertEquals( getRFFile("image1.png")
				+ getRFFile("image2.png")
				+ iadd + copyImage
				+ isub + copyImage
				+ imul + copyImage
				+ idiv + copyImage
				+ imod + copyImage
				+ cosFrame
				+ showImage
				, PLPRuntimeLog.getString());
	}

	
	@Test
	public void testImageOps2() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog9";
		String input = progname + " file f1, file f2, file f3{ "
				+ "image a "
				+ "image b "
				+ "f1->a; "
				+ "f2->b; "
				+ "image c "
				+ "c<-a%2;"
				+ "c->f3;"
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"image1.png","image2.png","writtenimage.png"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		//Thread.sleep(2000);
		assertEquals(getRFFile("image1.png")
				+ getRFFile("image2.png")
				+ imod + copyImage
				+ writeFile("writtenimage.png")
				, PLPRuntimeLog.getString());
	}
	
	@Test
	public void testMissingCases() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog9";
		String input = progname + " file f1, integer z{ "
				+ "image b "
				+ "image a "
				+ "f1->a; "
				+ "a->b; "
				+ "frame frm "
				+ "b->frm;"
				+ "frm->show;"
				+ "frm->hide;"
				+ "frm->xloc->z;"
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"image1.png","5"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		//Thread.sleep(2000);
		assertEquals(getRFFile("image1.png")
				+ cosFrame
				+ showImage
				+ hideImage 
				+ getX
				, PLPRuntimeLog.getString());
	}	
	
	@Test
	public void testScreenWdithHeight() throws Exception {
		// scan, parse, and type check the program
		devel = false;
		grade = true;
		String progname = "prog9";
		String input = progname + "{ "
				+ "integer i1 "
				+ "integer i2 "
				+ "i1<-screenwidth;"
				+ "i2<-screenheight; "
				+ "} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);
		//CodeGenUtils.dumpBytecode(bytecode);
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		String[] args = new String[]{"http://127.0.0.1/image1.png","5"};
		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
		//Thread.sleep(2000);
		assertEquals(getScreenWidth+"1366"+getScreenHeight+"768", PLPRuntimeLog.getString());
	}	

	
}
