package io.github.code1bundle.modes.console;

import io.github.code1bundle.core.Compressor;
import io.github.code1bundle.io.OutStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HuffConsole {
    private static final String LOGO = """
            ||  || ||  || |||||| |||||| ||| ||| |||||| |||  ||
            |||||| ||  || ||||   ||||   || | || ||__|| || | ||
            ||  || |||||| ||     ||     ||   || ||  || ||  |||
            """;
    private static final String message = """
            Successfully configured! Ready to process some files!
            
            Commands:
                compress - stands for compressing operation. Compresses into .huff file.
                decompress - stands for decompressing operation. Decompresses only from directory with .huff file and to your destination.
                exit - leave working process.
            
            Template:
                compress <abs-path-to-object-to-compress-or-decompress> <abs-path-to-file-to-store-result>
                decompress <abs-path-to-file-with-.huff>
            
            Example:
                compress C://Users//Josh//MyFiles C://Users//Josh//Storage//compressed.huff
                decompress C://Users//Josh//Storage//compressed.huff
            """;

    public static void launch() {
        System.out.println(LOGO);
        System.out.println(message);

        while(true) {
            System.out.println("You can write down instructions:");
            Scanner scan = new Scanner(System.in);
            String instruction = scan.nextLine().trim();

            if("exit".equals(instruction)) {
                System.out.println("Exiting application...");
                break;
            }

            Pattern compress_command = Pattern.compile("compress\\s+(\\S+)\\s+(\\S+)");
            Pattern decompress_command = Pattern.compile("decompress\\s+(\\S+)");

            Matcher compress_match = compress_command.matcher(instruction);
            Matcher decompress_match = decompress_command.matcher(instruction);

            if(compress_match.find()) {
                String C_from = compress_match.group(1);
                String C_to = compress_match.group(2);

                OutStream binOut = null;
                File dest = new File(C_to);
                try {
                    binOut = new OutStream(dest);
                } catch (FileNotFoundException e) {
                    System.out.println("Error occurs " + e.getMessage());
                }
                File in = new File(C_from);

                try {
                    Compressor.compress(in, binOut);
                    System.out.println("Successfully compressed to " + C_to);
                    System.out.println("Size of " + dest.getName() + " is " + dest.length() + " bytes");
                } catch (IOException e) {
                    System.out.println("Error occurs: " + e.getMessage());
                }
            }
            if(decompress_match.find()) {
                String D_from = decompress_match.group(1);
                File out = new File(D_from);

                try {
                    Compressor.decompress(out);
                } catch (IOException e) {
                    System.out.println("Error occurs: " + e.getMessage());
                } catch (NoSuchElementException e) {
                    System.out.println("End of decompression!");
                    System.out.println("Successfully decompressed to " + out.getParent());
                }
            }
        }
    }
}
