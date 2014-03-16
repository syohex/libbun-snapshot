package libbun.lang.bun;

import libbun.parser.ZSourceContext;
import libbun.util.ZTokenFunction;

public class WhiteSpaceTokenFunction extends ZTokenFunction {
	@Override public boolean Invoke(ZSourceContext SourceContext) {
		SourceContext.SkipWhiteSpace();
		return true;
	}
}
