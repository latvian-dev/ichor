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
	CLASS("class"),
	CONST("const"),
	CONTINUE("continue"),
	DEBUGGER("debugger"), // TODO
	DEFAULT("default"),
	DELETE("delete"),
	DO("do"), // TODO
	ELSE("else"),
	ENUM("enum"), // TODO
	EVAL("eval"), // TODO
	EXPORT("export"), // TODO
	EXTENDS("extends"),
	FALSE("false"),
	FINALLY("finally"),
	FOR("for"),
	FROM("from"), // TODO
	FUNCTION("function"),
	GET("get"), // TODO
	IF("if"),
	IMPORT("import"), // TODO
	IN("in"),
	INSTANCEOF("instanceof"),
	INTERFACE("interface"),
	LET("let"),
	NEW("new"),
	NULL("null"),
	OF("of"),
	PACKAGE("package"), // TODO
	PRIVATE("private"), // TODO
	PROTECTED("protected"), // TODO
	PUBLIC("public"), // TODO
	RETURN("return"),
	SET("set"), // TODO
	STATIC("static"), // TODO
	SUPER("super"), // TODO
	SWITCH("switch"),
	THIS("this"), // TODO
	THROW("throw"), // TODO
	TRUE("true"),
	TRY("try"),
	TYPEOF("typeof"),
	UNDEFINED("undefined"),
	VAR("var"),
	VOID("void"), // TODO
	WHILE("while"),
	WITH("with"), // TODO
	YIELD("yield"), // TODO

	;

	public static final Map<String, KeywordToken> MAP = Arrays.stream(values()).collect(Collectors.toMap(KeywordToken::toString, Function.identity()));

	public final String name;

	KeywordToken(String n) {
		name = n;
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
