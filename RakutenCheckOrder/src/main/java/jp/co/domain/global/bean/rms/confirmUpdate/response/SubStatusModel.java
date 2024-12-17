package jp.co.domain.global.bean.rms.confirmUpdate.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * サブステータスモデル
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SubStatusModel {

  /** サブステータスID */
  @JsonProperty("subStatusId")
  public long subStatusId;

  /** サブステータス名 */
  @JsonProperty("subStatusName")
  public String subStatusName;

  /** 並び順 */
  @JsonProperty("orderby")
  public long orderby;
}
