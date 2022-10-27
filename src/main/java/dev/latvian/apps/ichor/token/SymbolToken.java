package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.parser.Precedence;

public enum SymbolToken implements StaticToken {
	EOF("EOF"), // end of file
	DOT(".", Precedence.POSTFIX), // dot
	DDOT(".."), // double dot
	TDOT("..."), // triple dot
	COMMA(","), // comma
	LP("(", Precedence.POSTFIX), // left parenthesis
	RP(")", Precedence.POSTFIX), // right parenthesis
	LS("[", Precedence.POSTFIX), // left square bracket
	RS("]", Precedence.POSTFIX), // right square bracket
	LC("{"), // left curly bracket
	RC("}"), // right curly bracket
	SET("=", Precedence.ASSIGNMENT), // set
	ADD("+", Precedence.ADDITIVE), // addition
	ADD_SET("+=", Precedence.ASSIGNMENT), // add and set
	ADD1("++", Precedence.POSTFIX), // 1 addition
	SUB("-", Precedence.ADDITIVE), // subtraction & negation
	SUB_SET("-=", Precedence.ASSIGNMENT), // sub and set
	SUB1("--", Precedence.POSTFIX), // 1 subtraction
	MUL("*", Precedence.MULTIPLICATIVE), // multiplication
	MUL_SET("*=", Precedence.ASSIGNMENT), // multiply and set
	DIV("/", Precedence.MULTIPLICATIVE), // division
	DIV_SET("/=", Precedence.ASSIGNMENT), // divide and set
	MOD("%", Precedence.MULTIPLICATIVE), // modulo
	MOD_SET("%=", Precedence.ASSIGNMENT), // modulo and set
	NOT("!", Precedence.UNARY), // not
	BNOT("~", Precedence.UNARY), // bitwise not
	EQ("==", Precedence.EQUALITY), // equal
	NEQ("!=", Precedence.EQUALITY), // not equal
	SEQ("===", Precedence.EQUALITY), // shallow equal
	SNEQ("!==", Precedence.EQUALITY), // shallow not equal
	LT("<", Precedence.RELATIONAL), // less than
	GT(">", Precedence.RELATIONAL), // greater than
	LTE("<=", Precedence.RELATIONAL), // less than or equal
	GTE(">=", Precedence.RELATIONAL), // greater than or equal
	OR("||", Precedence.LOGICAL_OR), // or
	AND("&&", Precedence.LOGICAL_AND), // and
	BOR("|", Precedence.BITWISE_OR), // bitwise or
	BOR_SET("|=", Precedence.ASSIGNMENT), // bitwise or and set
	BAND("&", Precedence.BITWISE_AND), // bitwise and
	BAND_SET("&=", Precedence.ASSIGNMENT), // bitwise and and set
	XOR("^", Precedence.BITWISE_XOR), // exclusive or & bitwise exclusive or
	XOR_SET("^=", Precedence.ASSIGNMENT), // exclusive or & bitwise exclusive or
	LSH("<<", Precedence.SHIFT), // left shift
	LSH_SET("<<=", Precedence.ASSIGNMENT), // left shift
	RSH(">>", Precedence.SHIFT), // right shift
	RSH_SET(">>=", Precedence.ASSIGNMENT), // right shift
	URSH(">>>", Precedence.SHIFT), // unsigned right shift
	URSH_SET(">>>=", Precedence.ASSIGNMENT), // unsigned right shift
	POW("**", Precedence.EXPONENT), // power
	HOOK("?", Precedence.CONDITIONAL), // hook
	COL(":"), // colon
	SEMI(";"), // semicolon
	OC("?."), // optional chaining
	NC("??", Precedence.EQUALITY), // nullish coalescing
	ARROW("=>"), // arrow

	;

	public static final int RIGHT_ASSOCIATIVE = 4;

	public final String name;
	public final Precedence precedence;

	SymbolToken(String n, Precedence p) {
		name = n;
		precedence = p;
	}

	SymbolToken(String n) {
		this(n, Precedence.NONE);
	}

	@Override
	public String toString() {
		return name;
	}
}
