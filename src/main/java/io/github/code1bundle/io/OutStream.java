package io.github.code1bundle.io;

import java.io.*;

public final class OutStream {

    private final BufferedOutputStream out;
    private int buffer;
    private int n;

    public OutStream(File file) throws FileNotFoundException {
        OutputStream os = new FileOutputStream(file);
        out = new BufferedOutputStream(os);
    }

    private void writeBit(boolean x) throws IOException {
        buffer <<= 1;
        if (x) buffer |= 1;

        n++;
        if (n == 8) clearBuffer();
    }

    private void writeByte(int x) throws IOException {
        assert x >= 0 && x < 256;

        if (n == 0) {
            out.write(x);
            return;
        }

        for (int i = 0; i < 8; i++) {
            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    private void clearBuffer() throws IOException {
        if (n == 0) return;
        if (n > 0) buffer <<= (8 - n);
        out.write(buffer);
        n = 0;
        buffer = 0;
    }

    public void flush() throws IOException {
        clearBuffer();
        out.flush();
    }

    public void close() throws IOException {
        flush();
        out.close();
    }

    public void write(boolean x) throws IOException {
        writeBit(x);
    }

    public void write(byte x) throws IOException {
        writeByte(x & 0xff);
    }

    public void write(int x) throws IOException {
        writeByte((x >>> 24) & 0xff);
        writeByte((x >>> 16) & 0xff);
        writeByte((x >>>  8) & 0xff);
        writeByte((x) & 0xff);
    }

    public void write(char x) throws IOException {
        if (x >= 256) throw new IllegalArgumentException("Illegal 8-bit char = " + x);
        writeByte(x);
    }

    public void write(char x, int r) throws IOException {
        if (r == 8) {
            write(x);
            return;
        }
        if (r < 1 || r > 16) throw new IllegalArgumentException("Illegal value for r = " + r);
        if (x >= (1 << r))   throw new IllegalArgumentException("Illegal " + r + "-bit char = " + x);
        for (int i = 0; i < r; i++) {
            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    public void write(byte[] bytes) throws IOException {
        for (byte b : bytes) {
            write(b);
        }
    }
}
