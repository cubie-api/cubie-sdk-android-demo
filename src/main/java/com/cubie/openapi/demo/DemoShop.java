package com.cubie.openapi.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DemoShop {

  public static DemoItem getItem(String id) {
    return INVENTORY.get(id);
  }

  public static DemoItem getItemAt(int i) {
    return listItems().get(i);
  }

  public static List<DemoItem> listItems() {
    return new ArrayList<DemoItem>(INVENTORY.values());
  }

  public static LinkedHashMap<String, DemoItem> INVENTORY = new LinkedHashMap<String, DemoItem>();

  static {
    INVENTORY.put("sword", new DemoItem("sword", "Sword", new BigDecimal("90")));
    INVENTORY.put("shield", new DemoItem("shield", "Shield", new BigDecimal("60")));
    INVENTORY.put("arrow", new DemoItem("arrow", "Arrow", new BigDecimal("30")));
    INVENTORY.put("bow", new DemoItem("bow", "Bow", new BigDecimal("60")));
  }
}
