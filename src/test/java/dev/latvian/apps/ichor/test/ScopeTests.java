package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.util.AssignType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class ScopeTests {
	@Test
	public void redeclaration() {
		Assertions.assertThrows(IchorError.class, () -> {
			var root = new Scope();
			root.setMember("test", 5, AssignType.IMMUTABLE);

			var child = root.createChildScope();
			child.setMember("test", 10, AssignType.NONE);
		});
	}
}
