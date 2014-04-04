package libbun.lang.bun;

import libbun.parser.BSourceContext;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BTokenFunction;

public class NameTokenFunction extends BTokenFunction {

	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(!BLib._IsSymbol(ch) && !BLib._IsDigit(ch)) {
				break;
			}
			SourceContext.MoveNext();
		}
		SourceContext.Tokenize(StartIndex, SourceContext.GetPosition());
		return true;
	}

}
