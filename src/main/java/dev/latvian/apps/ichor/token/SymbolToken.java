package dev.latvian.apps.ichor.token;

public enum SymbolToken implements Token {
	EOF("EOF"), // end of file
	EOL("EOL"), // end of line
	DOT("."), // dot
	DDOT(".."), // double dot
	TDOT("..."), // triple dot
	COMMA(","), // comma
	LP("("), // left parenthesis
	RP(")"), // right parenthesis
	LS("["), // left square bracket
	RS("]"), // right square bracket
	LC("{"), // left curly bracket
	RC("}"), // right curly bracket
	ADD("+", SymbolToken.BINARY), // addition
	SUB("-", SymbolToken.BINARY), // subtraction & negation
	MUL("*", SymbolToken.BINARY), // multiplication
	DIV("/", SymbolToken.BINARY), // division
	MOD("%", SymbolToken.BINARY), // modulo
	SET("=", SymbolToken.BINARY), // set
	ADD1("++", SymbolToken.UNARY), // 1 addition
	SUB1("--", SymbolToken.UNARY), // 1 subtraction
	ADD_SET("+=", SymbolToken.BINARY), // add and set
	SUB_SET("-=", SymbolToken.BINARY), // sub and set
	MUL_SET("*=", SymbolToken.BINARY), // multiply and set
	DIV_SET("/=", SymbolToken.BINARY), // divide and set
	MOD_SET("%=", SymbolToken.BINARY), // modulo and set
	NOT("!", SymbolToken.UNARY), // not
	BNOT("~", SymbolToken.UNARY), // bitwise not
	EQ("==", SymbolToken.BINARY), // equal
	NEQ("!=", SymbolToken.BINARY), // not equal
	SEQ("===", SymbolToken.BINARY), // shallow equal
	SNEQ("!==", SymbolToken.BINARY), // shallow not equal
	LT("<", SymbolToken.BINARY), // less than
	GT(">", SymbolToken.BINARY), // greater than
	LTE("<=", SymbolToken.BINARY), // less than or equal
	GTE(">=", SymbolToken.BINARY), // greater than or equal
	OR("||", SymbolToken.BINARY), // or
	AND("&&", SymbolToken.BINARY), // and
	BOR("|", SymbolToken.BINARY), // bitwise or
	BAND("&", SymbolToken.BINARY), // bitwise and
	BOR_SET("|=", SymbolToken.BINARY), // bitwise or and set
	BAND_SET("&=", SymbolToken.BINARY), // bitwise and and set
	XOR("^", SymbolToken.BINARY), // exclusive or & bitwise exclusive or
	LSH("<<", SymbolToken.BINARY), // left shift
	RSH(">>", SymbolToken.BINARY), // right shift
	URSH(">>>", SymbolToken.BINARY), // unsigned right shift
	HOOK("?"), // hook
	COL(":"), // colon
	SEMI(";"), // semicolon
	OC("?."), // optional chaining
	NC("??", SymbolToken.BINARY), // nullish coalescing
	COMMENT("//"), // comment
	LB_COMMENT("/*"), // left block comment
	RB_COMMENT("*/"), // right block comment
	ARROW("=>", SymbolToken.BINARY), // arrow

	;

	public static final int UNARY = 1;
	public static final int BINARY = 2;

	public final String name;
	public final int flags;

	SymbolToken(String n, int f) {
		name = n;
		flags = f;
	}

	SymbolToken(String n) {
		this(n, 0);
	}

	@Override
	public String toString() {
		return name;
	}
}
