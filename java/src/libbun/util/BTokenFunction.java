package libbun.util;

import libbun.parser.BSourceContext;

public abstract class BTokenFunction extends BFunction {
	public BTokenFunction(int TypeId, String Name) {
		super(TypeId, Name);
	}
	protected BTokenFunction() {
		super(0,null);
	}
	public abstract boolean Invoke(BSourceContext SourceContext);
}
