package mighty.structure;

public class TerminalFactory {
  public static Node createTerminalString(String s) {
    Node start = new Node();
    Node ptr = start;
    Node end = new Node();

    for (char c : s.toCharArray()) {
      Node n = new Node(c);
      ptr.add(n);
      ptr = n;
    }

    ptr.add(end);

    start.setName("\"" + s + "\"");
    start.setSignificant(true);
    return start;
  }
}
