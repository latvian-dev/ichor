package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import org.jetbrains.annotations.Nullable;

public class AstTry extends AstStatement {
	public record AstCatch(String name, Interpretable body) {
	}

	public final Interpretable tryBlock;
	public final AstCatch catchBlock;
	public final Interpretable finallyBlock;

	public AstTry(Interpretable tryBlock, @Nullable AstCatch catchBlock, @Nullable Interpretable finallyBlock) {
		this.tryBlock = tryBlock;
		this.catchBlock = catchBlock;
		this.finallyBlock = finallyBlock;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("try ");
		builder.append(tryBlock);

		if (catchBlock != null) {
			builder.append(" catch (");
			builder.append(catchBlock.name);
			builder.append(") ");
			builder.append(catchBlock.body);
		}

		if (finallyBlock != null) {
			builder.append(" finally ");
			builder.append(finallyBlock);
		}
	}

	@Override
	public void interpret(Scope scope) {
	}
}
