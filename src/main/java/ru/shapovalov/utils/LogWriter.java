package ru.shapovalov.utils;

import ru.shapovalov.Constant;

import java.io.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LogWriter {

    static {
        loadFromProperties();
    }

    private LogWriter() {
        throw new AssertionError();
    }

    private static void loadFromProperties() {

        InputStream stream;

        try {
            stream = new FileInputStream(new File(Constant.LOGGING_PROPERTIES).getAbsolutePath());
            LogManager.getLogManager().readConfiguration(stream);
        } catch (FileNotFoundException ex) {
            System.err.println("Logger property file not found: " + ex.toString());
        } catch (IOException | SecurityException ex) {
            System.err.println("The log is not configured: " + ex.toString());
        }

    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
}
