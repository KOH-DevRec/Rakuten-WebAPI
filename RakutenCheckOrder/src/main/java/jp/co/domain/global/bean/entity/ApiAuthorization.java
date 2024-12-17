package jp.co.domain.global.bean.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * API認証情報
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Getter
@Setter
public class ApiAuthorization {

  /**
   * 店舗コード
   */
  private int tenpocd;

  /**
   * 店舗名
   */
  private String tenponm;

  /**
   * サービスシークレット
   */
  private String svcsrt;

  /**
   * ライセンスキー
   */
  private String lcskey;


}
