package mighty.traversing;

import java.util.Stack;

import mighty.structure.Node;

public class ChoiceRecord {
  public final Node        node;
  public final int         choice;
  public final int         index;
  public final Stack<Node> stack;
  public final ParseCons parsePtr;

  public ChoiceRecord(Node _node, int _choice, int _index, ParseCons parsePtr, Stack<Node> _stack) {
    node = _node;
    choice = _choice;
    index = _index;
    stack = new Stack<Node>();
    this.parsePtr = parsePtr;

    stack.addAll(_stack);
  }
}
