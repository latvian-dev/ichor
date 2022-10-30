package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.parser.expression.binary.AstAdd;
import dev.latvian.apps.ichor.parser.expression.binary.AstAddSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstAnd;
import dev.latvian.apps.ichor.parser.expression.binary.AstBitwiseAnd;
import dev.latvian.apps.ichor.parser.expression.binary.AstBitwiseOr;
import dev.latvian.apps.ichor.parser.expression.binary.AstBitwiseOrSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstDiv;
import dev.latvian.apps.ichor.parser.expression.binary.AstDivSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstEq;
import dev.latvian.apps.ichor.parser.expression.binary.AstGt;
import dev.latvian.apps.ichor.parser.expression.binary.AstGte;
import dev.latvian.apps.ichor.parser.expression.binary.AstLsh;
import dev.latvian.apps.ichor.parser.expression.binary.AstLshSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstLt;
import dev.latvian.apps.ichor.parser.expression.binary.AstLte;
import dev.latvian.apps.ichor.parser.expression.binary.AstMod;
import dev.latvian.apps.ichor.parser.expression.binary.AstModSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstMul;
import dev.latvian.apps.ichor.parser.expression.binary.AstMulSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstNc;
import dev.latvian.apps.ichor.parser.expression.binary.AstNeq;
import dev.latvian.apps.ichor.parser.expression.binary.AstOr;
import dev.latvian.apps.ichor.parser.expression.binary.AstPow;
import dev.latvian.apps.ichor.parser.expression.binary.AstRsh;
import dev.latvian.apps.ichor.parser.expression.binary.AstRshSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstSeq;
import dev.latvian.apps.ichor.parser.expression.binary.AstSetReplace;
import dev.latvian.apps.ichor.parser.expression.binary.AstSneq;
import dev.latvian.apps.ichor.parser.expression.binary.AstSub;
import dev.latvian.apps.ichor.parser.expression.binary.AstSubSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstUrsh;
import dev.latvian.apps.ichor.parser.expression.binary.AstUrshSet;
import dev.latvian.apps.ichor.parser.expression.binary.AstXor;
import dev.latvian.apps.ichor.parser.expression.binary.AstXorSet;
import dev.latvian.apps.ichor.parser.expression.unary.AstAdd1L;
import dev.latvian.apps.ichor.parser.expression.unary.AstBitwiseNot;
import dev.latvian.apps.ichor.parser.expression.unary.AstNegate;
import dev.latvian.apps.ichor.parser.expression.unary.AstPositive;
import dev.latvian.apps.ichor.parser.expression.unary.AstSub1L;
import dev.latvian.apps.ichor.util.EvaluableFactory;

public enum SymbolToken implements StaticToken {
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
	SET("=", AstSetReplace::new), // set
	ADD("+", AstAdd::new), // addition
	ADD_SET("+=", AstAddSet::new), // add and set
	ADD1("++", AstAdd1L::new, false), // 1 addition
	SUB("-", AstSub::new), // subtraction & negation
	SUB_SET("-=", AstSubSet::new), // sub and set
	SUB1("--", AstSub1L::new, false), // 1 subtraction
	MUL("*", AstMul::new), // multiplication
	MUL_SET("*=", AstMulSet::new), // multiply and set
	DIV("/", AstDiv::new), // division
	DIV_SET("/=", AstDivSet::new), // divide and set
	MOD("%", AstMod::new), // modulo
	MOD_SET("%=", AstModSet::new), // modulo and set
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
	BOR_SET("|=", AstBitwiseOrSet::new), // bitwise or and set
	BAND("&", AstBitwiseAnd::new), // bitwise and
	BAND_SET("&=", AstBitwiseOrSet::new), // bitwise and and set
	XOR("^", AstXor::new), // exclusive or & bitwise exclusive or
	XOR_SET("^=", AstXorSet::new), // exclusive or & bitwise exclusive or
	LSH("<<", AstLsh::new), // left shift
	LSH_SET("<<=", AstLshSet::new), // left shift
	RSH(">>", AstRsh::new), // right shift
	RSH_SET(">>=", AstRshSet::new), // right shift
	URSH(">>>", AstUrsh::new), // unsigned right shift
	URSH_SET(">>>=", AstUrshSet::new), // unsigned right shift
	POW("**", AstPow::new), // power
	HOOK("?"), // hook
	COL(":"), // colon
	SEMI(";"), // semicolon
	OC("?."), // optional chaining
	NC("??", AstNc::new), // nullish coalescing
	ARROW("=>"), // arrow
	JAVA_ARROW("->"), // java arrow

	;

	static {
		ADD.astUnary = AstPositive::new;
		SUB.astUnary = AstNegate::new;
	}

	public static final int RIGHT_ASSOCIATIVE = 4;

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
}
