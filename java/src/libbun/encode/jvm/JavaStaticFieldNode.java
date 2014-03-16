package libbun.encode.jvm;

import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.ZNode;
import libbun.type.ZType;

public class JavaStaticFieldNode extends ZLocalDefinedNode {
	Class<?> StaticClass;
	String FieldName;
	JavaStaticFieldNode(ZNode ParentNode, Class<?> StaticClass, ZType FieldType, String FieldName) {
		super(ParentNode, null, 0);
		this.StaticClass = StaticClass;
		this.Type = FieldType;
		this.FieldName = FieldName;
	}
}
