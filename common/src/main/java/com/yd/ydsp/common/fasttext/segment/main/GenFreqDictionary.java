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
import com.yd.ydsp.common.fasttext.segment.TermFrequencyCallback;

public class GenFreqDictionary {

    public static void main(String[] args) throws IOException {
        // "/home/leon/work/headquarters/working/commons/fasttext/data/dic/professional.txt"
        // /home/leon/work/headquarters/working/commons/fasttext/data/bin/professional.dic.bin
        String fileIn1 = "/home/leon/work/headquarters/working/commons/fasttext/src/conf.test/dic/professional.txt";
        String fileIn2 = "/home/leon/work/headquarters/working/commons/fasttext/src/conf.test/dic/sogo.txt";
        String fileOut1 = "/home/leon/work/headquarters/working/commons/fasttext/src/conf.test/bin/professional.dic.bin";
        String fileOut2 = "/home/leon/work/headquarters/working/commons/fasttext/src/conf.test/bin/sogo.dic.bin";
        genOne(fileIn1, fileOut1);
        genOne(fileIn2, fileOut2);
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
        dat.build(wordList, new TermFrequencyCallback());

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binDic));
        oos.writeObject(dat);
        oos.close();
    }
}
