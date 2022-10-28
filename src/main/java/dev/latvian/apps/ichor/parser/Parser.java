package dev.latvian.apps.ichor.parser;

import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.parser.expression.AstAssign;
import dev.latvian.apps.ichor.parser.expression.AstCall;
import dev.latvian.apps.ichor.parser.expression.AstExpression;
import dev.latvian.apps.ichor.parser.expression.AstGet;
import dev.latvian.apps.ichor.parser.expression.AstGrouping;
import dev.latvian.apps.ichor.parser.expression.AstLiteral;
import dev.latvian.apps.ichor.parser.expression.AstSet;
import dev.latvian.apps.ichor.parser.expression.AstSuper;
import dev.latvian.apps.ichor.parser.expression.AstThis;
import dev.latvian.apps.ichor.parser.expression.AstVarExpression;
import dev.latvian.apps.ichor.parser.expression.binary.AstAnd;
import dev.latvian.apps.ichor.parser.expression.binary.AstOr;
import dev.latvian.apps.ichor.parser.statement.AstBlock;
import dev.latvian.apps.ichor.parser.statement.AstClass;
import dev.latvian.apps.ichor.parser.statement.AstConstStatement;
import dev.latvian.apps.ichor.parser.statement.AstExpressionStatement;
import dev.latvian.apps.ichor.parser.statement.AstFunction;
import dev.latvian.apps.ichor.parser.statement.AstIf;
import dev.latvian.apps.ichor.parser.statement.AstReturn;
import dev.latvian.apps.ichor.parser.statement.AstStatement;
import dev.latvian.apps.ichor.parser.statement.AstVarStatement;
import dev.latvian.apps.ichor.parser.statement.AstWhile;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StaticToken;
import dev.latvian.apps.ichor.token.SymbolToken;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	private final List<PositionedToken> tokens;
	private int current;

	public Parser(List<PositionedToken> t) {
		tokens = t;
		current = 0;
	}

	public AstBlock parse() {
		var list = new ArrayList<AstStatement>();

		while (!isAtEnd()) {
			list.add(declaration());
		}

		var block = new AstBlock(list);
		block.line = 0;
		block.pos = 0;
		return block;
	}

	private AstExpression expression() {
		return assignment();
	}

	private AstStatement declaration() {
		if (match(KeywordToken.CLASS)) {
			return classDeclaration();
		} else if (match(KeywordToken.FUNCTION)) {
			return function();
		} else if (match(KeywordToken.VAR, KeywordToken.LET, KeywordToken.CONST)) {
			return varDeclaration(previous().token() == KeywordToken.CONST);
		} else {
			return statement();
		}
	}

	private AstStatement classDeclaration() {
		var name = consumeName("Expect class name.");

		AstVarExpression superclass = null;

		if (match(KeywordToken.EXTENDS)) {
			consumeName("Expect superclass name.");
			superclass = new AstVarExpression(previous().asString());
		}

		consume(SymbolToken.LC, "Expect '{' before class body.");

		var methods = new ArrayList<AstFunction>();

		while (!check(SymbolToken.RC) && !isAtEnd()) {
			methods.add(function());
		}

		consume(SymbolToken.RC, "Expect '}' after class body.");
		return new AstClass(name.asString(), superclass, methods.toArray(new AstFunction[0]));
	}

	private AstStatement statement() {
		if (match(KeywordToken.FOR)) {
			return forStatement();
		} else if (match(KeywordToken.IF)) {
			return ifStatement();
		} else if (match(KeywordToken.RETURN)) {
			return returnStatement();
		} else if (match(KeywordToken.WHILE)) {
			return whileStatement();
		} else if (check(SymbolToken.LC)) {
			return block();
		}

		return expressionStatement();
	}

	private AstStatement forStatement() {
		consume(SymbolToken.LP, "Expect '(' after 'for'.");

		AstStatement initializer;
		if (match(SymbolToken.SEMI)) {
			initializer = null;
		} else if (match(KeywordToken.VAR, KeywordToken.LET, KeywordToken.CONST)) {
			initializer = varDeclaration(previous().token() == KeywordToken.CONST);
		} else {
			initializer = expressionStatement();
		}

		AstExpression condition = null;
		if (!check(SymbolToken.SEMI)) {
			condition = expression();
		}
		consume(SymbolToken.SEMI, "Expect ';' after loop condition.");

		AstExpression increment = null;
		if (!check(SymbolToken.RP)) {
			increment = expression();
		}
		consume(SymbolToken.RP, "Expect ')' after for clauses.");
		var body = statement();

		if (increment != null) {
			body = new AstBlock(body, new AstExpressionStatement(increment));
		}

		if (condition == null) {
			condition = new AstLiteral(true);
		}
		body = new AstWhile(condition, new AstBlock(body));

		if (initializer != null) {
			body = new AstBlock(initializer, body);
		}

		return body;
	}

	private AstIf ifStatement() {
		consume(SymbolToken.LP, "Expect '(' after 'if'.");
		var condition = expression();
		consume(SymbolToken.RP, "Expect ')' after if condition."); // [parens]

		var ifTrue = block();
		AstStatement ifFalse = null;

		if (match(KeywordToken.ELSE)) {
			ifFalse = block();
		}

		return new AstIf(condition, ifTrue, ifFalse);
	}

	private AstStatement returnStatement() {
		var keyword = previous();
		AstExpression value = null;
		if (!check(SymbolToken.SEMI)) {
			value = expression();
		}

		consume(SymbolToken.SEMI, "Expect ';' after return value.");
		return new AstReturn(value).pos(keyword);
	}

	private AstStatement varDeclaration(boolean isConst) {
		var name = consumeName("Expect variable name.");

		AstExpression initializer = null;

		if (match(SymbolToken.SET)) {
			initializer = expression();
		}

		consume(SymbolToken.SEMI, "Expect ';' after variable declaration.");
		return isConst ? new AstConstStatement(name.asString(), initializer) : new AstVarStatement(name.asString(), initializer);
	}

	private AstStatement whileStatement() {
		consume(SymbolToken.LP, "Expect '(' after 'while'.");
		var condition = expression();
		consume(SymbolToken.RP, "Expect ')' after condition.");
		var body = block();

		return new AstWhile(condition, body);
	}

	private AstStatement expressionStatement() {
		var expr = expression();
		match(SymbolToken.SEMI);
		return new AstExpressionStatement(expr);
	}

	private AstFunction function() {
		var name = consumeName("Expect function name.");
		consume(SymbolToken.LP, "Expect '(' after function name.");
		var parameters = new ArrayList<String>();
		if (!check(SymbolToken.RP)) {
			do {
				if (parameters.size() >= 255) {
					error(peek(), "Can't have more than 255 parameters.");
				}

				parameters.add(consumeName("Expect parameter name.").asString());
			} while (match(SymbolToken.COMMA));
		}
		consume(SymbolToken.RP, "Expect ')' after parameters.");
		var body = block();
		return new AstFunction(name.asString(), parameters.toArray(new String[0]), body);
	}

	private AstStatement block() {
		if (match(SymbolToken.LC)) {
			var firstPos = consume(SymbolToken.LC, "Expect '{' before block.");

			var statements = new ArrayList<AstStatement>();

			while (!check(SymbolToken.RC) && !isAtEnd()) {
				statements.add(declaration());
			}

			consume(SymbolToken.RC, "Expect '}' after block.");
			var block = new AstBlock(statements);
			block.pos(firstPos);
			return block;
		} else {
			return statement();
		}
	}

	private AstExpression assignment() {
		var expr = or();

		if (match(SymbolToken.SET)) {
			var equals = previous();
			var value = assignment();

			if (expr instanceof AstVarExpression name) {
				return new AstAssign(name.name, value);
			} else if (expr instanceof AstGet get) {
				return new AstSet(get.from, get.name, value);
			}

			error(equals, "Invalid assignment target."); // [no-throw]
		}

		return expr;
	}

	private AstExpression or() {
		var expr = and();

		while (match(SymbolToken.OR)) {
			var right = and();
			expr = new AstOr(expr, right);
		}

		return expr;
	}

	private AstExpression and() {
		var expr = equality();

		while (match(SymbolToken.AND)) {
			var right = equality();
			expr = new AstAnd(expr, right);
		}

		return expr;
	}

	private AstExpression equality() {
		var expr = comparison();

		while (match(SymbolToken.NEQ, SymbolToken.EQ, SymbolToken.SNEQ, SymbolToken.SEQ)) {
			var operator = previous();
			var right = comparison();
			expr = ((SymbolToken) operator.token()).astBinary.create(expr, right);
		}

		return expr;
	}

	private AstExpression comparison() {
		var expr = term();

		while (match(SymbolToken.GT, SymbolToken.GTE, SymbolToken.LT, SymbolToken.LTE)) {
			var operator = previous();
			var right = term();
			expr = ((SymbolToken) operator.token()).astBinary.create(expr, right);
		}

		return expr;
	}

	private AstExpression term() {
		var expr = factor();

		while (match(SymbolToken.ADD, SymbolToken.SUB)) {
			var operator = previous();
			var right = factor();
			expr = ((SymbolToken) operator.token()).astBinary.create(expr, right);
		}

		return expr;
	}

	private AstExpression factor() {
		var expr = unary();

		while (match(SymbolToken.DIV, SymbolToken.MUL)) {
			var operator = previous();
			var right = unary();
			expr = ((SymbolToken) operator.token()).astBinary.create(expr, right);
		}

		return expr;
	}

	private AstExpression unary() {
		if (match(SymbolToken.NOT, SymbolToken.SUB, SymbolToken.BNOT, SymbolToken.ADD1, SymbolToken.SUB1)) {
			var operator = previous();
			var right = unary();
			return ((SymbolToken) operator.token()).astUnary.create(right);
		}

		return call();
	}

	private AstExpression finishCall(AstExpression callee) {
		var arguments = new ArrayList<AstExpression>();
		if (!check(SymbolToken.RP)) {
			do {
				if (arguments.size() >= 255) {
					error(peek(), "Can't have more than 255 arguments.");
				}
				arguments.add(expression());
			} while (match(SymbolToken.COMMA));
		}

		consume(SymbolToken.RP, "Expect ')' after arguments.");

		return new AstCall(callee, arguments.toArray(AstExpression.EMPTY_EXPRESSION_ARRAY));
	}

	private AstExpression call() {
		var expr = primary();

		while (true) {
			if (match(SymbolToken.LP)) {
				expr = finishCall(expr);
			} else if (match(SymbolToken.DOT)) {
				var name = consumeName("Expect property name after '.'.");
				expr = new AstGet(expr, name.asString());
			} else {
				break;
			}
		}

		return expr;
	}

	private AstExpression primary() {
		if (!isAtEnd() && peek().token().hasValue()) {
			var token = advance();
			return new AstLiteral(token.token().getValue()).pos(token);
		}

		if (match(KeywordToken.SUPER)) {
			var keyword = previous();
			consume(SymbolToken.DOT, "Expect '.' after 'super'.");
			var method = consumeName("Expect superclass method name.");
			return new AstSuper(method.asString()).pos(keyword);
		}

		if (match(KeywordToken.THIS)) {
			return new AstThis().pos(previous());
		}

		if (!isAtEnd() && peek().token() instanceof NameToken) {
			advance();
			return new AstVarExpression(previous().asString());
		}

		if (match(SymbolToken.LP)) {
			var expr = expression();
			consume(SymbolToken.RP, "Expect ')' after expression.");
			return new AstGrouping(expr);
		}

		throw error(peek(), "Expect expression.");
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

	private boolean match(StaticToken... types) {
		for (var type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}

		return false;
	}

	private PositionedToken consumeName(String message) {
		if (!isAtEnd() && peek().token() instanceof NameToken) {
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
		return !isAtEnd() && peek().token() == type;
	}

	private PositionedToken advance() {
		if (!isAtEnd()) {
			current++;
		}
		return previous();
	}

	private boolean isAtEnd() {
		return current >= tokens.size() || peek().token() == SymbolToken.EOF;
	}

	private PositionedToken peek() {
		return tokens.get(current);
	}

	private PositionedToken previous() {
		return tokens.get(current - 1);
	}
}
