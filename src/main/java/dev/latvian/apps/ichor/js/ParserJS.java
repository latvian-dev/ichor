package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.ast.CallableAst;
import dev.latvian.apps.ichor.ast.expression.AstArray;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.ast.expression.AstGetBase;
import dev.latvian.apps.ichor.ast.expression.AstGetByEvaluable;
import dev.latvian.apps.ichor.ast.expression.AstGetByIndex;
import dev.latvian.apps.ichor.ast.expression.AstGetByName;
import dev.latvian.apps.ichor.ast.expression.AstGetByNameOptional;
import dev.latvian.apps.ichor.ast.expression.AstGetFrom;
import dev.latvian.apps.ichor.ast.expression.AstGetScopeMember;
import dev.latvian.apps.ichor.ast.expression.AstGrouping;
import dev.latvian.apps.ichor.ast.expression.AstLiteral;
import dev.latvian.apps.ichor.ast.expression.AstNew;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.ast.expression.AstSet;
import dev.latvian.apps.ichor.ast.expression.AstSpread;
import dev.latvian.apps.ichor.ast.expression.AstSuper;
import dev.latvian.apps.ichor.ast.expression.AstThis;
import dev.latvian.apps.ichor.ast.expression.binary.AstBinary;
import dev.latvian.apps.ichor.ast.expression.unary.AstAdd1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstSub1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstUnary;
import dev.latvian.apps.ichor.ast.statement.AstBlock;
import dev.latvian.apps.ichor.ast.statement.AstBreak;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.ast.statement.AstConstStatement;
import dev.latvian.apps.ichor.ast.statement.AstContinue;
import dev.latvian.apps.ichor.ast.statement.AstDelete;
import dev.latvian.apps.ichor.ast.statement.AstExpressionStatement;
import dev.latvian.apps.ichor.ast.statement.AstIf;
import dev.latvian.apps.ichor.ast.statement.AstInterpretableGroup;
import dev.latvian.apps.ichor.ast.statement.AstLetStatement;
import dev.latvian.apps.ichor.ast.statement.AstReturn;
import dev.latvian.apps.ichor.ast.statement.AstWhile;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StaticToken;
import dev.latvian.apps.ichor.token.SymbolToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.util.EvaluableFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("StatementWithEmptyBody")
public class ParserJS {
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
	private static final StaticToken[] EXPONENTIAL_TOKENS = {SymbolToken.POW};
	private static final StaticToken[] UNARY_TOKENS = {SymbolToken.NOT, SymbolToken.ADD, SymbolToken.SUB, SymbolToken.BNOT, SymbolToken.ADD1, SymbolToken.SUB1};
	private static final StaticToken[] CLASS_TOKENS = {KeywordToken.CLASS, KeywordToken.INTERFACE};
	private static final StaticToken[] SET_OP_TOKENS = {SymbolToken.ADD_SET, SymbolToken.SUB_SET, SymbolToken.MUL_SET, SymbolToken.DIV_SET, SymbolToken.MOD_SET, SymbolToken.BOR_SET, SymbolToken.BAND_SET, SymbolToken.XOR_SET, SymbolToken.LSH_SET, SymbolToken.RSH_SET, SymbolToken.URSH_SET};

	public final ContextJS context;
	private final List<PositionedToken> tokens;
	private int current;

	public ParserJS(ContextJS cx, List<PositionedToken> t) {
		context = cx;
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

	private Interpretable declaration() {
		if (match(CLASS_TOKENS)) {
			return classDeclaration();
		} else if (match(KeywordToken.FUNCTION)) {
			var name = consumeName("Expected function name").asString();
			var func = function(0);
			return new AstConstStatement(name, func);
		} else if (match(VAR_TOKENS)) {
			return varDeclaration(previous().token() == KeywordToken.CONST);
		} else {
			return statement();
		}
	}

	private Interpretable classDeclaration() {
		var name = consumeName("Expected class name");

		Object superclass = null;

		if (match(KeywordToken.EXTENDS)) {
			consumeName("Expected superclass name");
			superclass = new AstGetScopeMember(previous().asString());
		}

		consume(SymbolToken.LC, "Expected '{' before class body");

		AstFunction constructor = null;
		var methods = new ArrayList<Map.Entry<String, AstFunction>>();

		while (!check(SymbolToken.RC) && canAdvance()) {
			int modifiers = AstFunction.MOD_CLASS;

			if (match(KeywordToken.STATIC)) {
				modifiers |= AstFunction.MOD_STATIC;
			}

			if (match(KeywordToken.GET)) {
				modifiers |= AstFunction.MOD_GET;
			}

			if (match(KeywordToken.SET)) {
				modifiers |= AstFunction.MOD_SET;
			}

			var fname = consumeName("Expected function name");

			if (fname.asString().equals("constructor")) {
				modifiers |= AstFunction.MOD_CONSTRUCTOR;

				if (constructor != null) {
					throw error(fname, "Constructor already defined");
				}

				constructor = function(modifiers);
			} else {
				methods.add(new AbstractMap.SimpleEntry<>(fname.asString(), function(modifiers)));
			}
		}

		consume(SymbolToken.RC, "Expected '}' after class body");
		return new AstClass(name.asString(), superclass, constructor, methods);
	}

	private Interpretable statement() {
		if (match(KeywordToken.FOR)) {
			return forStatement();
		} else if (match(KeywordToken.IF)) {
			return ifStatement();
		} else if (match(KeywordToken.RETURN)) {
			return returnStatement();
		} else if (match(KeywordToken.BREAK)) {
			return breakStatement();
		} else if (match(KeywordToken.CONTINUE)) {
			return continueStatement();
		} else if (match(KeywordToken.WHILE)) {
			return whileStatement();
		} else if (match(KeywordToken.DELETE)) {
			return deleteStatement();
		} else if (check(SymbolToken.LC)) {
			return block(false);
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

	private Interpretable breakStatement() {
		var keyword = previous();
		consume(SymbolToken.SEMI, "Expected ';' after break");
		return new AstBreak().pos(keyword);
	}

	private Interpretable continueStatement() {
		var keyword = previous();
		consume(SymbolToken.SEMI, "Expected ';' after continue");
		return new AstContinue().pos(keyword);
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

			list.add((isConst ? new AstConstStatement(name.asString(), initializer) : new AstLetStatement(name.asString(), initializer)).pos(name));
		}
		while (match(SymbolToken.COMMA));
		consume(SymbolToken.SEMI, "Expected ';' after variable declaration");
		return AstBlock.optimized(list);
	}

	private Interpretable whileStatement() {
		consume(SymbolToken.LP, "Expected '(' after 'while'");
		var condition = expression();
		consume(SymbolToken.RP, "Expected ')' after condition");
		var body = block(false);

		return new AstWhile(condition, body);
	}

	private Interpretable deleteStatement() {
		var keyword = previous();

		if (call() instanceof AstGetFrom get) {
			return new AstDelete(get);
		} else {
			throw error(keyword, "Expected a variable to delete");
		}
	}

	private Interpretable expressionStatement() {
		var expr = expression();
		match(SymbolToken.SEMI);
		return new AstExpressionStatement(expr);
	}

	private AstFunction function(int modifiers) {
		consume(SymbolToken.LP, "Expected '(' before function parameters");
		var parameters = new ArrayList<AstParam>();
		if (!check(SymbolToken.RP)) {
			do {
				parameters.add(param());
			} while (match(SymbolToken.COMMA));
		}
		consume(SymbolToken.RP, "Expected ')' after function parameters");
		var body = block(false);
		return new AstFunction(parameters.toArray(AstParam.EMPTY_PARAM_ARRAY), body, modifiers);
	}

	private Interpretable block(boolean forceReturn) {
		if (check(SymbolToken.LC)) {
			var firstPos = consume(SymbolToken.LC, "Expected '{' before block, got " + peek() + " instead");

			var statements = new ArrayList<Interpretable>();

			while (!check(SymbolToken.RC) && canAdvance()) {
				statements.add(declaration());
			}

			consume(SymbolToken.RC, "Expected '}' after block");
			var block = new AstBlock(statements);
			block.pos(firstPos);
			return block;
		} else {
			var statement = statement();

			if (forceReturn && !(statement instanceof AstReturn)) {
				return new AstReturn(statement);
			}

			return statement;
		}
	}

	private Evaluable expression() {
		return assignment();
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
		} else if (match(SET_OP_TOKENS)) {
			var operator = previous();
			var value = assignment();

			if (expr instanceof AstGetBase get) {
				var ast = (AstBinary) ((SymbolToken) operator.token()).astBinary.create();
				ast.left = get.optimize();
				ast.right = value.optimize();
				ast.pos(operator);
				return new AstSet(get, ast);
			}

			error(operator, "Invalid assignment target."); // [no-throw]
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

	private Evaluable nc() {
		return binary(NC_TOKENS, this::or);
	}

	private Evaluable or() {
		return binary(OR_TOKENS, this::and);
	}

	private Evaluable and() {
		return binary(AND_TOKENS, this::equality);
	}

	private Evaluable equality() {
		return binary(EQUALITY_TOKENS, this::comparison);
	}

	private Evaluable comparison() {
		return binary(COMPARISON_TOKENS, this::additive);
	}

	private Evaluable additive() {
		return binary(ADDITIVE_TOKENS, this::multiplicative);
	}

	private Evaluable multiplicative() {
		return binary(MULTIPLICATIVE_TOKENS, this::exponential);
	}

	private Evaluable exponential() {
		return binary(EXPONENTIAL_TOKENS, this::unary);
	}

	private Evaluable unary() {
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

	private AstParam param() {
		var name = consumeName("Expected parameter name");
		var param = new AstParam(name.asString());

		if (match(SymbolToken.COL)) {
			consumeName("Expected type name").asString();
			// param type for TS
		}

		if (match(SymbolToken.SET)) {
			param.defaultValue = expression().optimize();
		}

		return param;
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

				if (expr instanceof CallableAst c) {
					expr = c.createCall(arguments.toArray(EMPTY_OBJECT_ARRAY), false);

					if (expr instanceof Ast ast) {
						ast.pos(lp);
					}
				} else {
					throw error(lp, "Expression " + expr + " is not callable");
				}
			} else if (match(SymbolToken.DOT)) {
				var name = consumeName("Expected property name after '.'");
				expr = new AstGetByName(expr, name.asString()).pos(name);
			} else if (match(SymbolToken.OC)) {
				var name = consumeName("Expected property name after '?.'");
				expr = new AstGetByNameOptional(expr, name.asString()).pos(name);
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
			} else if (match(SymbolToken.LS)) {
				var keyo = expression().optimize();

				if (keyo instanceof String) {
					expr = new AstGetByName(expr, keyo.toString()).pos(previous());
				} else if (keyo instanceof Number n) {
					expr = new AstGetByIndex(expr, n.intValue()).pos(previous());
				} else {
					expr = new AstGetByEvaluable(expr, keyo);
				}

				consume(SymbolToken.RS, "Expected ']' after key");
			} else {
				break;
			}
		}

		return expr;
	}

	private Evaluable primary() {
		if (peekToken().hasValue()) {
			var token = advance();
			return new AstLiteral(token.token().getValue()).pos(token);
		} else if (match(KeywordToken.SUPER)) {
			var keyword = previous();
			return new AstSuper().pos(keyword);
		} else if (match(KeywordToken.THIS)) {
			return new AstThis().pos(previous());
		} else if (match(KeywordToken.NEW)) {
			if (peekToken() instanceof NameToken) {
				var name = consumeName("Expected class name after 'new'");
				consume(SymbolToken.LP, "Expected '(' after new " + name.asString());
				var arguments = new ArrayList<>(1);
				if (!check(SymbolToken.RP)) {
					do {
						arguments.add(expression().optimize());
					} while (match(SymbolToken.COMMA));
				}

				consume(SymbolToken.RP, "Expected ')' after arguments");
				return new AstNew(new AstGetScopeMember(name.asString()), arguments.toArray(EMPTY_OBJECT_ARRAY)).pos(name);
			} else {
				throw error(peek(), "Expected class name");
			}
		} else if (match(KeywordToken.FUNCTION)) {
			return function(0);
		} else if (peekToken() instanceof NameToken name) {
			int current0 = current;
			var param = param();

			if (peekToken() == SymbolToken.ARROW) {
				var arrow = advance();
				var body = block(true);
				return new AstFunction(new AstParam[]{param}, body, AstFunction.MOD_ARROW).pos(arrow);
			} else {
				current = current0; // required to jump back because x = y statement and default param look the same
				return new AstGetScopeMember(name.name()).pos(advance());
			}
		} else if (match(SymbolToken.LP)) {
			if (peekToken() == SymbolToken.RP) {
				advance();
				consume(SymbolToken.ARROW, "Expected '=>' after arrow function parameters");

				var body = block(true);
				return new AstFunction(AstParam.EMPTY_PARAM_ARRAY, body, AstFunction.MOD_ARROW).pos(previous());
			} else if (peekToken() instanceof NameToken && (peekToken(1) == SymbolToken.RP || peekToken(1) == SymbolToken.COMMA)) {
				var list = new ArrayList<AstParam>(1);

				do {
					list.add(param());
				}
				while (match(SymbolToken.COMMA));
				consume(SymbolToken.RP, "Expected ')' after arrow function parameters");
				consume(SymbolToken.ARROW, "Expected '=>' after arrow function parameters");

				var body = block(true);
				return new AstFunction(list.toArray(AstParam.EMPTY_PARAM_ARRAY), body, AstFunction.MOD_ARROW).pos(previous());
			}

			var lp = previous();
			var expr = expression();
			consume(SymbolToken.RP, "Expected ')' after expression");
			return new AstGrouping(expr).pos(lp);
		} else if (match(SymbolToken.LS)) {
			var ls = previous();
			var list = new ArrayList<>();

			while (match(SymbolToken.COMMA)) ;

			while (!check(SymbolToken.RS)) {
				list.add(expression().optimize());
				while (match(SymbolToken.COMMA)) ;
			}

			consume(SymbolToken.RS, "Expected ']' after array");
			return new AstArray(list).pos(ls);
		} else if (match(SymbolToken.LC)) {
			var lc = previous();
			var map = new LinkedHashMap<String, Object>();

			consume(SymbolToken.RC, "Expected '}' after object");
			return new AstLiteral(map).pos(lc);
		} else if (match(SymbolToken.TDOT)) {
			var p = previous();
			return new AstSpread(expression()).pos(p);
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
		if (peekToken() instanceof NameToken) {
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
		return peekToken() == type;
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

	private PositionedToken peek(int offset) {
		return tokens.get(current + offset);
	}

	private Token peekToken() {
		return canAdvance() ? peek().token() : SymbolToken.EOF;
	}

	private Token peekToken(int offset) {
		return canAdvance() ? peek(offset).token() : SymbolToken.EOF;
	}

	private PositionedToken previous() {
		return tokens.get(current - 1);
	}
}
