package libbun.parser.ssa;

import libbun.parser.ast.ZNode;

class Variable {
	public ZNode  Node;
	public String Name;
	public int    Index;
	public Variable(String Name, int Index, ZNode Node) {
		this.Node = Node;
		this.Index = Index;
		this.Name = Name;
	}
	@Override
	public String toString() {
		return "Variable { Name : " + this.Name + ", Index : " + this.Index + "}";
	}
}