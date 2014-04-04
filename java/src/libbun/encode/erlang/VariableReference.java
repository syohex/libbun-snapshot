

package libbun.encode.erlang;

import libbun.util.BField;

//endif VAJA

public class VariableReference {
	@BField public VariableDefinition SelfDef;
	@BField public VariableReference ParentRef;
	@BField public int UniqueID;
	@BField public int ScopeLevel;
	@BField public int Flag;

	public VariableReference/*constructor*/(VariableDefinition SelfDef, VariableReference ParentRef, int UniqueID, int ScopeLevel) {
		this.SelfDef = SelfDef;
		this.UniqueID = UniqueID;
		this.ParentRef = ParentRef;
		this.ScopeLevel = ScopeLevel;
		this.Flag = VarFlag.None;
	}
}
