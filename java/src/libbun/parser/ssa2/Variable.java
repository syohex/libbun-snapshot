package libbun.parser.ssa2;

import libbun.parser.ast.ZNode;

/**
 * @see
 * Brandis, M. M. and Moessenboeck, H.: Single-pass Generation of Static
 * Single-assignment Form for Structured Languages, ACM Trans.
 * Program. Lang. Syst., Vol. 16, No. 6, pp. 1684-1698
 *
 */

class Variable {
	ZNode  Node;
	String Name;
	int    Index;
	public Variable(String Name, ZNode Node) {
		this.Node = Node;
		this.Name = Name;
	}
}