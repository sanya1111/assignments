package ru.spbau.mit;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TreeSetImpl<E> extends AbstractSet<E> {

    private class Node {
        private Node left = null, right = null, parent = null;
        private E key;
        private int priority;
        private int size = 1;
        Random rand = new Random();

        private Node(E keyFrom) {
            key = keyFrom;
            priority = rand.nextInt();
        }

        private void update() {
            size = 1 + (left == null ? 0 : left.size)
                    + (right == null ? 0 : right.size);
            if (left != null) {
                left.parent = this;
            }
            if (right != null) {
                right.parent = this;
            }
        }
    }

    private Node root = null;
    private Comparator<E> comparator = null;

    private TreeSetImpl(Node rootWith, Comparator<E> comp) {
        root = rootWith;
        comparator = comp;
    }

    public TreeSetImpl(Comparator<E> comp) {
        comparator = comp;
    }

    public void split(E key, TreeSetImpl<E> left, TreeSetImpl<E> right) {
        if (root == null) {
            left.root = right.root = null;
        } else if (comparator.compare(root.key, key) > 0) {
            TreeSetImpl<E> result = new TreeSetImpl<E>(root.left, comparator);
            right.root = root;
            result.split(key, left, result);
            right.root.left = result.root;
        } else {
            TreeSetImpl<E> result = new TreeSetImpl<E>(root.right, comparator);
            left.root = root;
            result.split(key, result, right);
            left.root.right = result.root;
        }
        left.update();
        right.update();
    }

    public TreeSetImpl<E> merge(TreeSetImpl<E> right) {
        if (root == null) {
            return right;
        }
        if (right.root == null) {
            return this;
        }
        if (root.priority > right.root.priority) {
            root.right = (new TreeSetImpl<E>(root.right, comparator))
                    .merge(right).root;
            update();
            return this;
        } else {
            right.root.left = merge(new TreeSetImpl<E>(right.root.left,
                    comparator)).root;
            right.update();
            return right;
        }
    }

    private class TreeIterator implements Iterator<E> {
        Node current = root, lastUsed = null;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        private void toMinimum() {
            while (current.left != null) {
                current = current.left;
                toMinimum();
            }
        }

        private TreeIterator() {
            if (current != null) {
                toMinimum();
            }
        }

        private void getNext() {
            if (current.right != null) {
                current = current.right;
                toMinimum();
                return;
            }

            while (current.parent != null && current.parent.right == current) {
                current = current.parent;
            }
            current = current.parent;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E ret = current.key;
            lastUsed = current;
            getNext();
            return ret;
        }

        @Override
        public void remove() {
            if (lastUsed == null) {
                throw new IllegalStateException();
            }
            TreeSetImpl<E> result = (new TreeSetImpl<E>(lastUsed, comparator))
                    .delete(lastUsed.key);
            if (lastUsed.parent != null) {
                if (lastUsed.parent.left == lastUsed) {
                    lastUsed.parent.left = result.root;
                } else {
                    lastUsed.parent.right = result.root;
                }
            }
            if (lastUsed.left != null && lastUsed.left.parent == lastUsed) {
                lastUsed.left.parent = lastUsed.parent;
            }
            if (lastUsed.right != null && lastUsed.right.parent == lastUsed) {
                lastUsed.right.parent = lastUsed.parent;
            }
        }

    }

    @Override
    public Iterator<E> iterator() {
        return new TreeIterator();
    }

    private void update() {
        if (root != null) {
            root.update();
        }
    }

    @Override
    public int size() {
        if (root == null) {
            return 0;
        } else {
            return root.size;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        E value = (E) o;
        if (root == null) {
            return false;
        }
        if (comparator.compare(root.key, value) == 0) {
            return true;
        }
        if (comparator.compare(root.key, value) < 0) {
            return (new TreeSetImpl<E>(root.right, comparator)).contains(value);
        } else {
            return (new TreeSetImpl<E>(root.left, comparator)).contains(value);
        }
    }

    private TreeSetImpl<E> delete(E value) {
        if (comparator.compare(root.key, value) == 0) {
            return (new TreeSetImpl<E>(root.left, comparator))
                    .merge(new TreeSetImpl<E>(root.right, comparator));
        }
        if (comparator.compare(root.key, value) > 0) {
            root.left = (new TreeSetImpl<E>(root.left, comparator))
                    .delete(value).root;
        } else {
            root.right = (new TreeSetImpl<E>(root.right, comparator))
                    .delete(value).root;
        }
        update();
        return this;
    }

    @Override
    public boolean remove(Object o) {
        if (!contains(o)) {
            return false;
        }
        E value = (E) o;
        root = delete(value).root;
        return true;
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) {
            return false;
        }

        Node nodeWith = new Node(e);
        if (root == null) {
            root = nodeWith;
        } else if (nodeWith.priority > root.priority) {
            TreeSetImpl<E> left = new TreeSetImpl<E>(comparator);
            TreeSetImpl<E> right = new TreeSetImpl<E>(comparator);
            split(nodeWith.key, left, right);
            nodeWith.left = left.root;
            nodeWith.right = right.root;
            root = nodeWith;
        } else {
            if (comparator.compare(nodeWith.key, root.key) < 0) {
                TreeSetImpl<E> left = new TreeSetImpl<E>(root.left, comparator);
                left.add(e);
                root.left = left.root;
            } else {
                TreeSetImpl<E> right = new TreeSetImpl<E>(root.right,
                        comparator);
                right.add(e);
                root.right = right.root;
            }
        }
        update();
        return true;
    }

}
