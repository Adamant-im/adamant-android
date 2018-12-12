package im.adamant.android.core.retrofit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import im.adamant.android.core.entities.Transaction;
import im.adamant.android.core.entities.transaction_assets.NotUsedAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionChatAsset;
import im.adamant.android.core.entities.transaction_assets.TransactionStateAsset;

public class AdamantTransactonTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return type.getRawType() == Transaction.class
                ? (TypeAdapter<T>) customizeMyClassAdapter(gson)
                : null;
    }

    private <T> TypeAdapter<Transaction> customizeMyClassAdapter(Gson gson) {
        final TypeAdapter<Transaction<TransactionChatAsset>> chatAssetDelegate = gson.getDelegateAdapter(this, new TypeToken<Transaction<TransactionChatAsset>>() {});
        final TypeAdapter<Transaction<TransactionStateAsset>> stateAssetDelegate = gson.getDelegateAdapter(this, new TypeToken<Transaction<TransactionStateAsset>>() {});
        final TypeAdapter<Transaction<NotUsedAsset>> withoutAssetDelegate = gson.getDelegateAdapter(this, new TypeToken<Transaction<NotUsedAsset>>() {});

        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<Transaction>() {
            @Override
            public void write(JsonWriter out, Transaction value) throws IOException {
                JsonElement tree = null;
                switch (value.getType()){
                    case Transaction.CHAT_MESSAGE: {
                        tree = chatAssetDelegate.toJsonTree(value);
                    }
                    break;
                    case Transaction.STATE: {
                        tree = stateAssetDelegate.toJsonTree(value);
                    }
                    break;
                    case Transaction.SEND: {
                        tree = withoutAssetDelegate.toJsonTree(value);
                    }
                    break;
                    default: {
                        tree = withoutAssetDelegate.toJsonTree(value);
                    }
                }

                if (tree != null){
                    elementAdapter.write(out, tree);
                }
            }

            @Override
            public Transaction read(JsonReader in) throws IOException {
                JsonElement tree = elementAdapter.read(in);
                JsonObject jsonObject = tree.getAsJsonObject();
                JsonElement typeElement = jsonObject.get("type");
                if (typeElement == null) {throw new IOException("Empty transaction type!");}

                int type = typeElement.getAsInt();

                switch (type) {
                    case Transaction.CHAT_MESSAGE: {
                        return chatAssetDelegate.fromJsonTree(tree);
                    }
                    case Transaction.STATE: {
                        return stateAssetDelegate.fromJsonTree(tree);
                    }
                    case Transaction.SEND: {
                        return withoutAssetDelegate.fromJsonTree(tree);
                    }
                    default: {
                        return withoutAssetDelegate.fromJsonTree(tree);
                    }
                }
            }
        };
    }
}
