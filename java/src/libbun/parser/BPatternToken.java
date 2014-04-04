package libbun.parser;

import libbun.util.BField;

public class BPatternToken extends BToken {
	@BField public BSyntax	PresetPattern;
	public BPatternToken(BSource Source, int StartIndex, int EndIndex, BSyntax	PresetPattern) {
		super(Source, StartIndex, EndIndex);
		this.PresetPattern = PresetPattern;
	}

}
