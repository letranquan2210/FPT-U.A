public class LLStack<T> {
    private java.util.LinkedList<T> list = new java.util.LinkedList<T>();
    public LLStack() {
    }
    public void clear() {
        list.clear();
    }
    public boolean isEmpty() {
        return list.isEmpty();
    }
    public T topEl() {
        if (isEmpty())
            throw new java.util.EmptyStackException();
        return list.getLast();
    }
    public T pop() {
        if (isEmpty())
            throw new java.util.EmptyStackException();
        return list.removeLast();
    }
    public void push(T el) {
        list.add(el);
    }
    public String toString() {
        return list.toString();
    }
}

