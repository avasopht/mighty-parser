package com.avasopht.mightyParser.structure;

public class OptionFactory {
  public static Node createOptional(Node n) {
    Node end = new Node();
    Node option = new Node();
    Node choice = new Node();

    choice.add(option);
    choice.add(end);

    option.add(end);
    option.setChild(n);

    choice.setName("OPTION (" + n + ")");
    choice.setSignificant(true);
    return choice;
  }
}
