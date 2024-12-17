package jp.co.domain.global.bean.rms.searchOrder.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ページングレスポンスモデル
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
@JsonIgnoreProperties("ignoreUnknown=true")
public class PaginationResponseModel {

  /**
   * 総結果数
   */
  @JsonProperty("totalRecordsAmount")
  public long totalRecordsAmount;

  /**
   * 総ページ数
   */
  @JsonProperty("totalPages")
  public long totalPages;

  /**
   * リクエストページ番号
   */
  @JsonProperty("requestPage")
  public long requestPage;
}
