package dev.latvian.apps.ichor.parser;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.parser.expression.AstCall;
import dev.latvian.apps.ichor.parser.expression.AstGet;
import dev.latvian.apps.ichor.parser.expression.AstGetBase;
import dev.latvian.apps.ichor.parser.expression.AstGetByName;
import dev.latvian.apps.ichor.parser.expression.AstGetByNameOptional;
import dev.latvian.apps.ichor.parser.expression.AstGrouping;
import dev.latvian.apps.ichor.parser.expression.AstLiteral;
import dev.latvian.apps.ichor.parser.expression.AstSet;
import dev.latvian.apps.ichor.parser.expression.AstSuper;
import dev.latvian.apps.ichor.parser.expression.AstThis;
import dev.latvian.apps.ichor.parser.expression.binary.AstBinary;
import dev.latvian.apps.ichor.parser.expression.unary.AstAdd1R;
import dev.latvian.apps.ichor.parser.expression.unary.AstSub1R;
import dev.latvian.apps.ichor.parser.expression.unary.AstUnary;
import dev.latvian.apps.ichor.parser.statement.AstBlock;
import dev.latvian.apps.ichor.parser.statement.AstClass;
import dev.latvian.apps.ichor.parser.statement.AstConstStatement;
import dev.latvian.apps.ichor.parser.statement.AstExpressionStatement;
import dev.latvian.apps.ichor.parser.statement.AstFunction;
import dev.latvian.apps.ichor.parser.statement.AstIf;
import dev.latvian.apps.ichor.parser.statement.AstInterpretableGroup;
import dev.latvian.apps.ichor.parser.statement.AstReturn;
import dev.latvian.apps.ichor.parser.statement.AstVarStatement;
import dev.latvian.apps.ichor.parser.statement.AstWhile;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StaticToken;
import dev.latvian.apps.ichor.token.SymbolToken;
import dev.latvian.apps.ichor.util.EvaluableFactory;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	private static final StaticToken[] VAR_TOKENS = {KeywordToken.VAR, KeywordToken.LET, KeywordToken.CONST};
	private static final StaticToken[] NC_TOKENS = {SymbolToken.NC};
	private static final StaticToken[] AND_TOKENS = {SymbolToken.AND};
	private static final StaticToken[] OR_TOKENS = {SymbolToken.OR};
	private static final StaticToken[] EQUALITY_TOKENS = {SymbolToken.EQ, SymbolToken.NEQ, SymbolToken.SEQ, SymbolToken.SNEQ};
	private static final StaticToken[] COMPARISON_TOKENS = {SymbolToken.LT, SymbolToken.GT, SymbolToken.LTE, SymbolToken.GTE};
	private static final StaticToken[] ADDITIVE_TOKENS = {SymbolToken.ADD, SymbolToken.SUB};
	private static final StaticToken[] MULTIPLICATIVE_TOKENS = {SymbolToken.MUL, SymbolToken.DIV, SymbolToken.MOD};
	private static final StaticToken[] UNARY_TOKENS = {SymbolToken.NOT, SymbolToken.ADD, SymbolToken.SUB, SymbolToken.BNOT, SymbolToken.ADD1, SymbolToken.SUB1};

	private final List<PositionedToken> tokens;
	private int current;

	public Parser(List<PositionedToken> t) {
		tokens = t;
		current = 0;
	}

	public Interpretable parse() {
		var list = new ArrayList<Interpretable>();

		while (canAdvance()) {
			list.add(declaration());
		}

		return AstInterpretableGroup.optimized(list);
	}

	private Evaluable expression() {
		return assignment();
	}

	private Interpretable declaration() {
		if (match(KeywordToken.CLASS)) {
			return classDeclaration();
		} else if (match(KeywordToken.FUNCTION)) {
			return function();
		} else if (match(VAR_TOKENS)) {
			return varDeclaration(previous().token() == KeywordToken.CONST);
		} else {
			return statement();
		}
	}

	private Interpretable classDeclaration() {
		var name = consumeName("Expected class name");

		AstGet superclass = null;

		if (match(KeywordToken.EXTENDS)) {
			consumeName("Expected superclass name");
			superclass = new AstGet(previous().asString());
		}

		consume(SymbolToken.LC, "Expected '{' before class body");

		var methods = new ArrayList<AstFunction>();

		while (!check(SymbolToken.RC) && canAdvance()) {
			methods.add(function());
		}

		consume(SymbolToken.RC, "Expected '}' after class body");
		return new AstClass(name.asString(), superclass, methods.toArray(new AstFunction[0]));
	}

	private Interpretable statement() {
		if (match(KeywordToken.FOR)) {
			return forStatement();
		} else if (match(KeywordToken.IF)) {
			return ifStatement();
		} else if (match(KeywordToken.RETURN)) {
			return returnStatement();
		} else if (match(KeywordToken.WHILE)) {
			return whileStatement();
		} else if (check(SymbolToken.LC)) {
			return block(true);
		}

		return expressionStatement();
	}

	private Interpretable forStatement() {
		consume(SymbolToken.LP, "Expected '(' after 'for'");

		Interpretable initializer;
		if (match(SymbolToken.SEMI)) {
			initializer = null;
		} else if (match(VAR_TOKENS)) {
			initializer = varDeclaration(previous().token() == KeywordToken.CONST);
		} else {
			initializer = expressionStatement();
		}

		Evaluable condition = null;
		if (!check(SymbolToken.SEMI)) {
			condition = expression();
		}
		consume(SymbolToken.SEMI, "Expected ';' after loop condition");

		Evaluable increment = null;
		if (!check(SymbolToken.RP)) {
			increment = expression();
		}
		consume(SymbolToken.RP, "Expected ')' after for clauses");
		var body = statement();

		if (increment != null) {
			body = new AstInterpretableGroup(body, new AstExpressionStatement(increment));
		}

		if (condition == null) {
			condition = new AstLiteral(true);
		}
		body = new AstWhile(condition, new AstInterpretableGroup(body));

		if (initializer != null) {
			body = new AstInterpretableGroup(initializer, body);
		}

		return body;
	}

	private Interpretable ifStatement() {
		consume(SymbolToken.LP, "Expected '(' after 'if'");
		var condition = expression();
		consume(SymbolToken.RP, "Expected ')' after if condition"); // [parens]

		var ifTrue = block(false);
		Interpretable ifFalse = null;

		if (match(KeywordToken.ELSE)) {
			ifFalse = block(false);
		}

		return new AstIf(condition, ifTrue, ifFalse);
	}

	private Interpretable returnStatement() {
		var keyword = previous();
		Object value = null;

		if (!check(SymbolToken.SEMI)) {
			value = expression().optimize();
		}

		consume(SymbolToken.SEMI, "Expected ';' after return value");
		return new AstReturn(value).pos(keyword);
	}

	private Interpretable varDeclaration(boolean isConst) {
		var list = new ArrayList<Interpretable>(1);

		do {
			var name = consumeName("Expected variable name");

			Object initializer = null;

			if (match(SymbolToken.SET)) {
				initializer = expression().optimize();
			} else if (isConst) {
				error(name, "const must have an initializer!");
			}

			list.add((isConst ? new AstConstStatement(name.asString(), initializer) : new AstVarStatement(name.asString(), initializer)).pos(name));
		}
		while (match(SymbolToken.COMMA));
		consume(SymbolToken.SEMI, "Expected ';' after variable declaration");
		return AstInterpretableGroup.optimized(list);
	}

	private Interpretable whileStatement() {
		consume(SymbolToken.LP, "Expected '(' after 'while'");
		var condition = expression();
		consume(SymbolToken.RP, "Expected ')' after condition");
		var body = block(false);

		return new AstWhile(condition, body);
	}

	private Interpretable expressionStatement() {
		var expr = expression();
		match(SymbolToken.SEMI);
		return new AstExpressionStatement(expr);
	}

	private AstFunction function() {
		var name = consumeName("Expected function name");
		return function(name.asString());
	}

	private AstFunction function(String name) {
		consume(SymbolToken.LP, "Expected '(' after function name");
		var parameters = new ArrayList<String>();
		if (!check(SymbolToken.RP)) {
			do {
				parameters.add(consumeName("Expected parameter name").asString());
			} while (match(SymbolToken.COMMA));
		}
		consume(SymbolToken.RP, "Expected ')' after parameters");
		var body = block(true);
		return new AstFunction(name, parameters.toArray(EMPTY_STRING_ARRAY), body);
	}

	private Interpretable block(boolean isBlock) {
		if (check(SymbolToken.LC)) {
			var firstPos = consume(SymbolToken.LC, "Expected '{' before block, got " + peek() + " instead");

			var statements = new ArrayList<Interpretable>();

			while (!check(SymbolToken.RC) && canAdvance()) {
				statements.add(declaration());
			}

			consume(SymbolToken.RC, "Expected '}' after block");
			var block = isBlock ? new AstBlock(statements) : AstInterpretableGroup.optimized(statements);

			if (block instanceof Ast ast) {
				ast.pos(firstPos);
			}

			return block;
		} else {
			return statement();
		}
	}

	private Evaluable assignment() {
		var expr = nc();

		if (match(SymbolToken.SET)) {
			var operator = previous();
			var value = assignment();

			if (expr instanceof AstGetBase get) {
				return new AstSet(get, value.optimize());
			}

			error(operator, "Invalid assignment target."); // [no-throw]
		} else if (match(SymbolToken.ADD1)) {
			var ast = new AstAdd1R();
			ast.node = expr;
			ast.pos(previous());
			return ast;
		} else if (match(SymbolToken.SUB1)) {
			var ast = new AstSub1R();
			ast.node = expr;
			ast.pos(previous());
			return ast;
		}

		return expr;
	}

	private Evaluable binary(StaticToken[] token, EvaluableFactory next) {
		var expr = next.create();

		while (match(token)) {
			var operator = previous();
			var right = comparison();
			var ast = (AstBinary) ((SymbolToken) operator.token()).astBinary.create();
			ast.left = expr.optimize();
			ast.right = right.optimize();
			ast.pos(operator);
			expr = ast;
		}

		return expr;
	}

	public Evaluable nc() {
		return binary(NC_TOKENS, this::or);
	}

	public Evaluable or() {
		return binary(OR_TOKENS, this::and);
	}

	public Evaluable and() {
		return binary(AND_TOKENS, this::equality);
	}

	public Evaluable equality() {
		return binary(EQUALITY_TOKENS, this::comparison);
	}

	public Evaluable comparison() {
		return binary(COMPARISON_TOKENS, this::additive);
	}

	public Evaluable additive() {
		return binary(ADDITIVE_TOKENS, this::multiplicative);
	}

	public Evaluable multiplicative() {
		return binary(MULTIPLICATIVE_TOKENS, this::unary);
	}

	public Evaluable unary() {
		if (match(UNARY_TOKENS)) {
			var operator = previous();
			var right = unary();
			var ast = (AstUnary) ((SymbolToken) operator.token()).astUnary.create();
			ast.node = right.optimize();
			ast.pos(operator);
			return ast;
		}

		return call();
	}

	private Evaluable call() {
		var expr = primary();

		while (true) {
			if (match(SymbolToken.LP)) {
				var lp = previous();
				var arguments = new ArrayList<>(2);
				if (!check(SymbolToken.RP)) {
					do {
						arguments.add(expression().optimize());
					} while (match(SymbolToken.COMMA));
				}

				consume(SymbolToken.RP, "Expected ')' after arguments");
				expr = new AstCall(expr, arguments.toArray(EMPTY_OBJECT_ARRAY)).pos(lp);
			} else if (match(SymbolToken.DOT)) {
				var name = consumeName("Expected property name after '.'");
				expr = new AstGetByName(expr, name.asString()).pos(name);
			} else if (match(SymbolToken.OC)) {
				var name = consumeName("Expected property name after '?.'");
				expr = new AstGetByNameOptional(expr, name.asString()).pos(name);
			} else {
				break;
			}
		}

		return expr;
	}

	private Evaluable primary() {
		if (canAdvance() && peek().token().hasValue()) {
			var token = advance();
			return new AstLiteral(token.token().getValue()).pos(token);
		}

		if (match(KeywordToken.SUPER)) {
			var keyword = previous();
			consume(SymbolToken.DOT, "Expected '.' after 'super'");
			var method = consumeName("Expected superclass method name");
			return new AstSuper(method.asString()).pos(keyword);
		}

		if (match(KeywordToken.THIS)) {
			return new AstThis().pos(previous());
		}

		if (canAdvance() && peek().token() instanceof NameToken) {
			advance();
			return new AstGet(previous().asString()).pos(previous());
		}

		if (match(SymbolToken.LP)) {
			var lp = previous();
			var expr = expression();
			consume(SymbolToken.RP, "Expected ')' after expression");
			return new AstGrouping(expr).pos(lp);
		}

		throw error(peek(), "Expected expression");
	}

	//

	private ParseError error(PositionedToken token, String message) {
		throw new ParseError(token, message);
	}

	private boolean match(StaticToken token) {
		if (check(token)) {
			advance();
			return true;
		}

		return false;
	}

	private boolean match(StaticToken[] types) {
		if (types.length == 1) {
			if (check(types[0])) {
				advance();
				return true;
			}
		} else {
			for (var type : types) {
				if (check(type)) {
					advance();
					return true;
				}
			}
		}

		return false;
	}

	private PositionedToken consumeName(String message) {
		if (canAdvance() && peek().token() instanceof NameToken) {
			return advance();
		}

		throw new ParseError(peek(), message);
	}

	private PositionedToken consume(StaticToken type, String message) {
		if (check(type)) {
			return advance();
		}

		throw new ParseError(peek(), message);
	}

	private boolean check(StaticToken type) {
		return canAdvance() && peek().token() == type;
	}

	private PositionedToken advance() {
		if (canAdvance()) {
			current++;
		}
		return previous();
	}

	private boolean canAdvance() {
		return current < tokens.size() && peek().token() != SymbolToken.EOF;
	}

	private PositionedToken peek() {
		return tokens.get(current);
	}

	private PositionedToken previous() {
		return tokens.get(current - 1);
	}
}
