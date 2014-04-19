package libbun.lang.konoha;

import libbun.lang.bun.BunGrammar;
import libbun.parser.LibBunGamma;

public class KonohaGrammar {
	public static void ImportGrammar(LibBunGamma Gamma) {
		BunGrammar.ImportGrammar(Gamma);
		Gamma.DefineStatement("continue", new ContinuePatternFunction());
	}
}
