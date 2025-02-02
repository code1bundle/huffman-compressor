package io.github.code1bundle.modes.gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import io.github.code1bundle.core.Compressor;
import io.github.code1bundle.data.Constants;
import io.github.code1bundle.io.OutStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.NoSuchElementException;

public class HuffGUI extends JFrame {
    private JTextField dirPathField;
    private JTextArea outputArea;
    private JButton selectButton;
    private JButton compressButton;
    private JButton decompressButton;
    private JButton exitButton;
    private File packToCompress;
    private File saveTo;

    public HuffGUI() throws UnsupportedLookAndFeelException {
        huffGUISetup();
        add(createDirectorySelectionPanel());
        add(createOutputArea());
        add(createButtonsPanel());

        selectButton.addActionListener(action -> handleFileSelection());
        compressButton.addActionListener(action -> {
            try {
                handleCompressAction();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        decompressButton.addActionListener(action -> handleDecompressionAction());
        exitButton.addActionListener(action -> handleExitAction());
    }

    private void huffGUISetup() {
        setTitle("Huffman Compression");
        setSize(500, 400);
        setFont(Constants.FONT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    }

    private JPanel createDirectorySelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirPathField = new JTextField(34);

        selectButton = new JButton("Select");
        selectButton.setForeground(Color.BLACK);
        selectButton.setFont(Constants.BUTTON_FONT);
        selectButton.setBackground(Constants.BACKGROUND);
        selectButton.setBorderPainted(false);
        selectButton.addMouseListener(createEffectsListener());
        selectButton.setPreferredSize(new Dimension(90, 30));

        panel.add(dirPathField);
        panel.add(selectButton);
        return panel;
    }

    private JPanel createOutputArea() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(Constants.FONT);

        JScrollPane jScrollPane = new JScrollPane(outputArea);
        panel.add(jScrollPane);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        compressButton = new JButton("Compress");
        compressButton.setBackground(Constants.BACKGROUND);
        compressButton.setForeground(Color.BLACK);
        compressButton.setFont(Constants.BUTTON_FONT);
        compressButton.setBorderPainted(false);
        compressButton.addMouseListener(createEffectsListener());
        compressButton.setPreferredSize(Constants.BUTTON_SIZE);

        decompressButton = new JButton("Decompress");
        decompressButton.setBackground(Constants.BACKGROUND);
        decompressButton.setForeground(Color.BLACK);
        decompressButton.setFont(Constants.BUTTON_FONT);
        decompressButton.setBorderPainted(false);
        decompressButton.addMouseListener(createEffectsListener());
        decompressButton.setPreferredSize(Constants.BUTTON_SIZE);

        exitButton = new JButton("Exit");
        exitButton.setBackground(Constants.BACKGROUND);
        exitButton.setForeground(Color.BLACK);
        exitButton.setFont(Constants.BUTTON_FONT);
        exitButton.setBorderPainted(false);
        exitButton.addMouseListener(createEffectsListener());
        exitButton.setPreferredSize(Constants.BUTTON_SIZE);

        panel.add(compressButton);
        panel.add(decompressButton);
        panel.add(exitButton);
        return panel;
    }

    private MouseListener createEffectsListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ((JButton) e.getSource()).setBackground(Constants.BACKGROUND_PRESSED);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).setBackground(Constants.BACKGROUND_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).setBackground(Constants.BACKGROUND);
            }
        };
    }

    private void handleFileSelection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        chooser.setDialogTitle("Choose a Directory or a File to compress");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = chooser.getSelectedFile();
            dirPathField.setText(selectedDirectory.getAbsolutePath());
        } else {
            dirPathField.setText(null);
            outputArea.setText("Selection canceled.");
        }
    }

    private void handleCompressAction() throws FileNotFoundException {
        if (dirPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(HuffGUI.this, "Nothing to compress!");
            return;
        }

        packToCompress = new File(dirPathField.getText());
        invokeChooser("Select a Directory to save compressed data");
        saveTo = new File(dirPathField.getText() + "\\compressed.huff");
        OutStream out = new OutStream(saveTo);

        try {
            long startTime = System.currentTimeMillis();
            Compressor.compress(packToCompress, out);
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;

            outputArea.setText("Compression successfully completed!");
            outputArea.append("\n" + "Compressed to: " + dirPathField.getText());
            outputArea.append("\n" + "Elapsed time: " + time + Constants.TIME_UNIT);
            outputArea.append("\n" + "Compression ratio: " + ratio());
            outputArea.setFont(Constants.FONT);

        } catch (Exception ex) {
            outputArea.setText("Error during compression!");
            outputArea.append("\n" + "Nothing to compress or no directory for saving data.");
            outputArea.setFont(Constants.FONT);
        }
    }

    private void handleDecompressionAction() {
        invokeChooser("Select a Directory of Huffman file (.huff) for decompressing");

        if (dirPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(HuffGUI.this, "Nothing to decompress!");
            return;
        }
        File compressedFile = new File(dirPathField.getText() + "\\compressed.huff");
        outputArea.setText("Compressed file size: " + compressedFile.length() + " byte");
        outputArea.setFont(Constants.FONT);

        try {
            Compressor.decompress(compressedFile);
        } catch (NoSuchElementException ex) {

            /*
            * There is error occurs every time decompression completes if the last character if N = 8.
            * This is peculiarity of concrete Huffman algorithm implementation here.
            *
            * @throws NoSuchElementException
            */
            outputArea.append("\n" + "Successfully decompressed.");
            outputArea.append("\n" + "Decompressed to: " + dirPathField.getText());
            outputArea.setFont(Constants.FONT);
        } catch (Exception ex) {
            outputArea.append("\n" + "Error during decompression: " + ex.getMessage());
            outputArea.setFont(Constants.FONT);
        }
    }

    private void handleExitAction() {
        System.exit(0);
    }

    private void invokeChooser(String dialogTitle) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        chooser.setDialogTitle(dialogTitle);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = chooser.getSelectedFile();
            dirPathField.setText(selectedDirectory.getAbsolutePath());
        } else {
            dirPathField.setText(null);
            outputArea.setText("Selection canceled.");
        }
    }

    private long getDirLength(File file) {
        if(file.isFile()) {
            return file.length();
        } else if (file.isDirectory()) {
            long length = 0;
            File[] list = file.listFiles();
            if (null == list)
                return 0;
            for (File content : list) {
                length += getDirLength(content);
            }
            return length;
        } else {
            throw new RuntimeException("Unknown file type");
        }
    }

    private String ratio() {
        long newSize = saveTo.length();
        Double ratio = 100 - (((double) newSize / getDirLength(packToCompress)) * 100);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(ratio) + "%";
    }

    public static void launch() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        UIManager.put("TextField.border", BorderFactory.createLineBorder(Constants.BACKGROUND_HOVER, 3));
        UIManager.put("JTextArea.border", BorderFactory.createEmptyBorder());
        SwingUtilities.invokeLater(() -> {
            HuffGUI gui;
            try {
                gui = new HuffGUI();
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
            gui.setVisible(true);
        });
    }
}