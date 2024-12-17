package jp.co.domain.global.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jp.co.domain.global.bean.rms.resultsRespose.ResultModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 楽天ペイ受注API共通認証エラーレスポンスパラメータ
 *
 * --< CHANGE HISTORY >--

 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class AuthenticationErrorResponse {

  /**
   * エラー内容
   */
  @JsonProperty("Results")
  private ResultModel results;

}
