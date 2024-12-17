package jp.co.domain.global.bean.rms.searchOrder.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 受注検索APIリクエストパラメータ
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class SearchOrderRequest {

  /** ステータスリスト */
  @JsonProperty("orderProgressList")
  public List<Integer> orderProgressList;

  /** サブステータスIDリスト */
  @JsonProperty("subStatusIdList")
  public List<Integer> subStatusIdList;

  /** 期間検索種別 */
  @JsonProperty("dateType")
  public long dateType;

  /** 期間検索開始日時 */
  @JsonProperty("startDatetime")
  public String startDatetime;

  /** 期間検索終了日時 */
  @JsonProperty("endDatetime")
  public String endDatetime;

  /** 検索キーワード種別 */
  @JsonProperty("searchKeywordType")
  public long searchKeywordType;

  /** 検索キーワード */
  @JsonProperty("searchKeyword")
  public String searchKeyword;

  /** ページングリクエストモデル */
  @JsonProperty("PaginationRequestModel")
  public PaginationRequestModel paginationRequestModel;
}
