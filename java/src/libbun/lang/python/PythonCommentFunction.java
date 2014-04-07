package libbun.lang.python;

import libbun.parser.BSourceContext;
import libbun.util.BTokenFunction;
import libbun.util.Var;

public class PythonCommentFunction extends BTokenFunction {

	@Override
	public boolean Invoke(BSourceContext SourceContext) {
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
