package ru.shapovalov;

import ru.shapovalov.utils.GetData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) throws Exception {
        GetData getData = new GetData();
        if(getData.checkFile(Constant.NAME_CONFIG_FILE))
            getData.getSkipping();
    }
}
