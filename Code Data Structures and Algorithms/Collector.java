import java.util.Random;

public class Collector {
    private Heap heap = new Heap();
    private Random rd = new Random(10);
    private int val = 123;
    public void program() {
        int rn, p, q = 1, r = 1;
        if (heap.rootCnt() == 0) { // call heap.allocateAtom(0,val++);
             p = 0;
             rn = 1;
        }
        else {
             rn = Math.abs(rd.nextInt()) % 100 + 1;
             p  = Math.abs(rd.nextInt()) % (heap.rootCnt()+1); // possibly 
             q  = Math.abs(rd.nextInt()) % heap.rootCnt();     // new root;
             r  = Math.abs(rd.nextInt()) % heap.rootCnt();
        }
        if (rn <= 20)
             heap.allocateAtom(p,val++);
        else if (rn <= 40)
             heap.allocateNonAtom(p,q,r);
        else if (rn <= 60)
             heap.updateHead(q,r);
        else if (rn <= 80)
             heap.updateTail(q,r);
        else heap.deallocate(p);
        heap.printHeap();
    }
    static public void main(String a[]) {
        Collector c = new Collector();
        for (int i = 0; i < 50; i++)
            c.program();
    }
}
