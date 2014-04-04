package libbun.lang.bun;

import libbun.parser.ZSourceContext;
import libbun.util.BTokenFunction;

public class OperatorTokenFunction extends BTokenFunction {

	@Override public boolean Invoke(ZSourceContext SourceContext) {
		SourceContext.TokenizeDefinedSymbol(SourceContext.GetPosition());
		return true;
	}

}
