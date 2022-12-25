package dev.latvian.apps.ichor.ast.statement;

public abstract class AstLabeledStatement extends AstStatement implements LabeledStatement {
	public String label = "";

	@Override
	public String getLabel() {
		return label;
	}
}
