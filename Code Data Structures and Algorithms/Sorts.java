class RadixsortNode {
    public int[] arr;
    public RadixsortNode next = null;
    public RadixsortNode() {
    }
    public RadixsortNode(int[] a) {
        arr = new int[a.length];
        for (int i = 0; i < a.length; i++)
            arr[i] = a[i];
    }
    public RadixsortNode(int n) {
        arr = new int[n];
    }
}

@SuppressWarnings("unchecked")
public class Sorts {

    public void swap(Object[] a, int e1, int e2) {
        Object tmp = a[e1];
        a[e1] = a[e2];
        a[e2] = tmp;
    }

    public <T extends Comparable<? super T>> void insertionsort(T[] data) {
        for (int i = 1, j; i < data.length; i++) {
            T tmp = data[i];
            for (j = i; j > 0 && tmp.compareTo(data[j-1]) < 0; j--)
                data[j] = data[j-1];
            data[j] = tmp;
        }
    }

    public <T extends Comparable<? super T>> void selectionsort(T[] data) {
        int i, j, least;
        for (i = 0; i < data.length-1; i++) {
            for (j = i+1, least = i; j < data.length; j++)
                if (data[j].compareTo(data[least]) < 0)
                    least = j;
            if (least != i)
                swap(data,least,i);
        }
    }

    public <T extends Comparable<? super T>> void bubblesort(T[] data) {
        for (int i = 0; i < data.length-1; i++)
            for (int j = data.length-1; j > i; --j)
                if (data[j].compareTo(data[j-1]) < 0)
                    swap(data,j,j-1);
    }

    public <T extends Comparable<? super T>> void combsort(T[] data) {
        int step = data.length;
        int r, j;
        while ((step = (int)(step/1.3)) > 1)                // phase 1
            for (j = data.length-1; j >= step; j--) {
                r = j-step;                                
                if (data[j].compareTo(data[r]) < 0)
                      swap(data,j,r);
            }
           boolean again = true;
           for (int i = 0; i < data.length-1 && again; i++) // phase 2
                for (j = data.length-1, again = false; j > i; --j) {
                    if (data[j].compareTo(data[j-1]) < 0) {
                         swap(data,j,j-1);
                         again = true;
                    }
		}
    }

    public <T extends Comparable<? super T>> void Shellsort (T[] data) {
        int i, j, k, h, hCnt, increments[] = new int[20];
    //  create an appropriate number of increments h
        for (h = 1, i = 0; h < data.length; i++) {
            increments[i] = h;
            h = 3*h + 1;
        }
     // loop on the number of different increments h
        for (i--; i >= 0; i--) {
            h = increments[i];
         // loop on the number of subarrays h-sorted in ith pass
            for (hCnt = h; hCnt < 2*h; hCnt++) {
             // insertion sort for subarray containing every hth element of array data
                for (j = hCnt; j < data.length; ) {
                    T tmp = data[j];
                    k = j;
                    while (k-h >= 0 && tmp.compareTo(data[k-h]) < 0) {
                        data[k] = data[k-h];
                        k -= h;
                    }
                    data[k] = tmp;
                    j += h;
                }
            }
        }
    }

    private <T extends Comparable<? super T>> void moveDown(T[] data, int first, int last) {
        int largest = 2*first + 1;
        while (largest <= last) {
            if (largest < last && // first has two children (at 2*first+1 and
                                                          //    2*first+2);
                data[largest].compareTo(data[largest+1]) < 0)
                    largest++;
            if (data[first].compareTo(data[largest]) < 0) {  
                 swap(data,first,largest);       // if necessary, swap values
                 first = largest;                // and move down;
                 largest = 2*first + 1;
            }
            else largest = last + 1;// to exit the loop: the heap property
        }                           // isn't violated by data[first];
    }

    public <T extends Comparable<? super T>> void heapsort(T[] data) {
        for (int i = data.length/2 - 1; i >= 0; --i)
            moveDown(data,i,data.length-1);
        for (int i = data.length-1; i >= 1; --i) {
            swap(data,0,i);
            moveDown(data,0,i-1);
        }
    }

    private <T extends Comparable<? super T>> void quicksort(T[] data, int first, int last) {
        int lower = first + 1, upper = last;
        swap(data,first,(first+last)/2);
        T bound = data[first];
        while (lower <= upper) {
            while (bound.compareTo(data[lower]) > 0)
                 lower++;
            while (bound.compareTo(data[upper]) < 0)
                 upper--;
            if (lower < upper)
                 swap(data,lower++,upper--);
            else lower++;
        }
        swap(data,upper,first);
        if (first < upper-1)
            quicksort(data,first,upper-1);
        if (upper+1 < last)
            quicksort(data,upper+1,last);
    }

    public <T extends Comparable<? super T>> void quicksort(T[] data) {
        if (data.length < 2)
            return;
        int max = 0;
        // find the largest element and put it at the end of data;
        for (int i = 1; i < data.length; i++)
            if (data[max].compareTo(data[i]) < 0)
                max = i;
        swap(data,data.length-1,max);    // largest el is now in its
        quicksort(data,0,data.length-2); // final position;
    }

    public <T extends Comparable<? super T>> void insertionsort(T[] data, int first, int last) {
        for (int i = first, j; i <= last; i++) {
            T tmp = data[i];
            for (j = i; j > 0 && tmp.compareTo(data[j-1]) < 0; j--)
                data[j] = data[j-1];
            data[j] = tmp;
        }
    }

    public <T extends Comparable<? super T>> void quicksort2(T[] data, int first, int last) {
        if (last - first < 30)
             insertionsort(data,first,last);
        else {
             int lower = first + 1, upper = last;
             swap(data,first,(first+last)/2);
             T bound = data[first];
             while (lower <= upper) {
                 while (bound.compareTo(data[lower]) > 0)
                     lower++;
                 while (bound.compareTo(data[upper]) < 0)
                     upper--;
                 if (lower < upper)
                     swap(data,lower++,upper--);
                 else lower++;
             }
             swap(data,upper,first);
             if (first < upper-1)
                  quicksort2(data,first,upper-1);
             if (upper+1 < last)
                  quicksort2(data,upper+1,last);
        }
    }

    public <T extends Comparable<? super T>> void quicksort2(T[] data) {
        if (data.length < 2)
            return;
        int max = 0;
        // find the largest element and put it at the end of data;
        for (int i = 1; i < data.length; i++)
            if (data[max].compareTo(data[i]) < 0)
                max = i;
        swap(data,data.length-1,max);     // largest el is now in its
        quicksort2(data,0,data.length-2); // final position;
    }

    private Comparable[] temp; // used by merge();

    private <T extends Comparable<? super T>> void merge(T[] data, int first, int last) {
        int mid = (first + last) / 2;
        int i1 = 0, i2 = first, i3 = mid + 1;
        while (i2 <= mid && i3 <= last)
            if (data[i2].compareTo(data[i3]) < 0)
                 temp[i1++] = data[i2++];
            else temp[i1++] = data[i3++];
        while (i2 <= mid)
            temp[i1++] = data[i2++];
        while (i3 <= last)
            temp[i1++] = data[i3++];
        for (i1 = 0, i2 = first; i2 <= last; data[i2++] = (T) temp[i1++]);
    }

    private <T extends Comparable<? super T>> void mergesort(T[] data, int first, int last) {
        int mid = (first + last) / 2;
        if (first < mid)
            mergesort(data, first, mid);
        if (mid+1 < last)
            mergesort(data, mid+1, last);
        merge(data, first, last);
    }

    public <T extends Comparable<? super T>> void mergesort(T[] data) {
        if (data.length < 2)
            return;
        temp = new Comparable[data.length];
        mergesort(data,0,data.length-1);
    }

    private final int radix = 10;
    private final int digits = 10;
    private final int bits = 31;

    public void radixsort(int[] data) {
        int d, j, k, factor;
        Queue<Integer>[] queues = new Queue[radix]; // radix is 10;
        for (d = 0; d < radix; d++)
            queues[d] = new Queue<Integer>();
        for (d = 1, factor = 1; d <= digits; factor *= radix, d++) {
            for (j = 0; j < data.length; j++)
                queues[(data[j] / factor) % radix].enqueue(data[j]);
            for (j = k = 0; j < radix; j++)
                while (!queues[j].isEmpty())
                     data[k++] = queues[j].dequeue();
        }
    }

    public void bitRadixsort(int[] data, int b) {
        int pow2b = 1;
        pow2b <<= b;
        int i, j, k, pos = 0, mask = pow2b-1;
        int last = (bits % b == 0) ? (bits/b) : (bits/b + 1);
        Queue<Integer>[] queues = new Queue[pow2b];
        for (i = 0; i < pow2b; i++) 
            queues[i] = new Queue<Integer>();
        for (i = 0; i < last; i++) {
            for (j = 0; j < data.length; j++)
                queues[(data[j] & mask) >> pos].enqueue(data[j]);
            mask <<= b;
            pos = pos+b;
            for (j = k = 0; j < pow2b; j++) 
                while (!queues[j].isEmpty())
                    data[k++] = queues[j].dequeue();
        }
    }

    private void clear(int[] arr, int q) {
        arr[q] = -1;
    }
    private boolean isEmpty(int q) {
        return q == -1;
    }

    public void radixsort2(int[] data) {
        int d, j, k, factor, where;
        int[] queues = new int[data.length], queueHeads = new int[radix];
        int[] queueTails = new int[radix];
        RadixsortNode n2 = new RadixsortNode(data), n1 = new RadixsortNode();
        n1.arr = data;
        n2.next = n1;
        n1.next = n2;
        for (j = 0; j < radix; j++)
            clear(queueHeads,j);
        for (d = 1, factor = 1; d <= digits; factor *= radix, d++) {
            for (j = 0; j < data.length; j++) {
                where = (n1.arr[j] / factor) % radix; // dth digit;
                if (isEmpty(queueHeads[where]))
                     queueTails[where] = queueHeads[where] = j;
                else {
                     queues[queueTails[where]] = j;
                     queueTails[where] = j;
                }
            }
            for (j = 0; j < radix; j++)
                if (!(isEmpty(queueHeads[j])))
                     clear(queues,queueTails[j]);
            for (j = k = 0; j < radix; j++)
                while (!(isEmpty(queueHeads[j]))) {
                     n2.arr[k++] = n1.arr[queueHeads[j]];
                     queueHeads[j] = queues[queueHeads[j]]; // also clears
                }                                           // queueHeads[];
            n2 = n2.next;
            n1 = n1.next;
        }
        if (digits % 2 != 0) // if digits is an odd number;
            for (d = 0; d < data.length; d++)
                data[d] = n1.arr[d];
    }

    public void bitRadixsort2(int[] data, int b) {
        int pow2b = 1;
        pow2b <<= b;
        int d, j, k, where, pos = 0, mask = pow2b-1;
        int last = (bits % b == 0) ? (bits/b) : (bits/b + 1);
        int[] queues = new int[data.length], queueHeads = new int[pow2b];
        int[] queueTails = new int[pow2b];
        RadixsortNode n2 = new RadixsortNode(data), n1 = new RadixsortNode();
        n1.arr = data;
        n2.next = n1;
        n1.next = n2;
        for (d = 0; d < last; d++) {
            for (j = 0; j < pow2b; j++) 
                clear(queueHeads,j);
            for (j = 0; j < data.length; j++) {
                where = (n1.arr[j] & mask) >> pos;
                if (isEmpty(queueHeads[where]))
                     queueTails[where] = queueHeads[where] = j;
                else {
                     queues[queueTails[where]] = j;
                     queueTails[where] = j;
                }
            }
            mask <<= b;
            pos = pos+b;
            for (j = 0; j < pow2b; j++)
                if (!(isEmpty(queueHeads[j])))
                     clear(queues,queueTails[j]);
            for (j = k = 0; j < pow2b; j++)
                while (!(isEmpty(queueHeads[j]))) {
                     n2.arr[k++] = n1.arr[queueHeads[j]];
                     queueHeads[j] = queues[queueHeads[j]];
                }
            n2 = n2.next;
            n1 = n1.next;
        }
        if (last % 2 != 0) // if bits is an odd number;
            for (d = 0; d < data.length; d++)
                data[d] = n1.arr[d];
    }

    void countingsort(int data[]) {
        int i, largest = data[0];
        int[] tmp = new int[data.length];
        for (i = 1; i < data.length; i++)      // find the largest number
            if (largest < data[i])             // in data and create the array
                largest = data[i];             // of counters accordingly;
        int[] count = new int[largest+1];
	for (i = 0; i <= largest; i++)
            count[i] = 0;
        for (i = 0; i < data.length; i++)      // count numbers in data[];
            count[data[i]]++;
        for (i = 1; i <= largest; i++)         // count numbers <= i; 
            count[i] = count[i-1] + count[i];
        for (i = data.length-1; i >= 0; i--) { // put numbers in order in tmp[];
             tmp[count[data[i]]-1] = data[i];
             count[data[i]]--;
	}
        for (i = 0; i < data.length; i++)      // transfer numbers from tmp[]
             data[i] = tmp[i];                 // to the original array;
    }

}
