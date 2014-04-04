package libbun.parser.ssa;

import libbun.ast.BNode;

class Variable {
	public BNode  Node;
	public String Name;
	public int    Index;
	public Variable(String Name, int Index, BNode Node) {
		this.Node = Node;
		this.Index = Index;
		this.Name = Name;
	}
	@Override
	public String toString() {
		return "Variable { Name : " + this.Name + ", Index : " + this.Index + "}";
	}
}