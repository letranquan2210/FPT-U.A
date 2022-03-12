/****************************  SplayTree.java  ************************
 *                     generic splaying tree class
 */

public class SplayTree<T extends Comparable<? super T>> {
    protected SplayTreeNode<T> root = null;
    public SplayTree() {
    }
    private void continueRotation(SplayTreeNode<T> gr, SplayTreeNode<T> par,
                                  SplayTreeNode<T> ch, SplayTreeNode<T> desc) {
        if (gr != null) {                    // if par has a grandparent;
             if (gr.right == ch.parent)
                  gr.right = ch;
             else gr.left  = ch;
        }
        else root = ch;
        if (desc != null)
             desc.parent = par;
        par.parent = ch;
        ch.parent = gr;
    }
    private void rotateR(SplayTreeNode<T> p) {
        p.parent.left = p.right;
        p.right = p.parent;
        continueRotation(p.parent.parent,p.right,p,p.right.left);
    }
    private void rotateL(SplayTreeNode<T> p) {
        p.parent.right = p.left;
        p.left = p.parent;
        continueRotation(p.parent.parent,p.left,p,p.left.right);
    }
    private void semisplay(SplayTreeNode<T> p) {
        while (p != root) {
            if (p.parent.parent == null)     // if p's parent is 
                 if (p.parent.left == p)     // the root;
                      rotateR(p);
                 else rotateL(p);
            else if (p.parent.left == p)     // if p is a left child;
                 if (p.parent.parent.left == p.parent) {
                      rotateR(p.parent);
                      p = p.parent;
                 }
                 else {
                      rotateR(p); // rotate p and its parent;
                      rotateL(p); // rotate p and its new parent;
                 }
            else                             // if p is a right child;
                 if (p.parent.parent.right == p.parent) {
                      rotateL(p.parent);
                      p = p.parent;
                 }
                 else {
                      rotateL(p);            // rotate p and its parent;
                      rotateR(p);            // rotate p and its new parent;
                 }
                 if (root == null)           // update the root;
                      root = p;
        }
    }
    protected void visit(SplayTreeNode<T> p) {
        System.out.print(p.el + " ");
    }
    protected T search(T el) {
        SplayTreeNode<T> p = root;
        while (p != null)
            if (el.equals(p.el)) {
                 semisplay(p);
				 return p.el;
            }
            else if (el.compareTo(p.el) < 0)
                 p = p.left;
            else p = p.right;
        return null;
    }
    public void inorder() {
        inorder(root);
    }
    protected void inorder(SplayTreeNode<T> p) {
        if (p != null) {
             inorder(p.left);
             visit(p);
             inorder(p.right);
        }
    }
    public void insert(T el) {
        SplayTreeNode<T> p = root, prev = null;
        while (p != null) {  // find a place for inserting new node;
            prev = p;
            if (p.el.compareTo(el) < 0)
                 p = p.right;
            else p = p.left;
        }
        if (root == null)    // tree is empty;
             root = new SplayTreeNode<T>(el);
        else if (prev.el.compareTo(el) < 0)
             prev.right = new SplayTreeNode<T>(el,null,null,prev);
        else prev.left  = new SplayTreeNode<T>(el,null,null,prev);
    }
}

