package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.slot.Slot;
import org.jetbrains.annotations.Nullable;

public interface Keyword {
	private static KeywordToken create(String name) {
		return new KeywordToken(name);
	}

	Token ARGUMENTS = create("arguments").identifier();
	Token AS = create("as").literalPre(); // TODO
	Token ASYNC = create("async");
	Token AWAIT = create("await").literalPre();
	Token BREAK = create("break").literalPre().insertToken(Symbol.SEMI);
	Token CASE = create("case").literalPre();
	Token CATCH = create("catch");
	Token CLASS = create("class").identifier();
	DeclaringToken CONST = new DeclaringToken("const", Slot.IMMUTABLE);
	Token CONTINUE = create("continue").literalPre().insertToken(Symbol.SEMI);
	Token DEBUGGER = create("debugger").identifier();
	Token DEFAULT = create("default").identifier();
	Token DELETE = create("delete").literalPre();
	Token DO = create("do");
	Token ELSE = create("else");
	Token ENUM = create("enum").identifier(); // TODO
	Token EXPORT = create("export").identifier(); // TODO
	Token EXTENDS = create("extends");
	Token FINALLY = create("finally");
	Token FOR = create("for");
	Token FROM = create("from").literalPre().identifier();// TODO
	Token FUNCTION = create("function");
	Token GET = create("get").identifier(); // TODO
	Token IF = create("if").literalPre();
	Token IMPORT = create("import").identifier(); // TODO
	Token IN = new InKeywordToken();
	Token INSTANCEOF = new InstanceofKeywordToken();
	Token INTERFACE = create("interface").identifier();
	DeclaringToken LET = new DeclaringToken("let", Slot.DEFAULT);
	Token NEW = create("new").literalPre();
	Token OF = create("of").literalPre().identifier();
	Token PACKAGE = create("package").identifier(); // TODO
	Token PRIVATE = create("private").identifier(); // TODO
	Token PROTECTED = create("protected").identifier(); // TODO
	Token PUBLIC = create("public").identifier(); // TODO
	Token RETURN = create("return").literalPre().insertToken(Symbol.SEMI);
	Token SET = create("set").identifier(); // TODO
	Token STATIC = create("static").identifier(); // TODO
	Token SUPER = create("super").identifier();
	Token SWITCH = create("switch");
	Token THIS = create("this").identifier();
	Token THROW = create("throw").literalPre();
	Token TRY = create("try");
	Token TYPEOF = create("typeof").literalPre();
	DeclaringToken VAR = new DeclaringToken("var", Slot.DEFAULT);
	Token VOID = create("void").literalPre().identifier(); // TODO
	Token WHILE = create("while");
	Token YIELD = create("yield").literalPre().identifier(); // TODO

	@Nullable
	static Object get(String name) {
		return switch (name) {
			case "null" -> Special.NULL;
			case "undefined" -> Special.UNDEFINED;
			case "true" -> Boolean.TRUE;
			case "false" -> Boolean.FALSE;
			case "arguments" -> ARGUMENTS;
			case "as" -> AS;
			case "async" -> ASYNC;
			case "await" -> AWAIT;
			case "break" -> BREAK;
			case "case" -> CASE;
			case "catch" -> CATCH;
			case "class" -> CLASS;
			case "const" -> CONST;
			case "continue" -> CONTINUE;
			case "debugger" -> DEBUGGER;
			case "default" -> DEFAULT;
			case "delete" -> DELETE;
			case "do" -> DO;
			case "else" -> ELSE;
			case "enum" -> ENUM;
			case "export" -> EXPORT;
			case "extends" -> EXTENDS;
			case "finally" -> FINALLY;
			case "for" -> FOR;
			case "from" -> FROM;
			case "function" -> FUNCTION;
			case "get" -> GET;
			case "if" -> IF;
			case "import" -> IMPORT;
			case "in" -> IN;
			case "instanceof" -> INSTANCEOF;
			case "interface" -> INTERFACE;
			case "let" -> LET;
			case "new" -> NEW;
			case "of" -> OF;
			case "package" -> PACKAGE;
			case "private" -> PRIVATE;
			case "protected" -> PROTECTED;
			case "public" -> PUBLIC;
			case "return" -> RETURN;
			case "set" -> SET;
			case "static" -> STATIC;
			case "super" -> SUPER;
			case "switch" -> SWITCH;
			case "this" -> THIS;
			case "throw" -> THROW;
			case "try" -> TRY;
			case "typeof" -> TYPEOF;
			case "var" -> VAR;
			case "void" -> VOID;
			case "while" -> WHILE;
			case "yield" -> YIELD;
			default -> null;
		};
	}
}
