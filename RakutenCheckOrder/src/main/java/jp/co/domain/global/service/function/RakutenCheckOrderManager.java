package jp.co.domain.global.service.function;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.domain.global.bean.AuthenticationErrorResponse;
import jp.co.domain.global.bean.entity.ApiAuthorization;
import jp.co.domain.global.bean.rms.confirmUpdate.request.ConfirmOrderRequest;
import jp.co.domain.global.bean.rms.confirmUpdate.request.updateOrderSubStatusRequest;
import jp.co.domain.global.bean.rms.confirmUpdate.response.ConfirmOrderResponse;
import jp.co.domain.global.bean.rms.confirmUpdate.response.StatusModel;
import jp.co.domain.global.bean.rms.confirmUpdate.response.SubStatusModel;
import jp.co.domain.global.bean.rms.confirmUpdate.response.getSubStatusListResponse;
import jp.co.domain.global.bean.rms.confirmUpdate.response.updateOrderSubStatusResponse;
import jp.co.domain.global.bean.rms.resultsRespose.MessageModel;
import jp.co.domain.global.bean.rms.searchOrder.request.PaginationRequestModel;
import jp.co.domain.global.bean.rms.searchOrder.request.SearchOrderRequest;
import jp.co.domain.global.bean.rms.searchOrder.request.SortModel;
import jp.co.domain.global.bean.rms.searchOrder.response.SearchOrderResponse;
import jp.co.domain.global.util.ConstReq;
import jp.co.domain.global.util.ConstReq.OrderProgress;
import jp.co.domain.global.util.ConstReq.SearchDateType;
import jp.co.domain.global.util.ConstReq.SearchKeywordType;
import jp.co.domain.global.util.ConstReq.SortColumn;
import jp.co.domain.global.util.ConstReq.SortDirection;
import jp.co.domain.global.util.RakutenWebApiUtil;
import lombok.Data;

/**
 * マネージャークラス
 *
 * * --< CHANGE HISTORY >--
 *     X.XXXXXX    0000/00/00 ①XXXXXXXX
 */
@Component
@Data
public class RakutenCheckOrderManager {

  @Autowired
  ResourceLoader resourceLoader;

  Logger logger = LoggerFactory.getLogger(RakutenCheckOrderManager.class);

  private List<MessageModel> messageModelList = new ArrayList<MessageModel>();
  public List<String> updOrderError = new ArrayList<String>();
  public String responseJson = "";

  /**
   * サブステータスリスト取得
   *
   * @param apiAuth API認証情報
   * @param ldtNow  バッチ実行時間
   * @return サブステータスリスト
   * @throws IOException 入出力エラーが発生した場合
   */
  public long getSubStatusList(ApiAuthorization apiAuth, LocalDateTime ldtNow) throws IOException {
    long targetSubStatusID = 0;
    String requestJson = "";
    String targetSubStatusName = "追加料金対応";
    String targetSubStatusNameSumi = "【追加料金対応】";
    ObjectMapper mapper = new ObjectMapper();
    List<StatusModel> StatusList = new ArrayList<>();
    List<SubStatusModel> SubStatusList = new ArrayList<>();
    RakutenWebApiUtil apiUtil = new RakutenWebApiUtil();
    getSubStatusListResponse responseParam = new getSubStatusListResponse();
    AuthenticationErrorResponse error = new AuthenticationErrorResponse();

    // サブステータスリスト取得APIリクエスト実行
    apiUtil.getSubStatusList(apiAuth, requestJson);

    try {
      // 受注検索APIレスポンス(JSON)をデシリアライズ
      responseParam = mapper.readValue(apiUtil.getResponseJson(), getSubStatusListResponse.class);

      if (apiUtil.getStatusCode() == ConstReq.HTTP_OK) {
        if (responseParam.statusModelList != null) {
          StatusList.addAll(responseParam.statusModelList);

          for (StatusModel Status : StatusList) {
            if (Status.orderProgress == 100) {
              SubStatusList = Status.subStatusModelList;

              for (SubStatusModel SubStatus : SubStatusList) {
                // サブステータス名が"追加料金対応"または"【追加料金対応】"のサブステータスIDを取得
                if (SubStatus.subStatusName.equals(targetSubStatusName) || SubStatus.subStatusName.equals(targetSubStatusNameSumi)) {
                // サブステータス名に"追加料金対応"が含まれるサブステータスIDを取得
                // if (SubStatus.subStatusName.contains(targetSubStatusName)) {
                  targetSubStatusID = SubStatus.subStatusId;
                  break;
                }
              }
            }
          }
        }
      } else {
        for (MessageModel message : responseParam.messageModelList) {
          logger.error(String.format("ErrorType: %s, ErrorCode: %s, ErrorMessage: %s", message.messageType,message.messageCode, message.message));
        }
      }
    } catch (Exception e) {
      // 楽天ペイ受注API共通認証エラーレスポンス(JSON)をデシリアライズ
      error = mapper.readValue(apiUtil.getResponseJson(), AuthenticationErrorResponse.class);
      logger.error(String.format("ErrorCode: %s, ErrorMessage: %s", error.getResults().getErrorCode(),error.getResults().getMessage()));
      
      // 店舗情報が異なる場合
      if(error.getResults().getErrorCode().equals("ES01-01")) {
        targetSubStatusID = -1;
      }
     
    }
    
    return targetSubStatusID;
  }

  /**
   * 注文検索
   *
   * @param apiAuth API認証情報
   * @param ldtNow  バッチ実行時間
   * @return 注文番号リスト
   * @throws IOException 入出力エラーが発生した場合
   */
  public List<String> searchOrder(ApiAuthorization apiAuth, LocalDateTime ldtNow, int keywordFlg) throws IOException {

    long totalPages = 1;
    String startDatetime = "";
    String endDatetime = "";
    String requestJson = "";
    List<String> orderNumberList = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    RakutenWebApiUtil apiUtil = new RakutenWebApiUtil();
    SearchOrderRequest requestParam = new SearchOrderRequest();
    SearchOrderResponse responseParam = new SearchOrderResponse();
    AuthenticationErrorResponse error = new AuthenticationErrorResponse();

    // 検索範囲(TO)
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern(ConstReq.DATE_FORMAT);
    endDatetime = dtf.format(ldtNow);

    // 検索範囲(FROM)
    LocalDateTime ldtFrom = ldtNow.minusDays(ConstReq.SEARCH_RANGE);
    startDatetime = dtf.format(ldtFrom);

    for (int pageIdx = 1; pageIdx <= totalPages; pageIdx++) {

      // 受注検索APIリクエストパラメータ設定
      requestParam = createSearchOrderRequestParam(pageIdx, startDatetime, endDatetime, keywordFlg);

      // 受注検索APIリクエストパラメータをシリアライズ
      requestJson = mapper.writeValueAsString(requestParam);

      // 受注検索APIリクエスト実行
      apiUtil.searchOrder(apiAuth, requestJson);

      try {

        // 受注検索APIレスポンス(JSON)をデシリアライズ
        responseParam = mapper.readValue(apiUtil.getResponseJson(), SearchOrderResponse.class);

        if (apiUtil.getStatusCode() == ConstReq.HTTP_OK) {
          if (responseParam.orderNumberList != null) {
            orderNumberList.addAll(responseParam.orderNumberList);
          }

          totalPages = responseParam.paginationResponseModel.totalPages;

          if (pageIdx != totalPages) {
            TimeUnit.SECONDS.sleep(ConstReq.API_REQUEST_INTERVAL);
          }
        } else {
          for (MessageModel message : responseParam.messageModelList) {
            logger.error(String.format("ErrorType: %s, ErrorCode: %s, ErrorMessage: %s",message.messageType, message.messageCode, message.message));
          }
        }
      } catch (Exception e) {
        // 楽天ペイ受注API共通認証エラーレスポンス(JSON)をデシリアライズ
        error = mapper.readValue(apiUtil.getResponseJson(), AuthenticationErrorResponse.class);
        logger.error(String.format("ErrorCode: %s, ErrorMessage: %s", error.getResults().getErrorCode(),
            error.getResults().getMessage()));
      }
    }

    return orderNumberList;
  }

  /**
   * 楽天ペイ受注API 受注検索APIリクエストパラメータ生成
   *
   * @param requestPage   リクエストページ番号
   * @param startDatetime 期間検索開始日時
   * @param endDatetime   期間検索終了日時
   * @return 受注検索APIリクエストパラメータ
   */
  private SearchOrderRequest createSearchOrderRequestParam(long requestPage, String startDatetime, String endDatetime, int keywordFlg) {

    // 並び替え条件
    SortModel sortModel = new SortModel();
    sortModel.sortColumn = SortColumn.ORDER_DATETIME.getValue();
    sortModel.sortDirection = SortDirection.ASC.getValue();

    List<SortModel> sortModelList = new ArrayList<>();
    sortModelList.add(sortModel);

    // ページング条件
    PaginationRequestModel pagination = new PaginationRequestModel();
    pagination.requestRecordsAmount = ConstReq.MAX_SEARCH_ORDER_RECORDS_AMT;
    pagination.requestPage = requestPage;
    pagination.SortModelList = sortModelList;

    // 受注検索APIリクエストパラメータを生成
    SearchOrderRequest requestParam = new SearchOrderRequest();
    requestParam.orderProgressList = new ArrayList<>();
    requestParam.orderProgressList.add(OrderProgress.ORDER_CFM.getValue());
    requestParam.subStatusIdList = new ArrayList<>();
    requestParam.subStatusIdList.add(-1);  //①サブステータス未設定に限定
    requestParam.dateType = SearchDateType.ORDER_DATE.getValue();
    requestParam.startDatetime = startDatetime;
    requestParam.endDatetime = endDatetime;
    requestParam.searchKeywordType = SearchKeywordType.ITEM_NM.getValue();
    requestParam.paginationRequestModel = pagination;

    // キーワード検索分岐
    switch (keywordFlg) {
    case 0:
      break;
    case 1:
      requestParam.searchKeyword = ConstReq.KEYWORD_OPTION_KEYWORD1;
      break;
    case 2:
      requestParam.searchKeyword = ConstReq.KEYWORD_OPTION_KEYWORD2;
      break;
    }

    return requestParam;
  }

  /**
   * ステータス変更(注文確認)
   *
   * @param apiAuth API認証情報
   * @return 処理成否
   * @throws IOException
   */
  @SuppressWarnings("unused")
public boolean confirmOrder(ApiAuthorization apiAuth, List<String> orderNumberList) throws IOException {
    RakutenWebApiUtil apiUtil = new RakutenWebApiUtil();
    boolean ret = true;
    int cnt = 0;
    int orderNumberListSize = orderNumberList.size();
    List<String> updList = new ArrayList<>();
    
    updOrderError.clear();

    for (String orderNumber : orderNumberList) {
      try {
    	cnt++;
    	// orderNumberListを100件毎に分割
    	updList.add(orderNumber);
        // 100件ずつ実施
        if (cnt % 100 == 0 || cnt == orderNumberListSize) {
          TimeUnit.SECONDS.sleep(1);

          ConfirmOrderRequest confirmOrderRequest = new ConfirmOrderRequest();
          ConfirmOrderResponse confirmOrderResponse = new ConfirmOrderResponse();
          String requestJson = "";
          ObjectMapper mapper = new ObjectMapper();

          // リクエストパラメータ設定
          confirmOrderRequest.orderNumberList = updList;
          requestJson = mapper.writeValueAsString(confirmOrderRequest);

          try {
            // POST 注文情報取得API
            boolean gFlg = apiUtil.confirmOrder(apiAuth, requestJson);

            // 注文情報取得APIレスポンス(JSON)をデシリアライズ
            confirmOrderResponse = mapper.readValue(apiUtil.getResponseJson(), ConfirmOrderResponse.class);

            if (confirmOrderResponse.messageModelList.isEmpty()) {
              logger.error("updOrdShipAsyncReq Error(Retry).");
            }
            else {

              boolean mFlg = false;

              for (MessageModel messageModel : confirmOrderResponse.messageModelList) {
                  if (!messageModel.getMessageType().equals("INFO")) {
                    logger.error("店舗名: " + apiAuth.getTenponm() + ", 注文番号: " + messageModel.getOrderNumber() + ", エラー内容: " + messageModel.getMessage() + ", エラー種別:"+ messageModel.getMessageType() +", エラーコード:" + messageModel.getMessageCode());
                    
                    // 注文が既にキャンセルされている場合
                    if (messageModel.getMessageCode().equals("ORDER_EXT_API_CONFIRM_ORDER_ERROR_102")) {
                      logger.error("This Order May Have Been Canceled.");
                    
                    // 注文が既にステータス移動されている場合 
                    } else if (messageModel.getMessageCode().equals("ORDER_EXT_API_CONFIRM_ORDER_ERROR_103")) {
                      logger.error("This Order May Have Already Been Moved.");
                      
                    // 送料が未設定の場合
                    } else if (messageModel.getMessageCode().equals("ORDER_EXT_API_CONFIRM_ORDER_ERROR_105")) {
                      logger.error("This Order May Have Be An unset Amount.");
                      
                    } else {
                      updOrderError.add("以下店舗の注文番号でエラーが発生しました。");
                      updOrderError.add("店舗名: "       + apiAuth.getTenponm());
                      updOrderError.add("注文番号: "     + messageModel.getOrderNumber());
                      updOrderError.add("エラー内容: "   + messageModel.getMessage());
                      updOrderError.add("エラー種別:"    + messageModel.getMessageType());
                      updOrderError.add("エラーコード:" + messageModel.getMessageCode());
                      updOrderError.add("\r\n");
                      mFlg = true;
                      
                    }
                  }
              }
              if (mFlg == true) {
                updOrderError.add("【ステータス変更】エラー内容を確認し、該当する注文番号について対応をお願いいたします。");
                updOrderError.add("------------------------------------------------------------------------------------------------------------------------");
              }
            }
          } catch (Exception e) {
            logger.error(String.format("confirmOrder Exception: %s", e.toString()));
            ret = false;
          } finally {
            // 初期化
            updList.clear();
          }
        }
      } catch (Exception e) {
        logger.error(String.format("confirmOrder Exception: %s", e.toString()));
        ret = false;
      }
    }
    return ret;
  }

  /**
   * サブステータス変更(追加料金対応)
   *
   * @param apiAuth API認証情報
   * @return 処理成否
   * @throws IOException
   */
  @SuppressWarnings("unused")
public boolean updateOrderSubStatus(ApiAuthorization apiAuth, long subStatusId,List<String> orderNumberList) throws IOException {
	RakutenWebApiUtil apiUtil = new RakutenWebApiUtil();
	boolean ret = true;
    int cnt = 0;
    int orderNumberListSize = orderNumberList.size();
    List<String> updList = new ArrayList<>();
    
    updOrderError.clear();

    for (String orderNumber : orderNumberList) {
      try {
    	cnt++;
    	// orderNumberListを100件毎に分割
    	updList.add(orderNumber);
        // 100件ずつ実施
        if (cnt % 100 == 0 || cnt == orderNumberListSize) {
          TimeUnit.SECONDS.sleep(1);
          
          updateOrderSubStatusRequest updateOrderSubStatusRequest = new updateOrderSubStatusRequest();
          updateOrderSubStatusResponse updateOrderSubStatusResponse = new updateOrderSubStatusResponse();
          String requestJson = "";
          ObjectMapper mapper = new ObjectMapper();

          // リクエストパラメータ設定
          updateOrderSubStatusRequest.orderNumberList = updList;
          updateOrderSubStatusRequest.subStatusId = subStatusId;
          requestJson = mapper.writeValueAsString(updateOrderSubStatusRequest);

          try {
            // POST 注文情報取得API
            boolean gFlg = apiUtil.updateOrderSubStatus(apiAuth, requestJson);

            // 注文情報取得APIレスポンス(JSON)をデシリアライズ
            updateOrderSubStatusResponse = mapper.readValue(apiUtil.getResponseJson(),updateOrderSubStatusResponse.class);


            if (updateOrderSubStatusResponse.messageModelList.isEmpty()) {
                logger.error("updateOrderSubStatusResponse Error(Empty).");
            } else {

                boolean mFlg = false;

                for (MessageModel messageModel : updateOrderSubStatusResponse.messageModelList) {
                    if (!messageModel.getMessageType().equals("INFO")) {
                      logger.error("店舗名: " + apiAuth.getTenponm() + ", 注文番号: " + messageModel.getOrderNumber() + ", エラー内容: " + messageModel.getMessage() + ", エラー種別:"+ messageModel.getMessageType() +", エラーコード:" + messageModel.getMessageCode());
                      
                      // 注文が既にキャンセルされている場合
                      if (messageModel.getMessageCode().equals("ORDER_EXT_API_UPDATE_ORDERSUBSTATUS_ERROR_101")) {
                        logger.error("This Order May Have Been Canceled.");
                        
                      // 注文サブステータスIDが異なっている場合
                      } else if (messageModel.getMessageCode().equals("ORDER_EXT_API_UPDATE_ORDERSUBSTATUS_ERROR_102")) {
                        logger.error("The Specified Substatus ID May Be Wrong.");
                        
                      // 他店舗の注文が混ざっている場合（楽天API側の不具合？）
                      } else if (messageModel.getMessageCode().equals("ORDER_EXT_API_UPDATE_ORDERSUBSTATUS_ERROR_016")) {
                        logger.error("The Orders From Other Stores May Be Behind.");
                        
                      } else {
                        updOrderError.add("以下店舗の注文番号でエラーが発生しました。");
                        updOrderError.add("店舗名: "       + apiAuth.getTenponm());
                        updOrderError.add("注文番号: "     + messageModel.getOrderNumber());
                        updOrderError.add("エラー内容: "   + messageModel.getMessage());
                        updOrderError.add("エラー種別:"    + messageModel.getMessageType());
                        updOrderError.add("エラーコード:"  + messageModel.getMessageCode());
                        updOrderError.add("\r\n");
                        mFlg = true;
                        
                      }
                    }
                }
                if (mFlg == true) {
                  updOrderError.add("【サブステータス変更】エラー内容を確認し、該当する注文番号について対応をお願いいたします。");
                  updOrderError.add("------------------------------------------------------------------------------------------------------------------------");                 
                }
            }
      } catch (Exception e) {
        logger.error(String.format("updateOrderSubStatus Exception: %s", e.toString()));
        ret = false;
      } finally {
        // 初期化
        updList.clear();
      }
    }
  } catch (Exception e) {
    logger.error(String.format("updateOrderSubStatus Exception: %s", e.toString()));
    ret = false;
  }
    }
    return ret;
  }

}
