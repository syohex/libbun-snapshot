package libbun.lang.bun;

public abstract class BunPrecedence {
	public final static int _BinaryOperator					= 1;
	public final static int _LeftJoin						= 1 << 1;
	public final static int _PrecedenceShift					= 3;
	public final static int _CStyleMUL			    = (100 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleADD			    = (200 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleSHIFT			= (300 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleCOMPARE		    = (400 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _Instanceof            = BunPrecedence._CStyleCOMPARE;
	public final static int _CStyleEquals			= (500 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleBITAND			= (600 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleBITXOR			= (700 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleBITOR			= (800 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleAND			    = (900 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleOR				= (1000 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleTRINARY		    = (1100 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;				/* ? : */
	public final static int _CStyleAssign			= (1200 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;
	public final static int _CStyleCOMMA			= (1300 << BunPrecedence._PrecedenceShift) | BunPrecedence._BinaryOperator;

}
