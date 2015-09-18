package ru.spbau.mit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.w3c.dom.Node;

public class StringSetImpl implements StringSet, StreamSerializable {

    private class Node {
        private static final int CHILDREN_SIZE = 200;
        private Node[] children = new Node[CHILDREN_SIZE];
        private int prefCount = 0;
        private boolean end = false;

        private void push(char simbol) {
            children[simbol] = new Node();
        }

        private void remove(char simbol) {
            children[simbol] = null;
        }

        public boolean haveNext(char simbol) {
            return children[simbol] != null;
        }

        public Node next(char simbol) {
            return children[simbol];
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

    private Node forEachNode(String element,
            BiConsumer<Node, Character> actionFound,
            BiConsumer<Node, Character> actionNotFound,
            BiConsumer<Node, Character> after) {
        Node current = head;
        for (char simbol : element.toCharArray()) {
            if (current.haveNext(simbol)) {
                actionFound.accept(current, simbol);
            } else {
                actionNotFound.accept(current, simbol);
            }
            Node next = current.next(simbol);
            after.accept(current, simbol);
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
        Node tailNode = forEachNode(element, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
                node.incPref();
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
                node.incPref();
                node.push(simbol);
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {

            @Override
            public void accept(Node t, Character u) {
                // TODO Auto-generated method stub

            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
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
        Node tailNode = forEachNode(element, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
                containsResult = false;
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {

            @Override
            public void accept(Node t, Character u) {
                // TODO Auto-generated method stub

            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        });

        return containsResult && tailNode.isEnd();
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }

        Node tailNode = forEachNode(element, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {

            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
                node.decPref();
                if (node.next(simbol).getPref() == 1) {
                    node.remove(simbol);
                }
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
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
        Node tailNode = forEachNode(prefix, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
                prefixValid = 0;
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }

        }, new BiConsumer<Node, Character>() {
            @Override
            public void accept(Node node, Character simbol) {
            }

            @Override
            public BiConsumer<Node, Character> andThen(
                    BiConsumer<? super Node, ? super Character> after) {
                // TODO Auto-generated method stub
                return null;
            }
        });
        return tailNode.getPref() * prefixValid;
    }

}
