package jp.co.domain.global.util;

import lombok.Getter;

/**
 * 定数(リクエスト)クラス
 *
 * * --< CHANGE HISTORY >--
 *     X.XXXXXX    0000/00/00 ①XXXXXXXX
 */
public class ConstReq {

  /*--------------------------------------------------*/
  /* エンドポイント */
  /*--------------------------------------------------*/
  /** 楽天ペイ受注API：受注検索APIエンドポイント */
  public static final String ENDPOINT_SEARCH_ORDER = "https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder/";

  /** 楽天ペイ受注API：サブステータス情報取得APIエンドポイント */
  public static final String ENDPOINT_GET_SUBSTATUS_LIST = "https://api.rms.rakuten.co.jp/es/2.0/order/getSubStatusList/";

  /** 楽天ペイ受注API：注文確認APIエンドポイント */
  public static final String ENDPOINT_CONFIRM_ORDER = "https://api.rms.rakuten.co.jp/es/2.0/order/confirmOrder/";

  /** 楽天ペイ受注API：サブステータス情報更新APIエンドポイント */
  public static final String ENDPOINT_UPD_ORDER_SUBSTATUS = "https://api.rms.rakuten.co.jp/es/2.0/order/updateOrderSubStatus/";

  /*--------------------------------------------------*/
  /* リクエスト設定 */
  /*--------------------------------------------------*/
  /** 楽天ペイ受注API：受注検索API取得結果数 */
  public static final int MAX_SEARCH_ORDER_RECORDS_AMT = 1000;

  /** 楽天ペイ受注API：更新処理最大件数 */
  public static final int MAX_GET_ORDER_RECORDS_AMT = 100;

  /** 楽天ペイ受注APIリクエスト間隔(sec) */
  public static final int API_REQUEST_INTERVAL = 1;

  /** 楽天ペイ受注APIリクエストタイムアウト値(msec) */
  public static final int API_REQUEST_TIMEOUT = 60000;

  /** 検索キーワード1 */
  public static final String KEYWORD_OPTION_KEYWORD1 = "【KEYWORD1】";

  /** 検索キーワード2 */
  public static final String KEYWORD_OPTION_KEYWORD2 = "【KEYWORD2】";

  /** 200 OK */
  public static final int HTTP_OK = 200;

  /** 検索範囲start */
  public static final int SEARCH_RANGE = 30;

  /** 日時フォーマット */
  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss+0900";

  /**
   * 楽天ペイ受注API：受注検索API ステータス
   */
  public enum OrderProgress {

    /** ステータス: 注文確認待ち */
    ORDER_CFM(100);

    @Getter
    private int value;

    private OrderProgress(int value) {
      this.value = value;
    }
  }

  /**
   * 楽天ペイ受注API：受注検索API 期間検索種別
   */
  public enum SearchDateType {

    /** 注文日 */
    ORDER_DATE(1),

    /** 注文確認日 */
    ORDER_CFM_DATE(2),

    /** 注文確定日 */
    ORDER_FIX_DATE(3);

    @Getter
    private int value;

    private SearchDateType(int value) {
      this.value = value;
    }
  }

  /**
   * 楽天ペイ受注API：受注検索API 検索キーワード種別
   */
  public enum SearchKeywordType {

    /** なし */
    NONE(0),

    /** 商品名 */
    ITEM_NM(1);

    @Getter
    private int value;

    private SearchKeywordType(int value) {
      this.value = value;
    }
  }

  /**
   * 楽天ペイ受注API：受注検索API 並び替え項目
   */
  public enum SortColumn {

    /** 注文日時 */
    ORDER_DATETIME(1);

    @Getter
    private int value;

    private SortColumn(int value) {
      this.value = value;
    }
  }

  /**
   * 楽天ペイ受注API：受注検索API 並び替え方法
   */
  public enum SortDirection {

    /** 昇順 */
    ASC(1),

    /** 降順 */
    DESC(2);

    @Getter
    private int value;

    private SortDirection(int value) {
      this.value = value;
    }
  }

}
