package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Special;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum KeywordToken implements StaticToken {
	ARGUMENTS("arguments"),
	AS("as"),
	ASYNC("async"),
	AWAIT("await"),
	BREAK("break"),
	CASE("case"),
	CATCH("catch"),
	CLASS("class"),
	CONST("const"),
	CONTINUE("continue"),
	DEBUGGER("debugger"),
	DEFAULT("default"),
	DELETE("delete"),
	DO("do"),
	ELSE("else"),
	ENUM("enum"),
	EVAL("eval"),
	EXPORT("export"),
	EXTENDS("extends"),
	FALSE("false"),
	FINALLY("finally"),
	FOR("for"),
	FROM("from"),
	FUNCTION("function"),
	GET("get"),
	IF("if"),
	IMPORT("import"),
	IN("in"),
	INSTANCEOF("instanceof"),
	INTERFACE("interface"),
	LET("let"),
	NEW("new"),
	NULL("null"),
	OF("of"),
	PACKAGE("package"),
	PRIVATE("private"),
	PROTECTED("protected"),
	PUBLIC("public"),
	RETURN("return"),
	SET("set"),
	STATIC("static"),
	SUPER("super"),
	SWITCH("switch"),
	THIS("this"),
	THROW("throw"),
	TRUE("true"),
	TRY("try"),
	TYPEOF("typeof"),
	UNDEFINED("undefined"),
	VAR("var"),
	VOID("void"),
	WHILE("while"),
	WITH("with"),
	YIELD("yield"),

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
}
