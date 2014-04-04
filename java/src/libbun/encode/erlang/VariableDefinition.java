

package libbun.encode.erlang;

import libbun.util.BField;

//endif VAJA

public class VariableDefinition {
	@BField public String Name;
	@BField public int AssignedCount;
	@BField public VariableReference CurrentRef;

	public VariableDefinition/*constructor*/(String Name) {
		this.Name = Name;
		this.AssignedCount = 0;
		this.CurrentRef = null;
	}
}
