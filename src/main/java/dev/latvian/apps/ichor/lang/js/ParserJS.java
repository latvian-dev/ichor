package dev.latvian.apps.ichor.lang.js;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.ast.expression.AstAwait;
import dev.latvian.apps.ichor.ast.expression.AstCall;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;
import dev.latvian.apps.ichor.ast.expression.AstDelete;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.ast.expression.AstGetBase;
import dev.latvian.apps.ichor.ast.expression.AstGetByEvaluable;
import dev.latvian.apps.ichor.ast.expression.AstGetByIndex;
import dev.latvian.apps.ichor.ast.expression.AstGetByName;
import dev.latvian.apps.ichor.ast.expression.AstGetByNameOptional;
import dev.latvian.apps.ichor.ast.expression.AstGetScopeMember;
import dev.latvian.apps.ichor.ast.expression.AstList;
import dev.latvian.apps.ichor.ast.expression.AstMap;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.ast.expression.AstSet;
import dev.latvian.apps.ichor.ast.expression.AstSpread;
import dev.latvian.apps.ichor.ast.expression.AstSuperExpression;
import dev.latvian.apps.ichor.ast.expression.AstTemplateLiteral;
import dev.latvian.apps.ichor.ast.expression.AstTernary;
import dev.latvian.apps.ichor.ast.expression.AstThisExpression;
import dev.latvian.apps.ichor.ast.expression.AstType;
import dev.latvian.apps.ichor.ast.expression.unary.AstAdd1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstSub1R;
import dev.latvian.apps.ichor.ast.statement.AstBlock;
import dev.latvian.apps.ichor.ast.statement.AstBreak;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.ast.statement.AstContinue;
import dev.latvian.apps.ichor.ast.statement.AstDeclaration;
import dev.latvian.apps.ichor.ast.statement.AstDoWhile;
import dev.latvian.apps.ichor.ast.statement.AstEmptyBlock;
import dev.latvian.apps.ichor.ast.statement.AstExpressionStatement;
import dev.latvian.apps.ichor.ast.statement.AstFor;
import dev.latvian.apps.ichor.ast.statement.AstForIn;
import dev.latvian.apps.ichor.ast.statement.AstForOf;
import dev.latvian.apps.ichor.ast.statement.AstFunctionDeclareStatement;
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
import dev.latvian.apps.ichor.ast.statement.LabeledStatement;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.error.ParseErrorMessage;
import dev.latvian.apps.ichor.error.ParseErrorType;
import dev.latvian.apps.ichor.exit.ExitType;
import dev.latvian.apps.ichor.lang.js.ast.AstArguments;
import dev.latvian.apps.ichor.lang.js.ast.AstClassPrototype;
import dev.latvian.apps.ichor.lang.js.ast.AstDebugger;
import dev.latvian.apps.ichor.lang.js.ast.AstObjectPrototype;
import dev.latvian.apps.ichor.lang.js.ast.AstTypeOf;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPosSupplier;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

@SuppressWarnings({"UnusedReturnValue"})
public class ParserJS implements Parser {
	private record BinaryOp(Function<ParserJS, Object> next, Token... tokens) {
	}

	private static final Token[] VAR_TOKENS = {KeywordTokenJS.VAR, KeywordTokenJS.LET, KeywordTokenJS.CONST};
	private static final Token[] UNARY_TOKENS = {SymbolTokenJS.NOT, SymbolTokenJS.ADD, SymbolTokenJS.SUB, SymbolTokenJS.BNOT, SymbolTokenJS.ADD1, SymbolTokenJS.SUB1};
	private static final Token[] CLASS_TOKENS = {KeywordTokenJS.CLASS, KeywordTokenJS.INTERFACE};
	private static final BinaryOp BIN_NC = new BinaryOp(ParserJS::or, SymbolTokenJS.NC);
	private static final BinaryOp BIN_OR = new BinaryOp(ParserJS::and, SymbolTokenJS.OR);
	private static final BinaryOp BIN_AND = new BinaryOp(ParserJS::bitwiseOr, SymbolTokenJS.AND);
	private static final BinaryOp BIN_BITWISE_OR = new BinaryOp(ParserJS::bitwiseXor, SymbolTokenJS.BOR);
	private static final BinaryOp BIN_BITWISE_XOR = new BinaryOp(ParserJS::bitwiseAnd, SymbolTokenJS.XOR);
	private static final BinaryOp BIN_BITWISE_AND = new BinaryOp(ParserJS::equality, SymbolTokenJS.BAND);
	private static final BinaryOp BIN_EQUALITY = new BinaryOp(ParserJS::comparison, SymbolTokenJS.EQ, SymbolTokenJS.NEQ, SymbolTokenJS.SEQ, SymbolTokenJS.SNEQ);
	private static final BinaryOp BIN_COMPARISON = new BinaryOp(ParserJS::shift, SymbolTokenJS.LT, SymbolTokenJS.GT, SymbolTokenJS.LTE, SymbolTokenJS.GTE, KeywordTokenJS.IN, KeywordTokenJS.INSTANCEOF);
	private static final BinaryOp BIN_SHIFT = new BinaryOp(ParserJS::additive, SymbolTokenJS.LSH, SymbolTokenJS.RSH, SymbolTokenJS.URSH);
	private static final BinaryOp BIN_ADDITIVE = new BinaryOp(ParserJS::multiplicative, SymbolTokenJS.ADD, SymbolTokenJS.SUB);
	private static final BinaryOp BIN_MULTIPLICATIVE = new BinaryOp(ParserJS::exponential, SymbolTokenJS.MUL, SymbolTokenJS.DIV, SymbolTokenJS.MOD);
	private static final BinaryOp BIN_EXPONENTIAL = new BinaryOp(ParserJS::unary, SymbolTokenJS.POW);

	private static final Token[] FOR_OF_IN_TOKENS = {
			KeywordTokenJS.OF,
			KeywordTokenJS.IN
	};

	private final ContextJS context;
	private final RootScope rootScope;
	private PositionedToken current;
	private final Stack<LabeledStatement> labeledStatements;
	private final Map<String, AstType.Generic> genericTypeCache;

	public ParserJS(ContextJS cx, RootScope scope, PositionedToken r) {
		context = cx;
		rootScope = scope;
		current = r;
		labeledStatements = new Stack<>();
		genericTypeCache = new HashMap<>();
	}

	@Override
	public ContextJS getContext() {
		return context;
	}

	@Override
	public RootScope getRootScope() {
		return rootScope;
	}

	@Override
	public Interpretable parse() {
		var list = new ArrayList<Interpretable>();

		while (current.exists()) {
			list.add(declaration());
		}

		var group = AstInterpretableGroup.optimized(list);
		group.optimize(this);
		return group;
	}

	private Interpretable declaration() {
		var pos = current;

		int funcFlags = 0;

		if (advanceIf(KeywordTokenJS.ASYNC)) {
			funcFlags |= AstFunction.Mod.ASYNC;
		}

		if (advanceIf(CLASS_TOKENS)) {
			return classDeclaration(pos);
		} else if (advanceIf(KeywordTokenJS.FUNCTION)) {
			funcFlags |= AstFunction.Mod.STATEMENT;
			var name = name(ParseErrorType.EXP_FUNC_NAME);
			var func = function(null, null, funcFlags);
			func.functionName = name;
			ignoreSemi();
			return new AstFunctionDeclareStatement(func).pos(pos);
		} else if (advanceIf(VAR_TOKENS)) {
			var v = varDeclaration(pos);
			ignoreSemi();
			return v;
		} else {
			return statement();
		}
	}

	private AstType type() {
		var id = name(ParseErrorType.EXP_TYPE_NAME);

		AstType type = switch (id) {
			case "any" -> AstType.Generic.ANY;
			case "void" -> AstType.Generic.VOID;
			case "boolean" -> AstType.Generic.BOOLEAN;
			case "number" -> AstType.Generic.NUMBER;
			case "string" -> AstType.Generic.STRING;
			case "function" -> AstType.Generic.FUNCTION;
			case "object" -> AstType.Generic.OBJECT;
			case "bigint" -> AstType.Generic.BIGINT;
			case "Array" -> AstType.Generic.ARRAY;
			default -> genericTypeCache.computeIfAbsent(id, AstType.Generic::new);
		};

		if (advanceIf(SymbolTokenJS.LT)) {
			var list = new ArrayList<AstType>();

			do {
				list.add(type());
			} while (advanceIf(SymbolTokenJS.COMMA));

			consume(SymbolTokenJS.GT, ParseErrorType.EXP_GT_TYPE);
			type = new AstType.Typed(type, list.toArray(Empty.AST_TYPES));
		}

		while (advanceIf(SymbolTokenJS.LS)) {
			consume(SymbolTokenJS.RS, ParseErrorType.EXP_RS_ARRAY);
			type = new AstType.Array(type);
		}

		while (advanceIf(SymbolTokenJS.BOR)) {
			type = new AstType.Or(type, type());
		}

		return AstType.Generic.ANY;
	}

	private void ignoreSemi() {
		while (current.is(SymbolTokenJS.SEMI)) {
			advance();
		}
	}

	private boolean ignoreComma() {
		boolean found = false;

		while (current.is(SymbolTokenJS.COMMA)) {
			advance();
			found = true;
		}

		return found;
	}

	private void pushExit(LabeledStatement stmt) {
		labeledStatements.push(stmt);
	}

	private void popExit(LabeledStatement stmt) {
		labeledStatements.pop();
	}

	private Interpretable classDeclaration(PositionedToken pos) {
		AstClass astClass = new AstClass(name(ParseErrorType.EXP_CLASS_NAME)).pos(pos);

		if (advanceIf(KeywordTokenJS.EXTENDS)) {
			astClass.parent = new AstGetScopeMember(name(ParseErrorType.EXP_CLASS_NAME));
		}

		consume(SymbolTokenJS.LC, ParseErrorType.EXP_LC_CLASS);

		while (!current.is(SymbolTokenJS.RC)) {
			var fpos = current;

			int modifiers = AstFunction.Mod.CLASS;
			var type = AstClassFunction.Type.METHOD;

			if (advanceIf(KeywordTokenJS.STATIC)) {
				modifiers |= AstFunction.Mod.STATIC;
			}

			if (advanceIf(KeywordTokenJS.GET)) {
				modifiers |= AstFunction.Mod.GET;
				type = AstClassFunction.Type.GETTER;
			}

			if (advanceIf(KeywordTokenJS.SET)) {
				modifiers |= AstFunction.Mod.SET;
				type = AstClassFunction.Type.SETTER;
			}

			var fname = name(ParseErrorType.EXP_FUNC_NAME);

			if (fname.equals("constructor")) {
				modifiers |= AstFunction.Mod.CONSTRUCTOR;
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

		consume(SymbolTokenJS.RC, ParseErrorType.EXP_RC_CLASS);
		return astClass;
	}

	private Object[] arguments() {
		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_ARGS);

		var list = new ArrayList<>();

		while (!current.is(SymbolTokenJS.RP)) {
			list.add(expression());
			ignoreComma();
		}

		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_ARGS);
		return list.toArray(Empty.OBJECTS);
	}

	private Interpretable statement() {
		if (current.isIdentifier() && current.next.is(SymbolTokenJS.COL)) {
			var pos = current;
			var name = name(ParseErrorType.EXP_VAR_NAME);
			advance();
			var statement = unlabelledStatement(name);

			if (statement instanceof LabeledStatement stmt && stmt.getLabel().equals(name)) {
				return stmt;
			} else {
				throw new ParseError(pos, ParseErrorType.EXP_LABELLED_STATEMENT);
			}
		}

		return unlabelledStatement("");
	}

	private Interpretable unlabelledStatement(String label) {
		var pos = current;

		if (advanceIf(KeywordTokenJS.FOR)) {
			return forStatement(pos, label);
		} else if (advanceIf(KeywordTokenJS.IF)) {
			return ifStatement(pos, label);
		} else if (advanceIf(KeywordTokenJS.RETURN)) {
			return returnStatement(pos);
		} else if (advanceIf(KeywordTokenJS.BREAK)) {
			return exitStatement(pos, ExitType.BREAK);
		} else if (advanceIf(KeywordTokenJS.CONTINUE)) {
			return exitStatement(pos, ExitType.CONTINUE);
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
		} else if (current.is(SymbolTokenJS.LC)) {
			return block(false, label);
		} else if ((current.is(KeywordTokenJS.THIS) || current.is(KeywordTokenJS.SUPER)) && current.next.is(SymbolTokenJS.LP)) {
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
		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_FOR);

		Interpretable initializer;

		if (advanceIf(SymbolTokenJS.SEMI)) {
			initializer = new AstEmptyBlock(false).pos(pos);
		} else if (advanceIf(VAR_TOKENS)) {
			initializer = varDeclaration(current.prev);
		} else if (current.isIdentifier() && current.next.is(FOR_OF_IN_TOKENS)) {
			var n = name(ParseErrorType.EXP_VAR_NAME);
			initializer = new AstSingleDeclareStatement(KeywordTokenJS.LET, new AstDeclaration.Simple(n));
		} else {
			initializer = expressionStatement(false);
		}

		// var prev = previous();
		// var curr = current();
		// var next = peek();

		if (advanceIf(FOR_OF_IN_TOKENS)) {
			String name;

			if (initializer instanceof AstSingleDeclareStatement s) {
				if (s.variable instanceof AstDeclaration.Simple p) {
					name = p.name;
				} else {
					throw new ParseError(pos, ParseErrorType.DESTRUCT_NOT_SUPPORTED);
				}
			} else if (initializer instanceof AstExpressionStatement stmt && stmt.expression instanceof AstSet set && set.get instanceof AstGetScopeMember member) {
				name = member.name;
			} else {
				throw new ParseError(pos, ParseErrorType.EXP_INIT);
			}

			boolean of = current.prev.is(KeywordTokenJS.OF);
			AstForOf ast = (of ? new AstForOf() : new AstForIn()).pos(pos);
			ast.label = label;
			ast.name = name;
			ast.from = expression();

			consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_FOR);

			pushExit(ast);
			ast.body = statementBody();
			popExit(ast);

			return ast;
		} else {
			consume(SymbolTokenJS.SEMI, ParseErrorType.EXP_SEMI_FOR_INIT);
		}

		AstFor ast = new AstFor().pos(pos);
		ast.label = label;
		ast.initializer = initializer;

		if (!current.is(SymbolTokenJS.SEMI)) {
			ast.condition = expression();
		}

		consume(SymbolTokenJS.SEMI, ParseErrorType.EXP_SEMI_FOR_COND);

		if (!current.is(SymbolTokenJS.RP)) {
			ast.increment = expressionStatement(true);
		} else {
			ast.increment = new AstEmptyBlock(false);
		}

		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_FOR);
		pushExit(ast);
		ast.body = statementBody();
		popExit(ast);

		ignoreSemi();

		return ast;
	}

	private Interpretable ifStatement(PositionedToken pos, String label) {
		AstIf ast = new AstIf().pos(pos);
		ast.label = label;

		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_IF_COND);
		ast.condition = expression();
		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_IF_COND);

		pushExit(ast);
		ast.trueBody = statementBody();

		if (advanceIf(KeywordTokenJS.ELSE)) {
			ast.falseBody = statementBody();
		}

		popExit(ast);
		ignoreSemi();
		return ast;
	}

	private Interpretable returnStatement(PositionedToken pos) {
		Object value = Special.UNDEFINED;

		if (!current.is(SymbolTokenJS.SEMI) && !current.is(SymbolTokenJS.RC)) {
			value = expression();
		}

		ignoreSemi();
		return new AstReturn(value).pos(pos);
	}

	private LabeledStatement findStop(PositionedToken pos, String label, ExitType type) {
		if (label.isEmpty()) {
			for (int i = labeledStatements.size() - 1; i >= 0; i--) {
				var stmt = labeledStatements.get(i);

				if (stmt.handle(type)) {
					return stmt;
				}
			}

			throw new ParseError(pos, ParseErrorType.EXIT_NOT_SUPPORTED.format(type.name));
		}

		for (int i = labeledStatements.size() - 1; i >= 0; i--) {
			var stmt = labeledStatements.get(i);

			if (stmt.handle(type) && stmt.getLabel().equals(label)) {
				return stmt;
			}
		}

		throw new ParseError(pos, ParseErrorType.UNKNOWN_LABEL.format(label, type.name));
	}

	private Interpretable exitStatement(PositionedToken pos, ExitType type) {
		var stmt = findStop(pos, current.isIdentifier() ? name(ParseErrorType.EXP_VAR_NAME) : "", type);
		ignoreSemi();
		return (type == ExitType.BREAK ? new AstBreak(stmt) : new AstContinue(stmt)).pos(pos);
	}

	private Interpretable varDeclaration(PositionedToken c) {
		return varDeclaration((DeclaringToken) c.token, c);
	}

	private Interpretable varDeclaration(DeclaringToken type, TokenPosSupplier pos) {
		var list = new ArrayList<AstDeclaration>(1);

		do {
			list.add(varParam());
		} while (ignoreComma());

		// ignoreSemi();

		if (list.size() == 1) {
			return new AstSingleDeclareStatement(type, list.get(0)).pos(pos);
		}

		return new AstMultiDeclareStatement(type, list.toArray(Empty.AST_DECLARATIONS)).pos(pos);
	}

	private AstDeclaration varParam() {
		var decl = new AstDeclaration.Simple(name(ParseErrorType.EXP_VAR_NAME));

		if (advanceIf(SymbolTokenJS.COL)) {
			decl.type = type();
		}

		if (advanceIf(SymbolTokenJS.SET)) {
			decl.defaultValue = expression();
		}

		return decl;
	}

	private Interpretable whileStatement(PositionedToken pos, String label) {
		AstWhile ast = new AstWhile().pos(pos);
		ast.label = label;
		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_WHILE_COND);
		ast.condition = expression();
		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_WHILE_COND);
		pushExit(ast);
		ast.body = statementBody();
		popExit(ast);
		return ast;
	}

	private Interpretable doWhileStatement(PositionedToken pos, String label) {
		AstDoWhile ast = new AstDoWhile().pos(pos);
		ast.label = label;
		pushExit(ast);
		ast.body = statementBody();
		popExit(ast);
		consume(KeywordTokenJS.WHILE, ParseErrorType.EXP_TOKEN.format("while"));
		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_WHILE_COND);
		ast.condition = expression();
		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_WHILE_COND);
		ignoreSemi();
		return ast;
	}

	private Interpretable tryStatement(PositionedToken pos) {
		var tryBlock = statementBody();
		AstTry.AstCatch catchBlock = null;
		Interpretable finallyBlock = null;

		if (advanceIf(KeywordTokenJS.CATCH)) {
			consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_ARGS);
			var name = name(ParseErrorType.EXP_PARAM_NAME);
			consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_ARGS);
			catchBlock = new AstTry.AstCatch(name, statementBody());
		}

		if (advanceIf(KeywordTokenJS.FINALLY)) {
			finallyBlock = statementBody();
		}

		return new AstTry(tryBlock, catchBlock, finallyBlock).pos(pos);
	}

	private Interpretable switchStatement(PositionedToken pos) {
		AstSwitch ast = new AstSwitch().pos(pos);
		pushExit(ast);

		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_ARGS);
		ast.expression = expression();
		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_ARGS);
		consume(SymbolTokenJS.LC, ParseErrorType.EXP_LC_BLOCK);

		var cases = new ArrayList<AstSwitch.AstCase>(1);

		while (!current.is(SymbolTokenJS.RC)) {
			var cpos = current;

			if (cpos.is(KeywordTokenJS.CASE) || cpos.is(KeywordTokenJS.DEFAULT)) {
				advance();
				var value = cpos.is(KeywordTokenJS.CASE) ? expression() : null;
				consume(SymbolTokenJS.COL, ParseErrorType.EXP_COL_CASE);

				var stmts = new ArrayList<Interpretable>();

				while (!current.is(SymbolTokenJS.RC) && !current.is(KeywordTokenJS.CASE) && !current.is(KeywordTokenJS.DEFAULT)) {
					stmts.add(declaration());
				}

				if (cpos.is(KeywordTokenJS.CASE)) {
					cases.add(new AstSwitch.AstCase(value, AstInterpretableGroup.optimized(stmts)));
				} else {
					ast.defaultCase = new AstSwitch.AstCase(value, AstInterpretableGroup.optimized(stmts));
				}
			} else {
				throw new ParseError(current, ParseErrorType.EXP_CASE);
			}
		}

		consume(SymbolTokenJS.RC, ParseErrorType.EXP_RC_BLOCK);
		popExit(ast);

		ast.cases = cases.toArray(AstSwitch.AstCase.EMPTY);
		return ast;
	}

	private Interpretable throwStatement(PositionedToken pos) {
		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_ARGS);
		var exception = expression();
		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_ARGS);
		ignoreSemi();
		return new AstThrow(exception).pos(pos);
	}

	private Interpretable expressionStatement(boolean ignoreSemi) {
		var pos = current;
		var expr = expression();

		if (ignoreSemi) {
			ignoreSemi();
		}

		return new AstExpressionStatement(expr).pos(expr instanceof TokenPosSupplier s ? s : pos);
	}

	private AstFunction function(@Nullable AstClass owner, @Nullable AstClassFunction.Type type, int modifiers) {
		boolean isArrow = (modifiers & AstFunction.Mod.ARROW) != 0;

		consume(SymbolTokenJS.LP, ParseErrorType.EXP_LP_ARGS);
		var parameters = new ArrayList<AstParam>(1);
		if (!current.is(SymbolTokenJS.RP)) {
			do {
				if (advanceIf(SymbolTokenJS.TDOT)) {
					if ((modifiers & AstFunction.Mod.VARARGS) == 0) {
						modifiers |= AstFunction.Mod.VARARGS;
					} else {
						throw new ParseError(current.prev, ParseErrorType.MULTIPLE_VARARGS);
					}
				}

				parameters.add(param());
			} while (ignoreComma());
		}
		consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_ARGS);

		if (isArrow) {
			consume(SymbolTokenJS.ARROW, ParseErrorType.EXP_ARROW);
		}

		var body = block(true, "");

		if (owner != null) {
			return new AstClassFunction(owner, parameters.toArray(Empty.AST_PARAMS), body, modifiers, type);
		}

		return new AstFunction(parameters.toArray(Empty.AST_PARAMS), body, modifiers);
	}

	private Interpretable block(boolean forceReturn, String label) {
		var pos = current;

		if (advanceIf(SymbolTokenJS.SEMI)) {
			return new AstEmptyBlock(forceReturn).pos(pos);
		} else if (advanceIf(SymbolTokenJS.LC)) {
			if (advanceIf(SymbolTokenJS.RC)) {
				return new AstEmptyBlock(forceReturn).pos(pos);
			}

			AstBlock ast = new AstBlock().pos(pos);
			ast.label = label;

			var statements = new ArrayList<Interpretable>();
			pushExit(ast);

			while (!current.is(SymbolTokenJS.RC)) {
				statements.add(declaration());
			}

			consume(SymbolTokenJS.RC, ParseErrorType.EXP_RC_BLOCK);
			popExit(ast);

			ast.interpretable = statements.toArray(Interpretable.EMPTY_INTERPRETABLE_ARRAY);
			ast.forceReturn = forceReturn;
			return ast;
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
		return block(false, "");
	}

	@Override
	public Object expression() {
		return assignment();
	}

	private Object assignment() {
		var expr = nc();
		var pos = current;

		if (advanceIf(SymbolTokenJS.SET)) {
			var value = expression();

			if (expr instanceof AstGetBase get) {
				return new AstSet(get, value).pos(pos);
			}

			throw new ParseError(pos, ParseErrorType.INVALID_TARGET.format(value));
		} else if (advanceIf(SymbolTokenJS.SET_OP_TOKENS)) {
			var value = expression();

			if (expr instanceof AstGetBase get && pos.token instanceof Token t) {
				var ast = t.createBinaryAst(pos);

				if (ast == null) {
					throw new ParseError(pos, ParseErrorType.INVALID_BINARY.format(pos.token));
				}

				ast.left = get;
				ast.right = value;
				ast.pos(pos);
				return new AstSet(get, ast).pos(pos);
			}

			throw new ParseError(pos, ParseErrorType.INVALID_TARGET.format(value));
		} else if (advanceIf(SymbolTokenJS.HOOK)) {
			AstTernary ternary = new AstTernary();
			ternary.condition = expr;
			ternary.ifTrue = expression();
			consume(SymbolTokenJS.COL, ParseErrorType.EXP_TERNARY_COL);
			ternary.ifFalse = expression();
			ternary.pos(expr instanceof TokenPosSupplier s ? s : pos);
			return ternary;
		}

		return expr;
	}

	private Object binary(BinaryOp binaryOp) {
		var expr = binaryOp.next.apply(this);

		while (advanceIf(binaryOp.tokens)) {
			var operator = current.prev;

			if (operator.token instanceof Token tk) {
				var right = binaryOp.next.apply(this);
				var ast = tk.createBinaryAst(operator);

				if (ast == null) {
					throw new ParseError(operator, ParseErrorType.INVALID_BINARY.format(operator.token));
				}

				ast.left = expr;
				ast.right = right;
				ast.pos(operator);
				expr = ast;
			} else {
				throw new ParseError(operator, ParseErrorType.INVALID_TARGET.format(operator.token));
			}
		}

		return expr;
	}

	private Object nc() {
		return binary(BIN_NC);
	}

	private Object or() {
		return binary(BIN_OR);
	}

	private Object and() {
		return binary(BIN_AND);
	}

	private Object bitwiseOr() {
		return binary(BIN_BITWISE_OR);
	}

	private Object bitwiseXor() {
		return binary(BIN_BITWISE_XOR);
	}

	private Object bitwiseAnd() {
		return binary(BIN_BITWISE_AND);
	}

	private Object equality() {
		return binary(BIN_EQUALITY);
	}

	private Object comparison() {
		return binary(BIN_COMPARISON);
	}

	private Object shift() {
		return binary(BIN_SHIFT);
	}

	private Object additive() {
		return binary(BIN_ADDITIVE);
	}

	private Object multiplicative() {
		return binary(BIN_MULTIPLICATIVE);
	}

	private Object exponential() {
		return binary(BIN_EXPONENTIAL);
	}

	private Object unary() {
		if (advanceIf(UNARY_TOKENS)) {
			var operator = current.prev;

			if (operator.token instanceof Token tk) {
				var right = unary();
				var ast = tk.createUnaryAst(operator);

				if (ast == null) {
					throw new ParseError(operator, ParseErrorType.INVALID_UNARY.format(operator.token));
				}

				ast.node = right;
				ast.pos(operator);
				return ast;
			} else {
				throw new ParseError(operator, ParseErrorType.INVALID_TARGET.format(operator.token));
			}
		} else if (advanceIf(KeywordTokenJS.TYPEOF)) {
			return new AstTypeOf(unary());
		} else if (advanceIf(KeywordTokenJS.DELETE)) {
			var pos = current.prev;
			var expr = unary();

			if (expr instanceof AstGetBase get) {
				return new AstDelete(get).pos(pos);
			} else {
				return Boolean.FALSE;
			}
		} else if (advanceIf(KeywordTokenJS.AWAIT)) {
			var pos = current.prev;
			var expr = unary();
			return new AstAwait(expr).pos(pos);
		}

		return call();
	}

	private AstParam param() {
		var param = new AstParam(name(ParseErrorType.EXP_PARAM_NAME));

		if (advanceIf(SymbolTokenJS.COL)) {
			param.type = type();
		}

		if (advanceIf(SymbolTokenJS.SET)) {
			param.defaultValue = expression();
		}

		return param;
	}

	private Object call() {
		var newToken = current.is(KeywordTokenJS.NEW) ? advance() : null;

		var expr = primary();

		while (true) {
			var pos = current;

			if (current.is(SymbolTokenJS.LP)) {
				AstCall call = new AstCall().pos(newToken == null ? pos : newToken);
				call.function = expr;
				call.arguments = arguments();
				call.hasNew = newToken != null;
				expr = call;
			} else if (advanceIf(SymbolTokenJS.DOT)) {
				var name = name(ParseErrorType.EXP_NAME_DOT.format(current));
				expr = namedGet(expr, name, pos);
			} else if (advanceIf(SymbolTokenJS.OC)) {
				var name = name(ParseErrorType.EXP_NAME_OC);
				expr = new AstGetByNameOptional(expr, name).pos(pos);
			} else if (advanceIf(SymbolTokenJS.ADD1)) {
				var ast = new AstAdd1R();
				ast.node = expr;
				ast.pos(pos);
				expr = ast;
			} else if (advanceIf(SymbolTokenJS.SUB1)) {
				var ast = new AstSub1R();
				ast.node = expr;
				ast.pos(pos);
				expr = ast;
			} else if (advanceIf(SymbolTokenJS.LS)) {
				var keyo = expression();

				if (keyo instanceof CharSequence str) {
					expr = namedGet(expr, str.toString(), pos);
				} else if (keyo instanceof Number n) {
					expr = new AstGetByIndex(expr, n.intValue()).pos(pos);
				} else {
					expr = new AstGetByEvaluable(expr, keyo).pos(pos);
				}

				consume(SymbolTokenJS.RS, ParseErrorType.EXP_RS_KEY);
			} else if (advanceIf(SymbolTokenJS.TEMPLATE_LITERAL)) {
				var templateLiteral = templateLiteral(pos);
				templateLiteral.tag = expr;
				expr = templateLiteral;
			} else {
				break;
			}
		}

		return expr;
	}

	private Object namedGet(Object expr, String name, TokenPosSupplier pos) {
		if (name.equals("__proto__")) {
			return new AstObjectPrototype(expr).pos(pos);
		} else if (name.equals("prototype")) {
			return new AstClassPrototype(expr).pos(pos);
		} else {
			return new AstGetByName(expr, name).pos(pos);
		}
	}

	private Object primary() {
		var pos = current;

		if (!(pos.token instanceof Token)) {
			advance();
			return pos.token;
		}

		var literal = ((Token) pos.token).toEvaluable(this, pos.getPos());

		if (literal != null) {
			advance();

			if (literal instanceof Ast ast) {
				ast.pos(pos);
			}

			return literal;
		}

		int funcFlags = 0;

		if (advanceIf(KeywordTokenJS.ASYNC)) {
			funcFlags |= AstFunction.Mod.ASYNC;
		}

		if (advanceIf(KeywordTokenJS.SUPER)) {
			return new AstSuperExpression().pos(pos);
		} else if (advanceIf(KeywordTokenJS.THIS)) {
			return new AstThisExpression().pos(pos);
		} else if (advanceIf(KeywordTokenJS.ARGUMENTS)) {
			return new AstArguments().pos(pos);
		} else if (advanceIf(KeywordTokenJS.FUNCTION)) {
			return functionExpression(funcFlags);
		} else if (current.isIdentifier()) { // handle all keywords before this
			var name = name(ParseErrorType.EXP_VAR_NAME);

			if (advanceIf(SymbolTokenJS.ARROW)) {
				funcFlags |= AstFunction.Mod.ARROW;
				var apos = current.prev;
				var body = block(true, "");
				return new AstFunction(new AstParam[]{new AstParam(name)}, body, funcFlags).pos(apos);
			} else {
				//current = pos; // required to jump back because x = y statement and default param look the same
				//advance();
				return new AstGetScopeMember(name).pos(pos);
			}
		} else if (current.is(SymbolTokenJS.LP)) {
			if (current.next.is(SymbolTokenJS.RP)) {
				funcFlags |= AstFunction.Mod.ARROW;
				return function(null, null, funcFlags).pos(pos);
			} else if (current.next.isIdentifier() && (current.next.next.is(SymbolTokenJS.RP) && current.next.next.next.is(SymbolTokenJS.ARROW) || current.next.next.is(SymbolTokenJS.COMMA))) {
				funcFlags |= AstFunction.Mod.ARROW;
				return function(null, null, funcFlags).pos(pos);
			} else {
				advance();
				// var lp = previous();
				var expr = expression();
				consume(SymbolTokenJS.RP, ParseErrorType.EXP_RP_EXPR);
				// return new AstGrouping(expr).pos(lp);
				return expr;
			}
		} else if (advanceIf(SymbolTokenJS.LS)) {
			var list = new ArrayList<>();

			ignoreComma();

			while (!current.is(SymbolTokenJS.RS)) {
				list.add(expression());
				ignoreComma();
			}

			consume(SymbolTokenJS.RS, ParseErrorType.EXP_RS_ARRAY);
			return new AstList(list).pos(pos);
		} else if (advanceIf(SymbolTokenJS.LC)) {
			var map = new LinkedHashMap<String, Object>();

			ignoreComma();

			while (!current.is(SymbolTokenJS.RC)) {
				int flags = 0;

				if ((current.is(KeywordTokenJS.GET) || current.is(KeywordTokenJS.SET) && current.next.isIdentifier() && current.next.next.is(SymbolTokenJS.LP))) {
					if (advanceIf(KeywordTokenJS.GET)) {
						flags |= AstFunction.Mod.GET;
					} else if (advanceIf(KeywordTokenJS.SET)) {
						flags |= AstFunction.Mod.SET;
					}
				}

				var name = current.token instanceof CharSequence str ? str.toString() : name(ParseErrorType.EXP_VAR_NAME);

				if (current.is(SymbolTokenJS.LP)) {
					var func = function(null, null, flags);
					func.functionName = name;
					map.put(name, func);
				} else if (current.is(SymbolTokenJS.COMMA)) {
					map.put(name, new AstGetScopeMember(name).pos(current.prev));
				} else {
					consume(SymbolTokenJS.COL, ParseErrorType.EXP_COL_OBJECT);
					map.put(name, expression());
				}

				ignoreComma();
			}

			consume(SymbolTokenJS.RC, ParseErrorType.EXP_RC_OBJECT);
			return new AstMap(map).pos(pos);
		} else if (advanceIf(SymbolTokenJS.TDOT)) {
			return new AstSpread(expression()).pos(pos);
		} else if (advanceIf(SymbolTokenJS.TEMPLATE_LITERAL)) {
			if (advanceIf(SymbolTokenJS.TEMPLATE_LITERAL)) {
				return "";
			}

			return templateLiteral(pos);
		}

		throw new ParseError(pos, ParseErrorType.EXP_EXPR.format(pos.token));
	}

	private AstTemplateLiteral templateLiteral(TokenPosSupplier pos) {
		var parts = new ArrayList<>(3);

		while (true) {
			if (advanceIf(SymbolTokenJS.TEMPLATE_LITERAL)) {
				break;
			} else if (advanceIf(SymbolTokenJS.TEMPLATE_LITERAL_VAR)) {
				parts.add(expression());
				consume(SymbolTokenJS.RC, ParseErrorType.EXP_RC_TEMPLATE_LITERAL);
			} else {
				var nextEval = current.token instanceof Token tk ? tk.toEvaluable(this, current.pos) : current.token;

				if (nextEval != null) {
					parts.add(nextEval);
					advance();
				} else {
					throw new ParseError(current, ParseErrorType.EXP_EXPR.format(current.token));
				}
			}
		}

		return new AstTemplateLiteral(parts.toArray(Empty.OBJECTS)).pos(pos);
	}

	private Evaluable functionExpression(int flags) {
		if (advanceIf(KeywordTokenJS.GET)) {
			flags |= AstFunction.Mod.GET;
		} else if (advanceIf(KeywordTokenJS.SET)) {
			flags |= AstFunction.Mod.SET;
		}

		String funcName = null;

		if (current.isIdentifier()) {
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
		var s = current.identifier(error);
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
