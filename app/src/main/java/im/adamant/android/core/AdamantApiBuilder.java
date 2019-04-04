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

public class AdamantApiBuilder {

    private ObservableRxList<ServerNode> nodes;
    private ServerNode currentServerNode;
    private Gson gson;

    public AdamantApiBuilder(ObservableRxList<ServerNode> nodes, Gson gson) {
        this.nodes = nodes;
        this.gson = gson;
    }

    public Observable<AdamantApi> build() {
        return build(randomServerSelect());
    }

    public Observable<AdamantApi> build(int index) {
        return Observable.fromCallable(() -> {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            if (BuildConfig.DEBUG){
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }

            if (currentServerNode != null){
                currentServerNode.setStatus(ServerNode.Status.CONNECTING);
            }

            currentServerNode = nodes.get(index);
            currentServerNode.setStatus(ServerNode.Status.CONNECTED);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(currentServerNode.getUrl() + BuildConfig.API_BASE)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

            return  retrofit.create(AdamantApi.class);
        });
    }

    private int randomServerSelect() {
        int index =  (int) Math.round(Math.floor(Math.random() * nodes.size()));
        if (index >= nodes.size()){index = nodes.size() - 1;}

        return index;
    }
}
