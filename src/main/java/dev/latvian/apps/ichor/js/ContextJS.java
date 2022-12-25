package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.ContextProperty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ContextJS extends Context {
	public static final ContextProperty<Executor> TIMEOUT_EXECUTOR = new ContextProperty<>("timeoutExecutor", CompletableFuture.completedFuture(null).defaultExecutor());
	public static final ContextProperty<Executor> TIMEOUT_EXECUTOR_AFTER = new ContextProperty<>("timeoutExecutorAfter", null);

	public ContextJS() {
		safePrototypes.add(stringPrototype = StringJS.PROTOTYPE);
		safePrototypes.add(numberPrototype = NumberJS.PROTOTYPE);
		safePrototypes.add(booleanPrototype = BooleanJS.PROTOTYPE);
		safePrototypes.add(listPrototype = ArrayJS.PROTOTYPE);
		safePrototypes.add(mapPrototype = ObjectJS.PROTOTYPE);
	}
}
