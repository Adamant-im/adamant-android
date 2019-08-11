package im.adamant.android.core;

import com.google.gson.Gson;

import im.adamant.android.BuildConfig;
import im.adamant.android.core.entities.ServerNode;
import im.adamant.android.rx.ObservableRxList;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DefaultAdamantApiBuilderImpl extends AdamantApiBuilder {
    public DefaultAdamantApiBuilderImpl(ObservableRxList<ServerNode> nodes, Gson gson) {
        super(nodes, gson);
    }

    @Override
    protected void addInterceptors(OkHttpClient.Builder httpClient) {
        if (BuildConfig.DEBUG){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }
    }
}
