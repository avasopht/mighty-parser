package com.avasopht.mightyParser.structure;

import java.util.List;

public class ListFactory {
  public static Node createList(List<Node> list) {
    String name = null;

    Node start = new Node();
    Node end = new Node();

    Node ptr = start;

    for (Node n : list) {
      Node parent = new Node();
      parent.setChild(n);
      parent.setName("" + n);

      ptr.add(parent);
      ptr = parent;

      if (name == null) {
        name = "" + n;
      } else {
        name += " " + n;
      }
    }

    ptr.add(end);

    start.setName(name);
    start.setSignificant(true);
    return start;
  }
}
