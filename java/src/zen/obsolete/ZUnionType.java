package zen.obsolete;

import libbun.type.ZType;
import libbun.util.Field;
import libbun.util.ZArray;

public class ZUnionType extends ZType {

	@Field public final ZArray<ZType> UnionList = null;

	public ZUnionType() {
		super(0, "union", ZType.VarType);
		this.TypeId = this.RefType.TypeId;
	}

}
