package libbun.encode;

import libbun.parser.BLangInfo;
import libbun.util.BArray;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Nullable;
import libbun.util.Var;
import libbun.util.ZenMethod;

public abstract class SourceGenerator extends AbstractGenerator {
	@BField private final BArray<SourceBuilder> BuilderList = new BArray<SourceBuilder>(new SourceBuilder[4]);
	@BField protected SourceBuilder HeaderBuilder;
	@BField protected SourceBuilder CurrentBuilder;
	@BField protected String LineFeed = "\n";
	@BField protected String Tab = "   ";

	public SourceGenerator(BLangInfo LangInfo) {
		super(LangInfo);
		this.InitBuilderList();
	}

	@ZenMethod protected void InitBuilderList() {
		this.CurrentBuilder = null;
		this.BuilderList.clear(0);
		this.HeaderBuilder = this.AppendNewSourceBuilder();
		this.CurrentBuilder = this.AppendNewSourceBuilder();
	}

	protected final SourceBuilder AppendNewSourceBuilder() {
		@Var SourceBuilder Builder = new SourceBuilder(this, this.CurrentBuilder);
		this.BuilderList.add(Builder);
		return Builder;
	}

	protected final SourceBuilder InsertNewSourceBuilder() {
		@Var SourceBuilder Builder = new SourceBuilder(this, this.CurrentBuilder);
		@Var int i = 0;
		while(i < this.BuilderList.size()) {
			if(this.BuilderList.ArrayValues[i] == this.CurrentBuilder) {
				this.BuilderList.add(i, Builder);
				return Builder;
			}
			i = i + 1;
		}
		this.BuilderList.add(Builder);
		return Builder;
	}

	@ZenMethod protected void Finish(String FileName) {

	}

	@Override public final void WriteTo(@Nullable String FileName) {
		this.Finish(FileName);
		this.Logger.OutputErrorsToStdErr();
		BLib._WriteTo(this.LangInfo.NameOutputFile(FileName), this.BuilderList);
		this.InitBuilderList();
	}

	@Override public final String GetSourceText() {
		this.Finish(null);
		@Var SourceBuilder sb = new SourceBuilder(this, null);
		@Var int i = 0;
		while(i < this.BuilderList.size()) {
			@Var SourceBuilder Builder = this.BuilderList.ArrayValues[i];
			sb.Append(Builder.toString());
			Builder.Clear();
			sb.AppendLineFeed();
			i = i + 1;
		}
		this.InitBuilderList();
		return BLib._SourceBuilderToString(sb);
	}

	@Override public void Perform() {
		@Var int i = 0;
		//this.Logger.OutputErrorsToStdErr();
		BLib._PrintLine("---");
		while(i < this.BuilderList.size()) {
			@Var SourceBuilder Builder = this.BuilderList.ArrayValues[i];
			BLib._PrintLine(Builder.toString());
			Builder.Clear();
			i = i + 1;
		}
		this.InitBuilderList();
	}

	// Generator




}
