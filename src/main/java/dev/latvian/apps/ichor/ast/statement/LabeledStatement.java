package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.exit.ExitType;

public interface LabeledStatement extends Interpretable {
	default String getLabel() {
		return "";
	}

	default boolean handle(ExitType type) {
		return type == ExitType.BREAK || type == ExitType.CONTINUE;
	}
}
