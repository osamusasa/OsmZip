package xyz.osamusasa.osmzip.element;

import lombok.Data;

/**
 * 1つのZIPファイルに1つだけ存在しそのZIPファイル全体の情報を格納します。
 * アプリは最初にこの End of central directory record を読み込み、
 * オフセット情報を順にたどっていくことですべての格納ファイルにアクセスすることができます。
 */
@Data
public class EndOfCentralDictionary {

    /**
     * End of central directory record であることを示す固定値
     *
     *  signature = $06054B50 = $50, $4B, $05, $06
     */
    private int signature;

    /**
     * このディスク(End of central directory record の有る)の番号
     * ディスクの総数
     *
     *  0        (ディスク分割はしない)
     *  n [番目] (ディスクの識別番号)
     *  $FFFF    (ZIP64)
     */
    private int numberOfDisk;

    /**
     * 最初の Central directory header が有るディスクの番号
     *
     *  0        (ディスク分割はしない)
     *  n [番目] (ディスクの識別番号)
     *  $FFFF    (ZIP64)
     */
    private int numberOfDiskFirstCD;

    /**
     * 基本的にはファイル数のこと
     * ディスク分割において、同じディスクから取得できる Central directory header の数
     *
     *  n [個]
     *  $FFFF  (ZIP64)
     */
    private int totalNumberOfCDOnDisk;

    /**
     * 基本的にはファイル数のこと
     * ZIPファイルに格納してある Central directory header の総数
     *
     *  n [個]
     *  $FFFF  (ZIP64)
     */
    private int totalNumberOfCD;

    /**
     * 全ての Central directory header サイズの合計値
     *
     *  n [byte]
     *  $FFFFFFFF (ZIP64)
     */
    private long sizeOfTotalCD;

    /**
     * 最初の Central directory header までのオフセット
     * ZIPファイル先頭から(ディスク分割時はそのディスクの先頭から)のオフセット
     *
     *  n [byte]  (オフセット)
     *  $FFFFFFFF (ZIP64)
     */
    private long offsetFirstCD;

    /**
     * .ZIP file comment フィールドのサイズ
     *
     *  n [byte]
     *  0 [byte] (.ZIP file comment を使用しない)
     */
    private int lengthZipComment;

    /**
     * ZIPファイルに対してのコメントを格納する領域
     *
     *  文字列？ (安全性は保証されない)
     */
    private String zipComment;
}
