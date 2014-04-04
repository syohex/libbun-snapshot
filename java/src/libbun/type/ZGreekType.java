package libbun.type;

import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;

public class ZGreekType extends ZType {

	@BField public final int GreekId;

	public ZGreekType(int GreekId) {
		super(ZType.UniqueTypeFlag, BLib._GreekNames[GreekId], ZType.VarType);
		this.GreekId = GreekId;
	}

	public final static ZType[] _NewGreekTypes(ZType[] GreekTypes) {
		if(GreekTypes == null) {
			return BLib._NewTypeArray(BLib._GreekNames.length);
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

	@Override public ZType GetGreekRealType(ZType[] Greek) {
		if(Greek[this.GreekId] == null) {
			return ZType.VarType;
		}
		return Greek[this.GreekId];
	}

	@Override public final boolean AcceptValueType(ZType ValueType, boolean ExactMatch, ZType[] Greek) {
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
