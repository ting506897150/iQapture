package com.example.vcserver.iqapture.config;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * service-接口
 */

public interface HttpPostService {
    //Post请求后台数据
//    @FormUrlEncoded
    @POST("cip/register/LoginByMobile")//AnalytiQs/Interface/SignIn   api/account/SingIn
    Observable<String> login(@Query("username") String username, @Query("password") String password);

    @GET("Intelligence/API/Dataset/GetDataset")
    Observable<String> dataset(@Query("companyId") int companyId, @Query("userId") int userId, @Query("folderId") int folderId);

    @GET("Intelligence/API/Capture/GetRecord")
    Observable<String> filled(@Query("companyId") int companyId, @Query("userId") int userId, @Query("datasetId") int datasetId);

    @GET("Intelligence/api/Capture/GetQuestions")
    Observable<String> question(@Query("userId") int userId,@Query("companyId") int companyId,@Query("datasetId") int datasetId,@Query("recordId") int recordId,@Query("page") int page,@Query("row") int row);

    @GET("Intelligence/api/capture/EditQuestion")
    Observable<String> editquestion(@Query("question") String question);

    @FormUrlEncoded
    @POST("OpenBook/IOSAPI/AddImg")
    Observable<String> imageadd(@Field("UserID") int UserID, @Field("Image") String Image,@Field("FileName") String FileName);

    @GET("OpenBook/IOSAPI/GetImg")
    Observable<String> imageshow(@Query("ImgID") String ImgID);
}