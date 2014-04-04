package libbun.lang.bun;

import libbun.parser.BSourceContext;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BTokenFunction;

public class NumberLiteralTokenFunction extends BTokenFunction {

	public final static char _ParseDigit(BSourceContext SourceContext) {
		@Var char ch = '\0';
		while(SourceContext.HasChar()) {
			ch = SourceContext.GetCurrentChar();
			if(!BLib._IsDigit(ch)) {
				break;
			}
			SourceContext.MoveNext();
		}
		return ch;
	}

	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		@Var char ch = NumberLiteralTokenFunction._ParseDigit(SourceContext);
		if(ch == '.') {
			SourceContext.MoveNext();
			ch = NumberLiteralTokenFunction._ParseDigit(SourceContext);
			if(ch == 'e' || ch == 'E') {
				SourceContext.MoveNext();
				if(SourceContext.HasChar()) {
					ch = SourceContext.GetCurrentChar();
					if(ch == '+' || ch == '-') {
						SourceContext.MoveNext();
					}
				}
				if(SourceContext.HasChar() && !BLib._IsDigit(SourceContext.GetCurrentChar())) {
					SourceContext.LogWarning(StartIndex, "exponent has no digits");
				}
				NumberLiteralTokenFunction._ParseDigit(SourceContext);
			}
			SourceContext.Tokenize("$FloatLiteral$", StartIndex, SourceContext.GetPosition());
		}
		else {
			SourceContext.Tokenize("$IntegerLiteral$", StartIndex, SourceContext.GetPosition());
		}
		return true;
	}

}
