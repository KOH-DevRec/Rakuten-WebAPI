package jp.co.domain.global.bean.rms.confirmUpdate.response;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jp.co.domain.global.bean.rms.resultsRespose.MessageModel;
import lombok.Data;

/**
 * 楽天ペイ受注API：サブステータス情報取得APIレスポンスパラメータ
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class getSubStatusListResponse {

  /** メッセージモデルリスト */
  @JsonProperty("MessageModelList")
  public List<MessageModel> messageModelList;

  /** ステータスモデルリスト */
  @JsonProperty("StatusModelList")
  public List<StatusModel> statusModelList;
}
