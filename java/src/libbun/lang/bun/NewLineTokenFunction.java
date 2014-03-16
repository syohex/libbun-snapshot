package libbun.lang.bun;

import libbun.parser.ZSourceContext;
import libbun.util.Var;
import libbun.util.ZTokenFunction;

public class NewLineTokenFunction extends ZTokenFunction {

	@Override public boolean Invoke(ZSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition() + 1;
		SourceContext.MoveNext();
		SourceContext.SkipWhiteSpace();
		SourceContext.FoundIndent(StartIndex, SourceContext.GetPosition());
		return true;
	}

}
