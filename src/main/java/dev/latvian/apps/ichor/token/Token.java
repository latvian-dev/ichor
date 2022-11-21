package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import org.jetbrains.annotations.Nullable;

public interface Token {
	@Nullable
	default Evaluable toEvaluable(Parser parser) {
		return null;
	}
}
