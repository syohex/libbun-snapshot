package libbun.ast.decl;

import libbun.ast.BNode;
import libbun.parser.LibBunGamma;
import libbun.type.BFuncType;
import libbun.util.BField;
import libbun.util.Var;

public class BunPrototypeNode extends TopLevelNode {
	public final static int _FuncInfo = 0;
	@BField BunFunctionNode FunctionNode;
	public BunPrototypeNode(BunFunctionNode FunctionNode) {
		super(FunctionNode.ParentNode, 1);
		this.SetNode(BunPrototypeNode._FuncInfo, FunctionNode);
		this.FunctionNode = FunctionNode;
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return null; // FIXME
	}

	@Override public final void Perform(LibBunGamma Gamma) {
		@Var BFuncType FuncType = this.FunctionNode.GetFuncType();
		Gamma.Generator.SetPrototype(this.FunctionNode, this.FunctionNode.FuncName(), FuncType);

	}

}