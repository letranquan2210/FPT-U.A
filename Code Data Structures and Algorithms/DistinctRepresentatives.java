import java.io.*;
import java.util.*;

class Vertex {
    public int idNum, capacity, edgeFlow;
    public boolean forward; // direction;
    public Vertex twin;     // edge in opposite direction;
    public Vertex() {
    }
    public Vertex(int id, int c, int ef, boolean f) {
        idNum = id; capacity = c; edgeFlow = ef; forward = f; twin = null;
    }
    public boolean equals(Object v) {
        return idNum == ((Vertex)v).idNum;
    }
    public String toString() {
        return (idNum + " " + capacity + " " + edgeFlow + " " + forward);
    }
}

class VertexInArray {
    public String idName;
    public int vertexFlow;
    public boolean labeled = false;
    public int parent;
    public LinkedList<Vertex> adjacent = new LinkedList<>();
    public Vertex corrVer;   // corresponding vertex: vertex on parent's
    public VertexInArray() { // list of adjacent vertices with the same
    }                        // idNum as the cell's index;
    public VertexInArray(String s) {
        idName = s; 
    }
    public boolean equals(Object v) {
        return idName.equals(((VertexInArray)v).idName);
    }
    public void display() {
        System.out.print(idName + ' ' + vertexFlow + ' '
             + labeled + ' ' + parent + ' ' + corrVer + "-> ");
        System.out.print(adjacent);
        System.out.println();
    }
}

class Network {
    public Network() {
        vertices.add(source,new VertexInArray());
        vertices.add(sink,  new VertexInArray());
        ((VertexInArray)vertices.get(source)).idName = "source";
        ((VertexInArray)vertices.get(sink)).idName   = "sink";
        ((VertexInArray)vertices.get(source)).parent = none;
    }
    private final int sink = 1, source = 0, none = -1;
    private ArrayList<VertexInArray> vertices = new ArrayList<>();
    private int edgeSlack(Vertex u) {
        return u.capacity - u.edgeFlow;
    }
    private boolean labeled(Vertex p) {
        return ((VertexInArray)vertices.get(p.idNum)).labeled;
    }
    public void display() {
        for (int i = 0; i < vertices.size(); i++) {
            System.out.print(i + ": " );
            ((VertexInArray)vertices.get(i)).display();
        }
    }
    public void readCommittees(String fileName, InputStream fIn) {
        int ch = 1, pos;
        try {
            while (ch > -1) {
                while (true)
                    if (ch > -1 && !Character.isLetter((char)ch)) // skip 
                         ch = fIn.read();                  // nonletters;
                    else break;
                if (ch == -1)
                    break;
                String s = "";
                while (ch > -1 && ch != ':') {
                    s += (char)ch;
                    ch = fIn.read();
                }
                VertexInArray committee = new VertexInArray(s.trim());
                int commPos = vertices.size();
                Vertex commVer = new Vertex(commPos,1,0,false);
                vertices.add(committee);
                for (boolean lastMember = false; !lastMember; ) {
                    while (true)
                        if (ch > -1 && !Character.isLetter((char)ch))
                             ch = fIn.read();         // skip nonletters;
                        else break;
                    if (ch == -1)
                        break;
                    s = "";
                    while (ch > -1 && ch != ',' && ch != ';') {
                        s += (char)ch;
                        ch = fIn.read();
                    }
                    if (ch == ';')
                        lastMember = true;
                    VertexInArray member = new VertexInArray(s.trim());
                    Vertex memberVer = new Vertex(0,1,0,true);
                    if ((pos = vertices.indexOf(member)) == -1) {
                         memberVer.idNum = vertices.size();
                         member.adjacent.addFirst(new
                                         Vertex(sink,1,0,true));
                         member.adjacent.addFirst(commVer);
                         vertices.add(member);
                    }
                    else { 
                         memberVer.idNum = pos;
                         ((VertexInArray)vertices.get(pos)).
                                adjacent.addFirst(commVer);
                    }
                    committee.adjacent.addFirst(memberVer);
                    memberVer.twin = commVer;
                    commVer.twin = memberVer;
                }
                commVer = new Vertex(commPos,1,0,true);
                ((VertexInArray)vertices.get(source)).adjacent.
                                        addFirst(commVer);
            }
        } catch (IOException io) {
        }
        display();
    }
    private void label(Vertex u, int v) {
        VertexInArray uu = (VertexInArray) vertices.get(u.idNum);
        VertexInArray vv = (VertexInArray) vertices.get(v);
        uu.labeled = true;
        if (u.forward)
             uu.vertexFlow = Math.min(vv.vertexFlow,edgeSlack(u));
        else uu.vertexFlow = Math.min(vv.vertexFlow,u.edgeFlow);
        uu.parent  = v;
        uu.corrVer = u;
    }
    private void augmentPath() {
        int sinkFlow = ((VertexInArray)vertices.get(sink)).vertexFlow;
        Stack<String> path = new Stack<>();
        for (int i = sink; i != source;
             i = ((VertexInArray)vertices.get(i)).parent) {
            VertexInArray vv = (VertexInArray) vertices.get(i);
            path.push(vv.idName);
            if (vv.corrVer.forward)
                 vv.corrVer.edgeFlow += sinkFlow;
            else vv.corrVer.edgeFlow -= sinkFlow;
            if (vv.parent != source && i != sink)
                 vv.corrVer.twin.edgeFlow = vv.corrVer.edgeFlow;
        }
        for (int i = 0; i < vertices.size(); i++)
            ((VertexInArray)vertices.get(i)).labeled = false;
        System.out.print("  source");
        while (!path.isEmpty())
            System.out.print(" => " + path.pop());
        System.out.print(" (augmented by " + sinkFlow + ");\n");
    }
    public void FordFulkersonMaxFlow() {
        Stack<Integer> labeledS = new Stack<>();
        for (int i = 0; i < vertices.size(); i++) 
            ((VertexInArray) vertices.get(i)).labeled = false;
        ((VertexInArray)vertices.get(source)).vertexFlow =
            Integer.MAX_VALUE;
        labeledS.push(new Integer(source));
        System.out.println("Augmenting paths:");
        while (!labeledS.isEmpty()) {   // while not stuck;
            int v = ((Integer) labeledS.pop()).intValue();
            for (Iterator it = ((VertexInArray)vertices.get(v)).
                                        adjacent.iterator();
                 it.hasNext(); ) {
                Vertex u = (Vertex) it.next();
                if (!labeled(u)) {
                    if (u.forward && edgeSlack(u) > 0 ||
                        !u.forward && u.edgeFlow > 0)
                         label(u,v);
                    if (labeled(u))
                         if (u.idNum == sink) {
                              augmentPath();
                              labeledS.clear(); // look for another path;
                              labeledS.push(new Integer(source));
                              break;
                         }
                         else {
                              labeledS.push(new Integer(u.idNum));
                              ((VertexInArray)vertices.get(u.idNum)).
                                                        labeled = true;
                         }
                 }
             }
        }
    }
}

public class DistinctRepresentatives {
    static public void main(String args[]) {
        String fileName = "";
        Network net = new Network();
        InputStream fIn;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(isr);
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
            net.readCommittees(fileName,fIn);
            fIn.close();
        } catch(IOException io) {
            System.err.println("Cannot open " + fileName);
        }
        net.FordFulkersonMaxFlow();
        net.display();
    }
}
