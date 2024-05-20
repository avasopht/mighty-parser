package com.avasopht.mightyParser.structure;

public class RepetitionFactory {
  public static Node createRepetition(Node n) {
    Node end = new Node();
    Node repetition = new Node();
    Node choice = new Node();

    choice.add(end);
    choice.add(repetition);

    repetition.setChild(n);
    repetition.add(choice);

    choice.setName("REPEAT (" + n + ")");
    choice.setSignificant(true);

    return choice;
  }
}
