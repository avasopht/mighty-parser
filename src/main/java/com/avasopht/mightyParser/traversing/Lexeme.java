package com.avasopht.mightyParser.traversing;

import com.avasopht.mightyParser.structure.Node;

public class Lexeme {
  public final int    start;
  public final int    end;
  public final Node   grammar;
  public final String string;

  public Lexeme(Node _grammar, String s, int _start, int _end) {
    grammar = _grammar;
    string = s;
    start = _start;
    end = _end;
  }
}
