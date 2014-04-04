package libbun.encode.jvm;

import libbun.util.BLib;

public class DebugAsmGenerator extends AsmJavaGenerator {
	public DebugAsmGenerator() {
		BLib._SetDebugVerbose(true);
	}
}
