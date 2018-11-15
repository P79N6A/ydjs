package com.yd.ydsp.common.lang.i18n.provider;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.i18n.CharConverter;
import com.yd.ydsp.common.lang.i18n.CharConverterProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class ChineseCharConverterProvider implements CharConverterProvider {
    public static final String CODE_TABLE_CHARSET = "UTF-16BE";
    private char[] codeTable;

    public ChineseCharConverterProvider() {
    }

    public CharConverter createCharConverter() {
        this.loadCodeTable();
        return new CharConverter() {
            public char convert(char ch) {
                return ChineseCharConverterProvider.this.codeTable[ch];
            }
        };
    }

    protected final char[] loadCodeTable() {
        if(this.codeTable == null) {
            InputStream istream = this.getClass().getResourceAsStream(this.getCodeTableName() + ".ctable");
            if(istream == null) {
                throw new RuntimeException("Could not find code table: " + this.getCodeTableName());
            }

            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new InputStreamReader(istream, "UTF-16BE"));
                this.codeTable = new char[65536];

                for(int e = 0; e < 65536; ++e) {
                    this.codeTable[e] = (char)reader.read();
                }
            } catch (IOException var11) {
                throw new RuntimeException("Could not read code table: " + this.getCodeTableName(), var11);
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException var10) {
                        ;
                    }
                }

            }
        }

        return this.codeTable;
    }

    protected abstract String getCodeTableName();
}
