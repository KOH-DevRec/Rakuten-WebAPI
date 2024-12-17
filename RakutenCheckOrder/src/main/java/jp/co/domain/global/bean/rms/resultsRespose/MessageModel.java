package jp.co.domain.global.bean.rms.resultsRespose;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * メッセージモデル
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class MessageModel {

  /** メッセージ種別 */
  @JsonProperty("messageType")
  public String messageType;

  /** メッセージコード */
  @JsonProperty("messageCode")
  public String messageCode;

  /** メッセージ */
  @JsonProperty("message")
  public String message;

  /** 注文番号 */
  @JsonProperty("orderNumber")
  public String orderNumber;

}
