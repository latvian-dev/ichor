package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.ast.CallableAst;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.ast.expression.AstGetBase;
import dev.latvian.apps.ichor.ast.expression.AstGetByEvaluable;
import dev.latvian.apps.ichor.ast.expression.AstGetByIndex;
import dev.latvian.apps.ichor.ast.expression.AstGetByName;
import dev.latvian.apps.ichor.ast.expression.AstGetByNameOptional;
import dev.latvian.apps.ichor.ast.expression.AstGetFrom;
import dev.latvian.apps.ichor.ast.expression.AstGetScopeMember;
import dev.latvian.apps.ichor.ast.expression.AstGrouping;
import dev.latvian.apps.ichor.ast.expression.AstList;
import dev.latvian.apps.ichor.ast.expression.AstMap;
import dev.latvian.apps.ichor.ast.expression.AstNew;
import dev.latvian.apps.ichor.ast.expression.AstNumber;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.ast.expression.AstSet;
import dev.latvian.apps.ichor.ast.expression.AstSpread;
import dev.latvian.apps.ichor.ast.expression.AstString;
import dev.latvian.apps.ichor.ast.expression.AstSuperExpression;
import dev.latvian.apps.ichor.ast.expression.AstTemplateLiteral;
import dev.latvian.apps.ichor.ast.expression.AstTernary;
import dev.latvian.apps.ichor.ast.expression.AstThisExpression;
import dev.latvian.apps.ichor.ast.expression.unary.AstAdd1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstSub1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstUnary;
import dev.latvian.apps.ichor.ast.statement.AstBlock;
import dev.latvian.apps.ichor.ast.statement.AstBreak;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.ast.statement.AstContinue;
import dev.latvian.apps.ichor.ast.statement.AstDelete;
import dev.latvian.apps.ichor.ast.statement.AstExpressionStatement;
import dev.latvian.apps.ichor.ast.statement.AstFor;
import dev.latvian.apps.ichor.ast.statement.AstForIn;
import dev.latvian.apps.ichor.ast.statement.AstForOf;
import dev.latvian.apps.ichor.ast.statement.AstIf;
import dev.latvian.apps.ichor.ast.statement.AstInterpretableGroup;
import dev.latvian.apps.ichor.ast.statement.AstMultiDeclareStatement;
import dev.latvian.apps.ichor.ast.statement.AstReturn;
import dev.latvian.apps.ichor.ast.statement.AstSingleDeclareStatement;
import dev.latvian.apps.ichor.ast.statement.AstSuperStatement;
import dev.latvian.apps.ichor.ast.statement.AstThisStatement;
import dev.latvian.apps.ichor.ast.statement.AstWhile;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.error.ParseErrorMessage;
import dev.latvian.apps.ichor.error.ParseErrorType;
import dev.latvian.apps.ichor.token.BinaryOpToken;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StaticToken;
import dev.latvian.apps.ichor.token.SymbolToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPosSupplier;
import dev.latvian.apps.ichor.util.EmptyArrays;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"UnusedReturnValue"})
public class ParserJS implements Parser {
	private record BinaryOp(Function<ParserJS, Evaluable> next, StaticToken... tokens) {
	}

	private static final StaticToken[] VAR_TOKENS = {KeywordToken.VAR, KeywordToken.LET, KeywordToken.CONST};
	private static final StaticToken[] UNARY_TOKENS = {SymbolToken.NOT, SymbolToken.ADD, SymbolToken.SUB, SymbolToken.BNOT, SymbolToken.ADD1, SymbolToken.SUB1};
	private static final StaticToken[] CLASS_TOKENS = {KeywordToken.CLASS, KeywordToken.INTERFACE};
	private static final BinaryOp BIN_NC = new BinaryOp(ParserJS::or, SymbolToken.NC);
	private static final BinaryOp BIN_OR = new BinaryOp(ParserJS::and, SymbolToken.OR);
	private static final BinaryOp BIN_AND = new BinaryOp(ParserJS::bitwiseOr, SymbolToken.AND);
	private static final BinaryOp BIN_BITWISE_OR = new BinaryOp(ParserJS::bitwiseXor, SymbolToken.BOR);
	private static final BinaryOp BIN_BITWISE_XOR = new BinaryOp(ParserJS::bitwiseAnd, SymbolToken.XOR);
	private static final BinaryOp BIN_BITWISE_AND = new BinaryOp(ParserJS::equality, SymbolToken.BAND);
	private static final BinaryOp BIN_EQUALITY = new BinaryOp(ParserJS::comparison, SymbolToken.EQ, SymbolToken.NEQ, SymbolToken.SEQ, SymbolToken.SNEQ);
	private static final BinaryOp BIN_COMPARISON = new BinaryOp(ParserJS::shift, SymbolToken.LT, SymbolToken.GT, SymbolToken.LTE, SymbolToken.GTE, KeywordToken.IN, KeywordToken.INSTANCEOF);
	private static final BinaryOp BIN_SHIFT = new BinaryOp(ParserJS::additive, SymbolToken.LSH, SymbolToken.RSH, SymbolToken.URSH);
	private static final BinaryOp BIN_ADDITIVE = new BinaryOp(ParserJS::multiplicative, SymbolToken.ADD, SymbolToken.SUB);
	private static final BinaryOp BIN_MULTIPLICATIVE = new BinaryOp(ParserJS::exponential, SymbolToken.MUL, SymbolToken.DIV, SymbolToken.MOD);
	private static final BinaryOp BIN_EXPONENTIAL = new BinaryOp(ParserJS::unary, SymbolToken.POW);

	private static final StaticToken[] SET_OP_TOKENS = {
			SymbolToken.ADD_SET,
			SymbolToken.SUB_SET,
			SymbolToken.MUL_SET,
			SymbolToken.DIV_SET,
			SymbolToken.MOD_SET,
			SymbolToken.BOR_SET,
			SymbolToken.BAND_SET,
			SymbolToken.XOR_SET,
			SymbolToken.LSH_SET,
			SymbolToken.RSH_SET,
			SymbolToken.URSH_SET,
			SymbolToken.NC_SET,
	};

	private static final StaticToken[] FOR_OF_IN_TOKENS = {KeywordToken.OF, KeywordToken.IN};

	private final ContextJS context;
	private final List<PositionedToken> tokens;
	private int current;

	public ParserJS(ContextJS cx, List<PositionedToken> t) {
		context = cx;
		tokens = t;
		current = 0;
	}

	@Override
	public ContextJS getContext() {
		return context;
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
			var fn = previous();
			var param = new AstParam(consumeName(ParseErrorType.EXP_FUNC_NAME).asString());
			param.defaultValue = function(null, null, 0);
			return new AstSingleDeclareStatement(KeywordToken.CONST, param).pos(fn);
		} else if (match(VAR_TOKENS)) {
			return varDeclaration(previous());
		} else {
			return statement();
		}
	}

	private void ignoreSemi() {
		while (check(SymbolToken.SEMI)) {
			advance();
		}
	}

	private boolean ignoreComma() {
		boolean found = false;

		while (check(SymbolToken.COMMA)) {
			advance();
			found = true;
		}

		return found;
	}

	private Interpretable classDeclaration() {
		var name = consumeName(ParseErrorType.EXP_CLASS_NAME);
		var astClass = new AstClass(name.asString());
		astClass.pos(name);

		if (match(KeywordToken.EXTENDS)) {
			consumeName(ParseErrorType.EXP_CLASS_NAME);
			astClass.parent = new AstGetScopeMember(previous().asString());
		}

		consume(SymbolToken.LC, ParseErrorType.EXP_LC_CLASS);

		while (!check(SymbolToken.RC) && canAdvance()) {
			int modifiers = AstFunction.MOD_CLASS;
			var type = AstClassFunction.Type.METHOD;

			if (match(KeywordToken.STATIC)) {
				modifiers |= AstFunction.MOD_STATIC;
			}

			if (match(KeywordToken.GET)) {
				modifiers |= AstFunction.MOD_GET;
				type = AstClassFunction.Type.GETTER;
			}

			if (match(KeywordToken.SET)) {
				modifiers |= AstFunction.MOD_SET;
				type = AstClassFunction.Type.SETTER;
			}

			var fname = consumeName(ParseErrorType.EXP_FUNC_NAME);

			if (fname.asString().equals("constructor")) {
				modifiers |= AstFunction.MOD_CONSTRUCTOR;
				type = AstClassFunction.Type.CONSTRUCTOR;

				if (astClass.constructor != null) {
					throw error(fname, ParseErrorType.CONSTRUCTOR_EXISTS);
				}

				astClass.constructor = (AstClassFunction) function(astClass, type, modifiers);
			} else {
				var map = switch (type) {
					case GETTER -> astClass.getters;
					case SETTER -> astClass.setters;
					default -> astClass.methods;
				};

				if (map.containsKey(fname.asString())) {
					throw error(fname, ParseErrorType.METHOD_EXISTS);
				}

				map.put(fname.asString(), (AstClassFunction) function(astClass, type, modifiers));
			}
		}

		consume(SymbolToken.RC, ParseErrorType.EXP_RC_CLASS);
		return astClass;
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
		} else if ((check(KeywordToken.THIS) || check(KeywordToken.SUPER)) && peekToken(1) == SymbolToken.LP) {
			var k = advance();
			advance();

			var arguments = new ArrayList<Evaluable>();

			if (!check(SymbolToken.RP)) {
				do {
					arguments.add(expression().optimize());
				} while (ignoreComma());
			}

			consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);

			return (k.token() == KeywordToken.THIS ? new AstThisStatement(arguments.toArray(EmptyArrays.EVALUABLES)) : new AstSuperStatement(arguments.toArray(EmptyArrays.EVALUABLES))).pos(k);
		}

		return expressionStatement(true);
	}

	private Interpretable forStatement() {
		var pos = previous();
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_FOR);

		Interpretable initializer;
		if (match(SymbolToken.SEMI)) {
			initializer = null;
		} else if (match(VAR_TOKENS)) {
			initializer = varDeclaration(previous());
		} else if (peekToken() instanceof NameToken n) {
			advance();
			initializer = new AstSingleDeclareStatement(KeywordToken.LET, new AstParam(n.name()));
		} else {
			initializer = expressionStatement(false);
		}

		if (match(FOR_OF_IN_TOKENS)) {
			String name;

			if (initializer instanceof AstSingleDeclareStatement s) {
				name = s.variable.name;
			} else {
				throw error(previous(), ParseErrorType.EXP_INIT);
			}

			boolean of = previous().token() == KeywordToken.OF;

			var from = expression();

			consume(SymbolToken.RP, ParseErrorType.EXP_RP_FOR);
			var body = statementBody();


			return (of ? new AstForOf(name, from, body) : new AstForIn(name, from, body)).pos(pos);
		}

		Evaluable condition = null;
		if (!check(SymbolToken.SEMI)) {
			condition = expression();
		}
		consume(SymbolToken.SEMI, ParseErrorType.EXP_SEMI_FOR);

		Interpretable increment = null;
		if (!check(SymbolToken.RP)) {
			increment = expressionStatement(true);
		}

		consume(SymbolToken.RP, ParseErrorType.EXP_RP_FOR);
		var body = statementBody();

		return new AstFor(initializer, condition, increment, body).pos(pos);
	}

	private Interpretable ifStatement() {
		var pos = previous();

		consume(SymbolToken.LP, ParseErrorType.EXP_LP_IF_COND);
		var condition = expression();
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_IF_COND);

		var ifTrue = statementBody();
		Interpretable ifFalse = null;

		if (match(KeywordToken.ELSE)) {
			ifFalse = statementBody();
		}

		return new AstIf(condition, ifTrue, ifFalse).pos(pos);
	}

	private Interpretable returnStatement() {
		var keyword = previous();
		Evaluable value = null;

		if (!check(SymbolToken.SEMI)) {
			value = expression().optimize();
		}

		ignoreSemi();
		// consume(SymbolToken.SEMI, "Expected ';' after return value");
		return new AstReturn(value).pos(keyword);
	}

	private Interpretable breakStatement() {
		var keyword = previous();
		ignoreSemi();
		//consume(SymbolToken.SEMI, "Expected ';' after break");
		return new AstBreak().pos(keyword);
	}

	private Interpretable continueStatement() {
		var keyword = previous();
		ignoreSemi();
		//consume(SymbolToken.SEMI, "Expected ';' after continue");
		return new AstContinue().pos(keyword);
	}

	private Interpretable varDeclaration(PositionedToken token) {
		var list = new ArrayList<AstParam>(1);

		do {
			var param = new AstParam(consumeName(ParseErrorType.EXP_VAR_NAME).asString());

			if (match(SymbolToken.SET)) {
				param.defaultValue = expression().optimize();
			}

			list.add(param);
		} while (ignoreComma());
		// consume(SymbolToken.SEMI, "Expected ';' after variable declaration");
		ignoreSemi();

		if (list.size() == 1) {
			return new AstSingleDeclareStatement((StaticToken) token.token(), list.get(0)).pos(token);
		}

		return new AstMultiDeclareStatement((StaticToken) token.token(), list.toArray(EmptyArrays.AST_PARAMS)).pos(token);
	}

	private Interpretable whileStatement() {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_WHILE_COND);
		var condition = expression();
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_WHILE_COND);
		var body = statementBody();
		return new AstWhile(condition, body);
	}

	private Interpretable deleteStatement() {
		var keyword = previous();

		if (call() instanceof AstGetFrom get) {
			return new AstDelete(get);
		} else {
			throw error(keyword, ParseErrorType.EXP_VAR_NAME);
		}
	}

	private Interpretable expressionStatement(boolean ignoreSemi) {
		var expr = expression();

		if (ignoreSemi) {
			ignoreSemi();
		}

		return new AstExpressionStatement(expr);
	}

	private AstFunction function(@Nullable AstClass owner, @Nullable AstClassFunction.Type type, int modifiers) {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_ARGS);
		var parameters = new ArrayList<AstParam>();
		if (!check(SymbolToken.RP)) {
			do {
				parameters.add(param());
			} while (ignoreComma());
		}
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
		var body = block(false);

		if (owner != null) {
			return new AstClassFunction(owner, parameters.toArray(EmptyArrays.AST_PARAMS), body, modifiers, type);
		}

		return new AstFunction(parameters.toArray(EmptyArrays.AST_PARAMS), body, modifiers);
	}

	private Interpretable block(boolean forceReturn) {
		if (check(SymbolToken.LC)) {
			var firstPos = consume(SymbolToken.LC, ParseErrorType.EXP_LC_BLOCK);

			var statements = new ArrayList<Interpretable>();

			while (!check(SymbolToken.RC) && canAdvance()) {
				statements.add(declaration());
			}

			consume(SymbolToken.RC, ParseErrorType.EXP_RC_BLOCK);
			var block = new AstBlock(statements);
			block.pos(firstPos);
			return block;
		} else {
			var pos = peek().getPos();
			var statement = statement();

			if (forceReturn && !(statement instanceof AstReturn)) {
				if (statement instanceof AstExpressionStatement expr) {
					return new AstReturn(expr.expression);
				} else {
					throw error(pos, ParseErrorType.EXP_EXPR.format(statement));
				}
			}

			return statement;
		}
	}

	@Nullable
	private Interpretable statementBody() {
		if (match(SymbolToken.SEMI)) {
			return null;
		}

		var b = block(false);

		if (b instanceof AstInterpretableGroup g && g.interpretable.length == 0) {
			return null;
		}

		return b;
	}

	@Override
	public Evaluable expression() {
		return assignment();
	}

	private Evaluable assignment() {
		var expr = nc();

		if (match(SymbolToken.SET)) {
			var operator = previous();
			var value = expression();

			if (expr instanceof AstGetBase get) {
				return new AstSet(get, value.optimize());
			}

			throw error(operator, ParseErrorType.INVALID_TARGET);
		} else if (match(SET_OP_TOKENS)) {
			var operator = previous();
			var value = expression();

			if (expr instanceof AstGetBase get && operator.token() instanceof BinaryOpToken bin) {
				var ast = bin.createBinaryAst(operator);
				ast.left = get.optimize();
				ast.right = value.optimize();
				ast.pos(operator);
				return new AstSet(get, ast);
			}

			throw error(operator, ParseErrorType.INVALID_TARGET);
		} else if (match(SymbolToken.HOOK)) {
			var ifTrue = expression();
			consume(SymbolToken.COL, ParseErrorType.EXP_TERNARY_COL);
			var ifFalse = expression();
			return new AstTernary(expr, ifTrue, ifFalse);
		}

		return expr;
	}

	private Evaluable binary(BinaryOp binaryOp) {
		var expr = binaryOp.next.apply(this);

		while (match(binaryOp.tokens)) {
			var operator = previous();
			var right = binaryOp.next.apply(this);

			if (operator.token() instanceof BinaryOpToken binaryOpToken) {
				var ast = binaryOpToken.createBinaryAst(operator);
				ast.left = expr.optimize();
				ast.right = right.optimize();
				ast.pos(operator);
				expr = ast;
			}
		}

		return expr;
	}

	private Evaluable nc() {
		return binary(BIN_NC);
	}

	private Evaluable or() {
		return binary(BIN_OR);
	}

	private Evaluable and() {
		return binary(BIN_AND);
	}

	private Evaluable bitwiseOr() {
		return binary(BIN_BITWISE_OR);
	}

	private Evaluable bitwiseXor() {
		return binary(BIN_BITWISE_XOR);
	}

	private Evaluable bitwiseAnd() {
		return binary(BIN_BITWISE_AND);
	}

	private Evaluable equality() {
		return binary(BIN_EQUALITY);
	}

	private Evaluable comparison() {
		return binary(BIN_COMPARISON);
	}

	private Evaluable shift() {
		return binary(BIN_SHIFT);
	}

	private Evaluable additive() {
		return binary(BIN_ADDITIVE);
	}

	private Evaluable multiplicative() {
		return binary(BIN_MULTIPLICATIVE);
	}

	private Evaluable exponential() {
		return binary(BIN_EXPONENTIAL);
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
		var name = consumeName(ParseErrorType.EXP_PARAM_NAME);
		var param = new AstParam(name.asString());

		if (match(SymbolToken.COL)) {
			consumeName(ParseErrorType.EXP_TYPE_NAME).asString();
			// param type for TS
		}

		if (match(SymbolToken.SET)) {
			param.defaultValue = expression().optimize();
		}

		return param;
	}

	private Evaluable call() {
		var newToken = check(KeywordToken.NEW) ? consume(KeywordToken.NEW, null) : null;

		var expr = primary();

		while (true) {
			if (match(SymbolToken.LP)) {
				var lp = previous();
				var arguments = new ArrayList<Evaluable>(2);
				if (!check(SymbolToken.RP)) {
					do {
						arguments.add(expression().optimize());
					} while (ignoreComma());
				}

				consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);

				if (newToken != null) {
					expr = new AstNew(expr, arguments.toArray(EmptyArrays.EVALUABLES)).pos(newToken);
				} else if (expr instanceof CallableAst c) {
					expr = c.createCall(arguments.toArray(EmptyArrays.EVALUABLES), false);

					if (expr instanceof Ast ast) {
						ast.pos(lp);
					}
				} else {
					throw error(lp, ParseErrorType.EXPR_NOT_CALLABLE.format(expr));
				}
			} else if (match(SymbolToken.DOT)) {
				var name = consumeName(ParseErrorType.EXP_NAME_DOT);
				expr = new AstGetByName(expr, name.asString()).pos(name);
			} else if (match(SymbolToken.OC)) {
				var name = consumeName(ParseErrorType.EXP_NAME_OC);
				expr = new AstGetByNameOptional(expr, name.asString()).pos(name);
			} else if (match(SymbolToken.ADD1)) {
				var ast = new AstAdd1R();
				ast.node = expr;
				ast.pos(previous());
				expr = ast;
			} else if (match(SymbolToken.SUB1)) {
				var ast = new AstSub1R();
				ast.node = expr;
				ast.pos(previous());
				expr = ast;
			} else if (match(SymbolToken.LS)) {
				var keyo = expression().optimize();

				if (keyo instanceof AstString str) {
					expr = new AstGetByName(expr, str.value).pos(previous());
				} else if (keyo instanceof AstNumber n) {
					expr = new AstGetByIndex(expr, (int) n.value).pos(previous());
				} else {
					expr = new AstGetByEvaluable(expr, keyo);
				}

				consume(SymbolToken.RS, ParseErrorType.EXP_RS_KEY);
			} else {
				break;
			}
		}

		return expr;
	}

	private Evaluable primary() {
		var peekToken = peek();
		var literal = peekToken.token().toEvaluable(this, peekToken.pos());

		if (literal != null) {
			var token = advance();

			if (literal instanceof Ast ast) {
				ast.pos(token);
			}

			return literal;
		} else if (match(KeywordToken.SUPER)) {
			var keyword = previous();
			return new AstSuperExpression().pos(keyword);
		} else if (match(KeywordToken.THIS)) {
			return new AstThisExpression().pos(previous());
		} else if (match(KeywordToken.FUNCTION)) {
			return function(null, null, 0);
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
				consume(SymbolToken.ARROW, ParseErrorType.EXP_ARROW);

				var body = block(true);
				return new AstFunction(EmptyArrays.AST_PARAMS, body, AstFunction.MOD_ARROW).pos(previous());
			} else if (peekToken() instanceof NameToken && (peekToken(1) == SymbolToken.RP || peekToken(1) == SymbolToken.COMMA)) {
				var list = new ArrayList<AstParam>(1);

				do {
					list.add(param());
				} while (ignoreComma());
				consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
				consume(SymbolToken.ARROW, ParseErrorType.EXP_ARROW);

				var body = block(true);
				return new AstFunction(list.toArray(EmptyArrays.AST_PARAMS), body, AstFunction.MOD_ARROW).pos(previous());
			}

			var lp = previous();
			var expr = expression();
			consume(SymbolToken.RP, ParseErrorType.EXP_RP_EXPR);
			return new AstGrouping(expr).pos(lp);
		} else if (match(SymbolToken.LS)) {
			var ls = previous();
			var list = new ArrayList<Evaluable>();

			ignoreComma();

			while (!check(SymbolToken.RS)) {
				list.add(expression());
				ignoreComma();
			}

			consume(SymbolToken.RS, ParseErrorType.EXP_RS_ARRAY);
			return new AstList(list).pos(ls);
		} else if (match(SymbolToken.LC)) {
			var lc = previous();
			var map = new LinkedHashMap<String, Evaluable>();

			ignoreComma();

			while (!check(SymbolToken.RC)) {
				var name = consumeName(ParseErrorType.EXP_VAR_NAME);

				if (peekToken() == SymbolToken.LP) {
					var func = function(null, null, 0);
					map.put(name.asString(), func);
				} else {
					consume(SymbolToken.COL, ParseErrorType.EXP_COL_OBJECT);
					map.put(name.asString(), expression());
				}

				ignoreComma();
			}

			consume(SymbolToken.RC, ParseErrorType.EXP_RC_OBJECT);
			return new AstMap(map).pos(lc);
		} else if (match(SymbolToken.TDOT)) {
			var p = previous();
			return new AstSpread(expression()).pos(p);
		} else if (match(SymbolToken.TEMPLATE_LITERAL)) {
			var pos = previous();
			var next = advance();

			if (next.token() == SymbolToken.TEMPLATE_LITERAL) {
				return new AstString("").pos(pos);
			}

			var parts = new ArrayList<Evaluable>();
			boolean onlyStrings = true;
			Evaluable nextEval;

			do {
				if (next.token() == SymbolToken.TEMPLATE_LITERAL_VAR) {
					var expr = expression();

					if (!(expr instanceof AstString)) {
						onlyStrings = false;
					}

					parts.add(expr);
					consume(SymbolToken.RC, ParseErrorType.EXP_RC_TEMPLATE_LITERAL);
				} else if ((nextEval = next.token().toEvaluable(this, next.pos())) != null) {
					parts.add(nextEval);

					if (!(nextEval instanceof AstString)) {
						onlyStrings = false;
					}
				} else {
					throw error(next, ParseErrorType.EXP_EXPR.format(next.token()));
				}

				next = advance();
			}
			while (next.token() != SymbolToken.TEMPLATE_LITERAL);

			if (parts.isEmpty()) {
				return new AstString("").pos(pos);
			}

			if (onlyStrings) {
				var sb = new StringBuilder();

				for (var part : parts) {
					sb.append(((AstString) part).value);
				}

				return new AstString(sb.toString()).pos(pos);
			}

			return new AstTemplateLiteral(parts.toArray(EmptyArrays.EVALUABLES)).pos(pos);
		}

		throw error(peek(), ParseErrorType.EXP_EXPR.format(peekToken()));
	}

	//

	private ParseError error(TokenPosSupplier pos, ParseErrorMessage message) {
		throw new ParseError(pos, message);
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

	private PositionedToken consumeName(ParseErrorType message) {
		if (peekToken() instanceof NameToken || peekToken() instanceof KeywordToken) {
			return advance();
		}

		throw new ParseError(peek().pos(), message);
	}

	private PositionedToken consume(StaticToken type, ParseErrorMessage message) {
		if (check(type)) {
			return advance();
		}

		throw new ParseError(peek().pos(), message);
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
		return current < tokens.size();
	}

	@Nullable
	private PositionedToken peek() {
		return current >= tokens.size() ? null : tokens.get(current);
	}

	private PositionedToken peek(int offset) {
		return tokens.get(current + offset);
	}

	private Token peekToken() {
		return canAdvance() ? peek().token() : null;
	}

	private Token peekToken(int offset) {
		return canAdvance() ? peek(offset).token() : null;
	}

	private PositionedToken previous() {
		return tokens.get(current - 1);
	}
}
