

package libbun.encode.erlang;

import libbun.util.BField;



public class VariableDefinition {
	@BField public String Name;
	@BField public int AssignedCount;
	@BField public VariableReference CurrentRef;

	public VariableDefinition(String Name) {
		this.Name = Name;
		this.AssignedCount = 0;
		this.CurrentRef = null;
	}
}
