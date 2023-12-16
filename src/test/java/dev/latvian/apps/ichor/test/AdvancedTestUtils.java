package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AdvancedTestUtils {
	private final TestConsole console;
	public final Short short1 = 30;
	public final short short2 = 40;

	public AdvancedTestUtils(TestConsole console) {
		this.console = console;
	}

	public interface FloatSupplier {
		Float supplyFloat();
	}

	public void runnable(Runnable runnable) {
		runnable.run();
	}

	public void consumer(float x, Consumer<String> consumer) {
		consumer.accept(AstStringBuilder.wrapNumber(x) + " x hello");
	}

	public double supplier(Supplier<Number> supplier) {
		return supplier.get().doubleValue();
	}

	public void testFloat(float value) {
		console.log("Float value: " + AstStringBuilder.wrapNumber(value));
	}

	public void testFloatSupplier(FloatSupplier func) {
		console.log("Float value: " + AstStringBuilder.wrapNumber(func.supplyFloat()));
		System.out.printf("Func object: %s #%08X\n", func, func.hashCode());
		System.out.printf("Func class: %s #%08X\n", func.getClass().getName(), func.getClass().hashCode());
	}

	public void testMap(Map<String, Object> map) {
		console.log("Map: " + map);
	}

	public int getBean() {
		return 30;
	}
}
