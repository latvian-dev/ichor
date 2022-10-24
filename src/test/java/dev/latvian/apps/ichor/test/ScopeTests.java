package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.ScopeImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScopeTests {
	@Test
	public void redeclaration() {
		Assertions.assertThrows(IchorError.class, () -> {
			var root = new ScopeImpl();
			root.setMember("test", 5, AssignType.IMMUTABLE);

			var child = root.createChildScope();
			child.setMember("test", 10, AssignType.NONE);
		});
	}
}
