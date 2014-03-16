package libbun.lang.bun;

import libbun.parser.ZSourceContext;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZTokenFunction;

public class NameTokenFunction extends ZTokenFunction {

	@Override public boolean Invoke(ZSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(!LibZen._IsSymbol(ch) && !LibZen._IsDigit(ch)) {
				break;
			}
			SourceContext.MoveNext();
		}
		SourceContext.Tokenize(StartIndex, SourceContext.GetPosition());
		return true;
	}

}
