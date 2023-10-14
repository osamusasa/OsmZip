package xyz.osamusasa.osmzip.io;

import xyz.osamusasa.osmzip.element.CentralDictionaryHeader;
import xyz.osamusasa.osmzip.element.EndOfCentralDictionary;
import xyz.osamusasa.osmzip.element.FileData;
import xyz.osamusasa.osmzip.element.LocalFileHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

/**
 * ZIPファイルを読み込むクラス
 */
public class ZipReader {
    private static final ToIntBiFunction<byte[], Integer> intVal = (l, i) -> l[i+1] << 8 | l[i];
    private static final ToLongBiFunction<byte[],Integer> longVal = (l, i) -> l[i+3] << 24 | l[i+2] << 16 | l[i+1] << 8 | l[i];

    private final RandomAccessFile file;

    /**
     * 与えられたストリームから、ZIPリーダーを作成する
     *
     * @param file ZIPファイルを表すファイルオブジェクト
     */
    public ZipReader(File file) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, "r");
    }

    /**
     * EOCD(End Of Central Dictionary)を読み込む。
     *
     * @return Eocd
     * @throws OsmZipIOException Zip Format Error or Not implemented Feature
     */
    public EndOfCentralDictionary getEocd() throws OsmZipIOException {
        EndOfCentralDictionary eocd = new EndOfCentralDictionary();
        byte[] buf = new byte[22];

        try {
            long fileSize = file.length();
            int offset = -1;

            file.seek(fileSize - 22);
            file.read(buf);

            if (buf[0] == 0x50 && buf[1] == 0x4b && buf[2] == 0x05 && buf[3] == 0x06) {
                eocd.setSignature(0x06054b50);
                offset = 0;
            } else {
                throw new OsmZipIOException("EOCDのコメントが0バイトではない。 TODO:コメントが0バイト以外の場合の対応");
                // TODO
            }

            eocd.setNumberOfDisk(intVal.applyAsInt(buf,offset+4));
            eocd.setNumberOfDiskFirstCD(intVal.applyAsInt(buf,offset+6));
            eocd.setTotalNumberOfCDOnDisk(intVal.applyAsInt(buf,offset+8));
            eocd.setTotalNumberOfCD(intVal.applyAsInt(buf,offset+10));
            eocd.setSizeOfTotalCD(longVal.applyAsLong(buf,offset+12));
            eocd.setOffsetFirstCD(longVal.applyAsLong(buf,offset+16));
            eocd.setLengthZipComment(intVal.applyAsInt(buf,offset+20));

        } catch (OsmZipIOException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eocd;
    }

    /**
     * CDH(Central Dictionary Header)を読み込む
     *
     * @param offset オフセット
     * @return CDH
     * @throws OsmZipIOException Zip Format Error or Not implemented Feature
     */
    public CentralDictionaryHeader getCDH(long offset) throws OsmZipIOException {
        CentralDictionaryHeader cdh = new CentralDictionaryHeader();
        byte[] buf = new byte[46];

        try {
            file.seek(offset);
            file.read(buf);

            if (buf[0] == 0x50 && buf[1] == 0x4b && buf[2] == 0x01 && buf[3] == 0x02) {
                cdh.setSignature(0x02014b50);
            } else {
                throw new OsmZipIOException("CDHが開始するオフセットではない。：" + offset);
            }

            cdh.setMajorVersion(buf[4]);
            cdh.setMinerVersion(buf[5]);
            cdh.setNeedVersion(intVal.applyAsInt(buf, 6));
            cdh.setOptionFlag(intVal.applyAsInt(buf, 8));
            cdh.setCompressionMethod(intVal.applyAsInt(buf,10));
            cdh.setLastModTime(intVal.applyAsInt(buf, 12));
            cdh.setLastModDate(intVal.applyAsInt(buf, 14));
            cdh.setCrc32(longVal.applyAsLong(buf, 16));
            cdh.setCompressedSize(longVal.applyAsLong(buf, 20));
            cdh.setUncompressedSize(longVal.applyAsLong(buf, 24));
            cdh.setLengthFileName(intVal.applyAsInt(buf, 28));
            cdh.setLengthExtraField(intVal.applyAsInt(buf, 30));
            cdh.setLengthFileComment(intVal.applyAsInt(buf, 32));
            cdh.setDiskNumberStart(intVal.applyAsInt(buf, 34));
            cdh.setInternalFileAttr(intVal.applyAsInt(buf, 36));
            cdh.setExternalFileAttr(longVal.applyAsLong(buf, 38));
            cdh.setOffsetRelativeLH(longVal.applyAsLong(buf, 42));

            buf = new byte[cdh.getLengthFileName() + cdh.getLengthExtraField() + cdh.getLengthFileComment()];
            file.seek(offset+46);
            file.read(buf);

            if (cdh.getLengthFileName()>0)cdh.setFileName(new String(Arrays.copyOfRange(buf, 0, cdh.getLengthFileName())));
            if (cdh.getLengthExtraField()>0)cdh.setExtraField(Arrays.copyOfRange(buf, cdh.getLengthFileName(), cdh.getLengthExtraField()));
            if (cdh.getLengthFileComment()>0)cdh.setFileComment(new String(Arrays.copyOfRange(buf, cdh.getLengthFileName()+cdh.getLengthExtraField(), cdh.getLengthFileComment())));

        } catch (OsmZipIOException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cdh;
    }

    /**
     * LFH(Local File Header)を読み込む
     *
     * @param offset オフセット
     * @return LFH
     * @throws OsmZipIOException Zip Format Error or Not implemented Feature
     */
    public LocalFileHeader getLFH(long offset) throws OsmZipIOException {
        LocalFileHeader lfh = new LocalFileHeader();
        byte[] buf = new byte[46];

        try {
            file.seek(offset);
            file.read(buf);

            if (buf[0] == 0x50 && buf[1] == 0x4b && buf[2] == 0x03 && buf[3] == 0x04) {
                lfh.setSignature(0x04034b50);
            } else {
                throw new OsmZipIOException("LFHが開始するオフセットではない。：" + offset);
            }

            lfh.setNeedVersion(intVal.applyAsInt(buf, 4));
            lfh.setOptionFlag(intVal.applyAsInt(buf, 6));
            lfh.setCompressionMethod(intVal.applyAsInt(buf, 8));
            lfh.setLastModTime(intVal.applyAsInt(buf, 10));
            lfh.setLastModDate(intVal.applyAsInt(buf, 12));
            lfh.setCrc32(longVal.applyAsLong(buf, 14));
            lfh.setCompressedSize(longVal.applyAsLong(buf, 18));
            lfh.setUncompressedSize(longVal.applyAsLong(buf, 22));
            lfh.setLengthFileName(intVal.applyAsInt(buf, 26));
            lfh.setLengthExtraField(intVal.applyAsInt(buf, 28));

            buf = new byte[lfh.getLengthFileName() + lfh.getLengthExtraField()];
            file.seek(offset+30);
            file.read(buf);

            if (lfh.getLengthFileName()>0)lfh.setFileName(new String(Arrays.copyOfRange(buf, 0, lfh.getLengthFileName())));
            if (lfh.getLengthExtraField()>0)lfh.setExtraField(Arrays.copyOfRange(buf, lfh.getLengthFileName(), lfh.getLengthExtraField()));
        } catch (OsmZipIOException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lfh;
    }

    /**
     * File Dataを読み込む
     *
     * @param lfhOffset 対応するLFH(Local File Header)のオフセット
     * @param lfh 対応するLFH(Local File Header)
     * @return File Data
     */
    public FileData getFileData(long lfhOffset, LocalFileHeader lfh) {
        FileData fd = new FileData();
        byte[] data = new byte[(int)lfh.getUncompressedSize()];

        try {
            file.seek(lfhOffset + 30 + lfh.getLengthFileName() + lfh.getLengthExtraField());
            file.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fd.setData(data);
        return fd;
    }
}
