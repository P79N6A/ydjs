package com.yd.ydsp.common.fasttext.segment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class LoadDarts {

    public static Darts load(String dicFile) throws IOException, ClassNotFoundException {
        InputStream fis = new FileInputStream(dicFile);
        return load(fis);

    }

    public static Darts load(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(in);
        Darts dat = (Darts) ois.readObject();
        ois.close();
        return dat;
    }

    public static Darts load(File resourceAsFile) throws IOException, ClassNotFoundException {
        InputStream fis = new FileInputStream(resourceAsFile);
        return load(fis);
    }
}
