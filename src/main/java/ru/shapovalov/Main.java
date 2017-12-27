package ru.shapovalov;

import ru.shapovalov.dao.Skipping;
import ru.shapovalov.utils.GetData;
import ru.shapovalov.utils.Proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        GetData getData = new GetData();
        if (getData.checkFile(Constant.NAME_CONFIG_FILE)) {
            List<Skipping> skippingList = getData.getSkipping();
            ExecutorService pool = Executors.newFixedThreadPool(3);
            for (Skipping skipping : skippingList) {
                LOG.log(Level.INFO, "Start proxy Local port:" + skipping.getLocalPort() + " Remote host:" + skipping.getRemoteHost() + " Remote port:" + skipping.getRemotePort());
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ServerSocket server = new ServerSocket(skipping.getLocalPort());
                            new Proxy(server.accept(), skipping.getRemoteHost(), skipping.getRemotePort());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
