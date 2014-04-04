package libbun.lang.bun.regexp;

import libbun.parser.BSourceContext;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BTokenFunction;

public class RexExpLiteralTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
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

	private boolean ParseRegExpFlags(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(!BLib._IsDigitOrLetter(ch)) {
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
