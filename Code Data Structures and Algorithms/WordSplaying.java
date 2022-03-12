/***********************  WordSplaying.java  ***********************/

import java.io.*;

class Word implements Comparable<Word> {
    private String word = "";
    public int freq = 1;
    public Word() {
    }
    public Word(String s) {
        word = s;
    }
    public boolean equals(Object el) {
        return word.equals(((Word)el).word);
    }
    public int compareTo(Word el) {
        return word.compareTo(el.word);
    }
    public String toString() {
        return word + ": " + freq + " ";
    }
}

class WordSplay extends SplayTree<Word> {
    public WordSplay() {
        differentWords = wordCnt = 0;
    }
    private int differentWords, // counter of different words in text file;
                wordCnt;        // counter of all words in the same file;
    protected void visit(SplayTreeNode<Word> p) {
        differentWords++;
        wordCnt += p.el.freq;
    }
    public void run(InputStream fIn, String fileName) {
        int ch = 1;
        Word p;
        try {
            while (ch > -1) {
                while (true)
                    if (ch > -1 && !Character.isLetter((char)ch)) // skip 
                         ch = fIn.read();                  // nonletters;
                    else break;
                if (ch == -1)
                    break;
                String s = "";
                while (ch > -1 && Character.isLetter((char)ch)) {
                    s += Character.toUpperCase((char)ch);
                    ch = fIn.read();
                }
                if ((p = search(new Word(s))) == null)
                     insert(new Word(s));
                else p.freq++;
            }
       } catch (IOException io) {
            System.err.println("A problem with input.");
       }
       inorder();
       System.out.println("\n\nFile " + fileName
                + " contains " + wordCnt + " words among which "
                + differentWords + " are different\n");
    }
}

class WordSplaying {
    static public void main(String args[]) {
        String fileName = "";
        InputStream fIn;
        BufferedReader buffer = new BufferedReader(
                                new InputStreamReader(System.in));
        try {
            if (args.length == 0) {
                 System.out.print("Enter a file name: ");
                 fileName = buffer.readLine();
                 fIn = new FileInputStream(fileName);
            }
            else {
                 fIn = new FileInputStream(args[0]);
                 fileName = args[0];
            }
            (new WordSplay()).run(fIn,fileName);
            fIn.close();
        } catch(IOException io) {
            System.err.println("Cannot open " + fileName);
        }
    }
}

