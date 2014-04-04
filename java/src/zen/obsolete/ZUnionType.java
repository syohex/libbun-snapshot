package zen.obsolete;

import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BArray;

public class ZUnionType extends BType {

	@BField public final BArray<BType> UnionList = null;

	public ZUnionType() {
		super(0, "union", BType.VarType);
		this.TypeId = this.RefType.TypeId;
	}

}
