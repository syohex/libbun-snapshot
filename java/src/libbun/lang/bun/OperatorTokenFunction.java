package libbun.lang.bun;

import libbun.parser.ZSourceContext;
import libbun.util.ZTokenFunction;

public class OperatorTokenFunction extends ZTokenFunction {

	@Override public boolean Invoke(ZSourceContext SourceContext) {
		SourceContext.TokenizeDefinedSymbol(SourceContext.GetPosition());
		return true;
	}

}
