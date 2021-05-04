package se.andreasson.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UtilsFileReader {

    public static byte[] readFromFile(File file) {
        byte[] content = new byte[0];               //initiera
        System.out.println("Does file exists: " + file.exists());
        if (file.exists() && file.canRead()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                content = new byte[(int) file.length()]; //skapa array med rätta storleken nu när den är känd
                int count = fileInputStream.read(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }
}