package xyz.osamusasa.osmzip.element;

import lombok.Data;

/**
 * ファイルの情報やZIP化したときの状況を格納します。
 */
@Data
public class CentralDictionaryHeader {

    /**
     * Central directory header であることを示す固定値
     *
     *  signature = $02014B50 = $50, $4B, $01, $02
     */
    private int signature;

    /**
     * ZIPファイル製作を行ったOSとアプリの情報：アプリがサポートするZIPのバージョン
     *
     *  1.0 - Default value
     *  1.1 - File is a volume label
     *  2.0 - File is a folder (directory)
     *  2.0 - File is compressed using Deflate compression
     *  2.0 - File is encrypted using traditional PKWARE encryption
     *  2.1 - File is compressed using Deflate64(tm)
     *  2.5 - File is compressed using PKWARE DCL Implode
     *  2.7 - File is a patch data set
     *  4.5 - File uses ZIP64 format extensions
     *  4.6 - File is compressed using BZIP2 compression*
     *  5.0 - File is encrypted using DES
     *  5.0 - File is encrypted using 3DES
     *  5.0 - File is encrypted using original RC2 encryption
     *  5.0 - File is encrypted using RC4 encryption
     *  5.1 - File is encrypted using AES encryption
     *  5.1 - File is encrypted using corrected RC2 encryption**
     *  5.2 - File is encrypted using corrected RC2-64 encryption**
     *  6.1 - File is encrypted using non-OAEP key wrapping***
     *  6.2 - Central directory encryption
     *  6.3 - File is compressed using LZMA
     *  6.3 - File is compressed using PPMd+
     *  6.3 - File is encrypted using Blowfish
     *  6.3 - File is encrypted using Twofish
     */
    private short majorVersion;

    /**
     * ZIPファイル製作を行ったOSとアプリの情報：OSの種類
     *
     *   0 - MS-DOS and OS/2
     *  (FAT / VFAT / FAT32 file systems)
     *   1 - Amiga
     *   2 - OpenVMS
     *   3 - UNIX
     *   4 - VM/CMS
     *   5 - Atari ST
     *   6 - OS/2 H.P.F.S.
     *   7 - Macintosh
     *   8 - Z-System
     *   9 - CP/M
     *  10 - Windows NTFS
     *  11 - MVS (OS/390 - Z/OS)
     *  12 - VSE
     *  13 - Acorn Risc
     *  14 - VFAT
     *  15 - alternate MVS
     *  16 - BeOS
     *  17 - Tandem
     *  18 - OS/400
     *  19 - OS/X (Darwin)
     *  20 thru 255 - unused
     */
    private short minerVersion;

    /**
     * 展開に必要なZIPのバージョン
     *
     *  10 (ver.1.0) ⇒無圧縮ファイル
     *  20 (ver.2.0) ⇒フォルダ、デフレート、パスワード保護
     *  45 (ver.4.5) ⇒ZIP64
     */
    private int needVersion;

    /**
     * オプションフラグ
     *
     *  ビットフラグ
     *   %0000000000000001 (パスワード保護)
     *   %0000000000001000 (Data descriptor を使用)
     *   %0000100000000000 (ファイル名やコメントが UTF-8文字)
     */
    private int optionFlag;

    /**
     * ファイル圧縮に用いたアルゴリズム
     *
     *  0 (無圧縮ファイル)
     *  8 (デフレート形式)
     */
    private int compressionMethod;

    /**
     * タイムスタンプ(時刻)
     *
     *  ビット割り当て
     *   %1111100000000000 = 0～23 [時]
     *   %0000011111100000 = 0～59 [分]
     *   %0000000000011111 = 0～29 [×2秒] ⇒1で2秒分を表す
     */
    private int lastModTime;

    /**
     * タイムスタンプ(日付)
     *
     *  ビット割り当て
     *   %1111111000000000 = 0～   [＋1980年] ⇒西暦1980年からの経過年
     *   %0000000111100000 = 1～12 [月]
     *   %0000000000011111 = 1～31 [日]
     */
    private int lastModDate;

    /**
     * ファイルのデータから算出した CRC-32 の値
     */
    private long crc32;

    /**
     * 圧縮後のデータ量、File data のサイズ
     * 無圧縮なら uncompressed size フィールドと同値
     *
     *  n [byte]
     *  0 [byte]  (データ無し) ⇒フォルダ、空ファイルなど
     *  $FFFFFFFF (ZIP64)
     */
    private long compressedSize;

    /**
     * 圧縮前のデータ量、つまりファイルサイズ
     *
     *  n [byte]
     *  0 [byte]  (データ無し) ⇒フォルダ、空ファイルなど
     *  $FFFFFFFF (ZIP64)
     */
    private long uncompressedSize;

    /**
     * file name フィールドのサイズ
     *
     *  n [byte]
     *  0 [byte] (名前なし) ⇒標準入力からの流入
     */
    private int lengthFileName;

    /**
     * extra field フィールドのサイズ
     *
     *  n [byte]
     *  0 [byte] (extra field を使用しない)
     */
    private int lengthExtraField;

    /**
     * file comment フィールドのサイズ
     *
     *  n [byte]
     *  0 [byte] (file comment を使用しない)
     */
    private int lengthFileComment;

    /**
     * 対応する Local file header が格納されているディスクの番号
     *
     *  0        (ディスク分割はしない)
     *  n [番目] (ディスクの識別番号)
     *  $FFFF    (ZIP64)
     */
    private int diskNumberStart;

    /**
     * データの属性(性質を示す)ビットフラグ
     *
     *  0 (バイナリデータ)
     *  1 (ASCII、テキストファイル)
     *  ※確かな場合にビットOnする
     *  ※展開器によっては改行コードなどを変換
     */
    private int internalFileAttr;

    /**
     * ファイルの属性(読み込み専用とか隠しファイルとか)
     *
     *  ※この値はOSに依存
     */
    private long externalFileAttr;

    /**
     * 対応する Local file header までのオフセット
     * ZIPファイル先頭から(ディスク分割時はそのディスクの先頭から)のオフセット
     *
     *  対応する Local file header までのオフセット
     *  ZIPファイル先頭から(ディスク分割時はそのディスクの先頭から)のオフセット
     */
    private long offsetRelativeLH;

    /**
     * ファイル名を格納する領域
     *
     *  "ファイル名"
     *  ※ドライブ・デバイスレター・先頭のスラッシュは無し
     *  ※フォルダ区切りは普通のスラッシュ"/"
     */
    private String fileName;

    /**
     * 拡張データを格納する領域
     */
    private byte[] extraField;

    /**
     * 当該ファイルに対してのコメントを格納する領域
     */
    private String fileComment;

}
