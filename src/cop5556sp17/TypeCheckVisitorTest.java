/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */
 
package cop5556sp17;
 
import static org.junit.Assert.*;
 
import java.util.ArrayList;
 
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
 
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Kind;
 
public class TypeCheckVisitorTest {
     
 
    @Rule
    public ExpectedException thrown = ExpectedException.none();
 
    @Test
    public void testAssignmentBoolLit0() throws Exception{
        String input = "p {\nboolean y \ny <- false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);         
    }
 
    @Test
    public void testAssignmentBoolLitError0() throws Exception{
        String input = "p {\nboolean y \ny <- 3;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);         
    }         
 
    @Test
    public void testAssignmentBoolLit1() throws Exception{
        String input = "p {\ninteger y \ny <- 2+5+false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);     
    }
 
    @Test
    public void testScope() throws Exception{
        String input = "p {\n integer y \n while(true){boolean y } \n integer y \n y <- 2+5;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);     
    }
 
    @Test
    public void testScope2() throws Exception{
        String input = "p integer y {\n integer y \n y <- 23*7/4;\n}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);     
    }
     
    @Test
    public void testWhile() throws Exception{
        String input = "p {\n integer y \n while(1){integer y } \n y <- 2+5;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);     
    }
     
    @Test
    public void testAssign() throws Exception{
        String input = "p integer y, boolean x, file f, url u{\n image i frame z y <- 2+5;\n x <- false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);     
    }
     
    @Test
    public void testAssign2() throws Exception{
        String input = "p integer y, boolean x {\n y <- 2+5;\n x <- false;\n z <- 20;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);     
    }
     
    @Test
    public void testAssign3() throws Exception{
        String input = "p integer y, boolean x {\n y <- 2+5;\n x <- 2+6+8;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);     
    }
     
    @Test
    public void testAssign4() throws Exception{
        String input = "p {\n frame f1 integer f2\n f1<-f2;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);     
    }
 
    @Test
    public void testAssign5() throws Exception{
        String input = "p {\n frame f1 frame f2\n f1<-f2;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);     
    }
 
    @Test
    public void testAssign7() throws Exception{
        String input = "p {\n integer y \n boolean y}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }
     
    @Test
    public void testStatementSleep() throws Exception{
        String input = "p boolean y{\n sleep (y);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }
     
    @Test
    public void testStatement2() throws Exception{
        String input = "p boolean y{\n"
                        + " while (y) {\n"
                        + "integer y \n"
                        + "y <- y+3;}\n"
                        + "y<-false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }
     
    @Test
    public void testIfStatement() throws Exception{
        String input = "p boolean y{\n"
                        + "y <- true;\n"
                        + "if (y == true) {\n"
                        + "integer y \n"
                        + "boolean x \n"
                        + "y <- y+3;\n"
                        + "x <- true < x <= true > true >= false;\n"
                        + "}\n"
                        + "y<-false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }
     
    @Test
    public void testExpression() throws Exception{
        String input = "p boolean y{\n"
                        + "integer x \n"
                        + "image i\n"
                        + "image i2\n"
                        + "x <- x+3;\n"
                        + "i <- i + i2 - i;\n"
                        + "i2 <- i2 * 4* x;\n"
                        + "y <- 23 < x;\n"
                        + "y <- 2 >= 34;\n"
                        + "y <- x <= 34;\n"
                        + "y <- 38 > x;\n"
                        + "}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }
     
    @Test
    public void testExpression2() throws Exception{
        String input = "p boolean y{\n"
                        + "integer x \n"
                        + "image i\n"
                        + "image i2\n"
                        + "x <- x+3;\n"
                        + "i <- i + i2 - i;\n"
                        + "x <- i2 * 4* x;\n"
                        + "y <- 23 < x;\n"
                        + "y <- 2 >= 34;\n"
                        + "y <- x <= 34;\n"
                        + "y <- 38 > x;\n"
                        + "}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.program();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }
     
    @Test
    public void testChain() throws Exception{
        String input = "p integer x, integer y \n{\n frame f1 frame f2\n f2->move(x*y,y);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);     
    }
     
    @Test
    public void testIdentChain() throws Exception{
        String input = "p integer x, url u, file fl{\n"
                + "frame f1 frame f2 integer y image i\n"
                + "u -> i;\n"
                + "fl -> i;\n"
                + "f2 -> xloc;\n"
                + "f1 -> move(x, x);\n"
                + "i -> height;\n"
                + "i -> width;\n"
                + "i -> f1;\n"
                + "i -> fl;\n"
                + "i -> blur;\n"
                + "i |-> gray;\n"
                + "i -> scale(10);\n"
                + "f1 -> yloc;\n"
                + "u -> i |-> convolve -> f2 -> xloc;\n"
                + "f2->move(x*y,y);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        ArrayList<Statement> slist = ((Program)program).getB().getStatements();
        assertEquals(TypeName.IMAGE, ((BinaryChain)slist.get(0)).getTypeName());
        assertEquals(TypeName.IMAGE, ((BinaryChain)slist.get(1)).getTypeName());
        assertEquals(TypeName.INTEGER, ((BinaryChain)slist.get(2)).getTypeName());
        assertEquals(TypeName.FRAME, ((BinaryChain)slist.get(3)).getTypeName());
        assertEquals(TypeName.INTEGER, ((BinaryChain)slist.get(4)).getTypeName());
        assertEquals(TypeName.INTEGER, ((BinaryChain)slist.get(5)).getTypeName());
        assertEquals(TypeName.FRAME, ((BinaryChain)slist.get(6)).getTypeName());
        assertEquals(TypeName.NONE, ((BinaryChain)slist.get(7)).getTypeName());
        assertEquals(TypeName.IMAGE, ((BinaryChain)slist.get(8)).getTypeName());
        assertEquals(TypeName.IMAGE, ((BinaryChain)slist.get(9)).getTypeName());
        assertEquals(TypeName.IMAGE, ((BinaryChain)slist.get(10)).getTypeName());
        assertEquals(TypeName.INTEGER, ((BinaryChain)slist.get(11)).getTypeName());
        assertEquals(TypeName.INTEGER, ((BinaryChain)slist.get(12)).getTypeName());
        assertEquals(TypeName.FRAME, ((BinaryChain)slist.get(13)).getTypeName());
    }
     
    @Test
    public void testChain2() throws Exception{
        String input = "p integer x, url u, file fl{\n"
                + "frame f1 frame f2 integer y image i\n"
                + "u -> i;\n"
                + "fl -> i;\n"
                + "f2 |-> xloc;\n" //wrong here
                + "f1 -> move(x, x);\n"
                + "i -> height;\n"
                + "i -> width;\n"
                + "i -> f1;\n"
                + "i -> fl;\n"
                + "i -> blur;\n"
                + "i |-> gray;\n"
                + "i -> scale(10);\n"
                + "i -> x;\n"
                + "f1 -> yloc;\n"
                + "u -> i |-> convolve -> f2 -> xloc;\n"
                + "f2->move(x*y,y);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }
    
    @Test
    public void testChain4() throws Exception {
        String input = "p {\n"
        		+ "integer x\n"
        		+ "image i\n"
        		+ "}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }
    
    @Test
    public void testScale() throws Exception {
        String input = "p {\n"
        		+ "image i\n"
        		+ "i->scale(2,3);\n"
        		+ "}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }
    
    @Test
    public void testStatement() throws Exception {
        String input = "p {\n"
        		+ "image i\n"
        		+ "image i2\n"
        		+ "i <- i*2*i2;\n"
        		+ "}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }
    
    @Test
    public void testStatement3() throws Exception {
        String input = "p {\n"
        		+ "boolean b\n"
        		+ "boolean b2\n"
        		+ "integer x\n"
        		+ "b <- x != 2 >= true < b2;\n"
        		+ "}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }
    
    @Test
    public void testStatement4() throws Exception {
        String input = "p {\n"
        		+ "image i1\n"
        		+ "image i2\n"
        		+ "integer x\n"
        		+ "integer y\n"
        		+ "x <- i1 + i2 * y;\n"
        		+ "}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }
    
    /* ************* Last time failed tests ***************** */
    @Test
    public void testImageOpStatement() throws Exception {
        String input = "tos url u,\n integer x\n{integer y image i u -> i; i-> height; frame f i -> scale (x) -> f;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        ArrayList<Statement> slist = ((Program)program).getB().getStatements();
        assertEquals(TypeName.IMAGE, ((BinaryChain)slist.get(0)).getTypeName());
        assertEquals(TypeName.INTEGER	, ((BinaryChain)slist.get(1)).getTypeName());
        assertEquals(TypeName.FRAME, ((BinaryChain)slist.get(2)).getTypeName());
    }
     
    @Test
    public void testProg() throws Exception {
         
        String input = "p url u1 {}";
        Scanner scanner = new Scanner (input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        assertEquals(Kind.KW_URL, ((ArrayList<ParamDec>)((Program)program).getParams()).get(0).getFirstToken().kind);
    }
     
    @Test
    public void testProg2() throws Exception {
         
        String input = "tos integer x\n{image i frame f i -> scale (x) -> f;}";
        Scanner scanner = new Scanner (input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        assertEquals(Kind.KW_INTEGER, ((ArrayList<ParamDec>)((Program)program).getParams()).get(0).getFirstToken().kind);
        ArrayList<Statement> slist = ((Program)program).getB().getStatements();
        assertEquals(TypeName.FRAME, ((BinaryChain)slist.get(0)).getTypeName());
    }
     
    @Test
    public void testProg3() throws Exception {
         
        String input = "p url u1, url u2, file f1, file f2, integer i {}";
        Scanner scanner = new Scanner (input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        assertEquals(Kind.KW_URL, ((ArrayList<ParamDec>)((Program)program).getParams()).get(0).getFirstToken().kind);
        assertEquals(Kind.KW_URL, ((ArrayList<ParamDec>)((Program)program).getParams()).get(1).getFirstToken().kind);
        assertEquals(Kind.KW_FILE, ((ArrayList<ParamDec>)((Program)program).getParams()).get(2).getFirstToken().kind);
        assertEquals(Kind.KW_FILE, ((ArrayList<ParamDec>)((Program)program).getParams()).get(3).getFirstToken().kind);
        assertEquals(Kind.KW_INTEGER, ((ArrayList<ParamDec>)((Program)program).getParams()).get(4).getFirstToken().kind);
    }
     
    @Test
    public void testAssignRan() throws Exception{
        String input = "p integer y, boolean x {\n y <- 2+5;\n x <- false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        assertEquals(Kind.KW_INTEGER, ((ArrayList<ParamDec>)((Program)program).getParams()).get(0).getFirstToken().kind);
        assertEquals(Kind.KW_BOOLEAN, ((ArrayList<ParamDec>)((Program)program).getParams()).get(1).getFirstToken().kind);
        ArrayList<Statement> slist = ((Program)program).getB().getStatements();
        assertEquals(TypeName.INTEGER, ((BinaryExpression)((AssignmentStatement)slist.get(0)).getE()).getType());
        assertEquals(TypeName.BOOLEAN, ((BooleanLitExpression)((AssignmentStatement)slist.get(1)).getE()).getType());
    }
 
} 