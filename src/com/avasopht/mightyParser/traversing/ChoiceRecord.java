package com.avasopht.mightyParser.traversing;

import java.util.Stack;

import com.avasopht.mightyParser.structure.Node;

public class ChoiceRecord {
  public final Node        node;
  public final int         choice;
  public final int         index;
  public final Stack<Node> stack;

  public ChoiceRecord(Node _node, int _choice, int _index, Stack<Node> _stack) {
    node = _node;
    choice = _choice;
    index = _index;
    stack = new Stack<Node>();

    stack.addAll(_stack);
  }
}
