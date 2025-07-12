package dev.tizu.craftmaps.utils;

public class StringConversion {
	private StringConversion() {
	}

	public static String camelToTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder(input.length());

		var nextTitleCase = true;
		for (char c : input.toCharArray()) {
			if (c == '_') {
				c = ' ';
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toUpperCase(c);
				nextTitleCase = false;
			} else {
				c = Character.toLowerCase(c);
			}
			titleCase.append(c);
		}

		return titleCase.toString();
	}
}
