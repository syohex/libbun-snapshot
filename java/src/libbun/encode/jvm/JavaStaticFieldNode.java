package libbun.encode.jvm;

import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.BNode;
import libbun.type.BType;

public class JavaStaticFieldNode extends ZLocalDefinedNode {
	Class<?> StaticClass;
	String FieldName;
	JavaStaticFieldNode(BNode ParentNode, Class<?> StaticClass, BType FieldType, String FieldName) {
		super(ParentNode, null, 0);
		this.StaticClass = StaticClass;
		this.Type = FieldType;
		this.FieldName = FieldName;
	}
}
