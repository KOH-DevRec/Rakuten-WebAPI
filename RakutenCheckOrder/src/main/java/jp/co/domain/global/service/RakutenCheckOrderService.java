package jp.co.domain.global.service;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.domain.global.bean.MailModel;
import jp.co.domain.global.bean.entity.ApiAuthorization;
import jp.co.domain.global.service.function.RakutenCheckOrderExecSql;
import jp.co.domain.global.service.function.RakutenCheckOrderManager;
import jp.co.domain.global.service.function.RakutenCheckOrderSendMail;
import jp.co.domain.global.util.ConstCom;

/**
 * サービスクラス
 *
 * * --< CHANGE HISTORY >--
 *     X.XXXXXX     0000/00/00 ①XXXXXXXX
 */
@Service
public class RakutenCheckOrderService {

  @Autowired
  RakutenCheckOrderExecSql RakutenCheckOrderExecSql;

  @Autowired
  RakutenCheckOrderManager RakutenCheckOrderManager;

  @Autowired
  RakutenCheckOrderSendMail RakutenCheckOrderSendMail;

  public void updRakutenOrder(String[] args) throws IOException {

    System.setProperty("org.slf4j.simpleLogger.log.RakutenCheckOrderService.class","INFO"); // serviceクラスのみログレベル:INFOで出力
    Logger logger = LoggerFactory.getLogger(RakutenCheckOrderService.class);
    logger.info("================================ START RakutenCheckOrder SYSTEM ================================");
    String eMessage = "";
    int tenpoPrm = 0;
    long subStatusId = 0;
    int nothingKeywordNum = 0;
    int KEYWORD1Num = 1;
    int KEYWORD2Num = 2;
    boolean systemFlg = false;
    boolean apiFlg = false;
    boolean liFlg = false;
    List<String> orderNumberList1 = new ArrayList<>();
    List<String> orderNumberList2 = new ArrayList<>();
    List<String> orderNumberList3 = new ArrayList<>();

    // バッチ起動時間を検索範囲(TO)に使用する
    LocalDateTime ldtNow = LocalDateTime.now();

    // 引数取得
    try {
      tenpoPrm = Integer.parseInt(args[0]);
      logger.info("START PROCESSING WITH PRM :" + tenpoPrm);
      logger.info(String.format("Host :") + getHostName());
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      logger.error(String.format("ERROR EXISTS IN 【Get PRM】 PROCESS: %s", ex.toString()));
    }

    /*--------------------------------------------------*/
    /* 店舗情報取得                                     */
    /*--------------------------------------------------*/
    List<ApiAuthorization> authList = RakutenCheckOrderExecSql.getTenpoInfo(tenpoPrm);    
    
    // 空の場合は処理を行わない
    if (authList.isEmpty()) {
      logger.error("Not Exists Tenpo Info.");
      logger.info("================================ FINISH RakutenCheckOrder SYSTEM ================================");
      System.exit(0);
    }
    /*--------------------------------------------------*/
    // 1店舗ずつ処理を行う
    for (ApiAuthorization apiAuth : authList) {
      logger.info(String.format("START PROCESSING TENPOCD :%s TENPONM :%s", apiAuth.getTenpocd(), apiAuth.getTenponm()));

      /*--------------------------------------------------*/
      /* サブステータスリスト取得                         */
      /*--------------------------------------------------*/
      try {
        subStatusId = RakutenCheckOrderManager.getSubStatusList(apiAuth, ldtNow);
        
        // ログ出力
        logger.info(String.format("Search SubStatusID: %s", subStatusId));
        
        // サブステータスIDが取得できなかった場合は次の店舗へ
        if(subStatusId == 0) {          
          eMessage += systemError(apiAuth.getTenponm(), "", 1);
          logger.error(String.format("Could Not Find  The Target SubStatusID"));
          
          continue;
        // 店舗情報が異なっていた場合は次の店舗へ  
        } else if(subStatusId == -1) {
          eMessage += systemError(apiAuth.getTenponm(), "", 2);
          logger.error(String.format("Wrong License Key or Service Secret."));
          liFlg = true;
          
          continue;
        }
        
      } catch (Exception e) {
        e.printStackTrace();
        logger.error(String.format("ERROR EXISTS IN 【Search SubStatusID】 PROCESS: %s", e.toString()));
        
        // 接続エラーでない場合は通知メッセージを作成
        if(escapeError(e)) {
          eMessage += "ERROR CODE: " + e.toString() + "\r\n";
          eMessage += systemError(apiAuth.getTenponm(), "サブステータスリスト取得", 0);
          systemFlg = true;
        } else {
          logger.error("NETWORK ERROR.");
        }
        
        continue;
      }

      /*--------------------------------------------------*/
      /* 注文検索(1)                                      */
      /*--------------------------------------------------*/
      try {
        orderNumberList1 = RakutenCheckOrderManager.searchOrder(apiAuth, ldtNow, nothingKeywordNum);

        // ログ出力
        for (String orderNumber : orderNumberList1) {
          logger.debug(String.format("Search Order(1) List OrderNumber: %s", orderNumber));
        }
        logger.info(String.format("Search Order(1) List OrderCount :%s", orderNumberList1.size()));
        
      } catch (Exception e) {
        e.printStackTrace();
        logger.error(String.format("ERROR EXISTS IN 【Search Order(1)】 PROCESS: %s", e.toString()));
        
        // 接続エラーでない場合は通知メッセージを作成
        if(escapeError(e)) {
          eMessage += "ERROR CODE: " + e.toString() + "\r\n";
          eMessage += systemError(apiAuth.getTenponm(), "注文検索", 0);
          systemFlg = true;
        } else {
          logger.error("NETWORK ERROR.");
        }
        
        continue;
      }

      // 空の場合の次の店舗へ
      if (orderNumberList1.isEmpty()) {
        logger.warn("Not Exists Order Number.");
        continue;
      }
      
      /*--------------------------------------------------*/
      /* 注文検索(2)                                      */
      /*--------------------------------------------------*/
      // キーワード：【KEYWORD1】での検索
      try {
        orderNumberList2 = RakutenCheckOrderManager.searchOrder(apiAuth, ldtNow, KEYWORD1Num);
      
        // ログ出力
        for (String orderNumber : orderNumberList2) {
          logger.debug(String.format("Search Order(2) KEYWORD1 List OrderNumber: %s", orderNumber));
        }
        logger.info(String.format("Search Order(2) KEYWORD1 List OrderCount :%s", orderNumberList2.size()));
        
      } catch (Exception e) {
        e.printStackTrace();
        logger.error(String.format("ERROR EXISTS IN 【Search Order(2)】 PROCESS: %s", e.toString()));
        
        // 接続エラーでない場合は通知メッセージを作成
        if(escapeError(e)) {
          eMessage += "ERROR CODE: " + e.toString() + "\r\n";
          eMessage += systemError(apiAuth.getTenponm(), "注文検索", 0);
          systemFlg = true;
        } else {
          logger.error("NETWORK ERROR.");
        }

        continue;
      }
      
      // キーワード：【KEYWORD2】での検索
      try {
        orderNumberList3 = RakutenCheckOrderManager.searchOrder(apiAuth, ldtNow, KEYWORD2Num);
      
        // ログ出力
        for (String orderNumber : orderNumberList3) {
          logger.debug(String.format("Search Order(2) KEYWORD2 List OrderNumber: %s", orderNumber));
        }
        logger.info(String.format("Search Order(2) KEYWORD2 List OrderCount :%s", orderNumberList3.size()));
        
      } catch (Exception e) {
        e.printStackTrace();
        logger.error(String.format("ERROR EXISTS IN 【Search Order(2)】 PROCESS: %s", e.toString()));
        
        // 接続エラーでない場合は通知メッセージを作成
        if(escapeError(e)) {
          eMessage += "ERROR CODE: " + e.toString() + "\r\n";
          eMessage += systemError(apiAuth.getTenponm(), "注文検索", 0);
          systemFlg = true;
        } else {
          logger.error("NETWORK ERROR.");
        }

        continue;
      }
      
      /*--------------------------------------------------*/
      /* 注文番号リスト整理                               */
      /*--------------------------------------------------*/
      // (2)の注文番号を合わせる
      orderNumberList2.addAll(orderNumberList3);

      // (1)から(2)の注文番号を除去する
      // 空の場合は処理を行わない
      if (orderNumberList2.isEmpty() == false) {
        orderNumberList1.removeAll(orderNumberList2);
      }
      
      /*--------------------------------------------------*/
      /* ステータス変更(注文確認)                         */
      /*--------------------------------------------------*/
      if (orderNumberList1.size() > 0) {
        try {
          boolean confirmOrderFlg = RakutenCheckOrderManager.confirmOrder(apiAuth, orderNumberList1);
          
          if (confirmOrderFlg == false) {
            logger.error(String.format("OCCURRED CONFIRM ORDER ERROR. TENPOCD: %s TENPONM: %s", apiAuth.getTenpocd(), apiAuth.getTenponm()));

          }
          // ログ出力
          for (String orderNumber : orderNumberList1) {
            logger.debug(String.format("Confirm Order List OrderNumber: %s", orderNumber));
          }
          logger.info(String.format("Confirm Order List OrderCount :%s", orderNumberList1.size()));
          
        } catch (Exception e) {
          e.printStackTrace();
          logger.error(String.format("ERROR EXISTS IN Confrim Order PROCESS: %s", e.toString()));
          
          // 接続エラーでない場合は通知メッセージを作成
          if(escapeError(e)) {
            eMessage += "ERROR CODE: " + e.toString() + "\r\n";
            eMessage += systemError(apiAuth.getTenponm(), "ステータス移動", 0);
            systemFlg = true;
          } else {
            logger.error("NETWORK ERROR.");
          }

          continue;
        }
      }
      
      /*--------------------------------------------------*/
      /* サブステータス変更(追加料金対応)                 */
      /*--------------------------------------------------*/
      // キーワード：【KEYWORD1】【KEYWORD2】に該当する商品のサブステータス変更
      if (orderNumberList2.size() > 0) {
        try {
          boolean updateOrderSubStatusFlg1 = RakutenCheckOrderManager.updateOrderSubStatus(apiAuth, subStatusId,orderNumberList2);

          if (updateOrderSubStatusFlg1 == false) {
            logger.error(String.format("OCCURRED UPDATE ORDER SUBSTATUS ERROR. TENPOCD: %s TENPONM: %s", apiAuth.getTenpocd(), apiAuth.getTenponm()));

          }
            
          // ログ出力
          for (String orderNumber : orderNumberList2) {
            logger.debug(String.format("Update Order SubStatus List OrderNumber: %s", orderNumber));
          }
          logger.info(String.format("Update Order SubStatus List OrderCount :%s", orderNumberList2.size()));
          
        } catch (Exception e) {
          e.printStackTrace();
          logger.error(String.format("ERROR EXISTS IN Update Order SubStatus PROCESS: %s", e.toString()));
          
          // 接続エラーでない場合は通知メッセージを作成
          if(escapeError(e)) {
            eMessage += "ERROR CODE: " + e.toString() + "\r\n";
            eMessage += systemError(apiAuth.getTenponm(), "サブステータス変更", 0);
            systemFlg = true;
          } else {
            logger.error("NETWORK ERROR.");
          }
          
          continue;
        }
      }
      
      /*--------------------------------------------------*/
      // エラーメッセージ取得
      if (!RakutenCheckOrderManager.updOrderError.isEmpty()) {
        apiFlg = true;
        
        for (String err : RakutenCheckOrderManager.getUpdOrderError()) {
        	eMessage = eMessage + err + "\r\n";
        }
      }
      logger.info(String.format("END PROCESSING TENPOCD :%s TENPONM :%s", apiAuth.getTenpocd(), apiAuth.getTenponm()));
    }
    
    /*--------------------------------------------------*/
    /* メッセージ送信                                   */
    /*--------------------------------------------------*/
    // エラーメッセージが存在する場合、slackにメッセージを送信
    if (!(eMessage.equals(""))) {
      logger.error(String.format("ERROR EXISTS IN THIS PROCESS"));
      LocalTime now = LocalTime.now();                        // 現在時刻
      LocalTime nightStart = LocalTime.parse("20:00:00");    // 送信先切り替え時刻(開始)
      LocalTime nightEnd = LocalTime.parse("08:00:00");      // 送信先切り替え時刻(終了)
      boolean sendMail = false;
      
      // 20時～8時でシステムエラーだった場合
      if (systemFlg == true && (now.isAfter(nightStart) || now.isBefore(nightEnd))) {
        logger.error(String.format("SEND TO ERROR CHANNEL AND NIGHT SHIFT CHANNNEL"));
        sendMail = sendMail(eMessage,1);
        
      // 8時～20時でシステム／APIエラーだった場合
      } else if ((systemFlg == true || apiFlg == true) && (now.isAfter(nightEnd) && now.isBefore(nightStart)) ){
        logger.error(String.format("SEND TO ERROR CHANNEL"));
        sendMail = sendMail(eMessage,0);
      }
      
      // 8時～20時でライセンスキーエラーだった場合
      if (liFlg == true  && (now.isBefore(nightStart) && now.isAfter(nightEnd))) {
        logger.error(String.format("SEND TO API CHANNEL"));
        sendMail = sendMail(eMessage,2);
      }
      
      if (sendMail == true) {
        logger.info("SUCCESS SEND MESSAGE TO SLACK CHANNEL.");
      } else {
        //TODO FIX ん、時間外エラーの場合に条件分岐にするべき。
        logger.info("FAIL SEND MESSAGE TO SLACK CHANNEL.");
      }
    }
    
    /*--------------------------------------------------*/
    logger.info("================================ FINISH RakutenCheckOrder SYSTEM ================================");
    System.exit(0);
  }
  
  
  /* エラーメッセージ作成 */
  private String systemError(String temponm, String process, int mFlg)throws IOException {
    String Message           = "";
    String normalMessage     = "以下店舗の"+ process +"時にエラーが発生しました。";
    String tenpoMessage      = "店舗：" + temponm;
    String hostMessage       = "端末：" + getHostName();
    String subStatusMessage  = "以下店舗にはサブステータス「******」が存在していないため、処理できませんでした。";
    String licenseMessage    = "以下店舗の店舗情報（ライセンスキー、サービスシークレット）が認証できませんでした。";
    String systemSupport     = "【システムエラー】システム部にご連絡ください。";
    String salesSupportS     = "【サブステータスID取得】サブステータスの追加・修正をお願いいたします。";
    String salesSupportL     = "【店舗認証】ライセンスキーおよびサービスシークレットの更新・修正をお願いいたします。";
    String endMessage = "------------------------------------------------------------------------------------------------------------------------";
    
    switch(mFlg) {
    /* システム部対応 */
    case 0:
      Message = normalMessage + "\r\n" + tenpoMessage + "\r\n" + hostMessage + "\r\n" + "\r\n" + systemSupport + "\r\n";
      break;
    /* 営業部対応(サブステータス) */
    case 1:
      Message = subStatusMessage + "\r\n" + tenpoMessage + "\r\n" + hostMessage + "\r\n" + "\r\n" + salesSupportS + "\r\n";
      break;
    /* 営業部対応(ライセンスキー) */
    case 2:
      Message = licenseMessage + "\r\n" + tenpoMessage + "\r\n" + hostMessage + "\r\n" + "\r\n" + salesSupportL + "\r\n";
      break;
    }
    
    Message += endMessage + "\r\n";
    return Message;
  }
  
  /* 通知有無判定 */
  private boolean escapeError(Exception e) {
    boolean eResult;
    
    if(!(e instanceof java.net.ConnectException) &&                     // ポート接続ができなかった場合
       !(e instanceof java.net.UnknownHostException) &&                 // ホスト(楽天)IPのDNS解決ができなかった場合
       !(e instanceof java.net.SocketTimeoutException) &&               // 通信がタイムアウトした場合
       !(e instanceof javax.net.ssl.SSLProtocolException) &&            // SSL通信が確立されなかった場合 
       !(e instanceof javax.net.ssl.SSLHandshakeException) &&           // ハンドシェイクが確立されなかった場合
       !(e instanceof com.fasterxml.jackson.core.JsonParseException)){  //「Page Not Found」等でJsonParseができなかった場合
      /* 上記以外のエラーはシステムエラーとして通知する */
      eResult = true;
    } else {
      /* 上記通信エラーの場合は通知を送らない */
      eResult = false;
    }
    return eResult;
  }
  
  /* メッセージ送信 */
  private boolean sendMail(String message, int mFlg) {
    boolean sendResult =false;
    MailModel mailModel = new MailModel();
    
    switch(mFlg) {
    /* エラーチャンネルへ送信 */
    case 0:
      mailModel.setMailFrom(ConstCom.MAIL_FROM);
      mailModel.setMailTo(ConstCom.MAIL_TO);              // 送信先メールアドレス
      mailModel.setMailCc("");
      mailModel.setMailSubject(ConstCom.MAIL_SUBJECT);   // タイトル
      mailModel.setMailText(message); // 本文
      break;
    /* エラー/夜勤用チャンネルへ送信 */
    case 1:
      mailModel.setMailFrom(ConstCom.MAIL_FROM);         // 送信元メールアドレス
      mailModel.setMailTo(ConstCom.MAIL_TO_NIGHT);       // 送信先メールアドレス
      mailModel.setMailCc(ConstCom.MAIL_TO);             // 送信先メールアドレス
      mailModel.setMailSubject(ConstCom.MAIL_SUBJECT);   // タイトル
      mailModel.setMailText(message);                    // 本文
      break;
    /* APIチャンネルへ送信 */
    case 2:
      mailModel.setMailFrom(ConstCom.MAIL_FROM);        // 送信元メールアドレス
      mailModel.setMailTo(ConstCom.MAIL_TO_API);        // 送信先メールアドレス
      mailModel.setMailCc("");
      mailModel.setMailSubject(ConstCom.MAIL_SUBJECT_API);  // タイトル
      mailModel.setMailText(message); // 本文
      break;
    }
    
    /* メール送信 */
    try {
      sendResult = RakutenCheckOrderSendMail.sendMail(mailModel);          
    } catch (Exception e) {
      e.printStackTrace();
      Logger logger = LoggerFactory.getLogger(RakutenCheckOrderService.class);
      logger.error("ERROR SEND MESSAGE TO SLACK: %s", e.toString());
    }
    
    return sendResult;
  }
  
  /* 実行端末名取得 */
  private static String getHostName() {
    try {
        return InetAddress.getLocalHost().getHostName();
    }catch (Exception e) {
      e.printStackTrace();
      Logger logger = LoggerFactory.getLogger(RakutenCheckOrderService.class);
      logger.error("ERROR GET HOST NAME: %s", e.toString());
    }
    return "UnknownHost";
}
}
