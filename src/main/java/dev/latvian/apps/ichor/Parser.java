package dev.latvian.apps.ichor;

public interface Parser {
	Context getContext();

	Evaluable expression();
}
