package libbun.type;

import libbun.parser.BTypeChecker;

public class BNullableType extends BType {

	public BNullableType(BType ParamType) {
		super(BType.UniqueTypeFlag, null, ParamType);
	}

	@Override public boolean IsNullableType(BTypeChecker Gamma) {
		return true;
	}

	@Override public boolean IsMutableType(BTypeChecker Gamma) {
		return this.RefType.IsMutableType(Gamma);
	}

	@Override public final String GetName() {
		if(this.ShortName == null) {
			this.ShortName =  "maybe " + this.RefType.GetName();
		}
		return this.ShortName;
	}


}
