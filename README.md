Avasopht's Mighty Parser is a parsing framework for Java written in 2007 by Keldon Alleyne.

You can construct a mighty-parser definition using the example code listed below:

    ArrayList<Node> alphaNumericList = new ArrayList<Node>();
    ArrayList<Node> sentenceList = new ArrayList<Node>();
    ArrayList<Node> anotherWordList = new ArrayList<Node>();
    ArrayList<Node> wordList = new ArrayList<Node>();
    ArrayList<Node> whitespaceList = new ArrayList<Node>();

    Node whitespace;
    Node word;
    Node anotherWord;
    Node sentence;

    whitespaceList.add(TerminalFactory.createTerminalString(" "));
    whitespaceList.add(RepetitionFactory.createRepetition(TerminalFactory.createTerminalString(" ")));
    whitespace = ListFactory.createList(whitespaceList);

    alphaNumericList.add(number());
    alphaNumericList.add(letter());

    wordList.add(letter());
    wordList.add(RepetitionFactory.createRepetition(letter()));
    word = ListFactory.createList(wordList);

    anotherWordList.add(whitespace);
    anotherWordList.add(word);
    anotherWord = ListFactory.createList(anotherWordList);

    sentenceList.add(word);
    sentenceList.add(RepetitionFactory.createRepetition(anotherWord));
    sentenceList.add(OptionFactory.createOptional(TerminalFactory.createTerminalString(".")));
    sentence = ListFactory.createList(sentenceList);
