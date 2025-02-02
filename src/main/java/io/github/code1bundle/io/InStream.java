package io.github.code1bundle.io;

import java.io.*;
import java.util.NoSuchElementException;

public final class InStream {

    private static final int EOF = -1;
    private BufferedInputStream in;
    private int buffer;
    private int n;

    public InStream(File file) throws FileNotFoundException {
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            fillBuffer();
        }
    }

    private void fillBuffer() {
        try {
            buffer = in.read();
            n = 8;
        }
        catch (IOException e) {
            buffer = EOF;
            n = -1;
        }
    }

    public boolean isEmpty() {
        return buffer == EOF;
    }

    public boolean readBoolean() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n--;
        boolean bit = ((buffer >> n) & 1) == 1;
        if (n == 0) fillBuffer();
        return bit;
    }

    public char readChar() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");

        /*
         * Special case for align byte
         */
        if (n == 8) {
            int x = buffer;
            fillBuffer();
            return (char) (x & 0xff);
        }

        /*
         * Combines last N bits of current buffer with first 8-N bits of new buffer
         */
        int x = buffer;
        x <<= (8 - n);
        int oldN = n;
        fillBuffer();
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n = oldN;
        x |= (buffer >>> n);
        return (char) (x & 0xff);
    }

    public String readString() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");

        StringBuilder sb = new StringBuilder();
        while (!isEmpty()) {
            char c = readChar();
            sb.append(c);
        }
        return sb.toString();
    }

    public int readInt() {
        int x = 0;
        for (int i = 0; i < 4; i++) {
            char c = readChar();
            x <<= 8;
            x |= c;
        }
        return x;
    }

    public byte readByte() {
        char c = readChar();
        return (byte) (c & 0xff);
    }
}
