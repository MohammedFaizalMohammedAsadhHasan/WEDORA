package com.wedora.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility class for file operations.
 * Demonstrates OOP concept: Information Hiding
 * Internal file handling details are encapsulated.
 */
public class FileStorageUtil {

    private static final String DATA_DIR = "wedora_data";
    private static final String ENCODING = StandardCharsets.UTF_8.name();

    /**
     * Gets the data directory path, creating it if it doesn't exist.
     */
    public static Path getDataDirectory() throws IOException {
        Path dataDir = Paths.get(System.getProperty("user.home"), DATA_DIR);
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        return dataDir;
    }

    /**
     * Reads all lines from a file.
     */
    public static List<String> readAllLines(String filename) throws IOException {
        Path filePath = getDataDirectory().resolve(filename);
        if (!Files.exists(filePath)) {
            return List.of();
        }
        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return List.of();
        }
    }

    /**
     * Writes lines to a file, overwriting existing content.
     */
    public static void writeLines(String filename, List<String> lines) throws IOException {
        try {
            Path filePath = getDataDirectory().resolve(filename);
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException("Failed to write file " + filename + ": " + e.getMessage(), e);
        }
    }

    /**
     * Appends a line to a file.
     */
    public static void appendLine(String filename, String line) throws IOException {
        Path filePath = getDataDirectory().resolve(filename);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        Files.write(filePath, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), java.nio.file.StandardOpenOption.APPEND);
    }

    /**
     * Checks if a file exists.
     */
    public static boolean fileExists(String filename) throws IOException {
        Path filePath = getDataDirectory().resolve(filename);
        return Files.exists(filePath);
    }

    /**
     * Deletes a file if it exists.
     */
    public static void deleteFile(String filename) throws IOException {
        Path filePath = getDataDirectory().resolve(filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
