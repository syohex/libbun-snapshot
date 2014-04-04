package libbun.lang.bun;

import libbun.parser.ZSourceContext;
import libbun.util.BTokenFunction;

public class WhiteSpaceTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(ZSourceContext SourceContext) {
		SourceContext.SkipWhiteSpace();
		return true;
	}
}
