package libbun.lang.bun.regexp;

import libbun.parser.ZSourceContext;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZTokenFunction;

public class RexExpLiteralTokenFunction extends ZTokenFunction {
	@Override public boolean Invoke(ZSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		SourceContext.MoveNext();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(ch == '/') {
				SourceContext.MoveNext(); // eat '/'
				SourceContext.Tokenize("$RexExpLiteral$", StartIndex, SourceContext.GetPosition());
				if(this.ParseRegExpFlags(SourceContext)) {
				}
				return true;
			}
			if(ch == '\n') {
				break;
			}
			if(ch == '\\') {
				SourceContext.MoveNext();
			}
			SourceContext.MoveNext();
		}
		// case: "x /= 10", "x / y"
		return false;
	}

	private boolean ParseRegExpFlags(ZSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(!LibZen._IsDigitOrLetter(ch)) {
				if(ch == '\\') {
					// FIXME : we need to support "\u0000"
					return false;
				}
				break;
			}
			SourceContext.MoveNext();
		}
		SourceContext.Tokenize("$RexExpLiteralFlag$", StartIndex, SourceContext.GetPosition());
		return true;
	}
}
