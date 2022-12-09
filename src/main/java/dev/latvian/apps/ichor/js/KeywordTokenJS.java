package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.token.BooleanToken;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.Token;

import java.util.HashMap;
import java.util.Map;

public interface KeywordTokenJS {
	Map<String, Token> CACHE = new HashMap<>();

	private static Token cache(String name, Token token) {
		CACHE.put(name, token);
		return token;
	}

	private static Token cache(KeywordToken token) {
		return cache(token.name, token);
	}

	private static Token cache(String name, boolean canBeName) {
		return cache(name, new KeywordToken(name, canBeName));
	}

	private static Token cache(String name) {
		return cache(name, false);
	}

	Token NULL = cache("null", Special.NULL);
	Token UNDEFINED = cache("undefined", Special.UNDEFINED);
	Token TRUE = cache("true", BooleanToken.TRUE);
	Token FALSE = cache("false", BooleanToken.FALSE);
	Token IN = cache(new InKeywordJS());
	Token INSTANCEOF = cache(new InstanceofKeywordJS());
	// cacheKeyword(new KeywordToken("arguments")); // TODO

	DeclaringToken LET = (DeclaringToken) cache(new DeclaringToken("let", false, false));
	DeclaringToken CONST = (DeclaringToken) cache(new DeclaringToken("const", false, true));
	DeclaringToken VAR = (DeclaringToken) cache(new DeclaringToken("var", false, false));

	Token AS = cache("as"); // TODO
	Token ASYNC = cache("async"); // TODO
	Token AWAIT = cache("await"); // TODO
	Token BREAK = cache("break");
	Token CASE = cache("case");
	Token CATCH = cache("catch");
	Token CLASS = cache("class", true);
	Token CONTINUE = cache("continue");
	Token DEBUGGER = cache("debugger", true);
	Token DEFAULT = cache("default");
	Token DELETE = cache("delete");
	Token DO = cache("do");
	Token ELSE = cache("else");
	Token ENUM = cache("enum", true); // TODO
	Token EXPORT = cache("export", true); // TODO
	Token EXTENDS = cache("extends");
	Token FINALLY = cache("finally");
	Token FOR = cache("for");
	Token FROM = cache("from", true);// TODO
	Token FUNCTION = cache("function");
	Token GET = cache("get", true); // TODO
	Token IF = cache("if");
	Token IMPORT = cache("import", true); // TODO
	Token INTERFACE = cache("interface", true);
	Token NEW = cache("new");
	Token OF = cache("of", true);
	Token PACKAGE = cache("package", true); // TODO
	Token PRIVATE = cache("private", true); // TODO
	Token PROTECTED = cache("protected", true); // TODO
	Token PUBLIC = cache("public", true); // TODO
	Token RETURN = cache("return");
	Token SET = cache("set", true); // TODO
	Token STATIC = cache("static", true); // TODO
	Token SUPER = cache("super"); // TODO
	Token SWITCH = cache("switch");
	Token THIS = cache("this"); // TODO
	Token THROW = cache("throw");
	Token TRY = cache("try");
	Token TYPEOF = cache("typeof");
	Token VOID = cache("void", true); // TODO
	Token WHILE = cache("while");
	Token WITH = cache("with", true); // TODO
	Token YIELD = cache("yield", true); // TODO
}
