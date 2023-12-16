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
import dev.latvian.apps.ichor.ast.expression.unary.AstNot;
import dev.latvian.apps.ichor.ast.expression.unary.AstPositive;
import dev.latvian.apps.ichor.ast.expression.unary.AstSub1L;
import dev.latvian.apps.ichor.ast.expression.unary.AstUnary;
import org.jetbrains.annotations.Nullable;

public enum Symbol implements Token {
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
	ADD("+"), // addition
	ADD_SET("+="), // add and set
	ADD1("++"), // 1 addition
	SUB("-"), // subtraction & negation
	SUB_SET("-="), // sub and set
	SUB1("--"), // 1 subtraction
	MUL("*"), // multiplication
	MUL_SET("*="), // multiply, set
	DIV("/"), // division
	DIV_SET("/="), // divide, set
	MOD("%"), // modulo
	MOD_SET("%="), // modulo, set
	NOT("!"), // not
	BNOT("~"), // bitwise not
	EQ("=="), // equal
	NEQ("!="), // not equal
	SEQ("==="), // shallow equal
	SNEQ("!=="), // shallow not equal
	LT("<"), // less than
	GT(">"), // greater than
	LTE("<="), // less than or equal
	GTE(">="), // greater than or equal
	OR("||"), // or
	AND("&&"), // and
	BOR("|"), // bitwise or
	BOR_SET("|="), // bitwise or, set
	BAND("&"), // bitwise and
	BAND_SET("&="), // bitwise and, set
	XOR("^"), // exclusive or & bitwise exclusive or
	XOR_SET("^="), // exclusive or & bitwise exclusive or, set
	LSH("<<"), // left shift
	LSH_SET("<<="), // left shift, set
	RSH(">>"), // right shift
	RSH_SET(">>="), // right shift, set
	URSH(">>>"), // unsigned right shift
	URSH_SET(">>>="), // unsigned right shift, set
	POW("**"), // power
	POW_SET("**="), // power, set
	HOOK("?"), // hook
	COL(":"), // colon
	DCOL("::"), // double colon
	SEMI(";"), // semicolon
	OC("?."), // optional chaining
	NC("??"), // nullish coalescing
	NC_SET("??="), // nullish coalescing
	ARROW("=>"), // arrow
	TEMPLATE_LITERAL("`"), // template literal
	TEMPLATE_LITERAL_VAR("${"), // template literal variable
	COMMENT_LINE("//"), // comment line
	COMMENT_BLOCK_START("/*"), // comment block start
	SSTRING("'"), // single quote string
	DSTRING("\""), // double quote string

	;

	public static final Token[] SET_OP_TOKENS = {
			ADD_SET,
			SUB_SET,
			MUL_SET,
			DIV_SET,
			MOD_SET,
			BOR_SET,
			BAND_SET,
			XOR_SET,
			LSH_SET,
			RSH_SET,
			URSH_SET,
			POW_SET,
			NC_SET,
	};

	public final String name;

	Symbol(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Nullable
	public static Symbol read(TokenStream s, char t) {
		return switch (t) {
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
			case '*' -> s.readIf('=') ? MUL_SET : s.readIf('*') ? s.readIf('=') ? POW_SET : POW : MUL;
			case '/' -> s.readIf('=') ? DIV_SET : s.readIf('/') ? COMMENT_LINE : s.readIf('*') ? COMMENT_BLOCK_START : DIV;
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
			case '\'' -> SSTRING;
			case '"' -> DSTRING;
			default -> null;
		};
	}

	@Override
	@Nullable
	public AstUnary createUnaryAst(PositionedToken pos) {
		return switch (this) {
			case ADD -> new AstPositive();
			case SUB -> new AstNegate();
			case ADD1 -> new AstAdd1L();
			case SUB1 -> new AstSub1L();
			case NOT -> new AstNot();
			case BNOT -> new AstBitwiseNot();
			default -> null;
		};
	}

	@Override
	@Nullable
	public AstBinary createBinaryAst(PositionedToken pos) {
		return switch (this) {
			case ADD, ADD_SET -> new AstAdd();
			case SUB, SUB_SET -> new AstSub();
			case MUL, MUL_SET -> new AstMul();
			case DIV, DIV_SET -> new AstDiv();
			case MOD, MOD_SET -> new AstMod();
			case EQ -> new AstEq();
			case NEQ -> new AstNeq();
			case SEQ -> new AstSeq();
			case SNEQ -> new AstSneq();
			case LT -> new AstLt();
			case GT -> new AstGt();
			case LTE -> new AstLte();
			case GTE -> new AstGte();
			case OR -> new AstOr();
			case AND -> new AstAnd();
			case BOR, BOR_SET -> new AstBitwiseOr();
			case BAND, BAND_SET -> new AstBitwiseAnd();
			case XOR, XOR_SET -> new AstXor();
			case LSH, LSH_SET -> new AstLsh();
			case RSH, RSH_SET -> new AstRsh();
			case URSH, URSH_SET -> new AstUrsh();
			case POW, POW_SET -> new AstPow();
			case NC, NC_SET -> new AstNc();
			default -> null;
		};
	}

	@Override
	public boolean isLiteralPre() {
		return switch (this) {
			// case LP, SET, ARROW -> true;
			// default -> false;
			case DOT, DDOT, TDOT, RP, RC, RS, DCOL, OC, TEMPLATE_LITERAL, TEMPLATE_LITERAL_VAR -> false;
			default -> true;
		};
	}
}
