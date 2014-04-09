package libbun.encode.jvm;

import static org.objectweb.asm.Opcodes.V1_6;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import libbun.util.BLib;

public class ByteCodePrinter extends AsmJavaGenerator {
	private final ArrayList<String> StringifiedCodeList;

	public ByteCodePrinter() {
		super();
		BLib._SetDebugVerbose(true);
		this.StringifiedCodeList = new ArrayList<String>();
		this.AsmLoader = new AsmClassLoader(this) {
			@Override protected Class<?> findClass(String name) {
				AsmClassBuilder ClassBuilder = this.ClassBuilderMap.get(name);
				if(ClassBuilder != null) {
					byte[] b = ClassBuilder.GenerateBytecode();
					StringifiedCodeList.add(ByteCodeStringifier.Stringify(ClassBuilder));
					this.ClassBuilderMap.remove(name);
					try {
						return this.defineClass(name, b, 0, b.length);
					}
					catch(Error e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
				return null;
			}
		};
	}

	@Override public void WriteTo(String FileName) {
		if(FileName == null) {
			System.out.println();
			for(String Code : this.StringifiedCodeList) {
				System.out.println(Code);
			}
		}
		else {
			try {
				PrintStream Stream = new PrintStream(new FileOutputStream(FileName), true);
				for(String Code : this.StringifiedCodeList) {
					Stream.println(Code);
				}
				Stream.close();
			}
			catch (FileNotFoundException e) {
				BLib._Exit(1, "cannot to write: " + e);
			}
		}
	}

	@Override public void ExecMain() {
		this.Logger.OutputErrorsToStdErr();	// only report error
	}
}

class ByteCodeStringifier {
	public static String Stringify(AsmClassBuilder ClassBuilder) {
		ByteArrayOutputStream OutputBuffer = new ByteArrayOutputStream();
		PrintWriter Writer = new PrintWriter(OutputBuffer);
		TraceClassVisitor ClassVisitor = new TraceClassVisitor(null, new Textifier(), Writer);
		ClassVisitor.visit(V1_6, ClassBuilder.ClassQualifer, ClassBuilder.ClassName, null, ClassBuilder.SuperClassName, null);
		ClassVisitor.visitSource(ClassBuilder.SourceFile, null);
		for(FieldNode f : ClassBuilder.FieldList) {
			f.accept(ClassVisitor);
		}
		for(MethodNode m : ClassBuilder.MethodList) {
			m.accept(ClassVisitor);
		}
		ClassVisitor.visitEnd();
		return OutputBuffer.toString();
	}
}
