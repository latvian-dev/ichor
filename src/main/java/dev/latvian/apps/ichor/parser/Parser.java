package dev.latvian.apps.ichor.parser;

import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.parser.expr.AssignExpr;
import dev.latvian.apps.ichor.parser.expr.BinaryExpr;
import dev.latvian.apps.ichor.parser.expr.CallExpr;
import dev.latvian.apps.ichor.parser.expr.Expr;
import dev.latvian.apps.ichor.parser.expr.GetExpr;
import dev.latvian.apps.ichor.parser.expr.GroupingExpr;
import dev.latvian.apps.ichor.parser.expr.LiteralExpr;
import dev.latvian.apps.ichor.parser.expr.LogicalExpr;
import dev.latvian.apps.ichor.parser.expr.SetExpr;
import dev.latvian.apps.ichor.parser.expr.SuperExpr;
import dev.latvian.apps.ichor.parser.expr.ThisExpr;
import dev.latvian.apps.ichor.parser.expr.UnaryExpr;
import dev.latvian.apps.ichor.parser.expr.VariableExpr;
import dev.latvian.apps.ichor.parser.stmt.BlockStmt;
import dev.latvian.apps.ichor.parser.stmt.ClassStmt;
import dev.latvian.apps.ichor.parser.stmt.ExpressionStmt;
import dev.latvian.apps.ichor.parser.stmt.FunctionStmt;
import dev.latvian.apps.ichor.parser.stmt.IfStmt;
import dev.latvian.apps.ichor.parser.stmt.ReturnStmt;
import dev.latvian.apps.ichor.parser.stmt.Stmt;
import dev.latvian.apps.ichor.parser.stmt.VarStmt;
import dev.latvian.apps.ichor.parser.stmt.WhileStmt;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StaticToken;
import dev.latvian.apps.ichor.token.SymbolToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	public static class ParseError extends IchorError {
		public ParseError(PositionedToken token, String message) {
			super(token.line() + ":" + token.pos() + ": " + message);
		}
	}

	private final List<PositionedToken> tokens;
	private int current;

	public Parser(List<PositionedToken> t) {
		tokens = t;
		current = 0;
	}

	public List<Stmt> parse() {
		var list = new ArrayList<Stmt>();

		while (!isAtEnd()) {
			list.add(declaration());
		}

		return list;
	}

	private Expr expression() {
/* Parsing Expressions expression < Statements and State expression
    return equality();
*/
//> Statements and State expression
		return assignment();
//< Statements and State expression
	}

	//< expression
//> Statements and State declaration
	private Stmt declaration() {
		try {
//> Classes match-class
			if (match(KeywordToken.CLASS)) {
				return classDeclaration();
			}
//< Classes match-class
//> Functions match-fun
			if (match(KeywordToken.FUNCTION)) {
				return function("function");
			}
//< Functions match-fun
			if (match(KeywordToken.VAR)) {
				return varDeclaration();
			}

			return statement();
		} catch (ParseError error) {
			synchronize();
			return null;
		}
	}

	//< Statements and State declaration
//> Classes parse-class-declaration
	private Stmt classDeclaration() {
		PositionedToken name = consumeName("Expect class name.");
//> Inheritance parse-superclass

		VariableExpr superclass = null;
		if (match(SymbolToken.LT)) {
			consumeName("Expect superclass name.");
			superclass = new VariableExpr(previous());
		}

//< Inheritance parse-superclass
		consume(SymbolToken.LC, "Expect '{' before class body.");

		List<FunctionStmt> methods = new ArrayList<>();
		while (!check(SymbolToken.RC) && !isAtEnd()) {
			methods.add(function("method"));
		}

		consume(SymbolToken.RC, "Expect '}' after class body.");

/* Classes parse-class-declaration < Inheritance construct-class-ast
    return new Stmt.Class(name, methods);
*/
//> Inheritance construct-class-ast
		return new ClassStmt(name, superclass, methods);
//< Inheritance construct-class-ast
	}

	//< Classes parse-class-declaration
//> Statements and State parse-statement
	private Stmt statement() {
//> Control Flow match-for
		if (match(KeywordToken.FOR)) {
			return forStatement();
		}
//< Control Flow match-for
//> Control Flow match-if
		if (match(KeywordToken.IF)) {
			return ifStatement();
		}

//> Functions match-return
		if (match(KeywordToken.RETURN)) {
			return returnStatement();
		}
//< Functions match-return
//> Control Flow match-while
		if (match(KeywordToken.WHILE)) {
			return whileStatement();
		}
//< Control Flow match-while
//> parse-block
		if (match(SymbolToken.LC)) {
			return new BlockStmt(block());
		}
//< parse-block

		return expressionStatement();
	}

	//< Statements and State parse-statement
//> Control Flow for-statement
	private Stmt forStatement() {
		consume(SymbolToken.LP, "Expect '(' after 'for'.");

/* Control Flow for-statement < Control Flow for-initializer
    // More here...
*/
//> for-initializer
		Stmt initializer;
		if (match(SymbolToken.SEMI)) {
			initializer = null;
		} else if (match(KeywordToken.VAR)) {
			initializer = varDeclaration();
		} else {
			initializer = expressionStatement();
		}
//< for-initializer
//> for-condition

		Expr condition = null;
		if (!check(SymbolToken.SEMI)) {
			condition = expression();
		}
		consume(SymbolToken.SEMI, "Expect ';' after loop condition.");
//< for-condition
//> for-increment

		Expr increment = null;
		if (!check(SymbolToken.RP)) {
			increment = expression();
		}
		consume(SymbolToken.RP, "Expect ')' after for clauses.");
//< for-increment
//> for-body
		Stmt body = statement();

//> for-desugar-increment
		if (increment != null) {
			body = new BlockStmt(
					Arrays.asList(
							body,
							new ExpressionStmt(increment)));
		}

//< for-desugar-increment
//> for-desugar-condition
		if (condition == null) {
			condition = new LiteralExpr(true);
		}
		body = new WhileStmt(condition, body);

//< for-desugar-condition
//> for-desugar-initializer
		if (initializer != null) {
			body = new BlockStmt(Arrays.asList(initializer, body));
		}

//< for-desugar-initializer
		return body;
//< for-body
	}

	//< Control Flow for-statement
//> Control Flow if-statement
	private Stmt ifStatement() {
		consume(SymbolToken.LP, "Expect '(' after 'if'.");
		Expr condition = expression();
		consume(SymbolToken.RP, "Expect ')' after if condition."); // [parens]

		Stmt thenBranch = statement();
		Stmt elseBranch = null;
		if (match(KeywordToken.ELSE)) {
			elseBranch = statement();
		}

		return new IfStmt(condition, thenBranch, elseBranch);
	}

	//< Statements and State parse-print-statement
//> Functions parse-return-statement
	private Stmt returnStatement() {
		PositionedToken keyword = previous();
		Expr value = null;
		if (!check(SymbolToken.SEMI)) {
			value = expression();
		}

		consume(SymbolToken.SEMI, "Expect ';' after return value.");
		return new ReturnStmt(keyword, value);
	}

	//< Functions parse-return-statement
//> Statements and State parse-var-declaration
	private Stmt varDeclaration() {
		PositionedToken name = consumeName("Expect variable name.");

		Expr initializer = null;
		if (match(SymbolToken.SET)) {
			initializer = expression();
		}

		consume(SymbolToken.SEMI, "Expect ';' after variable declaration.");
		return new VarStmt(name, initializer);
	}

	//< Statements and State parse-var-declaration
//> Control Flow while-statement
	private Stmt whileStatement() {
		consume(SymbolToken.LP, "Expect '(' after 'while'.");
		Expr condition = expression();
		consume(SymbolToken.RP, "Expect ')' after condition.");
		Stmt body = statement();

		return new WhileStmt(condition, body);
	}

	//< Control Flow while-statement
//> Statements and State parse-expression-statement
	private Stmt expressionStatement() {
		Expr expr = expression();
		consume(SymbolToken.SEMI, "Expect ';' after expression.");
		return new ExpressionStmt(expr);
	}

	//< Statements and State parse-expression-statement
//> Functions parse-function
	private FunctionStmt function(String kind) {
		PositionedToken name = consumeName("Expect " + kind + " name.");
//> parse-parameters
		consume(SymbolToken.LP, "Expect '(' after " + kind + " name.");
		List<PositionedToken> parameters = new ArrayList<>();
		if (!check(SymbolToken.RP)) {
			do {
				if (parameters.size() >= 255) {
					error(peek(), "Can't have more than 255 parameters.");
				}

				parameters.add(consumeName("Expect parameter name."));
			} while (match(SymbolToken.COMMA));
		}
		consume(SymbolToken.RP, "Expect ')' after parameters.");
//< parse-parameters
//> parse-body

		consume(SymbolToken.LC, "Expect '{' before " + kind + " body.");
		List<Stmt> body = block();
		return new FunctionStmt(name, parameters, body);
//< parse-body
	}

	//< Functions parse-function
//> Statements and State block
	private List<Stmt> block() {
		List<Stmt> statements = new ArrayList<>();

		while (!check(SymbolToken.RC) && !isAtEnd()) {
			statements.add(declaration());
		}

		consume(SymbolToken.RC, "Expect '}' after block.");
		return statements;
	}

	//< Statements and State block
//> Statements and State parse-assignment
	private Expr assignment() {
/* Statements and State parse-assignment < Control Flow or-in-assignment
    Expr expr = equality();
*/
//> Control Flow or-in-assignment
		Expr expr = or();
//< Control Flow or-in-assignment

		if (match(SymbolToken.SET)) {
			PositionedToken equals = previous();
			Expr value = assignment();

			if (expr instanceof VariableExpr name) {
				return new AssignExpr(name.name(), value);
//> Classes assign-set
			} else if (expr instanceof GetExpr get) {
				return new SetExpr(get.object(), get.name(), value);
//< Classes assign-set
			}

			error(equals, "Invalid assignment target."); // [no-throw]
		}

		return expr;
	}

	//< Statements and State parse-assignment
//> Control Flow or
	private Expr or() {
		Expr expr = and();

		while (match(SymbolToken.OR)) {
			PositionedToken operator = previous();
			Expr right = and();
			expr = new LogicalExpr(expr, operator, right);
		}

		return expr;
	}

	//< Control Flow or
//> Control Flow and
	private Expr and() {
		Expr expr = equality();

		while (match(SymbolToken.AND)) {
			PositionedToken operator = previous();
			Expr right = equality();
			expr = new LogicalExpr(expr, operator, right);
		}

		return expr;
	}

	//< Control Flow and
//> equality
	private Expr equality() {
		Expr expr = comparison();

		while (match(SymbolToken.NEQ, SymbolToken.EQ, SymbolToken.SNEQ, SymbolToken.SEQ)) {
			PositionedToken operator = previous();
			Expr right = comparison();
			expr = new BinaryExpr(expr, operator, right);
		}

		return expr;
	}

	//< equality
//> comparison
	private Expr comparison() {
		Expr expr = term();

		while (match(SymbolToken.GT, SymbolToken.GTE, SymbolToken.LT, SymbolToken.LTE)) {
			PositionedToken operator = previous();
			Expr right = term();
			expr = new BinaryExpr(expr, operator, right);
		}

		return expr;
	}

	//< comparison
//> term
	private Expr term() {
		Expr expr = factor();

		while (match(SymbolToken.ADD, SymbolToken.SUB)) {
			PositionedToken operator = previous();
			Expr right = factor();
			expr = new BinaryExpr(expr, operator, right);
		}

		return expr;
	}

	//< term
//> factor
	private Expr factor() {
		Expr expr = unary();

		while (match(SymbolToken.DIV, SymbolToken.MUL)) {
			PositionedToken operator = previous();
			Expr right = unary();
			expr = new BinaryExpr(expr, operator, right);
		}

		return expr;
	}

	//< factor
//> unary
	private Expr unary() {
		if (match(SymbolToken.NOT, SymbolToken.SUB, SymbolToken.BNOT)) {
			PositionedToken operator = previous();
			Expr right = unary();
			return new UnaryExpr(operator, right);
		}

/* Parsing Expressions unary < Functions unary-call
    return primary();
*/
//> Functions unary-call
		return call();
//< Functions unary-call
	}

	//< unary
//> Functions finish-call
	private Expr finishCall(Expr callee) {
		List<Expr> arguments = new ArrayList<>();
		if (!check(SymbolToken.RP)) {
			do {
//> check-max-arity
				if (arguments.size() >= 255) {
					error(peek(), "Can't have more than 255 arguments.");
				}
//< check-max-arity
				arguments.add(expression());
			} while (match(SymbolToken.COMMA));
		}

		PositionedToken paren = consume(SymbolToken.RP,
				"Expect ')' after arguments.");

		return new CallExpr(callee, paren, arguments);
	}

	//< Functions finish-call
//> Functions call
	private Expr call() {
		Expr expr = primary();

		while (true) { // [while-true]
			if (match(SymbolToken.LP)) {
				expr = finishCall(expr);
//> Classes parse-property
			} else if (match(SymbolToken.DOT)) {
				PositionedToken name = consumeName("Expect property name after '.'.");
				expr = new GetExpr(expr, name);
//< Classes parse-property
			} else {
				break;
			}
		}

		return expr;
	}

	//< Functions call
//> primary
	private Expr primary() {
		if (!isAtEnd() && peek().token().isPrimary()) {
			return new LiteralExpr(peek().token().getPrimaryValue());
		}
//> Inheritance parse-super

		if (match(KeywordToken.SUPER)) {
			PositionedToken keyword = previous();
			consume(SymbolToken.DOT, "Expect '.' after 'super'.");
			PositionedToken method = consumeName("Expect superclass method name.");
			return new SuperExpr(keyword, method);
		}
//< Inheritance parse-super
//> Classes parse-this

		if (match(KeywordToken.THIS)) {
			return new ThisExpr(previous());
		}
//< Classes parse-this
//> Statements and State parse-identifier

		if (!isAtEnd() && peek().token() instanceof NameToken) {
			advance();
			return new VariableExpr(previous());
		}
//< Statements and State parse-identifier

		if (match(SymbolToken.LP)) {
			Expr expr = expression();
			consume(SymbolToken.RP, "Expect ')' after expression.");
			return new GroupingExpr(expr);
		}
//> primary-error

		throw error(peek(), "Expect expression.");
//< primary-error
	}

	//

	private ParseError error(PositionedToken token, String message) {
		throw new ParseError(token, message);
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
		return peek().token() == SymbolToken.EOF;
	}

	private PositionedToken peek() {
		return tokens.get(current);
	}

	private PositionedToken previous() {
		return tokens.get(current - 1);
	}

	private void synchronize() {
		advance();

		while (!isAtEnd()) {
			if (previous().token() == SymbolToken.SEMI) {
				return;
			}

			var p = peek().token();

			if (p == KeywordToken.CLASS
					|| p == KeywordToken.FUNCTION
					|| p == KeywordToken.VAR
					|| p == KeywordToken.FOR
					|| p == KeywordToken.IF
					|| p == KeywordToken.WHILE
					|| p == KeywordToken.RETURN
			) {
				return;
			}

			advance();
		}
	}

	public String result() {
		return "idk" ;
	}
}
