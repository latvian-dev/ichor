package dev.latvian.apps.ichor;

public interface Deletable {
	static void deleteObject(Scope scope, Object o) {
		if (o instanceof Deletable) {
			((Deletable) o).onDeleted(scope);
		}
	}

	void onDeleted(Scope scope);
}
