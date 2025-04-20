# Huffman Compressor Library
#### The Huffman Compressor Library is a Java-based library that provides functionality for compressing and decompressing files using the Huffman coding algorithm. It can be easily integrated into other Java applications via Maven or Gradle.

[<img alt="GitHub license" src="https://img.shields.io/github/license/Naereen/StrapDown.js.svg"/>](https://github.com/Naereen/StrapDown.js/blob/master/LICENSE)

## Features
- Efficient file compression and decompression using the Huffman coding algorithm.
- Modular design for easy integration into larger projects.
- Supports both CLI (Command-Line Interface) and GUI (Graphical User Interface) modes.
- Provides APIs for custom usage in third-party applications.

## Prerequisites
- Java Development Kit (JDK) - 21.0.5+
- Apache Maven 3.9.9+

## Tech Stack
- OpenJDK 21.0.5
- Apache Maven 3.9.9
- FlatLaf 3.5.4 (Optional, for GUI support)

## Getting Started
## THIS STILL IN WORK!
### <span style="color:red">For Devs:</span>
### Adding the Library as a Dependency

**Maven**

```xml

<dependency>
    <groupId>io.github.code1bundle</groupId>
    <artifactId>huffman-compressor</artifactId>
    <version>1.0.0</version> <!-- Replace with the latest version -->
</dependency>
```
**Gradle**
```
implementation 'io.github.code1bundle:huffman-compressor:1.0.0' // Replace with the latest version
```
### Using the Library in Your Project
**Compression**

```java
import io.github.code1bundle.core.Compressor;
import io.github.code1bundle.io.OutStream;

public class Main {
    public static void main(String[] args) {
        try {
            File inputFile = new File("path/to/input/file.txt");
            File outputFile = new File("path/to/output/compressed.huff");
            OutStream outStream = new OutStream(outputFile);
            Compressor.compress(inputFile, outStream);
            System.out.println("Compression successful!");
        } catch (IOException e) {
            System.err.println("Error during compression: " + e.getMessage());
        }
    }
}
```
**Decompression**

```java
import io.github.code1bundle.huffman.Compressor;

public class Main {
    public static void main(String[] args) {
        try {
            File compressedFile = new File("path/to/compressed/file.huff");
            Compressor.decompress(compressedFile);
            System.out.println("Decompression successful!");
        } catch (IOException e) {
            System.err.println("Error during decompression: " + e.getMessage());
        }
    }
}
```
### <span style="color:green">For Users:</span>
### Clone the repository:
```
git clone https://github.com/code1bundle/huffman-compressor.git
```
### Build with Apache Maven
**For GUI launch:**
```
mvn clean compile exec:java@gui
```
**For CLI launch:**
```
mvn clean compile exec:java@console
```
### Build without Apache Maven (download JavaFX SDK manually)
**GUI mode**
1) **Compile**
```
javac -d out ^
      --module-path "%FX_PATH%" ^
      --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
      -cp src ^
      src\main\java\io\github\code1bundle\core\Processor.java 
```  
2) **Run**
```
java -cp out ^
     --module-path "%FX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
     io.github.code1bundle.core.Processor
```

**CLI mode**
1) **Compile**
```
javac -d out -cp src src\main\java\io\github\code1bundle\core\Processor.java 
```
2) **Run**
```
java -cp out io.github.code1bundle.core.Processor -headless
```
## Usage in GUI mode
### Compression:
- Click the "Select" button to choose a directory or file to compress.
- Click the "Compress" button to start the compression process.
- The compressed file will be saved in the same directory with the ".huff" extension.
- The compression ratio and elapsed time will be displayed in the output area.
### Decompression:
- Click the "Decompress" button to find files with ".huff" and start the decompression process.
- The decompressed files will be saved in the same directory as the compressed file.
- The output area will display the decompression status.
### Exit:
- Click the "Exit" button to close the application.
## Usage in console mode
### Rules:
- Type `compress` or `decompress` commands to perform actions.
- Insert only absolute paths without any quotes.
### Compression:
- Template for compression command:
```
compress <path-to-object-for-compression> <path-to-file-with-.huff-to-store-compressed-result>
```
- Example for compression command:
```
compress C:\foo\bar C:\foo\bar\compressed-bar.huff
```
### Decompression:
- Template for decompression command:
```
decompress <path-to-file-with-.huff-to-decompress> 
```
- Example for decompression command:
```
decompress C:\foo\bar\compressed-bar.huff
```
### Exit:
- For exiting just type `exit` command

---

## Project Structure
### The project consists of the following main components:

**Compressor** : The core class responsible for compression and decompression logic.
**InStream / OutStream** : Classes for handling input and output streams.
**Constants** : A utility class defining constants used throughout the application.
**HuffGUI** : The GUI implementation (optional, for standalone usage).
**Processor** : The CLI implementation (optional, for standalone usage).

## Credits
Special thanks to DanielDFY for contributing to the Huffman algorithm implementation.
Utilizes code and resources from [FlatLaf](https://github.com/JFormDesigner/FlatLaf) for GUI styling.

## License
### This project is licensed under the MIT License. See the LICENSE file for details.
