package libbun.util;

import libbun.type.ZType;
import libbun.type.ZTypePool;

public abstract class BFunction implements BTypedObject {
	@BField final ZType  ZenType;
	@BField final String FUNCTION;
	public BFunction(int TypeId, String f) {
		this.ZenType  = ZTypePool.TypeOf(TypeId);
		if(f == null) {
			f= this.getClass().getSimpleName();
		}
		this.FUNCTION = f;
	}
	@Override public final ZType GetZenType() {
		return this.ZenType;
	}
	@Override public String toString() {
		return this.FUNCTION;
	}
}
