package dev.latvian.apps.ichor.lang.bf;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.util.CharacterScanner;

public class ParserBF implements Parser {
	private final ContextBF context;
	private final String code;
	private final CharacterScanner input;

	public ParserBF(ContextBF cx, String code, CharacterScanner input) {
		this.context = cx;
		this.code = code;
		this.input = input;
	}

	public ParserBF(ContextBF cx, String code) {
		this(cx, code, CharacterScanner.NO_INPUT);
	}

	@Override
	public ContextBF getContext() {
		return context;
	}

	@Override
	public String expression() {
		var out = new StringBuilder();
		var memory = new byte[context.getMaxMemory()];

		int c = 0;
		int pointer = 0;
		var s = code.toCharArray();

		for (int i = 0; i < s.length; i++) {
			switch (code.charAt(i)) {
				case '>' -> pointer = pointer == memory.length - 1 ? 0 : pointer + 1;
				case '<' -> pointer = pointer == 0 ? memory.length - 1 : pointer - 1;
				case '+' -> memory[pointer]++;
				case '-' -> memory[pointer]--;
				case '.' -> out.append((char) (memory[pointer]));
				case ',' -> memory[pointer] = (byte) input.nextChar();
				case '[' -> {
					if (memory[pointer] == 0) {
						i++;
						while (c > 0 || s[i] != ']') {
							if (s[i] == '[') {
								c++;
							} else if (s[i] == ']') {
								c--;
							}
							i++;
						}
					}
				}
				case ']' -> {
					if (memory[pointer] != 0) {
						i--;
						while (c > 0 || s[i] != '[') {
							if (s[i] == ']') {
								c++;
							} else if (s[i] == '[') {
								c--;
							}
							i--;
						}
					}
				}
			}
		}

		return out.toString();
	}
}
