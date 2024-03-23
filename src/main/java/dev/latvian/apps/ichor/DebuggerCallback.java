package dev.latvian.apps.ichor;

@FunctionalInterface
public interface DebuggerCallback {
	void onDebugger(Scope scope);
}
