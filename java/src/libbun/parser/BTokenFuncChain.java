package libbun.parser;

import libbun.util.BField;
import libbun.util.BTokenFunction;

public final class BTokenFuncChain {
	@BField public BTokenFunction   Func;
	@BField public BTokenFuncChain	ParentFunc;

	BTokenFuncChain(BTokenFunction Func, BTokenFuncChain Parent) {
		this.Func = Func;
		this.ParentFunc = Parent;
	}

}