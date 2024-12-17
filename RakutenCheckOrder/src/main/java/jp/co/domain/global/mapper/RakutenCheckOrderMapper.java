package jp.co.domain.global.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.co.domain.global.bean.entity.ApiAuthorization;

/**
 *
 * マッパー
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Mapper
public interface RakutenCheckOrderMapper {

  /**
   * 店舗認証情報取得
   *
   * @param  店舗パラメータ
   * @return 店舗認証情報
   */
   List<ApiAuthorization> execRakutenCheckOrder(int prm);
}
