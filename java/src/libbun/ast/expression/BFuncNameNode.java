package libbun.ast.expression;

import libbun.ast.BNode;
import libbun.ast.ZLocalDefinedNode;
import libbun.parser.BToken;
import libbun.type.BFunc;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BField;

public class BFuncNameNode extends ZLocalDefinedNode {
	@BField public final String FuncName;
	@BField public final BType RecvType;
	@BField public final int FuncParamSize;

	public BFuncNameNode(BNode ParentNode, BToken SourceToken, String FuncName, BFuncType FuncType) {
		super(ParentNode, SourceToken, 0);
		this.FuncName = FuncName;
		this.RecvType = FuncType.GetRecvType();
		this.FuncParamSize = FuncType.GetFuncParamSize();
		this.Type = FuncType;
	}

	public BFuncNameNode(BNode ParentNode, BToken SourceToken, String FuncName, BType RecvType, int FuncParamSize) {
		super(ParentNode, SourceToken, 0);
		this.FuncName = FuncName;
		this.RecvType = RecvType;
		this.FuncParamSize = FuncParamSize;
	}

	public final String GetSignature() {
		return BFunc._StringfySignature(this.FuncName, this.FuncParamSize, this.RecvType);
	}
}
