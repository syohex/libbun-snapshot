package libbun.parser;

import libbun.util.BField;

public class BPatternToken extends BToken {
	@BField public LibBunSyntax	PresetPattern;
	public BPatternToken(LibBunSource Source, int StartIndex, int EndIndex, LibBunSyntax	PresetPattern) {
		super(Source, StartIndex, EndIndex);
		this.PresetPattern = PresetPattern;
	}

}
