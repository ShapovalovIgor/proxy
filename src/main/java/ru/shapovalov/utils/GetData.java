package ru.shapovalov.utils;


import ru.shapovalov.Constant;
import ru.shapovalov.dao.Skipping;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetData {

    private static final Logger LOG = LogWriter.getLogger(GetData.class);
    private static final String CONFIG_FILE_PATCH = new File(Constant.NAME_CONFIG_FILE).getAbsolutePath();

    public List<Skipping> getSkipping() throws Exception {
        List<Skipping> listConnection = new ArrayList<Skipping>();
        String localPort = null;
        String remoteHost = null;
        String remotePort = null;
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CONFIG_FILE_PATCH), charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {

                if (line.isEmpty()) {
                    listConnection.add(getSkipping(localPort, remoteHost, remotePort));
                } else {
                    String stringType = checkTypeString(line);
                    switch (stringType) {
                        case "localPort":
                            localPort = getPort(line);
                            break;
                        case "remoteHost":
                            remoteHost = getRemoteHost(line);
                            break;
                        case "remotePort":
                            remotePort = getPort(line);
                            break;
                    }
                }
            }
            listConnection.add(getSkipping(localPort, remoteHost, remotePort));
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return checkNullValue(listConnection);
    }

    private List<Skipping> checkNullValue(List<Skipping> listConnection) {
        List<Skipping> skippingNoNull = new ArrayList<>();
        for (Skipping skipping : listConnection) {
            if (skipping != null)
                skippingNoNull.add(skipping);
        }
        return skippingNoNull;
    }

    public boolean checkFile(String patch) {
        if (Files.exists(Paths.get(patch))) {
            LOG.log(Level.INFO, "File exist {0}", patch);
            return true;
        } else {
            LOG.log(Level.WARNING, "File not exist {0}", patch);
            return false;
        }
    }

    private String checkTypeString(String line) {
        String re1 = ".*?";    // Non-greedy match on filler
        String re2 = "(?:[a-z][a-z]+)";    // Uninteresting: word
        String re3 = ".*?";    // Non-greedy match on filler
        String re4 = "((?:[a-z][a-z]+))";    // Word 1
        Pattern p = Pattern.compile(re1 + re2 + re3 + re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(line);
        if (m.find()) {
            String var1 = m.group(1);
            return var1.toString();
        }
        return null;
    }

    private Skipping getSkipping(String localPort, String remoteHost, String remotePort) throws IOException {
        Skipping skipping;
        if (checkInputParams(localPort, remoteHost, remotePort)) {
            return skipping = new Skipping(Integer.parseInt(localPort), remoteHost, Integer.parseInt(remotePort));
        }
        return null;
    }

    private String getPort(String string) {
        String re1 = ".*?";    // Non-greedy match on filler
        String re2 = "(\\d+)";    // Integer Number 1

        Pattern p = Pattern.compile(re1 + re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(string);
        if (m.find()) {
            String int1 = m.group(1);
            return int1.toString();
        }
        return null;
    }

    private String getRemoteHost(String string) {
        String re1 = ".*?";    // Non-greedy match on filler
        String re2 = "(?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+)(?![\\w\\.])";    // Uninteresting: fqdn
        String re3 = ".*?";    // Non-greedy match on filler
        String re4 = "((?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+))(?![\\w\\.])";    // Fully Qualified Domain Name 1

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(string);
        if (m.find()) {
            String int1 = m.group(1);
            return int1.toString();
        }
        return null;
    }

    private boolean checkInputParams(String localPort, String remoteHost, String remotePort) {
        try {
            if (!localPort.equals(null)
                    && !remoteHost.equals(null)
                    && !remotePort.equals(null)
                    && !localPort.isEmpty()
                    && !remoteHost.isEmpty()
                    && !remotePort.isEmpty()) {
                return true;
            }
        } catch (NullPointerException e) {
            LOG.log(Level.WARNING, "String is null: localPort and or remoteHost and or remotePort\n", e);
        }
        return false;
    }
}