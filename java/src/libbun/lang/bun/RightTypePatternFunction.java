package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.BTypeNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.type.BGenericType;
import libbun.type.BType;
import libbun.type.BTypePool;
import libbun.util.Var;
import libbun.util.BArray;
import libbun.util.BMatchFunction;

public class RightTypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftTypeNode) {
		@Var BToken SourceToken = TokenContext.GetToken();
		if(LeftTypeNode.Type.GetParamSize() > 0) {
			if(TokenContext.MatchToken("<")) {  // Generics
				@Var BArray<BType> TypeList = new BArray<BType>(new BType[4]);
				while(!TokenContext.StartsWithToken(">")) {
					if(TypeList.size() > 0 && !TokenContext.MatchToken(",")) {
						return null;
					}
					@Var BTypeNode ParamTypeNode = (BTypeNode) TokenContext.ParsePattern(ParentNode, "$OpenType$", BTokenContext._Optional);
					if(ParamTypeNode == null) {
						return LeftTypeNode;
					}
					TypeList.add(ParamTypeNode.Type);
				}
				LeftTypeNode = new BTypeNode(ParentNode, SourceToken, BTypePool._GetGenericType(LeftTypeNode.Type, TypeList, true));
			}
		}
		while(TokenContext.MatchToken("[")) {  // Array
			if(!TokenContext.MatchToken("]")) {
				return null;
			}
			LeftTypeNode = new BTypeNode(ParentNode, SourceToken, BTypePool._GetGenericType1(BGenericType._ArrayType, LeftTypeNode.Type));
		}
		return LeftTypeNode;
	}

}
