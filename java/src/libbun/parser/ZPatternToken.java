package libbun.parser;

import libbun.util.BField;

public class ZPatternToken extends ZToken {
	@BField public ZSyntax	PresetPattern;
	public ZPatternToken(ZSource Source, int StartIndex, int EndIndex, ZSyntax	PresetPattern) {
		super(Source, StartIndex, EndIndex);
		this.PresetPattern = PresetPattern;
	}

}
