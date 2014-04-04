package libbun.lang.bun.shell;

import java.io.File;

import libbun.parser.ZSource;
import libbun.parser.ZSyntax;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BStringNode;
import libbun.util.BArray;

// you must implement this class if you use shell grammar
public class ShellUtils {
	// suffix option symbol
	public final static String _background = "&";
	// prefix option symbol 
	public final static String _timeout = "timeout";
	public final static String _trace = "trace";

	public static String _ToCommandSymbol(String Symbol) {
		return "__$" + Symbol;
	}

	public static boolean _MatchStopToken(ZTokenContext TokenContext) { // ;,)]}&&||
		ZToken Token = TokenContext.GetToken();
		if(!TokenContext.HasNext()) {
			return true;
		}
		if(Token.IsIndent() || Token.EqualsText(";")) {
			return true;
		}
		if(Token.EqualsText(",") || Token.EqualsText(")") || Token.EqualsText("]") || 
				Token.EqualsText("}") || Token.EqualsText("&&") || Token.EqualsText("||") || Token.EqualsText("`")) {
			return true;
		}
		return false;
	}

	public static BNode _ToNode(BNode ParentNode, ZTokenContext TokenContext, BArray<BNode> NodeList) {
		ZToken Token = TokenContext.GetToken();
		BNode Node = new BStringNode(ParentNode, null, "");
		ZSyntax Pattern = TokenContext.NameSpace.GetRightSyntaxPattern("+");
		ZToken PlusToken = new ZToken(new ZSource(Token.GetFileName(), Token.GetLineNumber(), "+", TokenContext), 0, "+".length());
		for(BNode CurrentNode : NodeList.ArrayValues) {
			ZBinaryNode BinaryNode = new ZBinaryNode(ParentNode, PlusToken, Node, Pattern);
			BinaryNode.SetNode(ZBinaryNode._Right, CurrentNode);
			Node = BinaryNode;
		}
		return Node;
	}

	public static boolean _IsFileExecutable(String Path) {
		return new File(Path).canExecute();
	}

	public static String _ResolveHome(String Path) {
		if(Path.equals("~")) {
			return System.getenv("HOME");
		}
		else if(Path.startsWith("~/")) {
			return System.getenv("HOME") + Path.substring(1);
		}
		return Path;
	}

	public static String _GetUnixCommand(String cmd) {
		String[] Paths = System.getenv("PATH").split(":");
		for(String Path : Paths) {
			String FullPath = _ResolveHome(Path + "/" + cmd);
			if(_IsFileExecutable(FullPath)) {
				return FullPath;
			}
		}
		return null;
	}
}
