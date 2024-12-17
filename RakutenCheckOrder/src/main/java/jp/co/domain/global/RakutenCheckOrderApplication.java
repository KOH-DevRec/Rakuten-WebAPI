package jp.co.domain.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jp.co.domain.global.controller.RakutenCheckOrderController;

/**
 * 楽天注文確認
 *
 * --< CHANGE HISTORY >--
 *   X.XXXXXX  0000/00/00 ①XXXXXXXX
 */
@SpringBootApplication
public class RakutenCheckOrderApplication implements CommandLineRunner {

  @Autowired
  RakutenCheckOrderController RakutenCheckOrderController;

  public static void main(String[] args) {
  SpringApplication.run(RakutenCheckOrderApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
  RakutenCheckOrderController.updRakutenOrder(args);
  }

}
