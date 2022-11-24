package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.ast.expression.AstParam;

import java.util.function.Consumer;

public class Empty {
	public static final Object[] OBJECTS = new Object[0];
	public static final Class<?>[] CLASSES = new Class[0];
	public static final String[] STRINGS = new String[0];
	public static final Evaluable[] EVALUABLES = new Evaluable[0];
	public static final AstParam[] AST_PARAMS = new AstParam[0];
	public static final Consumer<?> CONSUMER = o -> {
	};

	public static <T> Consumer<T> consumer() {
		return (Consumer<T>) CONSUMER;
	}
}
