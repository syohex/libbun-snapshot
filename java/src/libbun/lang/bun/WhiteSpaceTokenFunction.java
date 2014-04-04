package libbun.lang.bun;

import libbun.parser.BSourceContext;
import libbun.util.BTokenFunction;

public class WhiteSpaceTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		SourceContext.SkipWhiteSpace();
		return true;
	}
}
