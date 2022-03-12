/***********************  SLList.java  ***************************
 *       generic singly linked list class with head only
 */

public class SLList {
    protected SLLNode head = null;
    public SLList() {
    }
    public void setToNull() {
        head = null;
    }
    public boolean isEmpty() {
        return head == null;
    }
    public Object first() {
        return head.info;
    }
    public SLLNode head() {
        return head;
    }
    public void printAll(java.io.PrintStream out) {
        for (SLLNode tmp = head; tmp != null; tmp = tmp.next)
            out.print(tmp.info.toString());
    }
    public void add(Object el) {
        head = new SLLNode(el,head);
    }
    public Object find(Object el) {
        SLLNode tmp = head;
        for ( ; tmp != null && !el.equals(tmp.info); tmp = tmp.next);
        if (tmp == null)
             return null;
        else return tmp.info;
    }
    public Object deleteHead() { // remove the head and return its info;
        Object el = head.info;
        head = head.next;
        return el;
    }
    public void delete(Object el) {    // find and remove el;  
        if (head != null)              // if non-empty list;
             if (el.equals(head.info)) // if head needs to be removed;
                  head = head.next;
             else {
                  SLLNode pred = head, tmp = head.next;
                  for ( ; tmp != null && !(tmp.info.equals(el));
                          pred = pred.next, tmp = tmp.next);
                  if (tmp != null)     // if found
                        pred.next = tmp.next;
             }
    }
}
