package libbun.util;

import libbun.parser.ZSourceContext;

public abstract class BTokenFunction extends BFunction {
	public BTokenFunction(int TypeId, String Name) {
		super(TypeId, Name);
	}
	protected BTokenFunction() {
		super(0,null);
	}
	public abstract boolean Invoke(ZSourceContext SourceContext);
}
