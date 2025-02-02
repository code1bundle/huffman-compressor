package io.github.code1bundle.huffman;

import io.github.code1bundle.io.InStream;
import io.github.code1bundle.io.OutStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class Huffman {

    private static final int R = 256;

    private Huffman() {}

    public static void compress(InStream binaryIn, OutStream binaryOut) throws IOException {
        String data = binaryIn.readString();
        char[] input = data.toCharArray();

        int[] freq = new int[R];
        for (char c : input) {
            freq[c]++;
        }

        Node root = buildTrie(freq);

        String[] st = new String[R];
        buildCode(st, root, "");

        writeTrie(binaryOut, root);

        binaryOut.write(input.length);

        for (char c : input) {
            String code = st[c];
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    binaryOut.write(false);
                } else if (code.charAt(j) == '1') {
                    binaryOut.write(true);
                } else throw new IllegalStateException("Illegal state");
            }
        }
    }

    private static Node buildTrie(int[] freq) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char i = 0; i < R; ++i) {
            if (freq[i] > 0)
                pq.add(new Node(i, freq[i], null, null));
        }

        if (pq.size() == 1) {
            if (freq['\0'] == 0)
                pq.add(new Node('\0', 0, null, null));
            else
                pq.add(new Node('\1', 0, null, null));
        }

        while (pq.size() > 1) {
            Node left = pq.remove();
            Node right = pq.remove();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }

        return pq.remove();
    }

    private static void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left, s + '0');
            buildCode(st, x.right, s + '1');
        } else {
            st[x.ch] = s;
        }
    }

    private static void writeTrie(OutStream binaryOut, Node x) throws IOException {
        if (x.isLeaf()) {
            binaryOut.write(true);
            binaryOut.write(x.ch, 8);
            return;
        }
        binaryOut.write(false);

        writeTrie(binaryOut, x.left);
        writeTrie(binaryOut, x.right);
    }

    public static void decompress(InStream binaryIn, OutStream binaryOut) throws IOException {
        Node root = readTrie(binaryIn);

        int length = binaryIn.readInt();

        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = binaryIn.readBoolean();
                if (bit) x = x.right;
                else x = x.left;
            }
            binaryOut.write(x.ch, 8);
        }
    }

    private static Node readTrie(InStream in) {
        boolean isLeaf = in.readBoolean();
        if (isLeaf) {
            return new Node(in.readChar(), -1, null, null);
        } else {
            return new Node('\0', -1, readTrie(in), readTrie(in));
        }
    }
}
