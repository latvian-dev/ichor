package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.prototype.Evaluable;

@FunctionalInterface
public interface EvaluableFactory {
	Evaluable create();
}
