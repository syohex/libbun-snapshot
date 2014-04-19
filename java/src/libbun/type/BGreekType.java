package libbun.type;

import libbun.util.BField;
import libbun.util.LibBunSystem;
import libbun.util.Var;

public class BGreekType extends BType {

	@BField public final int GreekId;

	public BGreekType(int GreekId) {
		super(BType.UniqueTypeFlag, LibBunSystem._GreekNames[GreekId], BType.VarType);
		this.GreekId = GreekId;
	}

	public final static BType[] _NewGreekTypes(BType[] GreekTypes) {
		if(GreekTypes == null) {
			return LibBunSystem._NewTypeArray(LibBunSystem._GreekNames.length);
		}
		else {
			@Var int i = 0;
			while(i < GreekTypes.length) {
				GreekTypes[i] = null;
				i = i + 1;
			}
			return GreekTypes;
		}
	}

	//	public final static ZType GetGreekType(int GreekId) {
	//		if(ZGreekType._GreekTypes[GreekId] == null) {
	//			ZGreekType._GreekTypes[GreekId] = new ZGreekType(GreekId);
	//		}
	//		return ZGreekType._GreekTypes[GreekId];
	//	}

	@Override public boolean IsGreekType() {
		return true;
	}

	@Override public BType GetGreekRealType(BType[] Greek) {
		if(Greek[this.GreekId] == null) {
			return BType.VarType;
		}
		return Greek[this.GreekId];
	}

	@Override public final boolean AcceptValueType(BType ValueType, boolean ExactMatch, BType[] Greek) {
		if(Greek[this.GreekId] == null) {
			if(ValueType.IsVarType()) {
				return true;
			}
			Greek[this.GreekId] = ValueType;
			return true;
		}
		else {
			return Greek[this.GreekId].AcceptValueType(ValueType, ExactMatch, Greek);
		}
	}
}
