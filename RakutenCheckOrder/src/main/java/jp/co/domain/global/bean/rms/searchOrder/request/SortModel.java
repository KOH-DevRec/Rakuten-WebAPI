package jp.co.domain.global.bean.rms.searchOrder.request;

import lombok.Data;

/**
 * 並び替えモデル
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
public class SortModel {

  /**
   * 並び替え項目
   */
  public long sortColumn;

  /**
   * 並び替え方法
   */
  public long sortDirection;
}
