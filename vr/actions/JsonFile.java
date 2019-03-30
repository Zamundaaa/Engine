package vr.actions;

import java.util.*;

import collectionsStuff.ArrayListBool;

public class JsonFile {

	private static final Map<Character, Character> closings = new HashMap<>();
	static {
		closings.put('{', '}');
		closings.put('(', ')');
		closings.put('[', ']');
	}

	protected StringBuilder sb = new StringBuilder();
	protected ArrayList<Character> parentheses = new ArrayList<>();
	protected ArrayListBool somethingin = new ArrayListBool();

	public JsonFile() {
		addSegment('{', null);
	}

	/**
	 * just calls {@link JsonFile#addSegment(char, String)} with null as name for
	 * convenience
	 */
	public void addSegment(char parentheses) {
		addSegment(parentheses, null);
	}

	public void addSegment(char parentheses, String name) {
		if (this.parentheses.size() > 0) {
			if (somethingin.getLast()) {
				sb.append(',');
			}
			newline();
		}
		if (name != null) {
			addTabs();
			sb.append('\"');
			sb.append(name);
			sb.append("\" : ");
		} else {
			addTabs();
		}
		sb.append(parentheses);
		this.parentheses.add(closings.get(parentheses));
		somethingin.add(false);
	}

//	public static void main(String[] args) {
//		JsonFile j = new JsonFile();
//		j.addValue("hey", "hallo");
//		j.addSegment('{', '}', "heylo");
//		j.addValue("empty", "empty");
//		j.addValue("empty2", "empty");
//		j.closeCurrentSegment();
//		j.addValue("empty3", "empty");
//		System.out.println(j.closeFile());
//	}

	public void addValue(String name, String value) {
		if (somethingin.getLast()) {
			sb.append(',');
		} else {
			somethingin.setLast(true);
		}
		newline();
		addTabs();
		sb.append('\"');
		sb.append(name);
		sb.append("\" : \"");
		sb.append(value);
		sb.append('\"');
	}

	// TODO for completeness!

//	public void addValue(String name, boolean value) {
//
//	}
//
//	public void addValue(String name, float value) {
//
//	}
//
//	public void addValue(String name, int value) {
//
//	}

	public void closeCurrentSegment() {
		char c = parentheses.remove(parentheses.size() - 1);
		newline();
		addTabs();
		sb.append(c);
	}

	private void newline() {
		sb.append('\n');
	}

	private void addTabs() {
		for (int i = 0; i < this.parentheses.size(); i++)
			sb.append("   ");
	}

	public String closeFile() {
		while (parentheses.size() > 0)
			closeCurrentSegment();
		return sb.toString();
	}

	public String getCurrentFile() {
		return sb.toString();
	}

	@Override
	public String toString() {
		return "A Json File creating helper";
	}

}
