package libbun.encode.jvm;

import libbun.util.LibBunSystem;

public class DebugAsmGenerator extends AsmJavaGenerator {
	public DebugAsmGenerator() {
		LibBunSystem._SetDebugVerbose(true);
	}
}
