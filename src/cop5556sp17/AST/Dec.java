package cop5556sp17.AST;

import org.objectweb.asm.Label;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public class Dec extends ASTNode {
	
	final Token ident;
	private TypeName typename;
	private int SlotNumber;
	private Label start;
	private Label end;
	
	public Label getStart() {
		return start;
	}

	public void setStart(Label start) {
		this.start = start;
	}

	public Label getEnd() {
		return end;
	}

	public void setEnd(Label end) {
		this.end = end;
	}

	public int getSlotNumber() {
		return SlotNumber;
	}

	public void setSlotNumber(int slotNumber) {
		SlotNumber = slotNumber;
	}

	public TypeName getTypename() {
		return typename;
	}

	public void setTypename(TypeName typename) {
		this.typename = typename;
	}

	public Dec(Token firstToken, Token ident) {
		super(firstToken);

		this.ident = ident;
	}

	public Token getType() {
		return firstToken;
	}

	public Token getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}

}
