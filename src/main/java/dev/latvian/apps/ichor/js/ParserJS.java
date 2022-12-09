package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.ast.CallableAst;
import dev.latvian.apps.ichor.ast.expression.AstBoolean;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.ast.expression.AstGetBase;
import dev.latvian.apps.ichor.ast.expression.AstGetByEvaluable;
import dev.latvian.apps.ichor.ast.expression.AstGetByIndex;
import dev.latvian.apps.ichor.ast.expression.AstGetByName;
import dev.latvian.apps.ichor.ast.expression.AstGetByNameOptional;
import dev.latvian.apps.ichor.ast.expression.AstGetScopeMember;
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
import dev.latvian.apps.ichor.ast.expression.AstTypeOf;
import dev.latvian.apps.ichor.ast.expression.unary.AstAdd1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstSub1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstUnary;
import dev.latvian.apps.ichor.ast.statement.AstBlock;
import dev.latvian.apps.ichor.ast.statement.AstBreak;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.ast.statement.AstContinue;
import dev.latvian.apps.ichor.ast.statement.AstDebugger;
import dev.latvian.apps.ichor.ast.statement.AstDelete;
import dev.latvian.apps.ichor.ast.statement.AstDoWhile;
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
import dev.latvian.apps.ichor.ast.statement.AstSwitch;
import dev.latvian.apps.ichor.ast.statement.AstThisStatement;
import dev.latvian.apps.ichor.ast.statement.AstThrow;
import dev.latvian.apps.ichor.ast.statement.AstTry;
import dev.latvian.apps.ichor.ast.statement.AstWhile;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.error.ParseErrorMessage;
import dev.latvian.apps.ichor.error.ParseErrorType;
import dev.latvian.apps.ichor.token.BinaryOpToken;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.SymbolToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPosSupplier;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Function;

@SuppressWarnings({"UnusedReturnValue"})
public class ParserJS implements Parser {
	private record BinaryOp(Function<ParserJS, Evaluable> next, Token... tokens) {
	}

	private static final Token[] VAR_TOKENS = {KeywordTokenJS.VAR, KeywordTokenJS.LET, KeywordTokenJS.CONST};
	private static final Token[] UNARY_TOKENS = {SymbolToken.NOT, SymbolToken.ADD, SymbolToken.SUB, SymbolToken.BNOT, SymbolToken.ADD1, SymbolToken.SUB1};
	private static final Token[] CLASS_TOKENS = {KeywordTokenJS.CLASS, KeywordTokenJS.INTERFACE};
	private static final BinaryOp BIN_NC = new BinaryOp(ParserJS::or, SymbolToken.NC);
	private static final BinaryOp BIN_OR = new BinaryOp(ParserJS::and, SymbolToken.OR);
	private static final BinaryOp BIN_AND = new BinaryOp(ParserJS::bitwiseOr, SymbolToken.AND);
	private static final BinaryOp BIN_BITWISE_OR = new BinaryOp(ParserJS::bitwiseXor, SymbolToken.BOR);
	private static final BinaryOp BIN_BITWISE_XOR = new BinaryOp(ParserJS::bitwiseAnd, SymbolToken.XOR);
	private static final BinaryOp BIN_BITWISE_AND = new BinaryOp(ParserJS::equality, SymbolToken.BAND);
	private static final BinaryOp BIN_EQUALITY = new BinaryOp(ParserJS::comparison, SymbolToken.EQ, SymbolToken.NEQ, SymbolToken.SEQ, SymbolToken.SNEQ);
	private static final BinaryOp BIN_COMPARISON = new BinaryOp(ParserJS::shift, SymbolToken.LT, SymbolToken.GT, SymbolToken.LTE, SymbolToken.GTE, KeywordTokenJS.IN, KeywordTokenJS.INSTANCEOF);
	private static final BinaryOp BIN_SHIFT = new BinaryOp(ParserJS::additive, SymbolToken.LSH, SymbolToken.RSH, SymbolToken.URSH);
	private static final BinaryOp BIN_ADDITIVE = new BinaryOp(ParserJS::multiplicative, SymbolToken.ADD, SymbolToken.SUB);
	private static final BinaryOp BIN_MULTIPLICATIVE = new BinaryOp(ParserJS::exponential, SymbolToken.MUL, SymbolToken.DIV, SymbolToken.MOD);
	private static final BinaryOp BIN_EXPONENTIAL = new BinaryOp(ParserJS::unary, SymbolToken.POW);

	private static final Token[] SET_OP_TOKENS = {
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

	private static final Token[] FOR_OF_IN_TOKENS = {KeywordTokenJS.OF, KeywordTokenJS.IN};

	private final ContextJS context;
	private PositionedToken current;

	public ParserJS(ContextJS cx, PositionedToken r) {
		context = cx;
		current = r;
	}

	@Override
	public ContextJS getContext() {
		return context;
	}

	public Interpretable parse() {
		var list = new ArrayList<Interpretable>();

		while (current.exists()) {
			list.add(declaration());
		}

		return AstInterpretableGroup.optimized(list);
	}

	private Interpretable declaration() {
		var pos = current;

		if (advanceIf(CLASS_TOKENS)) {
			return classDeclaration(pos);
		} else if (advanceIf(KeywordTokenJS.FUNCTION)) {
			var param = new AstParam(name(ParseErrorType.EXP_FUNC_NAME));
			param.defaultValue = function(null, null, 0);
			((AstFunction) param.defaultValue).functionName = param.name;
			ignoreSemi();
			return new AstSingleDeclareStatement(KeywordTokenJS.CONST, param).pos(pos);
		} else if (advanceIf(VAR_TOKENS)) {
			var v = varDeclaration(pos);
			ignoreSemi();
			return v;
		} else {
			return statement();
		}
	}

	private void ignoreSemi() {
		while (current.is(SymbolToken.SEMI)) {
			advance();
		}
	}

	private boolean ignoreComma() {
		boolean found = false;

		while (current.is(SymbolToken.COMMA)) {
			advance();
			found = true;
		}

		return found;
	}

	private Interpretable classDeclaration(PositionedToken pos) {
		var astClass = new AstClass(name(ParseErrorType.EXP_CLASS_NAME));
		astClass.pos(pos);

		if (advanceIf(KeywordTokenJS.EXTENDS)) {
			astClass.parent = new AstGetScopeMember(name(ParseErrorType.EXP_CLASS_NAME));
		}

		consume(SymbolToken.LC, ParseErrorType.EXP_LC_CLASS);

		while (!current.is(SymbolToken.RC)) {
			var fpos = current;

			int modifiers = AstFunction.MOD_CLASS;
			var type = AstClassFunction.Type.METHOD;

			if (advanceIf(KeywordTokenJS.STATIC)) {
				modifiers |= AstFunction.MOD_STATIC;
			}

			if (advanceIf(KeywordTokenJS.GET)) {
				modifiers |= AstFunction.MOD_GET;
				type = AstClassFunction.Type.GETTER;
			}

			if (advanceIf(KeywordTokenJS.SET)) {
				modifiers |= AstFunction.MOD_SET;
				type = AstClassFunction.Type.SETTER;
			}

			var fname = name(ParseErrorType.EXP_FUNC_NAME);

			if (fname.equals("constructor")) {
				modifiers |= AstFunction.MOD_CONSTRUCTOR;
				type = AstClassFunction.Type.CONSTRUCTOR;

				if (astClass.constructor != null) {
					throw new ParseError(fpos, ParseErrorType.CONSTRUCTOR_EXISTS);
				}

				astClass.constructor = (AstClassFunction) function(astClass, type, modifiers);
				astClass.constructor.functionName = "constructor";
			} else {
				var map = switch (type) {
					case GETTER -> astClass.getters;
					case SETTER -> astClass.setters;
					default -> astClass.methods;
				};

				if (map.containsKey(fname)) {
					throw new ParseError(fpos, ParseErrorType.METHOD_EXISTS);
				}

				var func = (AstClassFunction) function(astClass, type, modifiers);
				func.functionName = fname;
				map.put(fname, func);
			}
		}

		consume(SymbolToken.RC, ParseErrorType.EXP_RC_CLASS);
		return astClass;
	}

	private Evaluable[] arguments() {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_ARGS);

		var list = new ArrayList<Evaluable>();

		while (!current.is(SymbolToken.RP)) {
			list.add(expression());
			ignoreComma();
		}

		consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
		return list.toArray(Empty.EVALUABLES);
	}

	private Interpretable statement() {
		var pos = current;

		String label = "";

		if (current.isName() && current.next.is(SymbolToken.COL) && (current.next.next.is(KeywordTokenJS.WHILE) || current.next.next.is(KeywordTokenJS.FOR))) {
			label = name(ParseErrorType.EXP_VAR_NAME);
			advance();
		}

		if (advanceIf(KeywordTokenJS.FOR)) {
			return forStatement(pos, label);
		} else if (advanceIf(KeywordTokenJS.IF)) {
			return ifStatement(pos);
		} else if (advanceIf(KeywordTokenJS.RETURN)) {
			return returnStatement(pos);
		} else if (advanceIf(KeywordTokenJS.BREAK)) {
			return breakStatement(pos);
		} else if (advanceIf(KeywordTokenJS.CONTINUE)) {
			return continueStatement(pos);
		} else if (advanceIf(KeywordTokenJS.WHILE)) {
			return whileStatement(pos, label);
		} else if (advanceIf(KeywordTokenJS.DO)) {
			return doWhileStatement(pos, label);
		} else if (advanceIf(KeywordTokenJS.TRY)) {
			return tryStatement(pos);
		} else if (advanceIf(KeywordTokenJS.SWITCH)) {
			return switchStatement(pos);
		} else if (advanceIf(KeywordTokenJS.THROW)) {
			return throwStatement(pos);
		} else if (current.is(SymbolToken.LC)) {
			return block(false);
		} else if ((current.is(KeywordTokenJS.THIS) || current.is(KeywordTokenJS.SUPER)) && current.next.is(SymbolToken.LP)) {
			advance();
			var arguments = arguments();
			return (pos.is(KeywordTokenJS.THIS) ? new AstThisStatement(arguments) : new AstSuperStatement(arguments)).pos(pos);
		} else if (advanceIf(KeywordTokenJS.DEBUGGER)) {
			ignoreSemi();
			return new AstDebugger().pos(pos);
		}

		return expressionStatement(true);
	}

	private Interpretable forStatement(PositionedToken pos, String label) {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_FOR);

		Interpretable initializer;
		if (advanceIf(SymbolToken.SEMI)) {
			initializer = null;
		} else if (advanceIf(VAR_TOKENS)) {
			initializer = varDeclaration(current.prev);
		} else if (current.isName() && current.next.is(FOR_OF_IN_TOKENS)) {
			var n = name(ParseErrorType.EXP_VAR_NAME);
			initializer = new AstSingleDeclareStatement(KeywordTokenJS.LET, new AstParam(n));
		} else {
			initializer = expressionStatement(false);
		}

		// var prev = previous();
		// var curr = current();
		// var next = peek();

		if (advanceIf(FOR_OF_IN_TOKENS)) {
			String name;

			if (initializer instanceof AstSingleDeclareStatement s) {
				name = s.variable.name;
			} else if (initializer instanceof AstExpressionStatement stmt && stmt.expression instanceof AstSet set && set.get instanceof AstGetScopeMember member) {
				name = member.name;
			} else {
				throw new ParseError(pos, ParseErrorType.EXP_INIT);
			}

			boolean of = current.prev.is(KeywordTokenJS.OF);

			var from = expression();

			consume(SymbolToken.RP, ParseErrorType.EXP_RP_FOR);
			var body = statementBody();

			return (of ? new AstForOf(name, from, body, label) : new AstForIn(name, from, body, label)).pos(pos);
		} else {
			consume(SymbolToken.SEMI, ParseErrorType.EXP_SEMI_FOR_INIT);
		}

		Evaluable condition = null;
		if (!current.is(SymbolToken.SEMI)) {
			condition = expression();
		}
		consume(SymbolToken.SEMI, ParseErrorType.EXP_SEMI_FOR_COND);

		Interpretable increment = null;
		if (!current.is(SymbolToken.RP)) {
			increment = expressionStatement(true);
		}

		consume(SymbolToken.RP, ParseErrorType.EXP_RP_FOR);
		var body = statementBody();

		ignoreSemi();

		return new AstFor(initializer, condition, increment, body, label).pos(pos);
	}

	private Interpretable ifStatement(PositionedToken pos) {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_IF_COND);
		var condition = expression();
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_IF_COND);

		var ifTrue = statementBody();
		Interpretable ifFalse = null;

		if (advanceIf(KeywordTokenJS.ELSE)) {
			ifFalse = statementBody();
		}

		ignoreSemi();

		return new AstIf(condition, ifTrue, ifFalse).pos(pos);
	}

	private Interpretable returnStatement(PositionedToken pos) {
		Evaluable value = null;

		if (!current.is(SymbolToken.SEMI) && !current.is(SymbolToken.RC)) {
			value = expression();
		}

		ignoreSemi();
		return new AstReturn(value).pos(pos);
	}

	private Interpretable breakStatement(PositionedToken pos) {
		var label = "";

		if (current.isName()) {
			label = name(ParseErrorType.EXP_VAR_NAME);
		}

		ignoreSemi();
		return new AstBreak(label).pos(pos);
	}

	private Interpretable continueStatement(PositionedToken pos) {
		var label = "";

		if (current.isName()) {
			label = name(ParseErrorType.EXP_VAR_NAME);
		}

		ignoreSemi();
		return new AstContinue(label).pos(pos);
	}

	private Interpretable varDeclaration(PositionedToken c) {
		return varDeclaration((DeclaringToken) c.token, c);
	}

	private Interpretable varDeclaration(DeclaringToken type, TokenPosSupplier pos) {
		var list = new ArrayList<AstParam>(1);

		do {
			var param = new AstParam(current.name(ParseErrorType.EXP_VAR_NAME));
			advance();

			if (advanceIf(SymbolToken.SET)) {
				param.defaultValue = expression();
			}

			list.add(param);
		} while (ignoreComma());

		// ignoreSemi();

		if (list.size() == 1) {
			return new AstSingleDeclareStatement(type, list.get(0)).pos(pos);
		}

		return new AstMultiDeclareStatement(type, list.toArray(Empty.AST_PARAMS)).pos(pos);
	}

	private Interpretable whileStatement(PositionedToken pos, String label) {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_WHILE_COND);
		var condition = expression();
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_WHILE_COND);
		var body = statementBody();
		return new AstWhile(condition, body, label).pos(pos);
	}

	private Interpretable doWhileStatement(PositionedToken pos, String label) {
		var body = block(false);
		consume(KeywordTokenJS.WHILE, ParseErrorType.EXP_TOKEN.format("while"));
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_WHILE_COND);
		var condition = expression();
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_WHILE_COND);
		ignoreSemi();
		return new AstDoWhile(condition, body, label).pos(pos);
	}

	private Interpretable tryStatement(PositionedToken pos) {
		var tryBlock = block(false);
		AstTry.AstCatch catchBlock = null;
		Interpretable finallyBlock = null;

		if (advanceIf(KeywordTokenJS.CATCH)) {
			consume(SymbolToken.LP, ParseErrorType.EXP_LP_ARGS);
			var name = name(ParseErrorType.EXP_ARG_NAME);
			consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
			catchBlock = new AstTry.AstCatch(name, block(false));
		}

		if (advanceIf(KeywordTokenJS.FINALLY)) {
			finallyBlock = block(false);
		}

		return new AstTry(tryBlock, catchBlock, finallyBlock).pos(pos);
	}

	private Interpretable switchStatement(PositionedToken pos) {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_ARGS);
		var condition = expression();
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
		consume(SymbolToken.LC, ParseErrorType.EXP_LC_BLOCK);

		var cases = new ArrayList<AstSwitch.AstCase>(1);
		AstSwitch.AstCase defaultCase = null;

		while (!current.is(SymbolToken.RC)) {
			var cpos = current;

			if (cpos.is(KeywordTokenJS.CASE) || cpos.is(KeywordTokenJS.DEFAULT)) {
				advance();
				var value = cpos.is(KeywordTokenJS.CASE) ? expression() : null;
				consume(SymbolToken.COL, ParseErrorType.EXP_COL_CASE);

				var stmts = new ArrayList<Interpretable>();

				while (!current.is(SymbolToken.RC) && !current.is(KeywordTokenJS.CASE) && !current.is(KeywordTokenJS.DEFAULT)) {
					stmts.add(declaration());
				}

				if (cpos.is(KeywordTokenJS.CASE)) {
					cases.add(new AstSwitch.AstCase(value, AstInterpretableGroup.optimized(stmts)));
				} else {
					defaultCase = new AstSwitch.AstCase(value, AstInterpretableGroup.optimized(stmts));
				}
			} else {
				throw new ParseError(current, ParseErrorType.EXP_CASE);
			}
		}

		consume(SymbolToken.RC, ParseErrorType.EXP_RC_BLOCK);

		return new AstSwitch(condition, cases.toArray(AstSwitch.AstCase.EMPTY), defaultCase).pos(pos);
	}

	private Interpretable throwStatement(PositionedToken pos) {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_ARGS);
		var exception = expression();
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
		ignoreSemi();
		return new AstThrow(exception).pos(pos);
	}

	private Interpretable expressionStatement(boolean ignoreSemi) {
		var pos = current;
		var expr = expression();

		if (ignoreSemi) {
			ignoreSemi();
		}

		return new AstExpressionStatement(expr).pos(pos);
	}

	private AstFunction function(@Nullable AstClass owner, @Nullable AstClassFunction.Type type, int modifiers) {
		consume(SymbolToken.LP, ParseErrorType.EXP_LP_ARGS);
		var parameters = new ArrayList<AstParam>();
		if (!current.is(SymbolToken.RP)) {
			do {
				parameters.add(param());
			} while (ignoreComma());
		}
		consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
		var body = block(false);

		if (owner != null) {
			return new AstClassFunction(owner, parameters.toArray(Empty.AST_PARAMS), body, modifiers, type);
		}

		return new AstFunction(parameters.toArray(Empty.AST_PARAMS), body, modifiers);
	}

	private Interpretable block(boolean forceReturn) {
		var pos = current;

		if (pos.is(SymbolToken.LC)) {
			advance();
			var statements = new ArrayList<Interpretable>();

			while (!current.is(SymbolToken.RC)) {
				statements.add(declaration());
			}

			consume(SymbolToken.RC, ParseErrorType.EXP_RC_BLOCK);
			return new AstBlock(statements).pos(pos);
		} else {
			var statement = statement();

			if (forceReturn && !(statement instanceof AstReturn)) {
				if (statement instanceof AstExpressionStatement expr) {
					return new AstReturn(expr.expression).pos(pos);
				} else {
					throw new ParseError(pos, ParseErrorType.EXP_EXPR.format(statement));
				}
			}

			return statement;
		}
	}

	@Nullable
	private Interpretable statementBody() {
		if (advanceIf(SymbolToken.SEMI)) {
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
		return assignment().optimize();
	}

	private Evaluable assignment() {
		var expr = nc();
		var pos = current;

		if (advanceIf(SymbolToken.SET)) {
			var value = expression();

			if (expr instanceof AstGetBase get) {
				return new AstSet(get, value).pos(pos);
			}

			throw new ParseError(pos, ParseErrorType.INVALID_TARGET);
		} else if (advanceIf(SET_OP_TOKENS)) {
			var value = expression();

			if (expr instanceof AstGetBase get && pos.token instanceof BinaryOpToken bin) {
				var ast = bin.createBinaryAst(pos);
				ast.left = get;
				ast.right = value;
				ast.pos(pos);
				return new AstSet(get, ast).pos(pos);
			}

			throw new ParseError(pos, ParseErrorType.INVALID_TARGET);
		} else if (advanceIf(SymbolToken.HOOK)) {
			var ifTrue = expression();
			consume(SymbolToken.COL, ParseErrorType.EXP_TERNARY_COL);
			var ifFalse = expression();
			return new AstTernary(expr, ifTrue, ifFalse).pos(pos);
		}

		return expr;
	}

	private Evaluable binary(BinaryOp binaryOp) {
		var expr = binaryOp.next.apply(this);

		while (advanceIf(binaryOp.tokens)) {
			var operator = current.prev;
			var right = binaryOp.next.apply(this);

			if (operator.token instanceof BinaryOpToken binaryOpToken) {
				var ast = binaryOpToken.createBinaryAst(operator);
				ast.left = expr;
				ast.right = right;
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
		if (advanceIf(UNARY_TOKENS)) {
			var operator = current.prev;
			var right = unary();
			var ast = (AstUnary) ((SymbolToken) operator.token).astUnary.create();
			ast.node = right;
			ast.pos(operator);
			return ast;
		} else if (advanceIf(KeywordTokenJS.TYPEOF)) {
			return new AstTypeOf(unary());
		} else if (advanceIf(KeywordTokenJS.DELETE)) {
			var pos = current.prev;
			var expr = unary();

			if (expr instanceof AstGetBase get) {
				return new AstDelete(get).pos(pos);
			} else {
				return new AstBoolean(false).pos(pos);
			}
		}

		return call();
	}

	private AstParam param() {
		var param = new AstParam(name(ParseErrorType.EXP_ARG_NAME));

		if (advanceIf(SymbolToken.COL)) {
			name(ParseErrorType.EXP_TYPE_NAME);
			// param type for TS
		}

		if (advanceIf(SymbolToken.SET)) {
			param.defaultValue = expression();
		}

		return param;
	}

	private Evaluable call() {
		var newToken = current.is(KeywordTokenJS.NEW) ? advance() : null;

		var expr = primary();

		while (true) {
			var pos = current;

			if (current.is(SymbolToken.LP)) {
				var arguments = arguments();

				if (newToken != null) {
					expr = new AstNew(expr, arguments).pos(newToken);
				} else if (expr instanceof CallableAst c) {
					expr = c.createCall(arguments, false);

					if (expr instanceof Ast ast) {
						ast.pos(pos);
					}
				} else {
					throw new ParseError(pos, ParseErrorType.EXPR_NOT_CALLABLE.format(expr, expr.getClass().toString()));
				}
			} else if (advanceIf(SymbolToken.DOT)) {
				var name = name(ParseErrorType.EXP_NAME_DOT.format(current));
				expr = new AstGetByName(expr, name).pos(pos);
			} else if (advanceIf(SymbolToken.OC)) {
				var name = name(ParseErrorType.EXP_NAME_OC);
				expr = new AstGetByNameOptional(expr, name).pos(pos);
			} else if (advanceIf(SymbolToken.ADD1)) {
				var ast = new AstAdd1R();
				ast.node = expr;
				ast.pos(pos);
				expr = ast;
			} else if (advanceIf(SymbolToken.SUB1)) {
				var ast = new AstSub1R();
				ast.node = expr;
				ast.pos(pos);
				expr = ast;
			} else if (advanceIf(SymbolToken.LS)) {
				var keyo = expression();

				if (keyo instanceof AstString str) {
					expr = new AstGetByName(expr, str.value).pos(pos);
				} else if (keyo instanceof AstNumber n) {
					expr = new AstGetByIndex(expr, (int) n.value).pos(pos);
				} else {
					expr = new AstGetByEvaluable(expr, keyo).pos(pos);
				}

				consume(SymbolToken.RS, ParseErrorType.EXP_RS_KEY);
			} else {
				break;
			}
		}

		return expr;
	}

	private Evaluable primary() {
		var pos = current;
		var literal = pos.token.toEvaluable(this, pos.getPos());

		if (literal != null) {
			advance();

			if (literal instanceof Ast ast) {
				ast.pos(pos);
			}

			return literal;
		} else if (advanceIf(KeywordTokenJS.SUPER)) {
			return new AstSuperExpression().pos(pos);
		} else if (advanceIf(KeywordTokenJS.THIS)) {
			return new AstThisExpression().pos(pos);
		} else if (advanceIf(KeywordTokenJS.FUNCTION)) {
			return functionExpression();
		} else if (current.isName()) { // handle all keywords before this
			var name = name(ParseErrorType.EXP_VAR_NAME);

			if (advanceIf(SymbolToken.ARROW)) {
				var apos = current.prev;
				var body = block(true);
				return new AstFunction(new AstParam[]{new AstParam(name)}, body, AstFunction.MOD_ARROW).pos(apos);
			} else {
				//current = pos; // required to jump back because x = y statement and default param look the same
				//advance();
				return new AstGetScopeMember(name).pos(pos);
			}
		} else if (advanceIf(SymbolToken.LP)) {
			if (current.is(SymbolToken.RP)) {
				advance();
				consume(SymbolToken.ARROW, ParseErrorType.EXP_ARROW);

				var body = block(true);
				return new AstFunction(Empty.AST_PARAMS, body, AstFunction.MOD_ARROW).pos(pos);
			} else if (current.isName() && (current.next.is(SymbolToken.RP) && current.next.next.is(SymbolToken.ARROW) || current.next.is(SymbolToken.COMMA))) {
				var list = new ArrayList<AstParam>(1);

				do {
					list.add(param());
				} while (ignoreComma());
				consume(SymbolToken.RP, ParseErrorType.EXP_RP_ARGS);
				consume(SymbolToken.ARROW, ParseErrorType.EXP_ARROW);

				var body = block(true);
				return new AstFunction(list.toArray(Empty.AST_PARAMS), body, AstFunction.MOD_ARROW).pos(pos);
			}

			// var lp = previous();
			var expr = expression();
			consume(SymbolToken.RP, ParseErrorType.EXP_RP_EXPR);
			// return new AstGrouping(expr).pos(lp);
			return expr;
		} else if (advanceIf(SymbolToken.LS)) {
			var list = new ArrayList<Evaluable>();

			ignoreComma();

			while (!current.is(SymbolToken.RS)) {
				list.add(expression());
				ignoreComma();
			}

			consume(SymbolToken.RS, ParseErrorType.EXP_RS_ARRAY);
			return new AstList(list).pos(pos);
		} else if (advanceIf(SymbolToken.LC)) {
			var map = new LinkedHashMap<String, Evaluable>();

			ignoreComma();

			while (!current.is(SymbolToken.RC)) {
				int flags = 0;

				if ((current.is(KeywordTokenJS.GET) || current.is(KeywordTokenJS.SET) && current.next.isName() && current.next.next.is(SymbolToken.LP))) {
					if (advanceIf(KeywordTokenJS.GET)) {
						flags |= AstFunction.MOD_GET;
					} else if (advanceIf(KeywordTokenJS.SET)) {
						flags |= AstFunction.MOD_SET;
					}
				}

				var name = name(ParseErrorType.EXP_VAR_NAME);

				if (current.is(SymbolToken.LP)) {
					var func = function(null, null, flags);
					func.functionName = name;
					map.put(name, func);
				} else {
					consume(SymbolToken.COL, ParseErrorType.EXP_COL_OBJECT);
					map.put(name, expression());
				}

				ignoreComma();
			}

			consume(SymbolToken.RC, ParseErrorType.EXP_RC_OBJECT);
			return new AstMap(map).pos(pos);
		} else if (advanceIf(SymbolToken.TDOT)) {
			return new AstSpread(expression()).pos(pos);
		} else if (advanceIf(SymbolToken.TEMPLATE_LITERAL)) {
			if (advanceIf(SymbolToken.TEMPLATE_LITERAL)) {
				return new AstString("").pos(pos);
			}

			var parts = new ArrayList<Evaluable>();
			boolean onlyStrings = true;

			// `${i}: ${arr[i]}`
			// `_${_i_}_: _${_arr[i]_}_`

			while (true) {
				if (advanceIf(SymbolToken.TEMPLATE_LITERAL)) {
					break;
				} else if (advanceIf(SymbolToken.TEMPLATE_LITERAL_VAR)) {
					var expr = expression();

					if (!(expr instanceof AstString)) {
						onlyStrings = false;
					}

					parts.add(expr);
					consume(SymbolToken.RC, ParseErrorType.EXP_RC_TEMPLATE_LITERAL);
				} else {
					var nextEval = current.token.toEvaluable(this, current.pos);

					if (nextEval != null) {
						parts.add(nextEval);

						if (!(nextEval instanceof AstString)) {
							onlyStrings = false;
						}

						advance();
					} else {
						throw new ParseError(current, ParseErrorType.EXP_EXPR.format(current.token));
					}
				}
			}

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

			return new AstTemplateLiteral(parts.toArray(Empty.EVALUABLES)).pos(pos);
		}

		throw new ParseError(pos, ParseErrorType.EXP_EXPR.format(pos.token));
	}

	private Evaluable functionExpression() {
		int flags = 0;

		if (advanceIf(KeywordTokenJS.GET)) {
			flags |= AstFunction.MOD_GET;
		} else if (advanceIf(KeywordTokenJS.SET)) {
			flags |= AstFunction.MOD_SET;
		}

		String funcName = null;

		if (current.isName()) {
			funcName = name(ParseErrorType.EXP_FUNC_NAME);
		}

		var func = function(null, null, flags);
		func.functionName = funcName;
		return func;
	}

	private PositionedToken advance() {
		var c = current;
		current = current.next;
		return c;
	}

	private boolean advanceIf(Token token) {
		if (current.is(token)) {
			advance();
			return true;
		}

		return false;
	}

	private boolean advanceIf(Token[] token) {
		if (current.is(token)) {
			advance();
			return true;
		}

		return false;
	}

	private String name(ParseErrorMessage error) {
		var s = current.name(error);
		advance();
		return s;
	}

	private void consume(Token token, ParseErrorMessage error) {
		if (current.is(token)) {
			advance();
		} else {
			throw new ParseError(current, error);
		}
	}
}
