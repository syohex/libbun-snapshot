package libbun.lang.bun;

import libbun.parser.BSourceContext;
import libbun.util.BTokenFunction;

public class OperatorTokenFunction extends BTokenFunction {

	@Override public boolean Invoke(BSourceContext SourceContext) {
		SourceContext.TokenizeDefinedSymbol(SourceContext.GetPosition());
		return true;
	}

}
