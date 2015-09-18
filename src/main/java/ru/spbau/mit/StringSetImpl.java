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
        private int prefCount = 0;
        private boolean end = false;

        private void push(char symbol) {
            children[symbol] = new Node();
        }

        private void remove(char symbol) {
            children[symbol] = null;
        }

        public boolean hasNext(char symbol) {
            return children[symbol] != null;
        }

        public Node next(char symbol) {
            return children[symbol];
        }

        public void incPref() {
            prefCount++;
        }

        public void decPref() {
            prefCount--;
        }

        public int getPref() {
            return prefCount;
        }

        public void markEnd() {
            end = true;
        }

        public void markNotEnd() {
            end = false;
        }

        public boolean isEnd() {
            return end;
        }

        public void getSubtreeStrings(StringBuilder builder,
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
                node.incPref();
            }
        }, new BiConsumer() {
            @Override
            public void accept(Node node, char symbol) {
                node.incPref();
                node.push(symbol);
            }

        }, new BiConsumer() {

            @Override
            public void accept(Node t, char u) {
                // TODO Auto-generated method stub
            }
        });
        tailNode.incPref();
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
                node.decPref();
                if (node.next(symbol).getPref() == 1) {
                    node.remove(symbol);
                }
            }

        });
        tailNode.decPref();
        tailNode.markNotEnd();
        return true;
    }

    @Override
    public int size() {
        return head.getPref();
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
        return tailNode.getPref() * prefixValid;
    }
}
