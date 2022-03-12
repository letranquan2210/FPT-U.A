import java.io.*;

class SuffixTreeNode {
    public SuffixTreeNode[] descendants;
    public int[] left, right;
    public SuffixTreeNode suffixLink = null;
    public static int cnt = 0; // for printing only;
    public int id = 1;         // for printing only;
    public SuffixTreeNode() {
        this(128);
    }
    public SuffixTreeNode(int sz) {
        id = cnt++;
        descendants = new SuffixTreeNode[sz];
        left  = new int[sz];
        right = new int[sz];
        for (int i = 0; i < sz; i++)
             left[i] = -1;
    }
}

public class UkkonenSuffixTree {
    protected SuffixTreeNode root;
    protected int size, offset;
    protected String T;
    private int Lt = 1;
    private boolean endPoint;
    public UkkonenSuffixTree() {
        this(0,127);
    }
    public UkkonenSuffixTree(int from, int to) {
        size = to - from + 1;
        offset = from;
        root = new SuffixTreeNode(size);
        root.suffixLink = root;
    }
    public void printTree(int pos) {
        System.out.println();
        printTree(root,0,0,0,pos);
    }
    private void printTree(SuffixTreeNode p, int lvl, int lt, int rt, int pos) {
        for (int i = 1; i <= lvl; i++) 
             System.out.print("   ");
        if (p != null) {                    // if a nonleaf;
             if (p == root)
                  System.out.println(p.id);
             else if (p.suffixLink != null) // to print in the middle of 
                  System.out.println(T.substring(lt,rt+1) // update;
                         + " " + p.id + " " + p.suffixLink.id
                         +" [" + lt + " " + rt + "]");
             else System.out.println(T.substring(lt,pos+1) + " " + p.id);
             for (char i = 0; i < size; i++) 
                 if (p.left[i] != -1)       // if a tree node;
                     printTree(p.descendants[i],lvl+1,p.left[i],p.right[i],pos);
        }
        else System.out.println(T.substring(lt,pos+1) + " [" + lt + " " + rt + "]");
    }
    SuffixTreeNode testAndSplit(SuffixTreeNode p, int i) {
        int Rt = i-1;
        if (Lt <= Rt) {
             int pos = T.charAt(Lt)-offset;
             SuffixTreeNode pp = p.descendants[pos];
             int lt = p.left[pos];
             int rt = p.right[pos];
             if (T.charAt(i) == T.charAt(lt+Rt-Lt+1)) { // if T(lt..rt) is 
                  endPoint = true;                      // and extension of  
                  return p;                             // T(Lt..i); 
             }
             else{// insert a new node r between s and ss by splitting
                  // edge(p,pp) = T(lt..rt) into
                  // edge(p,r)  = T(lt..lt+Rt-Lt) and 
                  // edge(r,pp) = T(lt+Rt-Lt+1..rt);
                  pos = T.charAt(lt)-offset;
                  SuffixTreeNode r = p.descendants[pos] = new SuffixTreeNode(size);
                  p.right[pos] = lt+Rt-Lt;
                  pos = T.charAt(lt+Rt-Lt+1)-offset;
                  r.descendants[pos] = pp;
                  r.left [pos] = lt+Rt-Lt+1;
                  r.right[pos] = rt;
                  endPoint = false;
                  return r;
            }                 
        }
        else if (p.left[T.charAt(i)-offset] == -1)
             endPoint = false;
        else endPoint = true;
        return p;
    }
    private SuffixTreeNode findCanonicalNode(SuffixTreeNode p, int Rt) {
        if (Rt >= Lt) {
            int pos = T.charAt(Lt)-offset;
            SuffixTreeNode pp = p.descendants[pos];
            int lt = p.left[pos];
            int rt = p.right[pos];
            while (rt - lt <= Rt - Lt) {
                Lt = Lt + rt - lt + 1;
                p = pp;
                if (Lt <= Rt) {
                    pos = T.charAt(Lt)-offset;
                    pp = p.descendants[pos];
                    lt = p.left[pos];
                    rt = p.right[pos];
                    if (p == root)
                        pp = root;
                }
            }
        }
        return p;
    }
    private SuffixTreeNode update(SuffixTreeNode p, int i) {
        SuffixTreeNode prev = null, r = testAndSplit(p,i);
        while (!endPoint) {
            int pos = T.charAt(i)-offset;
            r.left [pos] = i;      // add a T(i)-edge to r;
            r.right[pos] = T.length()-1;
            if (prev != null)
                prev.suffixLink = r;
            prev = r;
            if (p == root)
                 Lt++;
            else p = p.suffixLink;
            p = findCanonicalNode(p,i-1);
            r = testAndSplit(p,i); // check if not the endpoint;
        }
        if (prev != null)
            prev.suffixLink = p;
        return p;
    }
    public void run(String text) {
        T = text;
        final int n = T.length(), pos = T.charAt(0)-offset;
        SuffixTreeNode canonicalNodeAP = root, canonicalNodeEP;
        root.left [pos] = 0;
        root.right[pos] = n-1;
        for (int i = 1; i < n; i++) {
            canonicalNodeEP = update(canonicalNodeAP,i);
            // and thus, endpoint = node(canonicalNodeEP,Lt,i);
            canonicalNodeAP = findCanonicalNode(canonicalNodeEP,i);
            // and so, active point = node(canonicalNodeAP,Lt,i);
            printTree(i);
        }
    }
}

