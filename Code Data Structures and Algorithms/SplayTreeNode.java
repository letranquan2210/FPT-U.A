/***********************  SplayTreeNode.java  ***********************/

public class SplayTreeNode<T extends Comparable<? super T>> {
    protected T el;
    protected SplayTreeNode<T> left, right, parent;
    public SplayTreeNode() {
        left = right = parent = null;
    }
    public SplayTreeNode(T el) {
        this(el,null,null,null);
    }
    public SplayTreeNode(T ob, SplayTreeNode<T> lt, 
                         SplayTreeNode<T> rt, SplayTreeNode<T> pr) {
        el = ob; left = lt; right = rt; parent = pr;
    }
}
