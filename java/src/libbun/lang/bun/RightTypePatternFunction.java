package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZTypeNode;
import libbun.type.ZGenericType;
import libbun.type.ZType;
import libbun.type.ZTypePool;
import libbun.util.Var;
import libbun.util.ZArray;
import libbun.util.ZMatchFunction;

public class RightTypePatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftTypeNode) {
		@Var ZToken SourceToken = TokenContext.GetToken();
		if(LeftTypeNode.Type.GetParamSize() > 0) {
			if(TokenContext.MatchToken("<")) {  // Generics
				@Var ZArray<ZType> TypeList = new ZArray<ZType>(new ZType[4]);
				while(!TokenContext.StartsWithToken(">")) {
					if(TypeList.size() > 0 && !TokenContext.MatchToken(",")) {
						return null;
					}
					@Var ZTypeNode ParamTypeNode = (ZTypeNode) TokenContext.ParsePattern(ParentNode, "$OpenType$", ZTokenContext._Optional);
					if(ParamTypeNode == null) {
						return LeftTypeNode;
					}
					TypeList.add(ParamTypeNode.Type);
				}
				LeftTypeNode = new ZTypeNode(ParentNode, SourceToken, ZTypePool._GetGenericType(LeftTypeNode.Type, TypeList, true));
			}
		}
		while(TokenContext.MatchToken("[")) {  // Array
			if(!TokenContext.MatchToken("]")) {
				return null;
			}
			LeftTypeNode = new ZTypeNode(ParentNode, SourceToken, ZTypePool._GetGenericType1(ZGenericType._ArrayType, LeftTypeNode.Type));
		}
		return LeftTypeNode;
	}

}
