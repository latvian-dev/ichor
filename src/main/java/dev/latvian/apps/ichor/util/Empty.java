package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.ast.expression.AstType;
import dev.latvian.apps.ichor.ast.statement.AstDeclaration;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.token.PositionedToken;

import java.util.function.Consumer;

public class Empty {
	public static final Object[] OBJECTS = new Object[0];
	public static final Class<?>[] CLASSES = new Class[0];
	public static final String[] STRINGS = new String[0];
	public static final AstParam[] AST_PARAMS = new AstParam[0];
	public static final AstDeclaration[] AST_DECLARATIONS = new AstDeclaration[0];
	public static final PositionedToken[] POSITIONED_TOKENS = new PositionedToken[0];
	public static final AstType[] AST_TYPES = new AstType[0];
	public static final Prototype[] PROTOTYPES = new Prototype[0];

	public static final Consumer<?> CONSUMER = o -> {
	};

	public static <T> Consumer<T> consumer() {
		return (Consumer<T>) CONSUMER;
	}
}
