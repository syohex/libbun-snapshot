package libbun.encode.jvm;

import libbun.util.LibZen;

public class DebugAsmGenerator extends AsmJavaGenerator {
	public DebugAsmGenerator() {
		LibZen._SetDebugVerbose(true);
	}
}
