package src.util;

import java.io.*;

/**
 * Utility class for saving and loading objects from files.
 */
public final class FileUtil {

    // Prevent instantiation
    private FileUtil() {
    }

    /**
     * Saves an object to a file.
     *
     * @param filePath path of the file
     * @param object object to save
     * @throws IOException if an I/O error occurs
     */
    public static void save(String filePath, Object object) throws IOException {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(filePath))) {

            outputStream.writeObject(object);
        }
    }

    /**
     * Loads an object from a file.
     *
     * @param filePath path of the file
     * @return loaded object
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the object's class cannot be found
     */
    public static Object load(String filePath)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(filePath))) {

            return inputStream.readObject();
        }
    }
}