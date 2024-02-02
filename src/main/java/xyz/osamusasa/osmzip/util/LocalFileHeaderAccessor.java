package xyz.osamusasa.osmzip.util;

import xyz.osamusasa.osmzip.element.LocalFileHeader;

import java.time.LocalDateTime;

/**
 * LFH(Local File Header)へのアクセスを簡易にするクラス
 */
public class LocalFileHeaderAccessor {
    private LocalFileHeader lfh;

    public LocalFileHeaderAccessor(LocalFileHeader lfh) {
        this.lfh = lfh;
    }

    /**
     * Extracts the version needed to extract a file from a LocalFileHeader.
     *
     * @return the version needed to extract the file
     */
    public int needToExtractVersion() {
        return lfh.getNeedVersion();
    }

    /**
     * Checks if the file is password protected.
     *
     * @return true if the file is password protected, false otherwise
     */
    public boolean isPasswordProtection() {
        // 0 000 000 000 000 001
        return (lfh.getOptionFlag() & 0000001) == 1;
    }

    /**
     * Checks if the Local File Header uses the Data Descriptor option.
     *
     * @return true if the Data Descriptor option is enabled, false otherwise
     */
    public boolean isUseDataDescriptor() {
        // 0 000 000 000 001 000
        return ((lfh.getOptionFlag() & 0000010) >> 3) == 1;
    }

    /**
     * Checks if the Local File Header uses the UTF-8 encoding for file names and comments.
     *
     * @return true if the UTF-8 option is enabled, false otherwise
     */
    public boolean isUTF8() {
        // 0 000 100 000 000 000
        return ((lfh.getOptionFlag() & 0004000) >> 11) == 1;
    }

    /**
     * Retrieves the compression method used for the file.
     *
     * @return the compression method used for the file
     */
    public int getCompressionMethod() {
        return lfh.getCompressionMethod();
    }

    /**
     * Retrieves the timestamp of the LocalFileHeader.
     *
     * @return the timestamp of the LocalFileHeader as LocalDateTime
     */
    public LocalDateTime getTimestamp() {
        // 1 111 111 000 000 000
        int year = (( lfh.getLastModDate() & 0177000 ) >> 9) + 1980;
        // 0 000 000 111 100 000
        int month = ( lfh.getLastModDate() & 0000740 ) >> 5;
        // 0 000 000 000 011 111
        int day = ( lfh.getLastModDate() & 0000037 );
        // 1 111 100 000 000 000
        int hour = (lfh.getLastModTime() & 0174000) >> 11;
        // 0 000 011 111 100 000
        int minute = (lfh.getLastModTime() & 0003740) >> 5;
        // 0 000 000 000 011 111
        int second = (lfh.getLastModTime() & 0000037) * 2;

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    /**
     * Retrieves the CRC-32 value of the file from the LocalFileHeader.
     *
     * @return the CRC-32 value of the file
     */
    public long getCrc32() {
        return lfh.getCrc32();
    }

    /**
     * Retrieves the compressed size of the file.
     *
     * @return the compressed size of the file
     */
    public long getCompressedSize() {
        return lfh.getCompressedSize();
    }

    /**
     * Retrieves the uncompressed size of the file.
     *
     * @return the uncompressed size of the file
     */
    public long getUncompressedSize() {
        return lfh.getUncompressedSize();
    }

    /**
     * Retrieves the file name from the LocalFileHeader.
     *
     * @return the file name
     */
    public String getFileName() {
        return lfh.getFileName();
    }

    /**
     * Retrieves the length of the extra field from the LocalFileHeader.
     *
     * @return the length of the extra field
     */
    public int getLengthExtraField() {
        return lfh.getLengthExtraField();
    }

    /**
     * Retrieves the extra field from the LocalFileHeader.
     *
     * @return the extra field as a byte array
     */
    public byte[] getExtraField() {
        return lfh.getExtraField();
    }
}
