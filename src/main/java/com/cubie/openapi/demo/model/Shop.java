package com.cubie.openapi.demo.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Shop {

  public static Item getItem(String id) {
    return INVENTORY.get(id);
  }

  public static Item getItemAt(int i) {
    return listItems().get(i);
  }

  public static List<Item> listItems() {
    return new ArrayList<Item>(INVENTORY.values());
  }

  public static LinkedHashMap<String, Item> INVENTORY = new LinkedHashMap<String, Item>();

  static {
    INVENTORY.put("sword", new Item("sword", "Sword", "TWD", new BigDecimal("90")));
    INVENTORY.put("shield", new Item("shield", "Shield", "TWD", new BigDecimal("60")));
    INVENTORY.put("arrow", new Item("arrow", "Arrow", "TWD", new BigDecimal("30")));
    INVENTORY.put("bow", new Item("bow", "Bow", "TWD", new BigDecimal("60")));
  }
}
