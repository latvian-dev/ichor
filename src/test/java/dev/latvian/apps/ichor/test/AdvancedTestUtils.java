package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdvancedTestUtils {
	public interface FloatSupplier {
		Float supplyFloat();
	}

	public static final Short short1 = 30;
	public static final short short2 = 40;

	public static void runnable(Runnable runnable) {
		runnable.run();
	}

	public static void consumer(float x, Consumer<String> consumer) {
		consumer.accept(AstStringBuilder.wrapNumber(x) + " x hello");
	}

	public static double supplier(Supplier<Number> supplier) {
		return supplier.get().doubleValue();
	}

	public static void testFloat(float value, TestConsole console) {
		console.log("Float value: " + AstStringBuilder.wrapNumber(value));
	}

	public static void testFloatSupplier(FloatSupplier func, TestConsole console) {
		console.log("Float value: " + AstStringBuilder.wrapNumber(func.supplyFloat()));
	}

	public static void testMap(Map<String, Object> map, TestConsole console) {
		console.log("Map: " + map);
	}
}
