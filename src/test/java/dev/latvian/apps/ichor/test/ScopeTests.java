package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.lang.js.ContextJS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScopeTests {
	@Test
	public void redeclaration() {
		Assertions.assertThrows(IchorError.class, () -> {
			var root = new RootScope(new ContextJS());
			root.addImmutable("test", 5);

			var child = root.push();
			child.setMember("test", 10);
		});
	}
}
