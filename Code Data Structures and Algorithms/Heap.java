import java.util.Random;

class Value {
    public int value;
    public Value(int v) {
        value = v;
    }
}

class Links {
    public int head = -1, tail = -1;
    public Links(int h, int t) {
        head = h; tail = t;
    }
}

class Cell {
    public boolean atom;
    public boolean marked = false;
    public int prev = -1, next = -1;
    public Object info = null; // either Value or Links;
}

public class Heap {
    private final int maxHeap = 6, maxRoot = 50, empty   = -1;
    private int rootCnt = 0;
    private boolean OK = true;
    private Cell[] heap = new Cell[maxHeap];
    private int[] roots = new int[maxRoot];
    private int freeCells = empty, nonFreeCells = empty;
    private Random rd = new Random(10);
    public Heap() {
        for (int i = maxHeap-1; i >= 0; i--) {
            heap[i] = new Cell();
            freeCells = insert(i,freeCells);
        }
        for (int i = maxRoot-1; i >= 0; i--)
            roots[i] = empty;
    }
    public int rootCnt() {
        return rootCnt;
    }
    public void updateHead(int p, int q) {       // Lisp's rplaca;
        if (roots[p] != empty && !heap[roots[p]].atom)
            ((Links)heap[roots[p]].info).head = roots[q];
    }
    public void updateTail(int p, int q) {       // Lisp's rplacd;
        if (roots[p] != empty && !heap[roots[p]].atom)
            ((Links)heap[roots[p]].info).tail = roots[q];
    }
    private int detach(int cell, int list) {
        if (heap[cell].next != empty)
            heap[heap[cell].next].prev = heap[cell].prev;
        if (heap[cell].prev != empty)
            heap[heap[cell].prev].next = heap[cell].next;
        if (cell == list)                 // head of the list;
             return heap[cell].next;
        else return list;
    }
    private int insert(int cell, int list) {
        heap[cell].prev = empty;
        if (cell == list)   // don't create a circular list;
             heap[cell].next = empty;
        else heap[cell].next = list;
        if (list != empty)
            heap[list].prev = cell;
        return cell;
    }
    private void collect() {
        int p, markDescendants = empty, markedCells  = empty;
        for (p = 0; p < rootCnt; p++) {
            if (roots[p] != empty) {
                nonFreeCells    = detach(roots[p],nonFreeCells);
                markDescendants = insert(roots[p],markDescendants);
                heap[roots[p]].marked = true;
            }
        }
        printList(markDescendants,"markDescendants C1 "+p);
        for (p = markDescendants; p != empty; p = markDescendants) {
            markDescendants = detach(p,markDescendants);
            markedCells     = insert(p,markedCells);
            if (!heap[p].atom) {
                if (!heap[((Links)heap[p].info).head].marked) {
                     nonFreeCells    = detach(((Links)heap[p].info).head,nonFreeCells);
                     markDescendants = insert(((Links)heap[p].info).head,markDescendants);
                     heap[((Links)heap[p].info).head].marked = true;
                }
                if (!heap[((Links)heap[p].info).tail].marked) {
                     nonFreeCells    = detach(((Links)heap[p].info).tail,nonFreeCells);
                     markDescendants = insert(((Links)heap[p].info).tail,markDescendants);
                     heap[((Links)heap[p].info).tail].marked = true;
                }
            }
        }
        printList(markedCells,"MarkedCells");
        for (p = markedCells; p != empty; p = heap[p].next)
            heap[p].marked = false;
        freeCells    = nonFreeCells;
        nonFreeCells = markedCells;
    }
    private boolean allocateAux(int p) {
        if (p == maxRoot) {
             System.out.println("No room for new roots");
             return !OK;
        }
        if (freeCells == empty)
             collect();
        if (freeCells == empty) {
             System.out.println("No room in heap for new cells");
             return !OK;
        }
        if (p == rootCnt)
             rootCnt++;
        roots[p] = freeCells;
        freeCells    = detach(roots[p],freeCells);
        nonFreeCells = insert(roots[p],nonFreeCells);
        return OK;
    }
    public void allocateAtom (int p, int val) {        // an instance of Lisp's setf;
        if (allocateAux(p) == OK) {
            heap[roots[p]].atom = true;
            heap[roots[p]].info = new Value(val);
        }
    }
    public void allocateNonAtom(int p, int q, int r) { // Lisp's cons;
        if (allocateAux(p) == OK) {
            heap[roots[p]].atom = false;
            heap[roots[p]].info = new Links(roots[q],roots[r]);
        }
    }
    public void deallocate(int p) {
        if (rootCnt > 0)
            if (Math.abs(rd.nextInt()) % 2 == 0)
                 roots[p] = roots[--rootCnt]; // remove variable when exiting a block;
            else roots[p] = empty; // set variable to null;
    }
    private void printList(int list, String name) {
        System.out.print(name + ": ");
        for (int i = list; i != empty; i = heap[i].next) {
            System.out.print("(" + i + " ");
            if (heap[i].atom)
                 System.out.print(((Value)heap[i].info).value);
            else if (heap[i].info != null)
                 System.out.print(((Links)heap[i].info).head + " " +
                                  ((Links)heap[i].info).tail);
            System.out.print(") ");
        }
        System.out.println();
    }
    public void printHeap() {
        System.out.print("roots: ");
        for (int i = 0; i < rootCnt; i++)
            System.out.print(roots[i] + " ");
        System.out.println();
        for (int i = 0; i < maxHeap; i++) {
            System.out.print("(" + i + ": " + heap[i].prev + " "
                 + heap[i].next + " "+ heap[i].atom + " " + heap[i].marked + " ");
            if (heap[i].atom)
                 System.out.print(((Value)heap[i].info).value);
            else if (heap[i].info != null)
                 System.out.print(((Links)heap[i].info).head + " " +
                                  ((Links)heap[i].info).tail);
            System.out.print(") ");
        }
        System.out.println();
        printList(freeCells,"FreeCells");
        printList(nonFreeCells,"NonFreeCells");
    }
}

