package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IFaces {
	public static void runnable(Runnable runnable) {
		runnable.run();
	}

	public static void consumer(float x, Consumer<String> consumer) {
		consumer.accept(AstStringBuilder.wrapNumber(x) + " x hello");
	}

	public static double supplier(Supplier<Number> supplier) {
		return supplier.get().doubleValue();
	}
}
