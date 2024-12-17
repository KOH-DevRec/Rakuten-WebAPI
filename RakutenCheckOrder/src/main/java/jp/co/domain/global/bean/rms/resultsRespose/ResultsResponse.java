package jp.co.domain.global.bean.rms.resultsRespose;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * エラーレスポンス
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResultsResponse {

  /** エラー内容 */
  @JsonProperty("Results")
  private ResultModel results;

}
