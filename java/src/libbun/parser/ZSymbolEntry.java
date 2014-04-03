package libbun.parser;

import libbun.parser.ast.BNode;
import libbun.util.Field;

public class ZSymbolEntry {
	@Field public ZSymbolEntry Parent;
	@Field public BNode Node;
	@Field public boolean IsDisabled = false;
	
	public ZSymbolEntry(ZSymbolEntry Parent, BNode Node) {
		this.Parent = Parent;
		this.Node = Node;
	}
}
