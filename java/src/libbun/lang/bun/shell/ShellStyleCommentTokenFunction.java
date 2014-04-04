package libbun.lang.bun.shell;

import libbun.util.Var;
import libbun.util.BTokenFunction;
import libbun.parser.ZSourceContext;

public class ShellStyleCommentTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(ZSourceContext SourceContext) {
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(ch == '\n') {
				break;
			}
			SourceContext.MoveNext();
		}
		return true;
	}
}
