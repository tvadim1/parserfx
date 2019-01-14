package ru.mai.utils;

import java.io.InputStream;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocReader {
    public static String read(InputStream is) throws Exception {
        System.out.println("FileMagic.valueOf="+FileMagic.valueOf(is));
        String text = "";
        if (FileMagic.valueOf(is) == FileMagic.OLE2) {
            WordExtractor ex = new WordExtractor(is);
            text = ex.getText();
            ex.close();
        } else if (FileMagic.valueOf(is) == FileMagic.OOXML) {
            XWPFDocument doc = new XWPFDocument(is);
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            text = extractor.getText();
            extractor.close();
        }
        return text;
    }
}