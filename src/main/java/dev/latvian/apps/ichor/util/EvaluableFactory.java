package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Evaluable;

@FunctionalInterface
public interface EvaluableFactory {
	Evaluable create();
}
