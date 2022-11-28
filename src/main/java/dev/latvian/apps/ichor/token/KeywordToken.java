package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.binary.AstIn;
import dev.latvian.apps.ichor.ast.expression.binary.AstInstanceOf;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.error.ParseErrorType;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum KeywordToken implements StaticToken, BinaryOpToken {
	ARGUMENTS("arguments"), // TODO
	AS("as"), // TODO
	ASYNC("async"), // TODO
	AWAIT("await"), // TODO
	BREAK("break"),
	CASE("case"),
	CATCH("catch"),
	CLASS("class", true),
	CONST("const"),
	CONTINUE("continue"),
	DEBUGGER("debugger"), // TODO
	DEFAULT("default"),
	DELETE("delete"),
	DO("do"), // TODO
	ELSE("else"),
	ENUM("enum", true), // TODO
	EVAL("eval"), // TODO
	EXPORT("export", true), // TODO
	EXTENDS("extends"),
	FALSE("false"),
	FINALLY("finally"),
	FOR("for"),
	FROM("from", true), // TODO
	FUNCTION("function"),
	GET("get", true), // TODO
	IF("if"),
	IMPORT("import", true), // TODO
	IN("in", true),
	INSTANCEOF("instanceof"),
	INTERFACE("interface", true),
	LET("let"),
	NEW("new"),
	NULL("null"),
	OF("of", true),
	PACKAGE("package", true), // TODO
	PRIVATE("private", true), // TODO
	PROTECTED("protected", true), // TODO
	PUBLIC("public", true), // TODO
	RETURN("return"),
	SET("set", true), // TODO
	STATIC("static", true), // TODO
	SUPER("super"), // TODO
	SWITCH("switch"),
	THIS("this"), // TODO
	THROW("throw"), // TODO
	TRUE("true"),
	TRY("try"),
	TYPEOF("typeof"),
	UNDEFINED("undefined"),
	VAR("var"),
	VOID("void", true), // TODO
	WHILE("while"),
	WITH("with", true), // TODO
	YIELD("yield", true), // TODO

	;

	public static final Map<String, KeywordToken> MAP = Arrays.stream(values()).collect(Collectors.toMap(KeywordToken::toString, Function.identity()));

	public final String name;
	public final boolean canBeName;

	KeywordToken(String n, boolean cn) {
		name = n;
		canBeName = cn;
	}

	KeywordToken(String n) {
		this(n, false);
	}

	@Override
	public String toString() {
		return name;
	}

	public Token toLiteralOrSelf() {
		return switch (this) {
			case NULL -> Special.NULL;
			case TRUE -> BooleanToken.TRUE;
			case FALSE -> BooleanToken.FALSE;
			default -> this;
		};
	}

	@Override
	public AstBinary createBinaryAst(PositionedToken pos) {
		if (this == IN) {
			return new AstIn();
		} else if (this == INSTANCEOF) {
			return new AstInstanceOf();
		}

		throw new ParseError(pos, ParseErrorType.INVALID_BINARY.format(name));
	}
}
