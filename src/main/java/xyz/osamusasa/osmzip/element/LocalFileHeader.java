package xyz.osamusasa.osmzip.element;

import lombok.Data;

/**
 * ファイルの外枠部分の情報を格納します。
 */
@Data
public class LocalFileHeader {

    /**
     * Central directory header であることを示す固定値
     *
     *  signature = $02014B50 = $50, $4B, $01, $02
     */
    private int signature;

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
}
