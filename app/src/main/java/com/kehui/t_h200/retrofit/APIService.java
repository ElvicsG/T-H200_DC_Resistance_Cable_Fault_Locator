package com.kehui.t_h200.retrofit;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by jwj on 2017/12/19.
 * 请求网络统一接口
 */

public interface APIService {
    //api接口
    @FormUrlEncoded
    @POST("/Api/App/{p}")
    public Call<String> api(@Path("p") String p, @Field("data") String json);


}
