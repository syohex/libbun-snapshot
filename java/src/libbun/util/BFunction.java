package libbun.util;

import libbun.type.BType;
import libbun.type.BTypePool;

public abstract class BFunction implements BTypedObject {
	@BField final BType  ZenType;
	@BField final String FUNCTION;
	public BFunction(int TypeId, String f) {
		this.ZenType  = BTypePool.TypeOf(TypeId);
		if(f == null) {
			f= this.getClass().getSimpleName();
		}
		this.FUNCTION = f;
	}
	@Override public final BType GetZenType() {
		return this.ZenType;
	}
	@Override public String toString() {
		return this.FUNCTION;
	}
}
