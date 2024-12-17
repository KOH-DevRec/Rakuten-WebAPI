package jp.co.domain.global.bean.rms.confirmUpdate.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ステータスモデル
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class StatusModel {

  /** ステータス　*/
  @JsonProperty("orderProgress")
  public long orderProgress;

  /** サブステータスモデルリスト */
  @JsonProperty("SubStatusModelList")
  public List<SubStatusModel> subStatusModelList;
}
