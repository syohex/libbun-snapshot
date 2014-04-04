package libbun.parser.ast;

import libbun.parser.ZToken;
import libbun.type.ZFunc;
import libbun.type.ZFuncType;
import libbun.type.ZType;
import libbun.util.BField;

public class ZFuncNameNode extends ZLocalDefinedNode {
	@BField public final String FuncName;
	@BField public final ZType RecvType;
	@BField public final int FuncParamSize;

	public ZFuncNameNode(BNode ParentNode, ZToken SourceToken, String FuncName, ZFuncType FuncType) {
		super(ParentNode, SourceToken, 0);
		this.FuncName = FuncName;
		this.RecvType = FuncType.GetRecvType();
		this.FuncParamSize = FuncType.GetFuncParamSize();
		this.Type = FuncType;
	}

	public ZFuncNameNode(BNode ParentNode, ZToken SourceToken, String FuncName, ZType RecvType, int FuncParamSize) {
		super(ParentNode, SourceToken, 0);
		this.FuncName = FuncName;
		this.RecvType = RecvType;
		this.FuncParamSize = FuncParamSize;
	}

	public final String GetSignature() {
		return ZFunc._StringfySignature(this.FuncName, this.FuncParamSize, this.RecvType);
	}
}
