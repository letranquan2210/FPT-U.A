//************************  ThreadedTreeNode.java  **********************
//            node of a generic binary search threaded tree

public class ThreadedTreeNode<T extends Comparable<? super T>> {
    protected T el;
    protected boolean hasSuccessor;
    protected ThreadedTreeNode<T> left, right;
    public ThreadedTreeNode() {
        left = right = null; hasSuccessor = false;
    }
    public ThreadedTreeNode(T el) {
        this(el,null,null);
    }
    public ThreadedTreeNode(T el, ThreadedTreeNode<T> l,
                                  ThreadedTreeNode<T> r) {
        this.el = el; left = l; right = r; hasSuccessor = false;
    }
}

