package zen.obsolete;

import libbun.type.ZType;
import libbun.util.BField;
import libbun.util.BArray;

public class ZUnionType extends ZType {

	@BField public final BArray<ZType> UnionList = null;

	public ZUnionType() {
		super(0, "union", ZType.VarType);
		this.TypeId = this.RefType.TypeId;
	}

}
