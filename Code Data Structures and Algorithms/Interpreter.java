import java.io.*;

class Id {
    private String id;
    public double value;
    public Id(String s, double d) {
        id = s; value = d;
    }
    public boolean equals(Object node) {
        return id.equals(((Id)node).id);
    }
    public String toString() {
        return id + " = " + value + "; ";
    }
}

public class Interpreter {
    private StreamTokenizer fIn = new StreamTokenizer(
                                  new BufferedReader(
                                  new InputStreamReader(System.in)));
    private java.util.LinkedList<Id> idList = new java.util.LinkedList<Id>();
    public Interpreter() {
        fIn.wordChars('$','$');// include underscores and dollar signs as  
        fIn.wordChars('_','_');// word constituents; examples of identifiers: 
                               // var1, x, _pqr123xyz, $aName;
        fIn.ordinaryChar('/'); // by default, '/' is a comment character;
        fIn.ordinaryChar('.'); // otherwise "n-123.45" 
        fIn.ordinaryChar('-'); // is considered a token;
    }
    private void issueError(String s) {
        System.out.println(s);
        Runtime.getRuntime().exit(-1);
    }
    private void addOrModify(String id, double e) {
        Id tmp = new Id(new String(id),e);
        int pos;
        if ((pos = idList.indexOf(tmp)) != -1)
             (idList.get(pos)).value = e;
        else idList.add(tmp);
    }
    private double findValue(String id) {
        int pos;
        if ((pos = idList.indexOf(new Id(id,0.0))) != -1)
             return idList.get(pos).value;
        else issueError("Unknown variable " + id);
        return 0.0;  // this statement is never reached;
    }
    private double factor() throws IOException {
        double val, minus = 1.0;
        fIn.nextToken();
        while (fIn.ttype == '+' || fIn.ttype == '-') { // take all '+'s 
            if (fIn.ttype == '-')                      // and '-'s;
                minus *= -1.0;
            fIn.nextToken();
        }
        if (fIn.ttype == fIn.TT_NUMBER || fIn.ttype == '.') { 
            if (fIn.ttype == fIn.TT_NUMBER) {  // factor can be a number:
                 val = fIn.nval;               // 123, .123, 123., 12.3;
                 fIn.nextToken();
            }
            else val = 0;
            if (fIn.ttype == '.') {
                 fIn.nextToken();
                 if (fIn.ttype == fIn.TT_NUMBER) {
                      String s = fIn.nval + "";
                      s = "." + s.substring(0,s.indexOf('.'));
                      val += Double.valueOf(s).doubleValue();
                 }
                 else fIn.pushBack(); 
            }
            else fIn.pushBack(); 
        }
        else if (fIn.ttype == '(') {           // or a parenthesized 
             val = expression();               // expression,
             if (fIn.ttype == ')')
                  fIn.nextToken();
             else issueError("Right parenthesis is left out.");
        }
        else {
             val = findValue(fIn.sval);        // or an identifier;
        }
        return minus*val;
    }
    private double term() throws IOException {
        double f = factor();
        while (true) {
            fIn.nextToken();
            switch (fIn.ttype) {
                case '*' : f *= factor(); break;
                case '/' : f /= factor(); break;
                default  : fIn.pushBack(); return f;
            }
        }
    }
    private double expression() throws IOException {
        double t = term();
        while (true) {
            fIn.nextToken();
            switch (fIn.ttype) {
                case '+' : t += term(); break;
                case '-' : t -= term(); break;
                default  : fIn.pushBack(); return t;
            }
        }
    }
    public void run() {
        try {
            System.out.println("The program processes statements in the "
                    + "following format:\n"
                    + "\t<id> = <expr>;\n\tprint <id>\n\tstatus\n\tend");
            while (true) {
                System.out.print("Enter a statement: ");
                fIn.nextToken();
                String str = fIn.sval;
                if (str.toUpperCase().equals("STATUS")) {
                     java.util.Iterator it = idList.iterator();
                     while (it.hasNext())
                         System.out.println(it.next());
                }
                else if (str.toUpperCase().equals("PRINT")) {
                     fIn.nextToken();
                     str = fIn.sval;
                     System.out.println(str + " = " + findValue(str));
                }
                else if (str.toUpperCase().equals("END"))
                     return;
                else {
                     fIn.nextToken();
                     if (fIn.ttype == '=') {
                          double e = expression();
                          fIn.nextToken();
                          if (fIn.ttype != ';')
                               issueError("There are some extras in the statement.");
                          else addOrModify(str,e);
                     }
                     else issueError("'=' is missing.");
                }
            }
        } catch (IOException e) {
             e.printStackTrace();
        }
    }
    public static void main(String args[]) {
        (new Interpreter()).run();
    }
}
