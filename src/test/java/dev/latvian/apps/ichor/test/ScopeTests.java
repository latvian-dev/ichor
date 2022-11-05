package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.util.AssignType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScopeTests {
	@Test
	public void redeclaration() {
		Assertions.assertThrows(IchorError.class, () -> {
			var root = new RootScope(new ContextJS());
			root.setMember("test", 5, AssignType.IMMUTABLE);

			var child = root.push();
			child.setMember("test", 10, AssignType.NONE);
		});
	}
}
