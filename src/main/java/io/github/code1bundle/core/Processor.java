package io.github.code1bundle.core;

import io.github.code1bundle.modes.console.HuffConsole;
import io.github.code1bundle.modes.gui.HuffGUI;

public class Processor {
    public static void main(String[] args) {
        decide(args);
    }

    public static void decide(String[] args) {
        if(args.length!=0 && "-headless".equals(args[0])) {
            HuffConsole.launch();
        }
        else {
            HuffGUI.launch();
        }
    }
}
