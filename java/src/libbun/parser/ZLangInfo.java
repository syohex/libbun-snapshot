package libbun.parser;

import libbun.util.Field;
import libbun.util.ZenMethod;

public class ZLangInfo {
	@Field public String GrammarInfo = "";

	@Field public String LangVersion = null;
	@Field public String Extension = null;

	public boolean AllowTopLevelScript = false;

	public ZLangInfo(String LangVersion, String Extension) {
		this.LangVersion = LangVersion;
		this.Extension   = Extension;
	}

	public final void AppendGrammarInfo(String GrammarInfo) {
		this.GrammarInfo = this.GrammarInfo + GrammarInfo + " ";
	}

	public final String GetGrammarInfo() {
		return this.GrammarInfo;
	}

	@ZenMethod public String NameOutputFile(String FileName) {
		if(FileName != null) {
			return FileName + "." + this.Extension;
		}
		return FileName;
	}

	public final String GetLibPath(String LibName) {
		return "lib/" + this.Extension + "/" + LibName + ".bun";
	}

}
