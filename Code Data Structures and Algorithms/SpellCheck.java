import java.io.*;

public class SpellCheck {
    static String s;
    static int ch;
    static int lineNum = 1;
    static void readWord(InputStream fIn) {
        try {
            while (true)
                if (ch > -1 && !Character.isLetter((char)ch)) { // skip 
                     ch = fIn.read();                  // non-letters;
                     if (ch == '\n')
                          lineNum++;
                }
                else break;
            if (ch == -1)
                return;
            s = "";
            while (ch > -1 && Character.isLetter((char)ch)) {
                s += Character.toUpperCase((char)ch);
                ch = fIn.read();
            }
        } catch (IOException io) {
            System.out.println("Problem with input.");
        }
    }
    static public void main(String args[]) {
        String fileName = "";
        InputStream fIn, dictionary;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(isr);
        Trie trie = null;
        try {
            dictionary = new FileInputStream("dictionary");
            readWord(dictionary);
            trie = new Trie(s.toUpperCase());  // initialize root;
            while (ch > -1) {
                readWord(dictionary);
                if (ch == -1)
                     break;
                trie.insert(s);
            }
            dictionary.close();
        } catch(IOException io) {
            System.err.println("Cannot open dictionary");
        }
        System.out.println("\nTrie: ");
        trie.printTrie();
        ch = ' ';
        lineNum = 1;
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
            System.out.println("Misspelled words:");
            while (true) {
                 readWord(fIn);
                 if (ch == -1)
                     break;
                 if (!trie.found(s))
                     System.out.println(s + " on line " + lineNum);
            }
            fIn.close();
        } catch(IOException io) {
            System.err.println("Cannot open " + fileName);
        }
    }
}
