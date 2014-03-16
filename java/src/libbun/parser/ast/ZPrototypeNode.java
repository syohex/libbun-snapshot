package libbun.parser.ast;

import libbun.parser.ZNameSpace;
import libbun.type.ZFuncType;
import libbun.util.Field;
import libbun.util.Var;

public class ZPrototypeNode extends ZTopLevelNode {
	public final static int _FuncInfo = 0;
	@Field ZFunctionNode FunctionNode;
	public ZPrototypeNode(ZFunctionNode FunctionNode) {
		super(FunctionNode.ParentNode, FunctionNode.SourceToken, 1);
		this.SetNode(ZPrototypeNode._FuncInfo, FunctionNode);
		this.FunctionNode = FunctionNode;
	}

	public final ZParamNode GetParamNode(int Index) {
		return this.FunctionNode.GetParamNode(Index);
	}

	@Override public final void Perform(ZNameSpace NameSpace) {
		@Var ZFuncType FuncType = this.FunctionNode.GetFuncType();
		NameSpace.Generator.SetPrototype(this.FunctionNode, this.FunctionNode.FuncName(), FuncType);

	}

}