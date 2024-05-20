package mighty.traversing;

import mighty.structure.Node;

public class ParseCons {
  private final Operation operation;
  private final ParseCons next;

  public ParseCons() {
    operation = new Nil();
    next = null;
  }

  private ParseCons(Operation operation, ParseCons ptr) {
    this.operation = operation;
    this.next = ptr;
  }

  public ParseCons getNext() {
    return next;
  }

  public ParseCons visit(Node node) {
    return new ParseCons(new Visit(node), this);
  }

  public ParseCons end(Node node) {
    return new ParseCons(new End(node), this);
  }

  public ParseCons parent(Node node) {
    return new ParseCons(new Parent(node), this);
  }

  public ParseCons reversed() {
    ParseCons lastPtr = null;
    for(ParseCons ptr = this; ptr != null; ptr = ptr.next) {
      lastPtr = new ParseCons(ptr.operation, lastPtr);
    }
    return lastPtr;
  }

  public Operation getOperation() {
    return operation;
  }

  public boolean hasFlag(String flag) {
    Node node = switch(operation) {
      case Visit visit -> visit.node;
      case Parent parent -> parent.node;
      case End end -> end.node;
      case Nil ignored -> null;
    };

    if(node != null) {
      return node.hasFlag(flag);
    }
    return false;
  }

  sealed interface Operation {}

  public record Nil () implements Operation {}
  public record Visit(Node node) implements Operation {}
  public record Parent(Node node) implements Operation {}
  public record End(Node node) implements Operation {}

}
