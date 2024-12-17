package jp.co.domain.global.bean.rms.confirmUpdate.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 楽天ペイ受注API：注文確認APIリクエストパラメータ
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class updateOrderSubStatusRequest {

  /** サブステータスID */
  @JsonProperty("subStatusId")
  public long subStatusId;


  /** 注文番号リスト */
  @JsonProperty("orderNumberList")
  public List<String> orderNumberList;
}
