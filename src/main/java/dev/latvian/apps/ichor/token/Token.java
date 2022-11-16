package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import org.jetbrains.annotations.Nullable;

public interface Token {
	@Nullable
	default Evaluable toEvaluable() {
		return null;
	}
}
