/************************  Trie.java  *******************************
 *
 */

class TrieNode {
    public boolean isLeaf;
}

class TrieNonLeaf extends TrieNode {
    public boolean endOfWord = false;
    public String letters;
    public TrieNode[] ptrs = new TrieNode[1];
    public TrieNonLeaf() {
        isLeaf = false;
    }
    public TrieNonLeaf(char ch) {
        letters = new String();
        letters += ch;
        isLeaf = false;
    }
}

class TrieLeaf extends TrieNode {
    public String suffix;
    public TrieLeaf() {
        isLeaf = true;
    }
    public TrieLeaf(String suffix) {
        this.suffix = new String(suffix);
        isLeaf = true;
    }
}

class Trie {
    private TrieNonLeaf root;
    private final int notFound = -1;
    public Trie() {
    }
    public Trie(String word) {
        root = new TrieNonLeaf(word.charAt(0)); // initialize the root
        createLeaf(word.charAt(0),word.substring(1),root); // to avoid later 
    }                                                      // test;
    public void printTrie() {
        printTrie(0,root,new String()); // assumption: the root is not null;
    }
    private void printTrie(int depth, TrieNode p, String prefix) {
        if (p.isLeaf) {
             for (int j = 1; j <= depth; j++)
                 System.out.print("   ");
             System.out.println("  >" + prefix + "|" + ((TrieLeaf)p).suffix);
        }
        else {
             for (int i = ((TrieNonLeaf)p).letters.length()-1; i >= 0; i--) {
                 if (((TrieNonLeaf)p).ptrs[i] != null) {
                     // add the letter corresponding to position i to prefix;
                     prefix = prefix.substring(0,depth) +
                              ((TrieNonLeaf)p).letters.charAt(i);
                     printTrie(depth+1,((TrieNonLeaf)p).ptrs[i],prefix);
                 }
                 else { // if leaf is null;
                      for (int j = 1; j <= depth+1; j++)
                          System.out.print("   ");
                      System.out.println(" >>" + prefix.substring(0,depth) +
                                ((TrieNonLeaf)p).letters.charAt(i));
                 }
             }
             if (((TrieNonLeaf)p).endOfWord) {
                  for (int j = 1; j <= depth+1; j++)
                      System.out.print("   ");
                  System.out.println(">>>" + prefix.substring(0,depth));
             }
        }
    }
    private int position(TrieNonLeaf p, char ch) {
        int i = 0;
        for ( ; i < p.letters.length() && p.letters.charAt(i) != ch; i++);
        if (i < p.letters.length())
             return i;
        else return notFound;
    } 
    public boolean found(String word) {
        TrieNode p = root;
        int pos, i = 0;
        while (true)
            if (p.isLeaf) {                     // node p is a leaf
                TrieLeaf lf = (TrieLeaf) p;     // where the matching
                if (word.substring(i).equals(lf.suffix)) // suffix of
                     return true;               // word should be found;
                else return false;
            }
            else if ((pos = position((TrieNonLeaf)p,word.charAt(i))) != notFound
                     && i+1 == word.length())   // the end of word has to
                 if (((TrieNonLeaf)p).ptrs[pos] == null) // correspond 
                      return true;              // with an empty leaf
                 else if(!(((TrieNonLeaf)p).ptrs[pos]).isLeaf &&
                          ((TrieNonLeaf)((TrieNonLeaf)p).ptrs[pos]).endOfWord)
                      return true;              // or the endOfWord marker on;
                 else return false;
            else if (pos != notFound && ((TrieNonLeaf)p).ptrs[pos] != null) {
                 p = ((TrieNonLeaf)p).ptrs[pos];// continue path, 
                 i++;                           // if possible,
            }
            else return false;                  // otherwise failure;
    }
    private void addCell(char ch, TrieNonLeaf p, int stop) {
        int i;
        int len = p.letters.length();
        char[] s = new char[len+1];
        TrieNode[] tmp = p.ptrs;
        p.ptrs = new TrieNode[len+1];
        for (i = 0; i < len+1; i++)
            p.ptrs[i] = null;
        if (stop < len)           // if ch does not follow all letters in p,
            for (i = len; i >= stop+1; i--) { // copy from tmp letters > ch;
                p.ptrs[i] = tmp[i-1];
                s[i] = p.letters.charAt(i-1);
            }
        s[stop] = ch;
        for (i = stop-1; i >= 0; i--) {       // and letters < ch;
            p.ptrs[i] = tmp[i];
            s[i] = p.letters.charAt(i);
        }
        p.letters = new String(s);
    }
    private void createLeaf(char ch, String suffix, TrieNonLeaf p) {
        int pos = position(p,ch);
        TrieLeaf lf = null;
        if (suffix != null && suffix.length() > 0) // don't create any leaf
            lf = new TrieLeaf(suffix);             // if there is no suffix;
        if (pos == notFound) {
            for (pos = 0; pos < p.letters.length() &&
                          p.letters.charAt(pos) < ch; pos++);
            addCell(ch,p,pos);
        }
        p.ptrs[pos] = lf;
    }
    public void insert(String word) {
        TrieNonLeaf p = root;
        TrieLeaf lf;
        int offset, pos, i = 0;
        while (true) {
            if (i == word.length()) {       // if the end of word reached,
                 if (p.endOfWord)
                      System.out.println("duplicate entry1: " + word);
                 p.endOfWord = true;        // set endOfWord to true;
                 return;
            }                               // if position in p indicated
            pos = position(p,word.charAt(i));
            if (pos == notFound) {          // by the first letter of word
                 createLeaf(word.charAt(i),word.substring(i+1),p);
                                            // does not exist, create
                 return;                    // a leaf and store in it the
            }                               // unprocessed suffix of word;
            else if (pos != notFound &&     // empty leaf in position pos;
                   p.ptrs[pos] == null) {
                 if (i+1 == word.length()) {
                      System.out.println("duplicate entry2: " + word);
                      return;
                 }
                 p.ptrs[pos] = new TrieNonLeaf(word.charAt(i+1));
                 ((TrieNonLeaf)(p.ptrs[pos])).endOfWord = true;
                 // check whether there is any suffix left:
                 String s = (word.length() > i+2) ? word.substring(i+2) : null;
                 createLeaf(word.charAt(i+1),s,(TrieNonLeaf)(p.ptrs[pos]));
                 return;
            }
            else if (pos != notFound &&     // if position pos is
                   p.ptrs[pos].isLeaf) {    // occupied by a leaf,
                 lf = (TrieLeaf) p.ptrs[pos]; // hold this leaf;
                 if (lf.suffix.equals(word.substring(i+1))) {
                      System.out.println("duplicate entry3: " + word);
                      return;
                 }
                 offset = 0;
                 // create as many non-leaves as the length of identical
                 // prefix of word and the string in the leaf (for cell 'R',
                 // leaf "EP", and word "REAR", two such nodes are created);
                 do {
                     pos = position(p,word.charAt(i+offset));
                     // word = "ABC", leaf = "ABCDEF" => leaf = "DEF";
                     if (word.length() == i+offset+1) {
                          p.ptrs[pos] = new TrieNonLeaf(lf.suffix.charAt(offset));
                          p = (TrieNonLeaf) p.ptrs[pos];
                          p.endOfWord = true;
                          createLeaf(lf.suffix.charAt(offset),
                                     lf.suffix.substring(offset+1),p);
                          return;
                     }
                     // word = "ABCDEF", leaf = "ABC" => leaf = "DEF";
                     else if (lf.suffix.length() == offset ) {
                          p.ptrs[pos] = new TrieNonLeaf(word.charAt(i+offset+1));
                          p = (TrieNonLeaf) p.ptrs[pos];
                          p.endOfWord = true;
                          createLeaf(word.charAt(i+offset+1),
                                     word.substring(i+offset+2),p);
                          return;
                     }
                     p.ptrs[pos] = new TrieNonLeaf(word.charAt(i+offset+1));
                     p = (TrieNonLeaf) p.ptrs[pos];
                     offset++;
                 } while (word.charAt(i+offset) == lf.suffix.charAt(offset-1));
                 offset--;
                 // word = "ABCDEF", leaf = "ABCPQR" =>
                 //     leaf('D') = "EF", leaf('P') = "QR";
                 // check whether there is any suffix left:
                 // word = "ABCD", leaf = "ABCPQR" =>
                 //     leaf('D') = null, leaf('P') = "QR";
                 String s = null;
                 if (word.length() > i+offset+2)
                      s = word.substring(i+offset+2);
                 createLeaf(word.charAt(i+offset+1),s,p);
                 // check whether there is any suffix left:
                 // word = "ABCDEF", leaf = "ABCP" =>
                 //     leaf('D') = "EF", leaf('P') = null;
                 if (lf.suffix.length() > offset+1)
                      s = lf.suffix.substring(offset+1);
                 else s = null;
                 createLeaf(lf.suffix.charAt(offset),s,p);
                 return;
            }
            else {
                 p = (TrieNonLeaf) p.ptrs[pos];
                 i++;
            }
        }
    }
}
