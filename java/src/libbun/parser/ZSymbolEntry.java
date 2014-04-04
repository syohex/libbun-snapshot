package libbun.parser;

import libbun.parser.ast.BNode;
import libbun.util.BField;

public class ZSymbolEntry {
	@BField public ZSymbolEntry Parent;
	@BField public BNode Node;
	@BField public boolean IsDisabled = false;
	
	public ZSymbolEntry(ZSymbolEntry Parent, BNode Node) {
		this.Parent = Parent;
		this.Node = Node;
	}
}
