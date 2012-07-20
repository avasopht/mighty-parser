package com.avasopht.mightyParser.structure;

import java.util.Collection;

public class ChoiceFactory {
  public static Node createChoice(Collection<Node> choices) {
    String name = null;

    Node choice = new Node();
    Node end = new Node();

    for (Node n : choices) {
      Node parent = new Node();
      parent.setChild(n);
      parent.add(end);

      choice.add(parent);

      if (name == null) {
        name = "CHOICE ( " + n;
      } else {
        name += " | " + n;
      }
    }

    name += " )";

    choice.setName(name);
    choice.setSignificant(true);
    return choice;
  }
}
