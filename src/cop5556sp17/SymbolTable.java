package cop5556sp17;



import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


import cop5556sp17.AST.Dec;


public class SymbolTable {
	
	
	//TODO  add fields
	int currentScope, nextScope;
	LinkedList<Integer> scopeStack;
	HashMap<String, LinkedList<DecScope>> table;	

	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		currentScope = nextScope++;
		scopeStack.addFirst(currentScope);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		scopeStack.removeFirst();
		if (! scopeStack.isEmpty())
			currentScope = scopeStack.getFirst();

	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		/* If the table already has the identifier stored in it
		 * then add the declaration in the list
		 * else add a new entry
		 */
		if (table.containsKey(ident)){
			LinkedList<DecScope> identAttributes = table.get(ident);
			
			// If variable has already been defined in the scope return false
			for (DecScope decCheck: identAttributes)
				if (decCheck.getScopeNum() == currentScope)
					return false;
			
			//Add the declaration to the beginning of the list
			identAttributes.addFirst(new DecScope(currentScope, dec));
		}
		else {
			LinkedList<DecScope> identAttributes = new LinkedList<>();
			identAttributes.add(new DecScope(currentScope, dec));
			table.put(ident, identAttributes);
		}
		
		return true;
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		
		/* If identifier is present in table get the ident dec which is closer to scope stack
		 * else return null
		 */
		if (table.containsKey(ident)){
			DecScope recentDec = table.get(ident).getFirst();
			int recentDecLevel = getStackPos(recentDec.getScopeNum());

			for (DecScope dec : table.get(ident)){
				int decLevel = getStackPos(dec.getScopeNum());
				if (recentDecLevel > decLevel){
					recentDec = dec;
					recentDecLevel = decLevel;
				}
			}
			
			/* If scope no of identifier has been popped out of scope stack return null
			 * else return dec
			 */
			return (recentDecLevel != Integer.MAX_VALUE)? recentDec.getDec() : null;
		}
		else
			return null;
	}
		
	private int getStackPos(int scopeNum) {
		// TODO Auto-generated method stub
		int count = 0;
		boolean scopeFound = false;
		Iterator<Integer> it = scopeStack.iterator();
		
		while (it.hasNext()){
			if (scopeNum == it.next()){
				scopeFound = true;
				break;
			}
			else
				count++;
		}
		
		/* If scope is present in stack
		 * then return stack index count
		 * else return -1 for error checking
		 */
		return scopeFound? count:Integer.MAX_VALUE;
	}


	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		nextScope = 0;
		currentScope = nextScope++;
		scopeStack = new LinkedList<Integer>();
		table = new HashMap<>();
		scopeStack.push(currentScope);
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}
	
	
	private class DecScope {
		int scopeNum;
		Dec dec;

		public DecScope(int scopeNum, Dec dec) {
			super();
			this.scopeNum = scopeNum;
			this.dec = dec;
		}

		public int getScopeNum() {
			return scopeNum;
		}

		public Dec getDec() {
			return dec;
		}

	}

}
