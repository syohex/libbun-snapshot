package libbun.type;

import libbun.parser.BTypeChecker;

public class BMutableType extends BType {

	public BMutableType(BType ParamType) {
		super(BType.UniqueTypeFlag, null, ParamType);
	}

	@Override public final boolean IsMutableType(BTypeChecker Gamma) {
		return true;
	}

	@Override public final String GetName() {
		if(this.ShortName == null) {
			this.ShortName =  "mutable " + this.RefType.GetName();
		}
		return this.ShortName;
	}
}
