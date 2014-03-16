package libbun.parser;

import libbun.parser.ast.ZNode;
import libbun.util.Field;

public class ZSymbolEntry {
	@Field public ZSymbolEntry Parent;
	@Field public ZNode Node;
	@Field public boolean IsDisabled = false;
	
	public ZSymbolEntry(ZSymbolEntry Parent, ZNode Node) {
		this.Parent = Parent;
		this.Node = Node;
	}
}
