package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.exit.ScopeExit;
import dev.latvian.apps.ichor.token.TokenPos;
import dev.latvian.apps.ichor.token.TokenPosSupplier;

public interface Interpretable {
	Interpretable[] EMPTY_INTERPRETABLE_ARRAY = new Interpretable[0];

	void interpret(Scope scope);

	default void interpretSafe(Scope scope) {
		try {
			interpret(scope);
		} catch (ScopeExit pass) {
			throw pass;
		} catch (IchorError pass) {
			if (pass.tokenPos == TokenPos.UNKNOWN && this instanceof TokenPosSupplier pos) {
				pass.tokenPos = pos.getPos();
			}

			throw pass;
		} catch (Throwable ex) {
			throw new InternalScriptError(ex).pos(this);
		}
	}

	default void optimize(Parser parser) {
	}
}
