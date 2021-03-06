package com.avasopht.mightyParser;

import java.util.ArrayList;

import com.avasopht.mightyParser.structure.ChoiceFactory;
import com.avasopht.mightyParser.structure.ListFactory;
import com.avasopht.mightyParser.structure.Node;
import com.avasopht.mightyParser.structure.OptionFactory;
import com.avasopht.mightyParser.structure.RepetitionFactory;
import com.avasopht.mightyParser.structure.TerminalFactory;
import com.avasopht.mightyParser.traversing.Lexeme;
import com.avasopht.mightyParser.traversing.Parser;

public class Main {
  public static void main(String argv[]) {
    ArrayList<Node> sentenceList = new ArrayList<Node>();
    ArrayList<Node> anotherWordList = new ArrayList<Node>();
    ArrayList<Node> wordList = new ArrayList<Node>();
    ArrayList<Node> whitespaceList = new ArrayList<Node>();

    Node whitespace;
    Node word;
    Node anotherWord;
    Node sentence;

    whitespaceList.add(TerminalFactory.createTerminalString(" "));
    whitespaceList.add(RepetitionFactory.createRepetition(TerminalFactory
        .createTerminalString(" ")));
    whitespace = ListFactory.createList(whitespaceList);
    whitespace.setName("WHITESPACE");

    wordList.add(letter());
    wordList.add(RepetitionFactory.createRepetition(letter()));
    word = ListFactory.createList(wordList);
    word.setName("WORD");

    anotherWordList.add(whitespace);
    anotherWordList.add(word);
    anotherWord = ListFactory.createList(anotherWordList);
    anotherWord.setName("ANOTHER_WORD");

    sentenceList.add(word);
    sentenceList.add(RepetitionFactory.createRepetition(anotherWord));
    sentenceList.add(OptionFactory.createOptional(TerminalFactory
        .createTerminalString(".")));
    sentence = ListFactory.createList(sentenceList);
    sentence.setName("SENTENCE");

    System.out.println(Parser.parse("The dirty fairies are dead", sentence));
    System.out.println(Parser.parse("The dirty fairies are dead.", sentence));
    System.out.println(Parser.parse("The dirty fai.ries are dead.", sentence));

    String s = "This is a really really long sentence you know ";
    for (int i = 0; i < 8; ++i) {
      s += s;
    }
    s += ".";
    long start = System.nanoTime();
    System.out.println(Parser.parse(s, sentence));
    start = System.nanoTime() - start;
    System.out.println("Time taken: " + ((float) start / 1000000000f)
        + " seconds");

    ArrayList<Node> grammars = new ArrayList<Node>();
    grammars.add(sentence);
    grammars.add(whitespace);
    grammars.add(anotherWord);

    Lexeme t;
    t = Parser.getToken("    help me", grammars);
    if (t != null) {
      System.out.println();
      System.out.println("" + t.grammar);
      System.out.println("\"" + t.string.substring(t.start, t.end) + "\"");
      System.out.println();
    }

    t = Parser.getToken("Even if I wanted to.", grammars);
    if (t != null) {
      System.out.println();
      System.out.println("" + t.grammar);
      System.out.println("\"" + t.string.substring(t.start, t.end) + "\"");
      System.out.println();
    }

    t = Parser.getToken("                  ", grammars);
    if (t != null) {
      System.out.println();
      System.out.println("" + t.grammar);
      System.out.println("\"" + t.string.substring(t.start, t.end) + "\"");
      System.out.println();
    }

  }

  @SuppressWarnings("unused")
  private static Node numeral() {
    ArrayList<Node> numbersList = new ArrayList<Node>();
    for (char c = '0'; c <= '9'; ++c) {
      Node numeral = TerminalFactory.createTerminalString("" + c);
      numeral.setSignificant(false);
      numbersList.add(numeral);
    }

    {
      Node numerals = ChoiceFactory.createChoice(numbersList);
      numerals.setName("NUMBER");
      return numerals;
    }
  }

  private static Node letter() {
    ArrayList<Node> lettersList = new ArrayList<Node>();
    for (char c = 'a'; c <= 'z'; ++c) {
      Node letter;

      letter = TerminalFactory.createTerminalString("" + c);
      letter.setSignificant(false);
      lettersList.add(letter);

      letter = TerminalFactory.createTerminalString(""
          + Character.toUpperCase(c));
      letter.setSignificant(false);
      lettersList.add(letter);
    }

    {
      Node letters = ChoiceFactory.createChoice(lettersList);
      letters.setName("LETTER");
      return letters;
    }
  }
}
