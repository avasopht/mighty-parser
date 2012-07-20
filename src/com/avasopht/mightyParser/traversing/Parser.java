package com.avasopht.mightyParser.traversing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.avasopht.mightyParser.structure.Node;

public class Parser {
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
          System.err
              .println("Line should be unreachable! Please report to www.konelabs.org");
          return false;
        }
      }
    }

    return true;
  }

  private static Node findEnd(Node start) {
    ArrayList<Node> openList = new ArrayList<Node>();
    ArrayList<Node> closedList = new ArrayList<Node>();

    openList.add(start);

    while (openList.size() > 0) {
      Node n = openList.remove(0);
      closedList.add(n);

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

      if (!(openList.contains(n) || closedList.contains(n))) {
        openList.add(n);
      }
    }

    return null;
  }

  public static int parse(String s, Node grammar) {
    if (!isValid(grammar)) {
      if (DEBUGGING) {
        DEBUG_ERR("Invalid grammar!");
      }
      return -1;
    }

    Node endNode = findEnd(grammar);

    Stack<ChoiceRecord> choiceStack = new Stack<ChoiceRecord>();
    Stack<Node> returnStack = new Stack<Node>();
    int max = 0;
    int index = 0;

    while (index <= s.length()) {

      char c;
      if (index < s.length()) {
        c = s.charAt(index);
      } else {
        c = '\0';
      }

      if (grammar == endNode) {
        if (max < index) {
          max = index;
        }
      }

      if (grammar == endNode && index == s.length()) {
        return max;
      }

      if (grammar.isTerminal() && (c == grammar.getChar())) {
        if (DEBUGGING) {
          DEBUG_OUT("Caught '" + c + "'", choiceStack.size());
        }
        ++index;

        grammar = grammar.get(0);
      } else if (grammar.hasChild()) {
        if (DEBUGGING) {
          DEBUG_OUT("Entered " + grammar, choiceStack.size());
        }

        returnStack.push(grammar);
        grammar = grammar.getChild();
      } else if (grammar.size() == 1 && !grammar.isTerminal()
          && !grammar.hasChild()) {
        if (DEBUGGING) {
          DEBUG_OUT("Travel " + grammar + ", no choice", choiceStack.size());
        }
        grammar = grammar.get(0);
      } else if (grammar.size() > 1) {
        if (DEBUGGING) {
          DEBUG_OUT("Choice taken at " + grammar, choiceStack.size());
        }
        ChoiceRecord record = new ChoiceRecord(grammar, 0, index, returnStack);
        choiceStack.push(record);
        grammar = grammar.get(0);
      } else if (grammar.isEnd() && returnStack.size() > 0) {
        if (DEBUGGING) {
          DEBUG_OUT("Pop return stack", choiceStack.size());
        }
        grammar = returnStack.pop().get(0);
      } else if ((grammar.isTerminal() && (c != grammar.getChar()))
          || grammar.isEnd()) {
        boolean die = true;

        // backtrack or die
        while (choiceStack.size() > 0 && die) {
          ChoiceRecord record = choiceStack.pop();

          if (DEBUGGING) {
            DEBUG_OUT("Popped choice stack", choiceStack.size());
          }

          if ((record.choice + 1) < record.node.size()) {
            int newChoice = record.choice + 1;
            index = record.index;
            grammar = record.node;
            returnStack = record.stack;

            record = new ChoiceRecord(grammar, newChoice, index, returnStack);
            choiceStack.push(record);

            if (DEBUGGING) {
              DEBUG_OUT("Took alternative choice " + newChoice + " at "
                  + grammar, choiceStack.size());
            }

            die = false;
            grammar = grammar.get(newChoice);
          }
        }

        if (die) {
          if (DEBUGGING) {
            DEBUG_OUT("Died", choiceStack.size());
          }
          return max;
        }
      } else {
        return -1;
      }
    }

    return -1;
  }
}
