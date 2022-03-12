import java.io.*;
import java.util.*;

class Variable implements Comparable<Variable>, Cloneable {
    public char id;
    public int exp;
    public Variable() {
    }
    public Variable(char c, int i) {
        id = c; exp = i;
    }
    public int compareTo(Variable v) {
        return id - v.id;
    }
    public boolean equals(Object v) {
        return id == ((Variable)v).id && exp == ((Variable)v).exp;
    }
    public Object clone() {
        return new Variable(id,exp);
    }
} 

class Term implements Comparable<Term>, Cloneable {
    public Term() {
    }
    public int coeff;
    public ArrayList<Variable> vars = new ArrayList<Variable>();
    public Object clone() {
        Term t = new Term();
        t.coeff = coeff;
        t.vars = (ArrayList<Variable>) vars.clone();
        for (int i = 0; i < vars.size(); i++)
            t.vars.set(i,(Variable)vars.get(i).clone());
        return t;
    }
   /** two terms are equal if all varibles are the same and
     * corresponding variables are raised to the same power;
     * the first cell of the node containing a term is excluded
     * from comparison, since it stores coefficient of the term;
     */
    public boolean equals(Object term) {
        int i;
        for (i = 0; i < Math.min(vars.size(),((Term)term).vars.size()) &&
                    vars.get(i).equals(((Term)term).vars.get(i)); i++);
        return i == vars.size() && vars.size() == ((Term)term).vars.size();
    }
    public int compareTo(Term term2) {
        if (vars.size() == 0)
            return 1;            // this is just a coefficient;
        else if (term2.vars.size() == 0)
            return -1;           // term2 is just a coefficient;
        Variable var1, var2;
        for (int i = 0; i < Math.min(vars.size(),term2.vars.size()); i++) {
            var1 = vars.get(i);
            var2 = term2.vars.get(i);
            if (var1.id < var2.id)
                 return -1;      // this precedes term2;
            else if (var2.id < var1.id)
                 return 1;       // term2 precedes this; 
            else if (var1.exp < var2.exp)
                 return -1;      // this precedes term2;
            else if (var2.exp < var1.exp)
                 return 1;       // term2 precedes this;
        }
        return vars.size() - term2.vars.size();
    }
}

class Polynomial {
    private LinkedList<Term> terms = new LinkedList<Term>();
    public Polynomial() {
    }
    private void error(String s) {
        System.out.println(s);
        Runtime.getRuntime().exit(-1);
    }
    public Polynomial add(Polynomial polyn2) {
        ListIterator<Term> p1, p2;
        Polynomial result = new Polynomial();
        int i;
        for (p1 = terms.listIterator(); p1.hasNext(); ) // create new polynomial
            result.terms.add((Term)p1.next().clone());  //  out of copies
        for (p2 = polyn2.terms.listIterator(); p2.hasNext(); ) // of this
            result.terms.add((Term)p2.next().clone());  // polynomial and polyn2;
        for (i = 0, p1 = result.terms.listIterator(); p1.hasNext();
               i++, p1 = result.terms.listIterator(i)) {
            Term term1 = p1.next();
            for (p2 = p1; p2.hasNext(); ) {
                Term term2 = p2.next();
                if (term1.equals(term2)) {
                     term2.coeff += term1.coeff;
                     result.terms.remove(term1);
                     if (term2.coeff == 0)      // remove terms with zero
                          result.terms.remove(term2); // coefficients;
                     i = -1; // to become i = 0 after autoincrement;
                     break;
                }
            }
        }
        Collections.sort(result.terms);
        return result;
    }
    public void get(InputStream fIn) {
        int ch = ' ', i, sign, exp;
        boolean coeffUsed;
        char id;
        Term term = new Term();
        try {
            while (ch > -1) {
                coeffUsed = false;
                while (true)
                    if (ch > -1 && Character.isWhitespace((char)ch)) // skip 
                         ch = fIn.read();                            // blanks;
                    else break;
                if (!Character.isLetterOrDigit((char)ch) &&
                     ch != ';' && ch != '-' && ch != '+')
                     error("Wrong character entered2");
                if (ch == -1)                    
                    break;
                sign = 1;
                while (ch == '-' || ch == '+') {   // first get sign(s) of Term
                    if (ch == '-')
                        sign *= -1;
                    ch = fIn.read();
                    while (Character.isWhitespace((char)ch))
                        ch = fIn.read();
                }
                if (Character.isDigit((char)ch)) { // and then its coefficient;
                     String number = "";
                     while (Character.isDigit((char)ch)) {      
                         number += (char) ch;
                         ch = fIn.read();
                     }
                     while (Character.isWhitespace((char)ch)) 
                         ch = fIn.read();
                     term.coeff = sign * Integer.valueOf(number).intValue();
                     coeffUsed = true;
                }
                else term.coeff = sign;
                for (i = 0; Character.isLetterOrDigit((char)ch); i++) {
                    id = (char) ch;          // process this term:
                    ch = fIn.read();         // get a variable name
                    if (Character.isDigit((char)ch)) {  // and an exponent
                         String number = "";            // (if any);
                         while (Character.isDigit((char)ch)) {
                             number += (char) ch;
                             ch = fIn.read();
                         }
                         exp = Integer.valueOf(number).intValue();
                         while (Character.isWhitespace((char)ch))
                             ch = fIn.read();
                    }
                    else exp = 1;
                    term.vars.add(new Variable(id,exp));
                }
                terms.add((Term)term.clone());
                term.vars = new ArrayList<Variable>();
                while (Character.isWhitespace((char)ch))
                    ch = fIn.read();
                if (ch == ';')          // finish if a semicolon is entered;
                     if (coeffUsed || i > 0)
                          break;
                     else error("Term is missing");  // e.g., 2x - ; or just ';'
                else if (ch != '-' && ch != '+')     // e.g., 2x  4y;
                     error("wrong character entered");
            }
        } catch (IOException io) {
        }
        for (Iterator<Term> p = terms.iterator(); p.hasNext(); ) {
            term = p.next();                 // order alphabetically variables
            if (term.vars.size() > 1)        // in each term separately;
                Collections.sort(term.vars);
        }
    }
    public void display() {
        boolean afterFirstTerm = false;
        for (Iterator<Term> it = terms.iterator(); it.hasNext(); ) {
            Term term = it.next();
            System.out.print(" ");
            if (term.coeff < 0)              // put '-' before polynomial
                 System.out.print("-");      // and between terms (if needed);
            else if (afterFirstTerm)         // don't put '+' in front of
                 System.out.print("+");      // polynomial;
            afterFirstTerm = true;                
            System.out.print(" ");           // print a coefficient if
            if (term.vars.size() == 0 ||     // the term has only 
                Math.abs(term.coeff) != 1)   // a coefficient or coefficient 
                 System.out.print(Math.abs(term.coeff)); // is not 1 or -1;
            for (int i = 1; i <= term.vars.size(); i++) {
                 Variable var = term.vars.get(i-1);
                 System.out.print(var.id);   // print a variable name
                 if (var.exp != 1)           // and an exponent, only
                      System.out.print(var.exp); // if it is not 1.
            }
        }
        System.out.println();
    }
}

class AddPolynomials {
    static public void main(String[] a) {
        Polynomial polyn1 = new Polynomial(), polyn2 = new Polynomial();
        System.out.println("Enter two polynomials, each ended with a semicolon:");
        polyn1.get(System.in);
        polyn2.get(System.in);
        System.out.println("The result is:");
        polyn1.add(polyn2).display();
    }
}
