package xyz.osamusasa.osmzip;

import xyz.osamusasa.osmzip.io.OsmZipIOException;

import java.io.File;

public class Zip {

    /**
     * zip圧縮
     */
    public void compress(){}

    /**
     * zip解凍
     */
    public void uncompress(String sourceFile, String targetDir){
        //zipファイル読み込み
        ZipFile zip = null;
        try {
            zip = ZipFile.read(new File(sourceFile));
        } catch (OsmZipIOException e) {
            e.printStackTrace();
        }

        //zipファイル書き込み
    }
}
