public class LongestCommonSubstring extends UkkonenSuffixTree {
    public LongestCommonSubstring(int from, int to) {
        super(from,to+2);
    }
    private int s1length, position, length;
    private void findLongest(String s1, String s2) {
        boolean[] dummy = {false, false};
        position = length = 0;
        s1length = s1.length();
        traverseTree(root,0,0,dummy);
        if (length == 0)
             System.out.println("Strings \"" + s1 + "\" and \"" + s2
                + "\" have no common substring");
        else System.out.println("A longest common substring for \"" 
                + s1 + "\" and \"" + s2 + "\" is " + "\"" 
                + T.substring(position-length,position) + "\" of length "
                + length);
    }
    private void traverseTree(SuffixTreeNode p, int lt, int len, boolean[] whichEdges) {
        boolean[] edges = {false, false};
        for (char i = 0; i < size; i++)
             if (p.left[i] != -1) {
                  if (p.descendants[i] == null)  // if it is an edge to 
                       if (p.left[i] <= s1length)// a leaf corresponding
                            whichEdges[0] = edges[0] = true; // to s1
                       else whichEdges[1] = edges[1] = true; // to s2
                  else {
                       traverseTree(p.descendants[i],p.left[i],
                                    len+(p.right[i]-p.left[i]+1),edges);
                       if (edges[0]) 
                           whichEdges[0] = true;
                       if (edges[1])
                           whichEdges[1] = true;
                  }
                  if (edges[0] && edges[1] && len > length) {
                       position = p.left[i];
                       length = len;
                  }
             }
    }
    public void run(String s1, String s2) {
        run(s1+(char)(size+offset-2)+s2+(char)(size+offset-1));
        findLongest(s1,s2);
    }
    static public void main(String[] a) {
        String s1  = "ababca";
        String s2  = "cabaca";
        if (a.length == 2) {
             s1 = a[0];
             s2 = a[1];
        }
        (new LongestCommonSubstring('a','z')).run(s1,s2);
    }
}

