package mighty.traversing;

import mighty.structure.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class Parser {
  public record Result(int readCharacters, ParseCons parseTree) {
  }

  private static boolean DEBUGGING = false;

  private static void DEBUG_OUT(String s, int stackLevel) {
    if (DEBUGGING) {
      for (int i = 0; i < stackLevel; ++i)
        System.out.print("   ");
      System.out.print(s + "\n");
    }
  }

  private static void DEBUG_ERR(String s) {
    if (DEBUGGING) {
      System.err.println(s);
    }
  }

  public static Lexeme getToken(String s, Collection<Node> grammars) {
    int max = 0;
    Node maxGrammar = null;
    for (Node grammar : grammars) {
      int index = parse(s, grammar);

      if ((maxGrammar == null && index > 0) || (index > max)) {
        maxGrammar = grammar;
        max = index;
      }
    }

    if (maxGrammar == null) {
      return null;
    } else {
      return new Lexeme(maxGrammar, s, 0, max);
    }
  }

  public static boolean isValid(Node grammar) {
    // Traverses every node and ensures there is an end node.
    // However, it does not ensure that every path leads to an end node.
    // Maybe the algorithm can be changes do that every node without edges is treated as an end node.

    List<Node> openList = new ArrayList<Node>();
    Collection<Node> closedList = new ArrayList<Node>();

    openList.add(grammar);

    while (openList.size() > 0) {
      Node n = openList.remove(0);
      closedList.add(n);

      for (boolean hadBlock = false; n != null; /* */) {
        if (n.isEnd()) {
          return hadBlock;
        } else if (n.isTerminal()) {
          hadBlock = true;
          n = n.get(0);
        } else if (n.size() > 1) {
          hadBlock = true;

          for (int i = 0; i < n.size(); ++i) {
            Node edge = n.get(i);
            if (!(closedList.contains(edge) || openList.contains(edge))) {
              openList.add(edge);
            }
          }

          n = null;
        } else if (n.hasChild()) {
          Node child = n.getChild();
          if (!(closedList.contains(child) || openList.contains(child))) {
            openList.add(child);
          }

          hadBlock = true;

          n = n.get(0);
        } else if (n.size() == 1) {
          n = n.get(0);
        } else {
          System.err.println("Line should be unreachable!");
          return false;
        }
      }
    }

    return true;
  }

  private static Node findEnd(Node start) {

    UniqueQueue<Node> openList = new UniqueQueue<>();

    openList.add(start);

    while (!openList.isEmpty()) {
      Node n = openList.removeNext();

      if (n.isEnd()) {
        return n;
      } else if (n.size() == 1) {
        n = n.get(0);
      } else if (n.size() > 1) {
        for (int i = 0; i < n.size(); ++i) {
          Node end = findEnd(n.get(i));
          if (end != null) {
            n = end;
            break;
          }
        }
      }

      if (!openList.hasVisited(n)) {
        openList.add(n);
      }
    }

    return null;
  }

  public static int parse(String s, final Node grammar) {
    Result result =  parseR(s, grammar);

    System.out.println("<Result>");
    for(ParseCons ptr = result.parseTree; ptr != null; ptr = ptr.getNext()) {
      if(ptr.hasFlag("show")) {
        System.out.println(ptr.getOperation());
      }
    }
    System.out.println("</Result>");

    return result.readCharacters;
  }
  public static Result parseR(String s, final Node grammar) {

    if (!isValid(grammar)) {
      if (DEBUGGING) {
        DEBUG_ERR("Invalid grammar!");
      }
      return new Result(-1, null);
    }

    Node endNode = findEnd(grammar);

    Node ptr = grammar;

    Stack<ChoiceRecord> choiceStack = new Stack<>();
    Stack<Node> returnStack = new Stack<>();
    int max = 0;
    int index = 0;

    ParseCons parsePtr = new ParseCons();

    parsePtr = parsePtr.parent(null);
    parsePtr = parsePtr.visit(ptr);

    while (index <= s.length()) {

      char c;
      if (index < s.length()) {
        c = s.charAt(index);
      } else {
        c = '\0';
      }

      if (ptr == endNode) {
        if (max < index) {
          max = index;
        }
      }

      if (ptr == endNode && index == s.length()) {
        // # End node (and last character of stream).
        return new Result(max, parsePtr.reversed());
      }

      if (ptr.isTerminal() && (c == ptr.getChar())) {
        // # Terminal node.
        if (DEBUGGING) {
          DEBUG_OUT("Caught '" + c + "'", choiceStack.size());
        }

        ++index;
        ptr = ptr.get(0);
        parsePtr = parsePtr.visit(ptr);
      } else if (ptr.hasChild()) {
        // # Parent node.
        if (DEBUGGING) {
          DEBUG_OUT("Entered " + ptr, choiceStack.size());
        }


        returnStack.push(ptr);
        parsePtr = parsePtr.parent(ptr);

        ptr = ptr.getChild();
        parsePtr = parsePtr.visit(ptr);
      } else if (ptr.size() == 1 && !ptr.isTerminal()
          && !ptr.hasChild()) {
        // Choice node (1 option)
        if (DEBUGGING) {
          DEBUG_OUT("Travel " + ptr + ", no choice", choiceStack.size());
        }

        ptr = ptr.get(0);
        parsePtr = parsePtr.visit(ptr);
      } else if (ptr.size() > 1) {
        // # Choice node (>1 option)
        if (DEBUGGING) {
          DEBUG_OUT("Choice taken at " + ptr, choiceStack.size());
        }
        ChoiceRecord record = new ChoiceRecord(ptr, 0, index, parsePtr, returnStack);
        choiceStack.push(record);

        ptr = ptr.get(0);
        parsePtr = parsePtr.visit(ptr);
      } else if (ptr.isEnd() && !returnStack.isEmpty()) {
        // # End node
        if (DEBUGGING) {
          DEBUG_OUT("Pop return stack", choiceStack.size());
        }

        Node parent = returnStack.pop();
        parsePtr = parsePtr.end(parent);

        ptr = parent.get(0);
        parsePtr = parsePtr.visit(ptr);
      } else if ((ptr.isTerminal() && (c != ptr.getChar()))
          || ptr.isEnd()) {
        boolean die = true;

        // backtrack or die
        while (!choiceStack.isEmpty() && die) {
          ChoiceRecord record = choiceStack.pop();

          if (DEBUGGING) {
            DEBUG_OUT("Popped choice stack", choiceStack.size());
          }

          if ((record.choice + 1) < record.node.size()) {
            int newChoice = record.choice + 1;
            index = record.index;
            ptr = record.node;
            returnStack = record.stack;
            parsePtr = record.parsePtr;

            record = new ChoiceRecord(ptr, newChoice, index, parsePtr, returnStack);
            choiceStack.push(record);

            if (DEBUGGING) {
              DEBUG_OUT("Took alternative choice " + newChoice + " at "
                  + ptr, choiceStack.size());
            }

            die = false;
            ptr = ptr.get(newChoice);
            parsePtr = parsePtr.visit(ptr);
          }
        }

        if (die) {
          if (DEBUGGING) {
            DEBUG_OUT("Died", 0);
          }
          return new Result(max, parsePtr.reversed());
        }
      } else {
        return new Result(-1, null);
      }
    }

    return new Result(-1, null);
  }
}
