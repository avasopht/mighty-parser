package mighty.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Node {

  // Initializers: ----------------------------------------------------------

  public Node() {
    term = '\0';

    initialize();
  }

  protected Node(char ch) {
    term = ch;

    initialize();
  }

  private void initialize() {
    child = null;
    flags = new HashSet<>();
    edges = new ArrayList<>();
    name = null;
    isSignificant = false;
  }

  // Methods: ---------------------------------------------------------------
  public void add(Node n) {
    edges.add(n);
  }

  public String toString() {
    if (name != null) {
      return name;
    } else if (isTerminal()) {
      return "" + term;
    } else if (isEnd()) {
      return "END";
    } else {
      return "";
    }
  }

  public void addFlag(String flag) {
    flags.add(flag.toLowerCase());
  }

  public boolean hasFlag(String flag) {
    return flags.contains(flag.toLowerCase());
  }

  public Node withFlag(String flag) {
    addFlag(flag);
    return this;
  }

  public Node contain(String containerName, String endName) {
    Node container = new Node().withFlag("show").withName(containerName);
    container.setChild(this);
    container.add(new Node().withFlag("show").withName(endName));
    return container;
  }

  public Node withName(String name) {
    setName(name);
    return this;
  }

  // Getters and setters: ---------------------------------------------------

  public boolean hasChild() {
    return child != null;
  }

  public boolean isTerminal() {
    return term != '\0';
  }

  public boolean isEnd() {
    return !(isTerminal() | hasChild() | (size() > 0));
  }

  public char getChar() {
    return term;
  }

  public Node get(int i) {
    return edges.get(i);
  }

  public Node getChild() {
    return child;
  }

  public void setChild(Node n) {
    child = n;
  }

  public int size() {
    return edges.size();
  }

  public void setName(String s) {
    name = s;
  }

  public void setSignificant(boolean b) {
    isSignificant = b;
  }

  boolean isSignificant() {
    return isSignificant;
  }

  // Class members: ---------------------------------------------------------

  private final char term;
  private Node       child;
  private HashSet<String> flags;
  private List<Node> edges;
  private String     name;
  boolean            isSignificant;
}
