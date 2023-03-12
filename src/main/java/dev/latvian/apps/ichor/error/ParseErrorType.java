package dev.latvian.apps.ichor.error;

public enum ParseErrorType implements ParseErrorMessage {
	INVALID_TARGET("Invalid assignment target %s"),
	INVALID_BINARY("'%s' is not a binary operator"),
	INVALID_UNARY("'%s' is not a unary operator"),
	EXP_TOKEN("Expected '%s' token"),
	UNEXP_TOKEN("Unexpected token '%s'"),
	EXP_EXPR("Expected expression, got '%s' instead"),
	EXP_INIT("Expected initializer"),
	EXP_FUNC_NAME("Expected function name"),
	EXP_CLASS_NAME("Expected class name"),
	EXP_PARAM_NAME("Expected parameter name"),
	EXP_TYPE_NAME("Expected type name"),
	EXP_VAR_NAME("Expected variable name"),
	EXP_LC_BLOCK("Expected '{' before block"),
	EXP_RC_BLOCK("Expected '}' after block"),
	EXP_RC_OBJECT("Expected '}' after object"),
	EXP_RC_TEMPLATE_LITERAL("Expected '}' after template literal"),
	EXP_COL_OBJECT("Expected ':' after name"),
	EXP_COL_CASE("Expected ':' after case"),
	EXP_RS_ARRAY("Expected ']' after array"),
	EXP_RP_EXPR("Expected ')' after expression"),
	EXP_LP_ARGS("Expected '(' before arguments"),
	EXP_RP_ARGS("Expected ')' after arguments"),
	EXP_LC_CLASS("Expected '{' before class body"),
	EXP_RC_CLASS("Expected '}' after class body"),
	EXP_LP_NEW_CLASS("Expected '(' after new %s"),
	EXP_LP_IF_COND("Expected '(' before if condition"),
	EXP_RP_IF_COND("Expected ')' after if condition"),
	EXP_LP_WHILE_COND("Expected '(' before while condition"),
	EXP_RP_WHILE_COND("Expected ')' after while condition"),
	EXP_LP_FOR("Expected '(' after for statement"),
	EXP_GT_TYPE("Expected '>' after type"),
	EXP_SEMI_FOR_INIT("Expected ';' after for statement initializer"),
	EXP_SEMI_FOR_COND("Expected ';' after for statement loop condition"),
	EXP_RP_FOR("Expected ')' after for statement clauses"),
	EXP_RS_KEY("Expected ']' after key"),
	EXP_ARROW("Expected '=>' after arrow function parameters"),
	EXP_TERNARY_COL("Expected ':' in ternary expression"),
	EXP_NAME_DOT("Expected property name after '.', got '%s' instead"),
	EXP_NAME_OC("Expected property name after '?.'"),
	EXP_CASE("Expected 'case' or 'default'"),
	METHOD_EXISTS("Method already defined"),
	CONSTRUCTOR_EXISTS("Constructor already defined"),
	EXPR_NOT_CALLABLE("Expression %s is not callable (%s)"),
	MULTIPLE_VARARGS("Function can only have one vararg parameter"),
	EXP_LABELLED_STATEMENT("Expected statement that supports labels"),
	UNKNOWN_LABEL("Label '%s' for %s statement not found"),
	EXIT_NOT_SUPPORTED("%s statement not supported here"),
	UNREACHABLE_STATEMENT("Unreachable statement"),
	DESTRUCT_NOT_SUPPORTED("Destructured param not supported here yet"),
	INVALID_THIS("Missing identifier after 'this'"),
	INVALID_SUPER("Missing identifier after 'super'"),

	;

	public record Formatted(ParseErrorType t, String msg) implements ParseErrorMessage {

		@Override
		public String getMessage() {
			return msg;
		}
	}

	public final String message;

	ParseErrorType(String m) {
		message = m;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public ParseErrorMessage format(Object... args) {
		if (args.length == 0) {
			return this;
		}

		return new Formatted(this, message.formatted(args));
	}
}
