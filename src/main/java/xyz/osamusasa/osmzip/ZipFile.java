package xyz.osamusasa.osmzip;

import lombok.ToString;
import xyz.osamusasa.osmzip.element.CentralDictionaryHeader;
import xyz.osamusasa.osmzip.element.EndOfCentralDictionary;
import xyz.osamusasa.osmzip.element.FileData;
import xyz.osamusasa.osmzip.element.LocalFileHeader;
import xyz.osamusasa.osmzip.io.OsmZipIOException;
import xyz.osamusasa.osmzip.io.ZipReader;
import xyz.osamusasa.osmzip.util.LocalFileHeaderAccessor;

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

    /**
     * End of central directory record の ディスクの総数を返す。
     *
     * @return ディスクの総数
     */
    public int getNumberOfDisk() {
        return eocd.getNumberOfDisk();
    }

    /**
     * Retrieves the number of the disk on which the first Central Directory (CD) starts.
     *
     * @return The number of the disk on which the first CD starts.
     */
    public int getNumberOfDiskFirstCD() {
        return eocd.getNumberOfDiskFirstCD();
    }

    /**
     * Retrieves the total number of Central Directory (CD) records present on the disk.
     *
     * @return The total number of CD records on the disk.
     */
    public int getTotalNumberOfCDOnDisk() {
        return eocd.getTotalNumberOfCDOnDisk();
    }

    /**
     * Retrieves the total number of Central Directory (CD) records.
     *
     * @return The total number of CD records.
     */
    public int getTotalNumberOfCD(){
        return eocd.getTotalNumberOfCD();
    }

    /**
     * Retrieves the total size of all Central Directory (CD) records.
     *
     * @return The total size of all CD records.
     */
    public long getSizeOfTotalCD() {
        return eocd.getSizeOfTotalCD();
    }

    /**
     * Retrieves the offset of the first Central Directory (CD) record.
     *
     * @return The offset of the first CD record.
     */
    public long getOffsetFirstCD() {
        return eocd.getOffsetFirstCD();
    }

    /**
     * Retrieves the length of the .ZIP file comment.
     *
     * @return The length of the .ZIP file comment.
     */
    public int getLengthZipComment() {
        return eocd.getLengthZipComment();
    }

    /**
     * Retrieves the .ZIP file comment.
     *
     * @return The .ZIP file comment.
     */
    public String getZipComment() {
        return eocd.getZipComment();
    }

    /**
     * Retrieves the LocalFileHeaderAccessor object for the file at the specified index.
     * TODO: FileDescription(FileMetaData)　みたいな形で、公開できるクラスでさらにラップして返したい。
     *       デフォルト値とかも設定できるような感じで
     *
     * @param index the index of the file in the ZipFile
     * @return the LocalFileHeaderAccessor object for the file
     */
    public LocalFileHeaderAccessor getFile(int index) {
        return new LocalFileHeaderAccessor(lfhs.get(index));
    }

    /**
     * Calculates the length of the local file header.
     * The local file header is a part of the ZIP file format that contains
     * metadata about a specific file within the ZIP archive.
     *
     * @return The length of the local file header in bytes.
     */
    public int lengthOfLocalFileHeader() {
        return lfhs.size();
    }

    /**
     * Retrieves the content of a file as a string.
     * TODO: 圧縮してあるパターンとか、文字コードとかいろんなパターンに対応が必要
     *
     * @param index the index of the file in the ZipFile
     * @return the content of the file as a string
     */
    public String getFileContentAsString(int index) {
        return new String(fds.get(index).getData());
    }
}
