package libbun.util;

import libbun.encode.jvm.JavaTypeTable;
import libbun.type.ZType;

public class ZNativeType extends ZType {
	@BField public Class<?>          JClass;

	public ZNativeType(Class<?> JType) {
		super(ZType.UniqueTypeFlag, JType.getSimpleName(), null);
		this.JClass = JType;
	}

	@Override public ZType GetSuperType() {
		return JavaTypeTable.GetZenType(this.JClass.getSuperclass());
	}

}
