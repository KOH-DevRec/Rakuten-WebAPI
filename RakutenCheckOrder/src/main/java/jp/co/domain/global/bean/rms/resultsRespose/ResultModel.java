package jp.co.domain.global.bean.rms.resultsRespose;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * エラー内容
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResultModel {

  /** エラーコード */
  @JsonProperty("errorCode")
  private String errorCode;

  /** エラーメッセージ */
  @JsonProperty("message")
  private String message;
}
