package ru.spbau.mit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class StringSetImpl implements StringSet, StreamSerializable {

    private class Node {
        private static final int CHILDREN_SIZE = 200;
        private Node[] children = new Node[CHILDREN_SIZE];
        private int subTreeStringsCount = 0;
        private boolean end = false;

        private void push(char symbol) {
            children[symbol] = new Node();
        }

        private void remove(char symbol) {
            children[symbol] = null;
        }

        private boolean hasNext(char symbol) {
            return children[symbol] != null;
        }

        private Node next(char symbol) {
            return children[symbol];
        }

        private void incSubTreeStringsCount() {
            subTreeStringsCount++;
        }

        private void decSubTreeStringsCount() {
            subTreeStringsCount--;
        }

        private int getSubTreeStringsCount() {
            return subTreeStringsCount;
        }

        private void markEnd() {
            end = true;
        }

        private void markNotEnd() {
            end = false;
        }

        private boolean isEnd() {
            return end;
        }

        private void getSubtreeStrings(StringBuilder builder,
                ArrayList<String> result) {
            if (isEnd()) {
                result.add(builder.toString());
            }

            for (int i = 0; i < CHILDREN_SIZE; i++) {
                Node node = children[i];
                if (node != null) {
                    builder.append((char) i);
                    node.getSubtreeStrings(builder, result);
                    builder.deleteCharAt(builder.length() - 1);
                }
            }
        }
    }

    private Node head = new Node();

    @Override
    public void serialize(OutputStream out) {
        ArrayList<String> result = new ArrayList<String>();
        head.getSubtreeStrings(new StringBuilder(), result);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                out))) {
            for (String str : result) {
                writer.write(str);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private interface BiConsumer {
        void accept(Node node, char symbol);
    }

    @Override
    public void deserialize(InputStream in) {
        head = new Node();
        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                in))) {
            while ((str = reader.readLine()) != null) {
                add(str);
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private Node forEachNode(String element, BiConsumer actionFound,
            BiConsumer actionNotFound, BiConsumer after) {
        Node current = head;
        for (char symbol : element.toCharArray()) {
            if (current.hasNext(symbol)) {
                actionFound.accept(current, symbol);
            } else {
                actionNotFound.accept(current, symbol);
            }
            Node next = current.next(symbol);
            after.accept(current, symbol);
            if (next == null) {
                return current;
            }
            current = next;
        }
        return current;
    }

    @Override
    public boolean add(String element) {
        if (contains(element)) {
            return false;
        }
        Node tailNode = forEachNode(element, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
                node.incSubTreeStringsCount();
            }
        }, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
                node.incSubTreeStringsCount();
                node.push(symbol);
            }

        }, new BiConsumer() {

            @Override
            public void accept(Node t, char u) {
                // TODO Auto-generated method stub
            }
        });
        tailNode.incSubTreeStringsCount();
        tailNode.markEnd();
        return true;
    }

    /*
     * WHY IN JAVA I CANT USE STATIC VARS IN METHOD?????
     */
    private static boolean containsResult = true;

    @Override
    public boolean contains(String element) {
        containsResult = true;
        Node tailNode = forEachNode(element, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
            }

        }, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
                containsResult = false;
            }

        }, new BiConsumer() {

            @Override
            public void accept(Node t, char u) {
                // TODO Auto-generated method stub

            }

        });

        return containsResult && tailNode.isEnd();
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }

        Node tailNode = forEachNode(element, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
            }

        }, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {

            }

        }, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
                node.decSubTreeStringsCount();
                if (node.next(symbol).getSubTreeStringsCount() == 1) {
                    node.remove(symbol);
                }
            }

        });
        tailNode.decSubTreeStringsCount();
        tailNode.markNotEnd();
        return true;
    }

    @Override
    public int size() {
        return head.getSubTreeStringsCount();
    }

    /*
     * AGAIN
     */
    private static int prefixValid = 0;

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        prefixValid = 1;
        Node tailNode = forEachNode(prefix, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
            }

        }, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
                prefixValid = 0;
            }

        }, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
            }

        });
        return tailNode.getSubTreeStringsCount() * prefixValid;
    }
}
