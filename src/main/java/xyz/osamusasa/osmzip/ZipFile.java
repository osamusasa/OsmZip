package xyz.osamusasa.osmzip;

import lombok.ToString;
import xyz.osamusasa.osmzip.element.CentralDictionaryHeader;
import xyz.osamusasa.osmzip.element.EndOfCentralDictionary;
import xyz.osamusasa.osmzip.element.FileData;
import xyz.osamusasa.osmzip.element.LocalFileHeader;
import xyz.osamusasa.osmzip.io.OsmZipIOException;
import xyz.osamusasa.osmzip.io.ZipReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Zipファイルを表すクラス
 */
@ToString
public class ZipFile {

    private EndOfCentralDictionary eocd;
    private List<CentralDictionaryHeader> cdhs;
    private List<LocalFileHeader> lfhs;
    private List<FileData> fds;

    public static ZipFile read(File file) throws OsmZipIOException {
        ZipFile zipFile = new ZipFile();
        ZipReader reader = null;

        try {
            reader = new ZipReader(file);

            zipFile.eocd = reader.getEocd();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OsmZipIOException e) {
            e.printStackTrace();
        }

        if (reader == null || zipFile.eocd == null) {
            throw new OsmZipIOException("failed to read End Of Central Directory");
        }

        zipFile.cdhs = new ArrayList<>();

        zipFile.cdhs.add(reader.getCDH(zipFile.eocd.getOffsetFirstCD()));

        if (zipFile.cdhs.size() == 0) {
            throw new OsmZipIOException("failed to read Central Directory Header");
        }

        zipFile.lfhs = new ArrayList<>();
        zipFile.fds = new ArrayList<>();

        for (CentralDictionaryHeader cdh: zipFile.cdhs) {
            LocalFileHeader lfh = reader.getLFH(cdh.getOffsetRelativeLH());
            zipFile.lfhs.add(lfh);
            zipFile.fds.add(reader.getFileData(cdh.getOffsetRelativeLH(), lfh));
        }

zipFile.fds.forEach((fd)->System.out.println(new String(fd.getData())));

        return zipFile;
    }
}
