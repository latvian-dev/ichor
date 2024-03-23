package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstAwait;
import dev.latvian.apps.ichor.ast.expression.AstCall;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;
import dev.latvian.apps.ichor.ast.expression.AstClassPrototype;
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
import dev.latvian.apps.ichor.ast.expression.AstObjectPrototype;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.ast.expression.AstSet;
import dev.latvian.apps.ichor.ast.expression.AstSpread;
import dev.latvian.apps.ichor.ast.expression.AstTemplateLiteral;
import dev.latvian.apps.ichor.ast.expression.AstTernary;
import dev.latvian.apps.ichor.ast.expression.AstType;
import dev.latvian.apps.ichor.ast.expression.AstTypeOf;
import dev.latvian.apps.ichor.ast.expression.unary.AstAdd1R;
import dev.latvian.apps.ichor.ast.expression.unary.AstSub1R;
import dev.latvian.apps.ichor.ast.statement.AstBlock;
import dev.latvian.apps.ichor.ast.statement.AstBreak;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.ast.statement.AstContinue;
import dev.latvian.apps.ichor.ast.statement.AstDebugger;
import dev.latvian.apps.ichor.ast.statement.AstDeclareStatement;
import dev.latvian.apps.ichor.ast.statement.AstDoWhile;
import dev.latvian.apps.ichor.ast.statement.AstEmptyBlock;
import dev.latvian.apps.ichor.ast.statement.AstExport;
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
import dev.latvian.apps.ichor.ast.statement.AstYield;
import dev.latvian.apps.ichor.ast.statement.LabeledStatement;
import dev.latvian.apps.ichor.ast.statement.decl.AstDeclaration;
import dev.latvian.apps.ichor.ast.statement.decl.DestructuredArray;
import dev.latvian.apps.ichor.ast.statement.decl.DestructuredArrayName;
import dev.latvian.apps.ichor.ast.statement.decl.DestructuredObject;
import dev.latvian.apps.ichor.ast.statement.decl.DestructuredObjectName;
import dev.latvian.apps.ichor.ast.statement.decl.NameDeclaration;
import dev.latvian.apps.ichor.ast.statement.decl.NestedDestructuredPart;
import dev.latvian.apps.ichor.error.ParseError;
import dev.latvian.apps.ichor.error.ParseErrorMessage;
import dev.latvian.apps.ichor.error.ParseErrorType;
import dev.latvian.apps.ichor.error.WIPFeatureError;
import dev.latvian.apps.ichor.exit.ExitType;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.token.Keyword;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.Symbol;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPosSupplier;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

@SuppressWarnings({"UnusedReturnValue"})
public class Parser {
	private record BinaryOp(Function<Parser, Object> next, Token... tokens) {
	}

	private static final Token[] VAR_TOKENS = {Keyword.VAR, Keyword.LET, Keyword.CONST};
	private static final Token[] UNARY_TOKENS = {Symbol.NOT, Symbol.ADD, Symbol.SUB, Symbol.BNOT, Symbol.ADD1, Symbol.SUB1};
	private static final Token[] CLASS_TOKENS = {Keyword.CLASS, Keyword.INTERFACE};
	private static final BinaryOp BIN_NC = new BinaryOp(Parser::or, Symbol.NC);
	private static final BinaryOp BIN_OR = new BinaryOp(Parser::and, Symbol.OR);
	private static final BinaryOp BIN_AND = new BinaryOp(Parser::bitwiseOr, Symbol.AND);
	private static final BinaryOp BIN_BITWISE_OR = new BinaryOp(Parser::bitwiseXor, Symbol.BOR);
	private static final BinaryOp BIN_BITWISE_XOR = new BinaryOp(Parser::bitwiseAnd, Symbol.XOR);
	private static final BinaryOp BIN_BITWISE_AND = new BinaryOp(Parser::equality, Symbol.BAND);
	private static final BinaryOp BIN_EQUALITY = new BinaryOp(Parser::comparison, Symbol.EQ, Symbol.NEQ, Symbol.SEQ, Symbol.SNEQ);
	private static final BinaryOp BIN_COMPARISON = new BinaryOp(Parser::shift, Symbol.LT, Symbol.GT, Symbol.LTE, Symbol.GTE, Keyword.IN, Keyword.INSTANCEOF);
	private static final BinaryOp BIN_SHIFT = new BinaryOp(Parser::additive, Symbol.LSH, Symbol.RSH, Symbol.URSH);
	private static final BinaryOp BIN_ADDITIVE = new BinaryOp(Parser::multiplicative, Symbol.ADD, Symbol.SUB);
	private static final BinaryOp BIN_MULTIPLICATIVE = new BinaryOp(Parser::exponential, Symbol.MUL, Symbol.DIV, Symbol.MOD);
	private static final BinaryOp BIN_EXPONENTIAL = new BinaryOp(Parser::unary, Symbol.POW);

	private static final Token[] FOR_OF_IN_TOKENS = {
			Keyword.OF,
			Keyword.IN
	};

	private final RootScope rootScope;
	private PositionedToken current;
	private final Stack<LabeledStatement> labeledStatements;
	private final Map<String, AstType.Generic> genericTypeCache;

	public Parser(RootScope scope, PositionedToken r) {
		rootScope = scope;
		current = r;
		labeledStatements = new Stack<>();
		genericTypeCache = new HashMap<>();
	}

	public RootScope getRootScope() {
		return rootScope;
	}

	public Interpretable parse() {
		var list = new ArrayList<Interpretable>();

		while (current != null && current.exists()) {
			list.add(declaration());
		}

		var group = AstInterpretableGroup.optimized(list);
		group.optimize(this);
		return group;
	}

	public Object optimize(Object o) {
		return o instanceof Optimizable optimizable ? optimizable.optimize(this) : o;
	}

	private Interpretable declaration() {
		var pos = current;

		if (advanceIf(Keyword.EXPORT)) {
			return new AstExport(declaration()).pos(pos);
		}

		int funcFlags = 0;

		if (advanceIf(Keyword.ASYNC)) {
			funcFlags |= AstFunction.Mod.ASYNC;
		}

		if (advanceIf(CLASS_TOKENS)) {
			return classDeclaration(pos);
		} else if (advanceIf(Keyword.FUNCTION)) {
			funcFlags |= AstFunction.Mod.STATEMENT;

			if (advanceIf(Symbol.MUL)) {
				funcFlags |= AstFunction.Mod.GENERATOR;
			}

			var name = identifier(ParseErrorType.EXP_FUNC_NAME);
			var func = function(null, null, funcFlags);
			func.functionName = name;
			ignoreSemi();
			return new AstFunctionDeclareStatement(func).pos(pos);
		} else if (advanceIf(VAR_TOKENS)) {
			var v = varDeclaration(current.prev);
			ignoreSemi();
			return v;
		} else {
			return statement();
		}
	}

	private AstType type() {
		var id = identifier(ParseErrorType.EXP_TYPE_NAME);

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
			default -> genericTypeCache.computeIfAbsent(id, s -> new AstType.Generic(s, AstType.CastFunc.NONE));
		};

		if (advanceIf(Symbol.LT)) {
			var list = new ArrayList<AstType>();

			do {
				list.add(type());
			} while (advanceIf(Symbol.COMMA));

			consume(Symbol.GT, ParseErrorType.EXP_GT_TYPE);
			type = new AstType.Typed(type, list.toArray(Empty.AST_TYPES));
		}

		while (advanceIf(Symbol.LS)) {
			consume(Symbol.RS, ParseErrorType.EXP_RS_ARRAY);
			type = new AstType.Array(type);
		}

		while (advanceIf(Symbol.BOR)) {
			type = new AstType.Or(type, type());
		}

		return AstType.Generic.ANY;
	}

	private void ignoreSemi() {
		while (current.is(Symbol.SEMI)) {
			advance();
		}
	}

	private boolean ignoreComma() {
		boolean found = false;

		while (current.is(Symbol.COMMA)) {
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
		AstClass astClass = new AstClass(identifier(ParseErrorType.EXP_CLASS_NAME)).pos(pos);

		if (advanceIf(Keyword.EXTENDS)) {
			astClass.parent = new AstGetScopeMember(identifier(ParseErrorType.EXP_CLASS_NAME));
		}

		consume(Symbol.LC, ParseErrorType.EXP_LC_CLASS);

		while (!current.is(Symbol.RC)) {
			var fpos = current;

			int modifiers = AstFunction.Mod.CLASS;
			var type = AstClassFunction.Type.METHOD;

			if (advanceIf(Keyword.STATIC)) {
				modifiers |= AstFunction.Mod.STATIC;
			}

			if (advanceIf(Keyword.GET)) {
				modifiers |= AstFunction.Mod.GET;
				type = AstClassFunction.Type.GETTER;
			}

			if (advanceIf(Keyword.SET)) {
				modifiers |= AstFunction.Mod.SET;
				type = AstClassFunction.Type.SETTER;
			}

			var fname = identifier(ParseErrorType.EXP_FUNC_NAME);

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

		consume(Symbol.RC, ParseErrorType.EXP_RC_CLASS);
		return astClass;
	}

	private Object[] arguments() {
		consume(Symbol.LP, ParseErrorType.EXP_LP_ARGS);

		var list = new ArrayList<>();

		while (!current.is(Symbol.RP)) {
			list.add(expression());
			ignoreComma();
		}

		consume(Symbol.RP, ParseErrorType.EXP_RP_ARGS);
		return list.toArray(Empty.OBJECTS);
	}

	private Interpretable statement() {
		if (current.isIdentifier() && current.next.is(Symbol.COL)) {
			var pos = current;
			var name = varIdentifier();
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

		if (advanceIf(Keyword.FOR)) {
			return forStatement(pos, label);
		} else if (advanceIf(Keyword.IF)) {
			return ifStatement(pos, label);
		} else if (advanceIf(Keyword.RETURN)) {
			return returnStatement(pos);
		} else if (advanceIf(Keyword.BREAK)) {
			return exitStatement(pos, ExitType.BREAK);
		} else if (advanceIf(Keyword.CONTINUE)) {
			return exitStatement(pos, ExitType.CONTINUE);
		} else if (advanceIf(Keyword.WHILE)) {
			return whileStatement(pos, label);
		} else if (advanceIf(Keyword.DO)) {
			return doWhileStatement(pos, label);
		} else if (advanceIf(Keyword.TRY)) {
			return tryStatement(pos);
		} else if (advanceIf(Keyword.SWITCH)) {
			return switchStatement(pos);
		} else if (advanceIf(Keyword.THROW)) {
			return throwStatement(pos);
		} else if (advanceIf(Keyword.YIELD)) {
			return yieldStatement(pos);
		} else if (current.is(Symbol.LC)) {
			return block(false, label);
		} else if ((current.is(Keyword.THIS) || current.is(Keyword.SUPER)) && current.next.is(Symbol.LP)) {
			advance();
			var arguments = arguments();
			return (pos.is(Keyword.THIS) ? new AstThisStatement(arguments) : new AstSuperStatement(arguments)).pos(pos);
		} else if (advanceIf(Keyword.DEBUGGER)) {
			ignoreSemi();
			return new AstDebugger().pos(pos);
		}

		return expressionStatement(true);
	}

	private Interpretable forStatement(PositionedToken pos, String label) {
		consume(Symbol.LP, ParseErrorType.EXP_LP_FOR);

		Interpretable initializer;

		if (current.is(Symbol.SEMI)) {
			initializer = new AstEmptyBlock(false).pos(pos);
		} else if (advanceIf(VAR_TOKENS)) {
			initializer = varDeclaration(current.prev);
		} else if (current.isIdentifier() && current.next.is(FOR_OF_IN_TOKENS)) {
			var n = varIdentifier();
			initializer = new AstSingleDeclareStatement(Keyword.LET, new NameDeclaration(n), Special.UNDEFINED);
		} else {
			initializer = expressionStatement(false);
		}

		if (advanceIf(FOR_OF_IN_TOKENS)) {
			var assignToken = Keyword.LET;
			AstDeclaration declaration;

			if (initializer instanceof AstSingleDeclareStatement s) {
				assignToken = s.assignToken;
				declaration = s.declaration;
			} else {
				throw new ParseError(pos, ParseErrorType.UNSUPPORTED_FOR_DECL);
			}

			boolean of = current.prev.is(Keyword.OF);
			AstForOf ast = (of ? new AstForOf() : new AstForIn()).pos(pos);
			ast.label = label;
			ast.assignToken = assignToken;
			ast.declaration = declaration;
			ast.from = expression();

			consume(Symbol.RP, ParseErrorType.EXP_RP_FOR);

			pushExit(ast);
			ast.body = statementBody();
			popExit(ast);

			return ast;
		} else {
			consume(Symbol.SEMI, ParseErrorType.EXP_SEMI_FOR_INIT);
		}

		AstFor ast = new AstFor().pos(pos);
		ast.label = label;
		ast.initializer = initializer;

		if (!current.is(Symbol.SEMI)) {
			ast.condition = expression();
		}

		consume(Symbol.SEMI, ParseErrorType.EXP_SEMI_FOR_COND);

		if (!current.is(Symbol.RP)) {
			ast.increment = expressionStatement(true);
		} else {
			ast.increment = new AstEmptyBlock(false);
		}

		consume(Symbol.RP, ParseErrorType.EXP_RP_FOR);
		pushExit(ast);
		ast.body = statementBody();
		popExit(ast);

		ignoreSemi();

		return ast;
	}

	private Interpretable ifStatement(PositionedToken pos, String label) {
		AstIf ast = new AstIf().pos(pos);
		ast.label = label;

		consume(Symbol.LP, ParseErrorType.EXP_LP_IF_COND);
		ast.condition = expression();
		consume(Symbol.RP, ParseErrorType.EXP_RP_IF_COND);

		pushExit(ast);
		ast.trueBody = statementBody();

		if (advanceIf(Keyword.ELSE)) {
			ast.falseBody = statementBody();
		}

		popExit(ast);
		ignoreSemi();
		return ast;
	}

	private Interpretable returnStatement(PositionedToken pos) {
		Object value = Special.UNDEFINED;

		if (!current.is(Symbol.SEMI) && !current.is(Symbol.RC)) {
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
		var stmt = findStop(pos, current.isIdentifier() ? varIdentifier() : "", type);
		ignoreSemi();
		return (type == ExitType.BREAK ? new AstBreak(stmt) : new AstContinue(stmt)).pos(pos);
	}

	private Interpretable varDeclaration(PositionedToken c) {
		return varDeclaration((DeclaringToken) c.token, c);
	}

	private Interpretable varDeclaration(DeclaringToken type, TokenPosSupplier pos) {
		var list = new ArrayList<AstDeclareStatement.Part>(1);

		do {
			var decl = destructDeclaration();

			if (advanceIf(Symbol.SET)) {
				list.add(new AstDeclareStatement.Part(decl, expression()));
			} else {
				list.add(new AstDeclareStatement.Part(decl, Special.UNDEFINED));
			}
		} while (ignoreComma());

		// ignoreSemi();

		if (list.size() == 1) {
			return new AstSingleDeclareStatement(type, list.get(0).declaration, list.get(0).value).pos(pos);
		}

		return new AstMultiDeclareStatement(type, list.toArray(AstDeclareStatement.Part.EMPTY)).pos(pos);
	}

	private AstDeclaration destructDeclaration() {
		if (advanceIf(Symbol.LC)) { // { x }
			if (advanceIf(Symbol.RC)) { // {}
				return DestructuredObject.EMPTY_OBJECT;
			}

			ignoreComma();
			var parts = new ArrayList<AstDeclaration>(3);
			var rest = "";
			var ignoredRest = new HashSet<String>();

			while (true) {
				if (advanceIf(Symbol.RC)) {
					break;
				} else if (advanceIf(Symbol.TDOT)) { // ...rest
					rest = varIdentifier();
					consume(Symbol.RC, ParseErrorType.EXP_RC_BLOCK);
					break;
				} else {
					var n = new DestructuredObjectName(varIdentifier());

					if (advanceIf(Symbol.COL)) { // x:
						if (current.is(Symbol.LC) || current.is(Symbol.LS)) { // x: { y } | x: [ y ]
							parts.add(new NestedDestructuredPart(n.name, destructDeclaration()));
							ignoreComma();
							continue;
						}

						n.rename = varIdentifier(); // x: y
					}

					if (advanceIf(Symbol.SET)) { // x = default
						n.defaultValue = expression();
					}

					parts.add(n);
					ignoredRest.add(n.name);
					ignoreComma();
				}
			}

			return new DestructuredObject(parts.toArray(AstDeclaration.EMPTY), rest, Set.copyOf(ignoredRest));
		} else if (advanceIf(Symbol.LS)) { // [ x ]
			if (advanceIf(Symbol.RS)) { // []
				return DestructuredArray.EMPTY_ARRAY;
			}

			var parts = new ArrayList<AstDeclaration>(3);
			var rest = "";
			var index = 0;

			while (true) {
				if (advanceIf(Symbol.RS)) {
					break;
				} else if (advanceIf(Symbol.TDOT)) {
					rest = varIdentifier();
					consume(Symbol.RS, ParseErrorType.EXP_RS_ARRAY);
					break;
				} else if (advanceIf(Symbol.COMMA)) {
					index++;
				} else if (advanceIf(Symbol.LS)) {
					throw new WIPFeatureError().pos(current); // nested array destruct
				} else {
					var name = varIdentifier();
					parts.add(new DestructuredArrayName(name, index));
				}
			}

			return new DestructuredArray(parts.toArray(AstDeclaration.EMPTY), rest, index);
		} else {
			var decl = new NameDeclaration(varIdentifier()); // x

			if (advanceIf(Symbol.COL)) { // x:
				if (current.is(Symbol.LC) || current.is(Symbol.LS)) { // x: { y } | x: [ y ]
					return new NestedDestructuredPart(decl.name, destructDeclaration());
				}

				decl.type = type(); // x: string
			}

			return decl;
		}
	}

	private Interpretable whileStatement(PositionedToken pos, String label) {
		AstWhile ast = new AstWhile().pos(pos);
		ast.label = label;
		consume(Symbol.LP, ParseErrorType.EXP_LP_WHILE_COND);
		ast.condition = expression();
		consume(Symbol.RP, ParseErrorType.EXP_RP_WHILE_COND);
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
		consume(Keyword.WHILE, ParseErrorType.EXP_TOKEN.format("while"));
		consume(Symbol.LP, ParseErrorType.EXP_LP_WHILE_COND);
		ast.condition = expression();
		consume(Symbol.RP, ParseErrorType.EXP_RP_WHILE_COND);
		ignoreSemi();
		return ast;
	}

	private Interpretable tryStatement(PositionedToken pos) {
		var tryBlock = statementBody();
		AstTry.AstCatch catchBlock = null;
		Interpretable finallyBlock = null;

		if (advanceIf(Keyword.CATCH)) {
			consume(Symbol.LP, ParseErrorType.EXP_LP_ARGS);
			var name = identifier(ParseErrorType.EXP_PARAM_NAME);
			consume(Symbol.RP, ParseErrorType.EXP_RP_ARGS);
			catchBlock = new AstTry.AstCatch(name, statementBody());
		}

		if (advanceIf(Keyword.FINALLY)) {
			finallyBlock = statementBody();
		}

		return new AstTry(tryBlock, catchBlock, finallyBlock).pos(pos);
	}

	private Interpretable switchStatement(PositionedToken pos) {
		AstSwitch ast = new AstSwitch().pos(pos);
		pushExit(ast);

		consume(Symbol.LP, ParseErrorType.EXP_LP_ARGS);
		ast.expression = expression();
		consume(Symbol.RP, ParseErrorType.EXP_RP_ARGS);
		consume(Symbol.LC, ParseErrorType.EXP_LC_BLOCK);

		var cases = new ArrayList<AstSwitch.AstCase>(1);

		while (!current.is(Symbol.RC)) {
			var cpos = current;

			if (cpos.is(Keyword.CASE) || cpos.is(Keyword.DEFAULT)) {
				advance();
				var value = cpos.is(Keyword.CASE) ? expression() : null;
				consume(Symbol.COL, ParseErrorType.EXP_COL_CASE);

				var stmts = new ArrayList<Interpretable>();

				while (!current.is(Symbol.RC) && !current.is(Keyword.CASE) && !current.is(Keyword.DEFAULT)) {
					stmts.add(declaration());
				}

				if (cpos.is(Keyword.CASE)) {
					cases.add(new AstSwitch.AstCase(value, AstInterpretableGroup.optimized(stmts)));
				} else {
					ast.defaultCase = new AstSwitch.AstCase(value, AstInterpretableGroup.optimized(stmts));
				}
			} else {
				throw new ParseError(current, ParseErrorType.EXP_CASE);
			}
		}

		consume(Symbol.RC, ParseErrorType.EXP_RC_BLOCK);
		popExit(ast);

		ast.cases = cases.toArray(AstSwitch.AstCase.EMPTY);
		return ast;
	}

	private Interpretable throwStatement(PositionedToken pos) {
		consume(Symbol.LP, ParseErrorType.EXP_LP_ARGS);
		var exception = expression();
		consume(Symbol.RP, ParseErrorType.EXP_RP_ARGS);
		ignoreSemi();
		return new AstThrow(exception).pos(pos);
	}

	private Interpretable yieldStatement(PositionedToken pos) {
		boolean generator = advanceIf(Symbol.MUL);

		Object value = Special.UNDEFINED;

		if (!current.is(Symbol.SEMI) && !current.is(Symbol.RC)) {
			value = expression();
		}

		ignoreSemi();
		return new AstYield(generator, value).pos(pos);
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

		consume(Symbol.LP, ParseErrorType.EXP_LP_ARGS);
		var parameters = new ArrayList<AstParam>(1);
		if (!current.is(Symbol.RP)) {
			do {
				if (advanceIf(Symbol.TDOT)) {
					if ((modifiers & AstFunction.Mod.VARARGS) == 0) {
						modifiers |= AstFunction.Mod.VARARGS;
					} else {
						throw new ParseError(current.prev, ParseErrorType.MULTIPLE_VARARGS);
					}
				}

				parameters.add(param());
			} while (ignoreComma());
		}
		consume(Symbol.RP, ParseErrorType.EXP_RP_ARGS);

		if (isArrow) {
			consume(Symbol.ARROW, ParseErrorType.EXP_ARROW);
		}

		var body = block(true, "");

		if (owner != null) {
			return new AstClassFunction(owner, parameters.toArray(Empty.AST_PARAMS), body, modifiers, type);
		}

		return new AstFunction(parameters.toArray(Empty.AST_PARAMS), body, modifiers);
	}

	private Interpretable block(boolean forceReturn, String label) {
		var pos = current;

		if (advanceIf(Symbol.SEMI)) {
			return new AstEmptyBlock(forceReturn).pos(pos);
		} else if (advanceIf(Symbol.LC)) {
			if (advanceIf(Symbol.RC)) {
				return new AstEmptyBlock(forceReturn).pos(pos);
			}

			AstBlock ast = new AstBlock().pos(pos);
			ast.label = label;

			var statements = new ArrayList<Interpretable>();
			pushExit(ast);

			while (!current.is(Symbol.RC)) {
				statements.add(declaration());
			}

			consume(Symbol.RC, ParseErrorType.EXP_RC_BLOCK);
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

	public Object expression() {
		return assignment();
	}

	private Object assignment() {
		var expr = nc();
		var pos = current;

		if (advanceIf(Symbol.SET)) {
			var value = expression();

			if (expr instanceof AstGetBase get) {
				return new AstSet(get, value).pos(pos);
			}

			throw new ParseError(pos, ParseErrorType.INVALID_TARGET.format(value));
		} else if (advanceIf(Symbol.SET_OP_TOKENS)) {
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
		} else if (advanceIf(Symbol.HOOK)) {
			var ternary = new AstTernary();
			ternary.condition = expr;
			ternary.ifTrue = expression();
			consume(Symbol.COL, ParseErrorType.EXP_TERNARY_COL);
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
		} else if (advanceIf(Keyword.TYPEOF)) {
			return new AstTypeOf(unary());
		} else if (advanceIf(Keyword.DELETE)) {
			var pos = current.prev;
			var expr = unary();

			if (expr instanceof AstGetBase get) {
				return new AstDelete(get).pos(pos);
			} else {
				return Boolean.FALSE;
			}
		} else if (advanceIf(Keyword.AWAIT)) {
			var pos = current.prev;
			var expr = unary();
			return new AstAwait(expr).pos(pos);
		}

		return call();
	}

	private AstParam param() {
		var param = new AstParam(identifier(ParseErrorType.EXP_PARAM_NAME));

		if (advanceIf(Symbol.COL)) {
			param.type = type();
		}

		if (advanceIf(Symbol.SET)) {
			param.defaultValue = expression();
		}

		return param;
	}

	private Object call() {
		var newToken = current.is(Keyword.NEW) ? advance() : null;

		var expr = primary();

		while (true) {
			var pos = current;

			if (current.is(Symbol.LP)) {
				AstCall call = new AstCall().pos(newToken == null ? pos : newToken);
				call.function = expr;
				call.arguments = arguments();
				call.hasNew = newToken != null;
				expr = call;
			} else if (advanceIf(Symbol.DOT)) {
				var name = identifier(ParseErrorType.EXP_NAME_DOT.format(current));
				expr = namedGet(expr, name, pos);
			} else if (advanceIf(Symbol.OC)) {
				var name = identifier(ParseErrorType.EXP_NAME_OC);
				expr = new AstGetByNameOptional(expr, name).pos(pos);
			} else if (advanceIf(Symbol.ADD1)) {
				var ast = new AstAdd1R();
				ast.node = expr;
				ast.pos(pos);
				expr = ast;
			} else if (advanceIf(Symbol.SUB1)) {
				var ast = new AstSub1R();
				ast.node = expr;
				ast.pos(pos);
				expr = ast;
			} else if (advanceIf(Symbol.LS)) {
				var keyo = expression();

				if (keyo instanceof CharSequence str) {
					expr = namedGet(expr, str.toString(), pos);
				} else if (keyo instanceof Number n) {
					expr = new AstGetByIndex(expr, n.intValue()).pos(pos);
				} else {
					expr = new AstGetByEvaluable(expr, keyo).pos(pos);
				}

				consume(Symbol.RS, ParseErrorType.EXP_RS_KEY);
			} else if (advanceIf(Symbol.TEMPLATE_LITERAL)) {
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

		if (advanceIf(Keyword.ASYNC)) {
			funcFlags |= AstFunction.Mod.ASYNC;
		}

		if (advanceIf(Keyword.FUNCTION)) {
			return functionExpression(funcFlags);
		} else if (current.isIdentifier()) { // handle all keywords before this
			var name = varIdentifier();

			if (advanceIf(Symbol.ARROW)) {
				funcFlags |= AstFunction.Mod.ARROW;
				var apos = current.prev;
				var body = block(true, "");
				return new AstFunction(new AstParam[]{new AstParam(name)}, body, funcFlags).pos(apos);
			} else {
				//current = pos; // required to jump back because x = y statement and default param look the same
				//advance();
				return new AstGetScopeMember(name).pos(pos);
			}
		} else if (current.is(Symbol.LP)) {
			if (current.next.is(Symbol.RP)) {
				funcFlags |= AstFunction.Mod.ARROW;
				return function(null, null, funcFlags).pos(pos);
			} else if (current.next.isIdentifier() && (current.next.next.is(Symbol.RP) && current.next.next.next.is(Symbol.ARROW) || current.next.next.is(Symbol.COMMA))) {
				funcFlags |= AstFunction.Mod.ARROW;
				return function(null, null, funcFlags).pos(pos);
			} else {
				advance();
				// var lp = previous();
				var expr = expression();
				consume(Symbol.RP, ParseErrorType.EXP_RP_EXPR);
				// return new AstGrouping(expr).pos(lp);
				return expr;
			}
		} else if (advanceIf(Symbol.LS)) {
			var list = new ArrayList<>();

			ignoreComma();

			while (!current.is(Symbol.RS)) {
				list.add(expression());
				ignoreComma();
			}

			consume(Symbol.RS, ParseErrorType.EXP_RS_ARRAY);
			return new AstList(list).pos(pos);
		} else if (advanceIf(Symbol.LC)) {
			var map = new LinkedHashMap<String, Object>();

			ignoreComma();

			while (!current.is(Symbol.RC)) {
				int flags = 0;

				if ((current.is(Keyword.GET) || current.is(Keyword.SET) && current.next.isIdentifier() && current.next.next.is(Symbol.LP))) {
					if (advanceIf(Keyword.GET)) {
						flags |= AstFunction.Mod.GET;
					} else if (advanceIf(Keyword.SET)) {
						flags |= AstFunction.Mod.SET;
					}
				}

				var name = keyIdentifier();

				if (current.is(Symbol.LP)) {
					var func = function(null, null, flags);
					func.functionName = name;
					map.put(name, func);
				} else if (current.is(Symbol.COMMA) || current.is(Symbol.RC)) {
					map.put(name, new AstGetScopeMember(name).pos(current.prev));
				} else {
					consume(Symbol.COL, ParseErrorType.EXP_COL_OBJECT);
					map.put(name, expression());
				}

				ignoreComma();
			}

			consume(Symbol.RC, ParseErrorType.EXP_RC_OBJECT);
			return new AstMap(map).pos(pos);
		} else if (advanceIf(Symbol.TDOT)) {
			return new AstSpread(expression()).pos(pos);
		} else if (advanceIf(Symbol.TEMPLATE_LITERAL)) {
			if (advanceIf(Symbol.TEMPLATE_LITERAL)) {
				return "";
			}

			return templateLiteral(pos);
		}

		throw new ParseError(pos, ParseErrorType.EXP_EXPR.format(pos.token));
	}

	private AstTemplateLiteral templateLiteral(TokenPosSupplier pos) {
		var parts = new ArrayList<>(3);

		while (true) {
			if (advanceIf(Symbol.TEMPLATE_LITERAL)) {
				break;
			} else if (advanceIf(Symbol.TEMPLATE_LITERAL_VAR)) {
				parts.add(expression());
				consume(Symbol.RC, ParseErrorType.EXP_RC_TEMPLATE_LITERAL);
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
		if (advanceIf(Symbol.MUL)) {
			flags |= AstFunction.Mod.GENERATOR;
		}

		if (advanceIf(Keyword.GET)) {
			flags |= AstFunction.Mod.GET;
		} else if (advanceIf(Keyword.SET)) {
			flags |= AstFunction.Mod.SET;
		}

		String funcName = null;

		if (current.isIdentifier()) {
			funcName = identifier(ParseErrorType.EXP_FUNC_NAME);
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

	private String identifier(ParseErrorMessage error) {
		var s = current.identifier(error);
		advance();
		return s;
	}

	private String varIdentifier() {
		return identifier(ParseErrorType.EXP_VAR_NAME.format(current.token));
	}

	private String keyIdentifier() {
		if (Special.isInvalid(current.token) || current.token instanceof CharSequence || current.token instanceof Boolean || current.token instanceof KeywordToken) {
			return String.valueOf(advance().token);
		} else if (current.token instanceof Number) {
			return AstStringBuilder.wrapNumber(advance().token);
		} else {
			return varIdentifier();
		}
	}

	private void consume(Token token, ParseErrorMessage error) {
		if (current.is(token)) {
			advance();
		} else {
			throw new ParseError(current, error);
		}
	}
}
