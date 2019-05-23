import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ChinaMobile {
    @Headers({
            "Host: sxapp.zj.chinamobile.com",
            "Accept: image/png,image/svg+xml,image/;q=0.8,video/;q=0.8,/;q=0.5",
            "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_4 like Mac OS X) " +
                    "AppleWebKit/605.1.15 (KHTML, like Gecko) " +
                    "Mobile/16D57 MicroMessenger/7.0.3(0x17000321) " +
                    "NetType/4G Language/zh_CN",
            "Accept-Language: zh-cn",
            "Accept-Encoding: gzip, deflate",
            "Referer: http://sxapp.zj.chinamobile.com/route/page/ywdj8",
            "Connection: keep-alive",
    })
    @GET("/home/code")
    Call<ResponseBody> verificationCode(
            @Query("p") String timestamp);


    @Headers({
            "Accept: application/json, text/javascript, */*; q=0.01",
            "Host: sxapp.zj.chinamobile.com",
            "Connection: keep-alive",
            "X-Requested-With: XMLHttpRequest",
            "Accept-Language: zh-cn",
            "Accept-Encoding: gzip, deflate",
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "Origin: http://sxapp.zj.chinamobile.com",
            "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_4 like Mac OS X) " +
                    "AppleWebKit/605.1.15 (KHTML, like Gecko) " +
                    "Mobile/16D57 MicroMessenger/7.0.3(0x17000321) " +
                    "NetType/4G Language/zh_CN",
            "Referer: http://sxapp.zj.chinamobile.com/route/page/ywdj8",
    })
    @FormUrlEncoded
    @POST("http://sxapp.zj.chinamobile.com/route/json/ywdj")
    Call<ResponseBody> query(
            @Field("data[code]") String code,
            @Field("data[phone]") String phone,
            @Field("data[yzm]") String yzm);
}