package com.zxingbar.code.internetsssss.client;

import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import io.reactivex.Observable;
import retrofit2.http.Multipart;

public interface NetAllUrl
{
    String SERVICE_URL = "https://api.mtmin.com/";

    @POST("umi")
    @Multipart
    Observable<ReturnData<ReturnResultConfigInfos>> getAdConfigInfos(@PartMap Map<String,RequestBody> params);

    @POST("rs")
    @Multipart
    Observable<ReturnData<ReturnResultConfigInfos>> updateAdConfigInfos(@PartMap Map<String,RequestBody> params);
}