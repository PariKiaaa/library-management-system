package src.util;

import java.io.*;

/**
 * Utility class for file operations with serialization support.
 */
public final class FileUtil {

    // Private constructor to prevent instantiation
    private FileUtil() {
    }

    // Saves an object to a file using serialization
    public static void save(String filePath, Object object) throws IOException {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(filePath))) {

            outputStream.writeObject(object);
        }
    }

    // Loads an object from a file using deserialization
    public static Object load(String filePath)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(filePath))) {

            return inputStream.readObject();
        }
    }
}