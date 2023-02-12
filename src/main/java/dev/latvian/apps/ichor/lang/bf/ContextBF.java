package dev.latvian.apps.ichor.lang.bf;

import dev.latvian.apps.ichor.Context;

public class ContextBF extends Context {
	private int maxMemory;

	public ContextBF() {
		maxMemory = 65535;
	}

	public int getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(int m) {
		maxMemory = m;
	}
}
