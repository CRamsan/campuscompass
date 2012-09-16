package com.cesarandres.campuscompass.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentNDSU {

	public static class DummyItem {

		public String id;
		public String content;

		public DummyItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}

	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	static {
		addItem(new DummyItem("1", "Item 1"));
		addItem(new DummyItem("2", "Item 2"));
		addItem(new DummyItem("3", "Item 3"));
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static final String[] places_names = { "AES Greenhouse",
			"Agricultural and Biosystems Engineering" };
	public static final String[] places_descriptions = {
			"",
			"Advanced Electronics Design and Manufacturing, Agricultural and Biosystems Engineering, Northern Crops Institute" };
}
