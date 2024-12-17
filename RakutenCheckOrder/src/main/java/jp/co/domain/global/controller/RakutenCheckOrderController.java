package jp.co.domain.global.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jp.co.domain.global.service.RakutenCheckOrderService;

/**
 *
 * コントローラー
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@Controller
public class RakutenCheckOrderController {

  @Autowired
  RakutenCheckOrderService RakutenCheckOrderService;

  @SuppressWarnings("javadoc")
  public void updRakutenOrder(String[] args) throws IOException {
  RakutenCheckOrderService.updRakutenOrder(args);
  }

}
