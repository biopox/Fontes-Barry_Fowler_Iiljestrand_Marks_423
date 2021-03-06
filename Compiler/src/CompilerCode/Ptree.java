package CompilerCode;

import java.util.List;
import java.util.ArrayList;

import CompilerCode.Token.type_enum;

/**
 * Parse Tree Class
 * Keeps track of the hierarchy order of the parsing
 * Idea described by Isaiah and Jacob
 * @author Ben
 *
 */
public class Ptree {
	protected Token token;
	protected List<Ptree> children;
	
	/**
	 * Constructs Ptree from a token. Used to add a token to the tree
	 * @param tok token being added to the Ptree node
	 */
	Ptree(Token tok) {
		token = tok;
		children = new ArrayList<Ptree>();
	}
	
	/**
	 * Constructs a new Ptree node, used for intermediary tree nodes
	 * @param e type the Ptree is intended to be
	 */
	Ptree(type_enum e) {
		token = new Token(e);
		children = new ArrayList<Ptree>();
	}
	
	/**
	 * Prints the tree using the the recursive PT function
	 */
	public void printTree() {
		System.out.println("\nParse Tree:");
		PT(0); //assumes the calling Ptree is root
		System.out.println();
	}
	
	/**
	 * Prints the leaf value
	 * Recursively calls self on all children
	 * @param i the depth of the current child
	 */
	private void PT(int i) {
		System.out.println();
		for(int j = 0; j < i; j++){
			System.out.print("  ");
		}
		System.out.print(this.token.type);
		if (this.token.token != null) {
			System.out.print(" | " + this.token.token);
		}
		for(Ptree child : children){
			child.PT(i+1);
		}
	}

	
	/**
	 * Checks that the children are not null
	 * @return true if no children are null
	 */
	public boolean verifyChildren() {
		if (this.children.contains(null)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Adds a single tree to the child list
	 * @param treein a single child Ptree to add to this class' list
	 */
	public void addChild(Ptree treein) {
		//System.out.println("Added Child");
		children.add(treein);
	}
	
	
	/**
	 * kills the first born (removes the first child in the list)
	 * does nothing if there are no children nodes
	 */
	public void removeChildren() {
		while(!this.children.isEmpty()) {
			this.children.remove(0);
		}
	}
	
	
	/**
	 * Removes the last child added to the list
	 */
	public void removeChild() {
		if(!(this.children.size() == 0)) {
			children.remove(children.size() - 1);
		}
	}
	
	
	/**
	 * Checks if all goto calls have a matching goto jump location
	 * @param tree root of the parse tree
	 * @return true if all goto statements match up
	 */
	public void parseErrorChecker() {
		breakChecker(false);
		List<Ptree> gotoJumpPlace = new ArrayList<Ptree>();
		findTrees(gotoJumpPlace, type_enum.gotoJumpPlace);
		
		List<Ptree> gotoCalls = new ArrayList<Ptree>();
		findTrees(gotoCalls, type_enum.gotoStatement);
		
		boolean b;
		for(Ptree tcall : gotoCalls) {
			b = false;
			for(Ptree tjump : gotoJumpPlace) {
				if(tcall.children.get(1).token.token.equals(tjump.children.get(0).token.token)) {
					b = true;
				}
			}
			if(!b) {
				ErrorHandler.addError("No goto jump place for goto call " + tcall.children.get(1).token.token + " at line " + tcall.children.get(1).token.lineNumber);
				return;
			}
		}
		
		for(Ptree tjump : gotoJumpPlace) {
			b = false;
			for(Ptree tcall : gotoCalls) {
				if(tcall.children.get(1).token.token.equals(tjump.children.get(0).token.token)) {
					b = true;
				}
			}
			if(!b) {
				ErrorHandler.addError("Unused goto jump place: " + tjump.children.get(0).token.token + " at line " + tjump.children.get(0).token.lineNumber);
				return;
			}
		}
	}

	
	/**
	 * Checks that all break statements in the program are inside loops
	 * @param tree parse tree location
	 * @param inLoop whether the current node is in 
	 */
	private void breakChecker(boolean inLoop) {
		if(token.type == type_enum.k_break && !inLoop) {
			ErrorHandler.addError("break statement at " + token.lineNumber + " is not in a loop");
		}
		
		if(token.type == type_enum.whileStatement || token.type == type_enum.forStatement) {
			inLoop = true;
		}
		
		for(Ptree t : children) {
			t.breakChecker(inLoop);
		}
	}
	
	/**
	 * finds all of one type of Ptree recursively
	 * @param token to be found
	 * @param trees must be initialized to a new array list before call
	 * @return List of Ptrees containing specified token
	 */
	public void findTrees(List<Ptree> trees, type_enum type) {
		if(this.token.type == type) {
			trees.add(this);
		}
		
		for(Ptree t: this.children) {
			t.findTrees(trees, type);
		}
	}
	
	/**
	 * Returns the first tree of the specified type it finds
	 * @param tree node being evaluated
	 * @param type the type being looked for
	 * @return tree if found, null otherwise
	 */
	public Ptree findTree(type_enum type) {
		if(this.token.type == type) {
			return this;
		}
		
		for(Ptree t: this.children) {
			if(t.findTree(type) != null) {
				return t.findTree(type);
			}
		}
		
		return null;
	}
}
