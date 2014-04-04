package libbun.parser;

import libbun.util.BField;
import libbun.util.BTokenFunction;

public final class ZTokenFunc {
	@BField public BTokenFunction      Func;
	@BField public ZTokenFunc	ParentFunc;

	ZTokenFunc(BTokenFunction Func, ZTokenFunc Parent) {
		this.Func = Func;
		this.ParentFunc = Parent;
	}

}