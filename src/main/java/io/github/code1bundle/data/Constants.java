package io.github.code1bundle.data;

import java.awt.*;
import java.util.HashSet;

    public class Constants {
        public final static HashSet<String> IGNORE_SET = new HashSet<>();

        static { IGNORE_SET.add(".huffignore"); }
        private Constants () {}

        public final static String HUFF_SUFFIX = ".huff";
        public final static boolean FILE_BIT = true;
        public final static boolean DIR_BIT = false;
        public final static boolean EMPTY_BIT = true;
        public final static boolean NON_EMPTY_BIT = false;

        public final static Color BACKGROUND = new Color(255, 255, 255);
        public final static Color BACKGROUND_HOVER = Color.LIGHT_GRAY;
        public final static Color BACKGROUND_PRESSED = Color.GRAY;
        public final static Dimension BUTTON_SIZE = new Dimension(150, 30);
        public final static Font FONT = new Font("Dialog", Font.BOLD, 14);
        public final static Font BUTTON_FONT = new Font("Dialog", Font.BOLD, 12);
        public final static String TIME_UNIT = " ms.";
    }
