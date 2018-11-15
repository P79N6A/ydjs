package com.yd.ydsp.common.fasttext.segment.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yd.ydsp.common.fasttext.segment.Darts;

public class GenCommonDictionary {

    public static void main(String[] args) throws IOException {
        String fileIn1 = "/home/leon/work/headquarters/working/commons/fasttext/src/conf.test/dic/words.txt";
        String fileOut1 = "/home/leon/work/headquarters/working/commons/fasttext/src/conf.test/bin/words.dic.bin";
        genOne(fileIn1, fileOut1);
    }

    public static void genOne(String dic, String binDic) throws IOException {
        Darts dat = new Darts();
        InputStream worddata = new FileInputStream(dic);
        BufferedReader in = new BufferedReader(new InputStreamReader(worddata, "UTF8"));
        String newword;
        List<char[]> wordList = new ArrayList<char[]>(800000);
        while ((newword = in.readLine()) != null) {
            if (newword.length() > 1) {
                wordList.add(newword.toCharArray());
            }

        }
        in.close();
        dat.build(wordList);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binDic));
        oos.writeObject(dat);
        oos.close();
    }
}
