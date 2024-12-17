package jp.co.domain.global.service.function;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.domain.global.bean.entity.ApiAuthorization;
import jp.co.domain.global.mapper.RakutenCheckOrderMapper;

/**
 * SQL実行
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Component
public class RakutenCheckOrderExecSql {

  @Autowired
  RakutenCheckOrderMapper RakutenCheckOrderMapper;

  /**
   * 店舗認証情報取得
   *
   * @param      店舗パラメータ
   * @return     店舗認証情報
   */
  public List<ApiAuthorization> getTenpoInfo(int prm) {
  return RakutenCheckOrderMapper.execRakutenCheckOrder(prm);
  }
}
