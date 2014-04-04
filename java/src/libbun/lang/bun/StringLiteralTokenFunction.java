package libbun.lang.bun;

import libbun.parser.BSourceContext;
import libbun.util.Var;
import libbun.util.BTokenFunction;

public class StringLiteralTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		SourceContext.MoveNext();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(ch == '\"') {
				SourceContext.MoveNext(); // eat '"'
				SourceContext.Tokenize("$StringLiteral$", StartIndex, SourceContext.GetPosition());
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
		SourceContext.LogWarning(StartIndex, "unclosed \"");
		SourceContext.Tokenize("$StringLiteral$", StartIndex, SourceContext.GetPosition());
		return false;
	}
}
