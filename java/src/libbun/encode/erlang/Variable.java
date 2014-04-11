

package libbun.encode.erlang;

import libbun.util.BField;



public class Variable {
	@BField public int Read;
	@BField public int Next;
	@BField public int Flags;

	public Variable() {
		this.Read = -1;
		this.Next = 0;
		this.Flags = 0;
	}

	void UsedByCurrentScope() {
		this.Flags = this.Flags | 1;
	}
	boolean IsUsedByCurrentScope() {
		return (this.Flags & 1) != 0;
	}
	void UsedByChildScope() {
		this.Flags = this.Flags | (1 << 1);
	}
	boolean IsUsedByChildScope() {
		return (this.Flags & (1 << 1)) != 0;
	}
	boolean IsUsed() {
		return this.IsUsedByCurrentScope() || this.IsUsedByChildScope();
	}
	void CreatedTemporary() {
		this.Flags = this.Flags | (1 << 2);
	}
	boolean IsCreatedTemporary() {
		return (this.Flags & (1 << 2)) != 0;
	}
}
