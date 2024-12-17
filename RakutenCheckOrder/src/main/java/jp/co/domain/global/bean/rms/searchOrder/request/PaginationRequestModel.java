package jp.co.domain.global.bean.rms.searchOrder.request;

import java.util.List;

import lombok.Data;

/**
 * ページングリクエストモデル
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Data
public class PaginationRequestModel {

  /**
   * 1ページあたりの取得結果数
   */
  public long requestRecordsAmount;

  /**
   * リクエストページ番号
   */
  public long requestPage;

  /**
   * 並び替えモデルリスト
   */
  public List<SortModel> SortModelList;
}
