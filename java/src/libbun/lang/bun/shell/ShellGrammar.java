package libbun.lang.bun.shell;

import libbun.parser.ZNameSpace;

public class ShellGrammar {
	public static void ImportGrammar(ZNameSpace NameSpace) {
		CommandSymbolTokenFunction commandSymbolToken = new CommandSymbolTokenFunction();
		ImportCommandPatternFunction importCommandPattern = new ImportCommandPatternFunction();
		PrefixOptionPatternFunction prefixOptionPattern = new PrefixOptionPatternFunction();

		NameSpace.AppendTokenFunc("#", new ShellStyleCommentTokenFunction());
		NameSpace.AppendTokenFunc("Aa_", commandSymbolToken);
		NameSpace.AppendTokenFunc("1", commandSymbolToken);

		NameSpace.DefineStatement("import", new ImportPatternFunction());
		NameSpace.DefineExpression("command", importCommandPattern);
		NameSpace.DefineExpression(ImportCommandPatternFunction._PatternName, importCommandPattern);
		NameSpace.DefineExpression(SimpleArgumentPatternFunction._PatternName, new SimpleArgumentPatternFunction());
		NameSpace.DefineExpression(RedirectPatternFunction._PatternName, new RedirectPatternFunction());
		NameSpace.DefineExpression(SuffixOptionPatternFunction._PatternName, new SuffixOptionPatternFunction());
		NameSpace.DefineExpression(CommandSymbolPatternFunction._PatternName, new CommandSymbolPatternFunction());
		NameSpace.DefineExpression(ShellUtils._timeout, prefixOptionPattern);
		NameSpace.DefineExpression(ShellUtils._trace, prefixOptionPattern);
		NameSpace.DefineExpression(PrefixOptionPatternFunction._PatternName, prefixOptionPattern);

		NameSpace.Generator.LangInfo.AppendGrammarInfo("shell");
	}
}


/**

if you use shell grammar, you must implement following class and function

# argument class definition
## normal argument
class CommandArg {
	var value : String
}

function createCommandArg(value : String) : CommandArg

## substitution argument
class SubstitutedArg extends CommandArg {
	var values : String[]
}

function createSubstitutedArg(value : String) : SubstitutedArg

# command executor definition
function ExecCommandInt(argsList : CommandArg[][]) : int          // return exit status
function ExecCommandBoolean(argsList : CommandArg[][]) : boolean  // return true if exit status is 0 or false if exit status is not 0
function ExecCommandString(argsList : CommandArg[][]) : String    // return command standard out

**/