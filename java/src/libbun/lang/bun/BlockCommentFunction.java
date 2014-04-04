package libbun.lang.bun;

import libbun.parser.BSourceContext;
import libbun.util.Var;
import libbun.util.BTokenFunction;

public class BlockCommentFunction extends BTokenFunction {

	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		@Var char NextChar = SourceContext.GetCharAtFromCurrentPosition(+1);
		if(NextChar != '/' && NextChar != '*') {
			return false;  // another tokenizer
		}
		if(NextChar == '/') { // SingleLineComment
			while(SourceContext.HasChar()) {
				@Var char ch = SourceContext.GetCurrentChar();
				if(ch == '\n') {
					break;
				}
				SourceContext.MoveNext();
			}
			return true;
		}
		@Var int NestedLevel = 0;
		@Var char PrevChar = '\0';
		while(SourceContext.HasChar()) {
			NextChar = SourceContext.GetCurrentChar();
			//System.out.println("P,N"+PrevChar+","+NextChar);
			if(PrevChar == '*' && NextChar == '/') {
				NestedLevel = NestedLevel - 1;
				if(NestedLevel == 0) {
					SourceContext.MoveNext();
					return true;
				}
			}
			if(PrevChar == '/' && NextChar == '*') {
				NestedLevel = NestedLevel + 1;
			}
			SourceContext.MoveNext();
			PrevChar = NextChar;
		}
		SourceContext.LogWarning(StartIndex, "unfound */");
		return true;
	}

}
