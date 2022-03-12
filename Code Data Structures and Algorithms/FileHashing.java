import java.io.*;
import java.io.File;

public class FileHashing {
    private final int bucketSize = 2, tableSize = 3, strLen = 20;
    private final int recordLen = strLen;
    private final byte empty = '*', delMarker = '#';
    private long[] positions;
    private BufferedReader buffer = new BufferedReader(
                                    new InputStreamReader(System.in));
    private RandomAccessFile outfile;
    private RandomAccessFile sorted;
    private RandomAccessFile overflow;
    public FileHashing() {
    }

    private void print(byte[] s) { // print a byte array;
        for(int k = 0; k < s.length; k++)
            System.out.print((char)s[k]);
    }

    private long hash(byte[] s) {
        long xor = 0, pack;
        int i, j, slength; // exclude trailing blanks:
        for (slength = s.length; s[slength-1] == ' '; slength--);
        for (i = 0; i < slength; ) { 
            for (pack = j = 0; ; j++, i++) {
                pack |= (long) s[i];  // include s[i] in the rightmost 
                if (j == 3 || i == slength - 1) { // byte of pack;
                    i++;
                    break;
                }
                pack <<= 8;
            }             // xor at one time 8 bytes from s;
            xor ^= pack;  // last iteration may put less
        }                 // than 8 bytes in pack;
        return (xor % tableSize) * bucketSize * recordLen;
    }// return byte position of home bucket for s;

    private byte[] getName() throws IOException {
        System.out.print("Enter a name & phone#: ");
        String s = buffer.readLine();
        for (int i = s.length(); i < recordLen; i++)
            s += ' ';
        return s.getBytes(); // s => line
    }

    private int comparesTo(byte[] s1, byte[] s2) { // same length
        for (int i = 0; i < s1.length; i++)        // of s1 and s2 
            if (s1[i] != s2[i])                    // is assumed;
                return s1[i] - s2[i];
        return 0;
    }

    void insert() throws IOException {
        insertion(getName());
    }

    void insertion(byte[] line) throws IOException {
        byte[] name = new byte[recordLen];
	boolean done = false, inserted = false;
        int counter = 0;
        long address = hash(line);
        outfile.seek(address);
        while (!done && outfile.read(name) != -1) {
	    if (name[0] == empty || name[0] == delMarker) {
	         outfile.seek(address+counter*recordLen);
                 outfile.write(line);
                 done = inserted = true;
            }
            else if (comparesTo(name,line) == 0) {
                 print(line);
                 System.out.println(" is already in the file");
                 return;
            }
            else counter++;
            if (counter == bucketSize)
                 done = true;
            else outfile.seek(address+counter*recordLen);
        }
        if (!inserted) {
            done = false;
            counter = 0;
            overflow.seek(0);
            while (!done && overflow.read(name) != -1) {
                if (name[0] == delMarker)
                     done = true;
                else if (comparesTo(name,line) == 0) {
                     print(line);
                     System.out.println(" is already in the file");
                     return;
                }
                else counter++;
            }
            if (done)
                 overflow.seek(counter*recordLen);
            else overflow.seek(overflow.length());
            overflow.write(line);
        }
    }

    private void delete() throws IOException {
        byte[] line = getName();
        long address = hash(line);
        outfile.seek(address);
        int counter = 0;
        boolean done = false, deleted = false;
        byte[] name = new byte[recordLen];
        while (!done && outfile.read(name) != -1) {
            if (comparesTo(line,name) == 0) {
                 outfile.seek(address+counter*recordLen);
                 outfile.write(delMarker);
                 done = deleted = true;
            }
            else counter++;
            if (counter == bucketSize)
                 done = true;
            else outfile.seek(address+counter*recordLen);
        }
        if (!deleted) {
            done = false;
            counter = 0;
            overflow.seek(0);
            while (!done && overflow.read(name) != -1) {
                if (comparesTo(line,name) == 0) {
                     overflow.seek(counter*recordLen);
                     overflow.write(delMarker);
                     done = deleted = true;
                }
                else counter++;
                overflow.seek(counter*recordLen);
            }
        }
        if (!deleted) {
            print(line);
            System.out.println(" is not in database");
        }
    }

    private void swap(long[] arr, int i, int j) {
        long tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    private int partition(int low, int high) throws IOException {
        byte[] rec = new byte[recordLen];
        byte[] pivot = new byte[recordLen];
        int i, lastSmall;
        swap(positions,low,(low+high)/2);
        outfile.seek(positions[low]*recordLen);
        outfile.read(pivot);
        for (lastSmall = low, i = low+1; i <= high; i++) {
            outfile.seek(positions[i]*recordLen);
            outfile.read(rec);
            if (comparesTo(rec,pivot) < 0) {
                lastSmall++;
                swap(positions,lastSmall,i);
            }
        }
        swap(positions,low,lastSmall);
        return lastSmall;
    }

    private void sort(int low, int high) throws IOException {
        if (low < high) {
            int pivotLoc = partition(low, high);
            sort(low, pivotLoc-1);
            sort(pivotLoc+1, high);
        }
    }

    private void sortFile() throws IOException {
        byte[] rec = new byte[recordLen];
        sort(1,(int)positions[0]); // positions[0] contains the # of elements;
        for (int i = 1; i <= positions[0]; i++) {  // put data from 
            outfile.seek(positions[i]*recordLen);  // outfile in sorted order
            outfile.read(rec);
            sorted.write(rec);                     // in file sorted;
        }
    }

    // data from overflow file and outfile are all stored in outfile and
    // prepared for external sort by loading positions of the data to an array;

    private void combineFiles() throws IOException {
        byte[] rec = new byte[recordLen];
        int counter = bucketSize*tableSize;
        outfile.seek(outfile.length());
        overflow.seek(0);
        while (overflow.read(rec) != -1) { // transfer from
            if (rec[0] != delMarker) {     // overflow to outfile only
                counter++;                 // valid (non-deleted) items;
                outfile.write(rec);
            }
        }
        positions = new long[counter+1];
        outfile.seek(0);         // load to the array positions
        int arrCnt = 1;          // of valid data stored in output file;
        for (int i = 0; i < counter; i++) {
            outfile.seek(i*recordLen);
            outfile.read(rec);
            if (rec[0] != empty && rec[0] != delMarker)
                positions[arrCnt++] = i;
        }
        positions[0] = --arrCnt; // store the number of data in position 0;
    }

    public void processFile(String fileName) {
        char command = '1';
        byte[] line = new byte[recordLen];
        String commandLine;
        try {
            (new File(".\\","outfile")).delete();
            (new File(".\\","overflow")).delete();
            (new File(".\\","sorted")).delete();
            RandomAccessFile fIn = new RandomAccessFile(fileName,"rw");
            outfile = new RandomAccessFile("outfile","rw");
            sorted = new RandomAccessFile("sorted","rw");
            overflow = new RandomAccessFile("overflow","rw");
            for (int i = 1; i <= tableSize*bucketSize*recordLen; i++) 
                outfile.write(empty);       // initialize outfile;
            while (fIn.read(line) != -1)    // load fIn to outfile;
                insertion(line);
            printFile("outfile",outfile);
            printFile("overflow",overflow);
            while (command != '3') {
                System.out.print("Enter your choice "
                                + "(1. insert, 2. delete, 3. exit): ");
                commandLine = buffer.readLine();
                command = commandLine.charAt(0);
                if (command == '1')
                     insert();
                else if (command == '2')
                     delete();
                else if (command != '3')
                     System.out.println("Wrong command entered, please retry.");
                printFile("outfile",outfile);
                printFile("overflow",overflow);
            }
            combineFiles();
            printFile("outfile",outfile);
            sortFile();
            printFile("sorted",sorted);
            outfile.close();
            sorted.close();
            overflow.close();
            fIn.close();
            (new File(".\\","names")).delete();
            (new File(".\\","sorted")).renameTo(new File(".\\","names"));
        } catch (IOException ioe) {
        }
    }

    private void printFile(String name, RandomAccessFile f) throws IOException {
        byte ch = '1';
        RandomAccessFile outf = new RandomAccessFile("hash.out","rw");
        outf.seek(outf.length());
        System.out.println(name);
        outf.writeBytes(name + "\n");
        f.seek(0);
        while (true) {
            for (int i = 1; i <= bucketSize; i++) {
                for (int j = 1; j <= recordLen; j++) {
                    try {
                        ch = f.readByte();
                    } catch (EOFException e) {
                        System.out.println();
                        outf.write('\n');
                        outf.close();
                        return;
                    }
                    System.out.print((char)ch);
                    outf.write(ch);
                }
                System.out.print('|');
                outf.write('|');
            }
            System.out.println('|');
            outf.write('|');
            outf.write('\n');
        }
    }

    static public void main(String args[]) {
        String fileName = "";
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(isr);
        FileHashing fClass = new FileHashing();
        try {
            if (args.length == 0) {
                 System.out.print("Enter a file name: ");
                 fileName = buffer.readLine();
            }
            else fileName = args[0];
        } catch(IOException io) {
            System.err.println("Cannot open " + fileName);
        }
        fClass.processFile(fileName);
    }
}

