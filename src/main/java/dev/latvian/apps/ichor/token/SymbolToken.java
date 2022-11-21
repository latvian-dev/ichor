package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.ast.expression.binary.AstAdd;
import dev.latvian.apps.ichor.ast.expression.binary.AstAnd;
import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.binary.AstBitwiseAnd;
import dev.latvian.apps.ichor.ast.expression.binary.AstBitwiseOr;
import dev.latvian.apps.ichor.ast.expression.binary.AstDiv;
import dev.latvian.apps.ichor.ast.expression.binary.AstEq;
import dev.latvian.apps.ichor.ast.expression.binary.AstGt;
import dev.latvian.apps.ichor.ast.expression.binary.AstGte;
import dev.latvian.apps.ichor.ast.expression.binary.AstLsh;
import dev.latvian.apps.ichor.ast.expression.binary.AstLt;
import dev.latvian.apps.ichor.ast.expression.binary.AstLte;
import dev.latvian.apps.ichor.ast.expression.binary.AstMod;
import dev.latvian.apps.ichor.ast.expression.binary.AstMul;
import dev.latvian.apps.ichor.ast.expression.binary.AstNc;
import dev.latvian.apps.ichor.ast.expression.binary.AstNeq;
import dev.latvian.apps.ichor.ast.expression.binary.AstOr;
import dev.latvian.apps.ichor.ast.expression.binary.AstPow;
import dev.latvian.apps.ichor.ast.expression.binary.AstRsh;
import dev.latvian.apps.ichor.ast.expression.binary.AstSeq;
import dev.latvian.apps.ichor.ast.expression.binary.AstSneq;
import dev.latvian.apps.ichor.ast.expression.binary.AstSub;
import dev.latvian.apps.ichor.ast.expression.binary.AstUrsh;
import dev.latvian.apps.ichor.ast.expression.binary.AstXor;
import dev.latvian.apps.ichor.ast.expression.unary.AstAdd1L;
import dev.latvian.apps.ichor.ast.expression.unary.AstBitwiseNot;
import dev.latvian.apps.ichor.ast.expression.unary.AstNegate;
import dev.latvian.apps.ichor.ast.expression.unary.AstPositive;
import dev.latvian.apps.ichor.ast.expression.unary.AstSub1L;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.error.ParseErrorType;
import dev.latvian.apps.ichor.util.EvaluableFactory;
import org.jetbrains.annotations.Nullable;

public enum SymbolToken implements StaticToken, BinaryOpToken {
	EOF("EOF"), // end of file
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
	SET("="), // set
	ADD("+", AstAdd::new), // addition
	ADD_SET("+=", AstAdd::new), // add and set
	ADD1("++", AstAdd1L::new, false), // 1 addition
	SUB("-", AstSub::new), // subtraction & negation
	SUB_SET("-=", AstSub::new), // sub and set
	SUB1("--", AstSub1L::new, false), // 1 subtraction
	MUL("*", AstMul::new), // multiplication
	MUL_SET("*=", AstMul::new), // multiply and set
	DIV("/", AstDiv::new), // division
	DIV_SET("/=", AstDiv::new), // divide and set
	MOD("%", AstMod::new), // modulo
	MOD_SET("%=", AstMod::new), // modulo and set
	NOT("!", AstNegate::new, false), // not
	BNOT("~", AstBitwiseNot::new, false), // bitwise not
	EQ("==", AstEq::new), // equal
	NEQ("!=", AstNeq::new), // not equal
	SEQ("===", AstSeq::new), // shallow equal
	SNEQ("!==", AstSneq::new), // shallow not equal
	LT("<", AstLt::new), // less than
	GT(">", AstGt::new), // greater than
	LTE("<=", AstLte::new), // less than or equal
	GTE(">=", AstGte::new), // greater than or equal
	OR("||", AstOr::new), // or
	AND("&&", AstAnd::new), // and
	BOR("|", AstBitwiseOr::new), // bitwise or
	BOR_SET("|=", AstBitwiseOr::new), // bitwise or and set
	BAND("&", AstBitwiseAnd::new), // bitwise and
	BAND_SET("&=", AstBitwiseAnd::new), // bitwise and and set
	XOR("^", AstXor::new), // exclusive or & bitwise exclusive or
	XOR_SET("^=", AstXor::new), // exclusive or & bitwise exclusive or
	LSH("<<", AstLsh::new), // left shift
	LSH_SET("<<=", AstLsh::new), // left shift
	RSH(">>", AstRsh::new), // right shift
	RSH_SET(">>=", AstRsh::new), // right shift
	URSH(">>>", AstUrsh::new), // unsigned right shift
	URSH_SET(">>>=", AstUrsh::new), // unsigned right shift
	POW("**", AstPow::new), // power
	HOOK("?"), // hook
	COL(":"), // colon
	DCOL("::"), // double colon
	SEMI(";"), // semicolon
	OC("?."), // optional chaining
	NC("??", AstNc::new), // nullish coalescing
	NC_SET("??=", AstNc::new), // nullish coalescing
	ARROW("=>"), // arrow
	TEMPLATE_LITERAL("`"), // template literal
	TEMPLATE_LITERAL_VAR("${"), // template literal variable

	;

	public static final int RIGHT_ASSOCIATIVE = 4;

	static {
		ADD.astUnary = AstPositive::new;
		SUB.astUnary = AstNegate::new;
	}


	public final String name;
	public EvaluableFactory astUnary;
	public EvaluableFactory astBinary;

	SymbolToken(String name) {
		this.name = name;
	}

	SymbolToken(String name, EvaluableFactory factory, boolean binary) {
		this(name);

		if (binary) {
			this.astBinary = factory;
		} else {
			this.astUnary = factory;
		}
	}

	SymbolToken(String name, EvaluableFactory astBinary) {
		this(name);
		this.astBinary = astBinary;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public AstBinary createBinaryAst(PositionedToken pos) {
		if (astBinary != null && astBinary.create() instanceof AstBinary bin) {
			return bin;
		}

		throw new ParseError(pos, ParseErrorType.INVALID_BINARY.format(name));
	}

	@Nullable
	public static SymbolToken read(TokenStream s, char t) {
		return switch (t) {
			case 0 -> EOF;
			case '.' -> s.readIf('.') ? s.readIf('.') ? TDOT : DDOT : DOT;
			case ',' -> COMMA;
			case '(' -> LP;
			case ')' -> RP;
			case '[' -> LS;
			case ']' -> RS;
			case '{' -> LC;
			case '}' -> RC;
			case '=' -> s.readIf('>') ? ARROW : s.readIf('=') ? s.readIf('=') ? SEQ : EQ : SET;
			case '+' -> s.readIf('=') ? ADD_SET : s.readIf('+') ? ADD1 : ADD;
			case '-' -> s.readIf('=') ? SUB_SET : s.readIf('-') ? SUB1 : SUB;
			case '*' -> s.readIf('=') ? MUL_SET : s.readIf('*') ? POW : MUL;
			case '/' -> s.readIf('=') ? DIV_SET : DIV;
			case '%' -> s.readIf('=') ? MOD_SET : MOD;
			case '!' -> s.readIf('=') ? s.readIf('=') ? SNEQ : NEQ : NOT;
			case '~' -> BNOT;
			case '<' -> s.readIf('<') ? s.readIf('=') ? LSH_SET : LSH : s.readIf('=') ? LTE : LT;
			case '>' -> s.readIf('>') ? s.readIf('>') ? s.readIf('=') ? URSH_SET : URSH : s.readIf('=') ? RSH_SET : RSH : s.readIf('=') ? GTE : GT;
			case '^' -> s.readIf('=') ? XOR_SET : XOR;
			case '?' -> s.readIf('.') ? OC : s.readIf('?') ? NC : HOOK;
			case '|' -> s.readIf('=') ? BOR_SET : s.readIf('|') ? OR : BOR;
			case '&' -> s.readIf('=') ? BAND_SET : s.readIf('&') ? AND : BAND;
			case ':' -> s.readIf(':') ? DCOL : COL;
			case ';' -> SEMI;
			case '`' -> TEMPLATE_LITERAL;
			case '$' -> s.readIf('{') ? TEMPLATE_LITERAL_VAR : null;
			default -> null;
		};
	}
}
