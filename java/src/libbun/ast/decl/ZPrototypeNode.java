package libbun.ast.decl;

import libbun.parser.BNameSpace;
import libbun.type.BFuncType;
import libbun.util.BField;
import libbun.util.Var;

public class ZPrototypeNode extends ZTopLevelNode {
	public final static int _FuncInfo = 0;
	@BField BFunctionNode FunctionNode;
	public ZPrototypeNode(BFunctionNode FunctionNode) {
		super(FunctionNode.ParentNode, FunctionNode.SourceToken, 1);
		this.SetNode(ZPrototypeNode._FuncInfo, FunctionNode);
		this.FunctionNode = FunctionNode;
	}

	//	public final ZLetVarNode GetParamNode(int Index) {
	//		return this.FunctionNode.GetParamNode(Index);
	//	}

	@Override public final void Perform(BNameSpace NameSpace) {
		@Var BFuncType FuncType = this.FunctionNode.GetFuncType();
		NameSpace.Generator.SetPrototype(this.FunctionNode, this.FunctionNode.FuncName(), FuncType);

	}

}