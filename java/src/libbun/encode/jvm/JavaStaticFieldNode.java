package libbun.encode.jvm;

import libbun.ast.BNode;
import libbun.ast.ZLocalDefinedNode;
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
