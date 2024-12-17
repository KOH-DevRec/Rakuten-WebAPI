package jp.co.domain.global.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

import jp.co.domain.global.bean.entity.ApiAuthorization;
import lombok.Getter;

/**
 * 楽天 Web API ユーティリティ
 *
 * * --< CHANGE HISTORY >--
 *     X.XXXXXX    0000/00/00 ①XXXXXXXX
 */
public class RakutenWebApiUtil {

  /**
   * HTTPステータスコード
   */
  @Getter
  private long statusCode;

  /**
   * APIレスポンス(JSON)
   */
  @Getter
  private String responseJson;

  /**
   * 楽天ペイ受注API：受注検索API
   *
   * @param authorization API認証情報
   * @param requestParam  受注検索APIリクエストパラメータ(JSON)
   * @return 受注検索APIリクエスト成否
   * @throws IOException APIリクエストの送信に失敗、またはAPIレスポンスの解析に失敗した場合
   */
  public boolean searchOrder(ApiAuthorization authorization, String requestParam) throws IOException {

    return doPost(authorization, ConstReq.ENDPOINT_SEARCH_ORDER, requestParam);
  }

  /**
   * 楽天ペイ受注API：サブステータス情報取得API
   *
   * @param authorization API認証情報
   * @param requestParam
   * @return 注文確認APIリクエスト成否
   * @throws IOException APIリクエストの送信に失敗、またはAPIレスポンスの解析に失敗した場合
   */
  public boolean getSubStatusList(ApiAuthorization authorization, String requestParam) throws IOException {

    return doPost(authorization, ConstReq.ENDPOINT_GET_SUBSTATUS_LIST, requestParam);
  }

  /**
   * 楽天ペイ受注API：注文確認API
   *
   * @param authorization API認証情報
   * @param requestParam  注文確認APIリクエストパラメータ(JSON)
   * @return 注文確認APIリクエスト成否
   * @throws IOException APIリクエストの送信に失敗、またはAPIレスポンスの解析に失敗した場合
   */
  public boolean confirmOrder(ApiAuthorization authorization, String requestParam) throws IOException {

    return doPost(authorization, ConstReq.ENDPOINT_CONFIRM_ORDER, requestParam);
  }

  /**
   * 楽天ペイ受注API：注文確認API
   *
   * @param authorization API認証情報
   * @param requestParam  注文確認APIリクエストパラメータ(JSON)
   * @return 注文確認APIリクエスト成否
   * @throws IOException APIリクエストの送信に失敗、またはAPIレスポンスの解析に失敗した場合
   */
  public boolean updateOrderSubStatus(ApiAuthorization authorization, String requestParam) throws IOException {

    return doPost(authorization, ConstReq.ENDPOINT_UPD_ORDER_SUBSTATUS, requestParam);
  }

  /**
   * POST
   *
   * @param authorization API認証情報
   * @param endpoint    エンドポイント
   * @param requestParam  APIリクエストパラメータ(JSON)
   * @return APIリクエスト成否
   * @throws IOException 入出力エラーが発生した場合
   */
  private boolean doPost(ApiAuthorization authorization, String endpoint, String requestParam) throws IOException {

    boolean isSuccess = false;
    URL url = null;
    HttpURLConnection con = null;

    try {

      // コネクション取得
      url = new URL(endpoint);
      con = (HttpURLConnection) url.openConnection();

      // 各種設定
      con.setRequestMethod("POST");
      con.setConnectTimeout(ConstReq.API_REQUEST_TIMEOUT);
      con.setReadTimeout(ConstReq.API_REQUEST_TIMEOUT);
      con.setDoInput(true);
      con.setDoOutput(true);

      // リクエストヘッダー設定
      con.addRequestProperty("Accept-Language", Locale.getDefault().toString());
      con.addRequestProperty("Authorization",
          String.format("ESA %s",
              Base64.getEncoder().encodeToString(
                  String.format("%s:%s", authorization.getSvcsrt(), authorization.getLcskey())
                      .getBytes(StandardCharsets.UTF_8))));
      con.addRequestProperty("Content-Type", "application/json; charset=utf-8");

      // リクエストボディ設定
      try (OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), "UTF-8")) {
        out.write(requestParam);
        out.close();
      }

      // コネクションを開く
      con.connect();

      this.statusCode = con.getResponseCode();

      if (this.statusCode == HttpURLConnection.HTTP_OK) {

        // HTTPステータスコードが[200:OK]の場合
        try (InputStream in = con.getInputStream()) {
          this.responseJson = readResponse(in);
          isSuccess = true;
        }

      } else {

        // 上記以外の場合
        try (InputStream in = con.getErrorStream()) {
          this.responseJson = readResponse(in);
          isSuccess = false;
        }

      }

    } finally {

      if (con != null) {
        con.disconnect();
      }
    }

    return isSuccess;
  }

  /**
   * レスポンス読み出し
   *
   * @param in レスポンス入力ストリーム
   * @return レスポンス(JSON)
   * @throws IOException 入出力エラーが発生した場合
   */
  private String readResponse(InputStream in) throws IOException {
    StringBuffer strBr = new StringBuffer();
    String line = "";

    try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {

      while ((line = br.readLine()) != null) {
        strBr.append(line);
      }

      br.close();
    }
    return strBr.toString();
  }
}
