package jp.co.domain.global.bean.rms.searchOrder.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 受注検索APIレスポンス
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class SearchOrderResponse {

  /**
   * メッセージモデルリスト
   */
  @JsonProperty("MessageModelList")
  public List<jp.co.domain.global.bean.rms.resultsRespose.MessageModel> messageModelList;

  /**
   * 注文番号リスト
   */
  @JsonProperty("orderNumberList")
  public List<String> orderNumberList;

  /**
   * ページングレスポンスモデル
   */
  @JsonProperty("PaginationResponseModel")
  public PaginationResponseModel paginationResponseModel;
}
