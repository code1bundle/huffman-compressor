package io.github.code1bundle.core;

import io.github.code1bundle.data.Constants;
import io.github.code1bundle.huffman.Huffman;
import io.github.code1bundle.io.InStream;
import io.github.code1bundle.io.OutStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Compressor {

    public static void compress(File src, OutStream binaryOut) throws IOException {
        if (null == src)
            throw new IllegalArgumentException("Null source file for compress");

        if (src.isFile()) {
            compressFile(src, binaryOut);
            System.out.println("Compressing: " + src.getName());
        } else if (src.isDirectory()) {
            compressDir(src, binaryOut);
        } else
            throw new RuntimeException("Unknown kind of source");
    }

    private static void compressFile(File file, OutStream binaryOut) throws IOException {
        assert file.isFile();

        String fileName = file.getName();
        boolean isEmptyFile = (file.length() == 0);

        writeFileHead(fileName, isEmptyFile, binaryOut);

        if (!isEmptyFile) {
            InStream binaryIn = new InStream(file);
            Huffman.compress(binaryIn, binaryOut);
        }
    }

    private static void writeFileHead(String fileName, boolean isEmptyFile, OutStream binaryOut) throws IOException {
        /*
         * Marks that processing object is a file
         */
        binaryOut.write(Constants.FILE_BIT);

        byte[] bytes = fileName.getBytes();
        binaryOut.write(bytes.length);
        binaryOut.write(bytes);

        binaryOut.write(isEmptyFile);
    }

    private static void compressDir(File dir, OutStream binaryOut) throws IOException {
        assert dir.isDirectory();

        File[] files = dir.listFiles();

        if (null == files)
            throw new RuntimeException("Null file list of dir");

        ArrayList<File> list = new ArrayList<>();
        for (File file : files) {
            if (Constants.IGNORE_SET.contains(file.getName())) {
                continue;
            }
            list.add(file);
        }
        int length = list.size();
        if (length == 0) throw new RuntimeException("Nothing to compress");

        writeDirHead(dir.getName(), length, binaryOut);

        for (File file : list) {
            compress(file, binaryOut);
        }
    }

    private static void writeDirHead(String dirName, int length, OutStream binaryOut) throws IOException {
        /*
         * Marks that processing object is a directory
         */
        binaryOut.write(Constants.DIR_BIT);

        byte[] bytes = dirName.getBytes();
        binaryOut.write(bytes.length);
        binaryOut.write(bytes);

        /*
         * EMPTY_BIT stands for empty directory and NON_EMPTY_BIT vise versa
         */
        if (length == 0) {
            binaryOut.write(Constants.EMPTY_BIT);
        } else {
            binaryOut.write(Constants.NON_EMPTY_BIT);
            binaryOut.write(length);
        }
    }

    public static void decompress(File file) throws IOException {
        if (null == file)
            throw new IllegalArgumentException("Null source file for decompress");


        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!suffix.equals(Constants.HUFF_SUFFIX))
            throw new RuntimeException("Unsupported file suffix");

        File parent = new File(file.getAbsoluteFile().getParent());
        decompress(parent, new InStream(file));
    }

    private static void decompress(File parent, InStream binaryIn) throws IOException {
        if (binaryIn.readBoolean() == Constants.FILE_BIT) {
            decompressFile(parent, binaryIn);
            System.out.println("Decompressing: " + parent.getName());
        } else {
            int nameLength = binaryIn.readInt();
            byte[] bytes = new byte[nameLength];
            for (int i = 0; i < nameLength; ++i) {
                bytes[i] = binaryIn.readByte();
            }

            String dirName = new String(bytes, StandardCharsets.UTF_8);
            File dir = new File(parent, dirName);
            if (!dir.mkdirs()) {
                throw new RuntimeException("Failed to make dir: " + dirName);
            }

            if (binaryIn.readBoolean() != Constants.EMPTY_BIT) {
                int length = binaryIn.readInt();
                for (int i = 0; i < length; ++i) {
                    decompress(dir, binaryIn);
                }
            }
        }
    }
    private static void decompressFile(File parent, InStream binaryIn) throws IOException {
        int nameLength = binaryIn.readInt();
        byte[] bytes = new byte[nameLength];
        for (int i = 0; i < nameLength; ++i) {
            bytes[i] = binaryIn.readByte();
        }

        String fileName = new String(bytes, StandardCharsets.UTF_8);
        OutStream binaryOut = new OutStream(new File(parent, fileName));

        if (binaryIn.readBoolean() != Constants.EMPTY_BIT) {
            Huffman.decompress(binaryIn, binaryOut);
        }

        binaryOut.close();
    }
}