package im.adamant.android.helpers;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import im.adamant.android.BuildConfig;
import im.adamant.android.interactors.push.SupportedPushNotificationFacadeType;
import im.adamant.android.rx.ObservableRxList;
import im.adamant.android.core.entities.ServerNode;
import io.reactivex.disposables.Disposable;

public class Settings {
    private static final String NODES_KEY = "nodes_key";
    private static final String ACCOUNT_PASSPHRASE = "account_passphrase";
    private static final String KEY_PAIR_MUST_BE_STORED = "key_pair_must_be_stored";
    private static final String NOTIFICATION_TOKEN = "notification_token";
    private static final String ADDRESS_OF_NOTIFICATION_SERVICE = "address_of_notification_service";
    private static final String PUSH_NOTIFICATION_SERVICE = "push_notification_service";
    private static final String LAST_TRANSACTION_TIMESTAMP = "last_transaction_timestamp";
    private static final String UNSUBSCRIBE_PUSH_NOTIFICATION_TRANSACTION = "unsubscribe_fcm_service_transaction";

    private ObservableRxList<ServerNode> nodes = new ObservableRxList<>();
    private String accountPassphrase = "";
    private String accountPincode = "";
    private String accountSign = "";
    private boolean isKeyPairMustBeStored;
    private String notificationToken = "";
    private String addressOfNotificationService = "";
    private String unsubscribeFcmTransaction = "";
    private int lastTransactionTimestamp;
    private SupportedPushNotificationFacadeType pushNotificationFacadeType;

    private SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;

        accountPassphrase = this.preferences.getString(ACCOUNT_PASSPHRASE, "");
        isKeyPairMustBeStored = this.preferences.getBoolean(KEY_PAIR_MUST_BE_STORED, false);
        notificationToken = this.preferences.getString(NOTIFICATION_TOKEN, "");
        unsubscribeFcmTransaction = this.preferences.getString(UNSUBSCRIBE_PUSH_NOTIFICATION_TRANSACTION, "");
        addressOfNotificationService = this.preferences.getString(ADDRESS_OF_NOTIFICATION_SERVICE, BuildConfig.DEFAULT_NOTIFICATION_SERVICE_ADDRESS);
        pushNotificationFacadeType = SupportedPushNotificationFacadeType.valueOf(
                this.preferences.getString(
                        PUSH_NOTIFICATION_SERVICE,
                        SupportedPushNotificationFacadeType.DISABLED.name()
                )
        );
        lastTransactionTimestamp = this.preferences.getInt(LAST_TRANSACTION_TIMESTAMP, 0);

        loadNodes();
    }

    public void addNode(ServerNode node) {
        nodes.add(node);
        updateNodes();
    }

    public void resetNodesToDefault() {
        nodes.clear();
        List<ServerNode> serverNodeList = new ArrayList<>();
        for (String nodeUrl: getDefaultNodes()) {
            serverNodeList.add(new ServerNode(nodeUrl));
        }
        nodes.addAll(serverNodeList);
        updateNodes();
    }

    public void removeNode(ServerNode node) {
        if (nodes.contains(node)){
            nodes.remove(node);
            updateNodes();
        }
    }

    public ObservableRxList<ServerNode> getNodes() {
        return nodes;
    }

    public String getAccountPassphrase() {
        return accountPassphrase;
    }

    public void setAccountPassphrase(String accountPassphrase) {
        this.accountPassphrase = accountPassphrase;
        this.preferences
                .edit()
                .putString(ACCOUNT_PASSPHRASE, accountPassphrase)
                .apply();
    }

    public boolean isKeyPairMustBeStored() {
        return isKeyPairMustBeStored;
    }

    public void setKeyPairMustBeStored(boolean keyPairMustBeStored) {
        isKeyPairMustBeStored = keyPairMustBeStored;
        this.preferences
                .edit()
                .putBoolean(KEY_PAIR_MUST_BE_STORED, keyPairMustBeStored)
                .apply();
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
        this.preferences
                .edit()
                .putString(NOTIFICATION_TOKEN, notificationToken)
                .apply();
    }

    public String getAddressOfNotificationService() {
        return addressOfNotificationService;
    }

    public void setAddressOfNotificationService(String addressOfNotificationService) {
        this.addressOfNotificationService = addressOfNotificationService;
        this.preferences
                .edit()
                .putString(ADDRESS_OF_NOTIFICATION_SERVICE, addressOfNotificationService)
                .apply();
    }

    public SupportedPushNotificationFacadeType getPushNotificationFacadeType() {
        return pushNotificationFacadeType;
    }

    public void setPushNotificationFacadeType(SupportedPushNotificationFacadeType type) {
        this.pushNotificationFacadeType = type;
        this.preferences
                .edit()
                .putString(PUSH_NOTIFICATION_SERVICE, type.name())
                .apply();
    }

    public int getLastTransactionTimestamp() {
        return lastTransactionTimestamp;
    }

    public void setLastTransactionTimestamp(int lastTransactionTimestamp) {
        this.lastTransactionTimestamp = lastTransactionTimestamp;
        this.preferences
                .edit()
                .putInt(LAST_TRANSACTION_TIMESTAMP, lastTransactionTimestamp)
                .apply();
    }

    public String getUnsubscribeFcmTransaction() {
        return unsubscribeFcmTransaction;
    }

    public void setUnsubscribeFcmTransaction(String unsubscribeFcmTransaction) {
        this.unsubscribeFcmTransaction = unsubscribeFcmTransaction;
        this.preferences
                .edit()
                .putString(UNSUBSCRIBE_PUSH_NOTIFICATION_TRANSACTION, unsubscribeFcmTransaction)
                .apply();
    }

    private void updateNodes() {
        Disposable subscribe = nodes.getCurrentList()
                .map(ServerNode::getUrl)
                .toList()
                .subscribe((list) -> {
                    Set<String> set = new HashSet<>(list);
                    preferences
                            .edit()
                            .putStringSet(NODES_KEY, set)
                            .apply();
                });
    }

    private Set<String> getDefaultNodes() {
        Set<String> defaults = new HashSet<>();
        if (BuildConfig.TEST_NET) {
            defaults.add(BuildConfig.TEST_NET_DEFAULT_NODE_1);
        } else {
            defaults.add(BuildConfig.PROD_NET_DEFAULT_NODE_1);
            defaults.add(BuildConfig.PROD_NET_DEFAULT_NODE_2);
            defaults.add(BuildConfig.PROD_NET_DEFAULT_NODE_3);
            defaults.add(BuildConfig.PROD_NET_DEFAULT_NODE_4);
            defaults.add(BuildConfig.PROD_NET_DEFAULT_NODE_5);
            defaults.add(BuildConfig.PROD_NET_DEFAULT_NODE_6);
        }

        return defaults;
    }

    private void loadNodes() {
        Set<String> nodeUrls = preferences.getStringSet(NODES_KEY, getDefaultNodes());

        if (nodeUrls.size() == 0){
            nodeUrls = getDefaultNodes();
            preferences
                    .edit()
                    .putStringSet(NODES_KEY, nodeUrls)
                    .apply();
        }

        for (String nodeUrl : nodeUrls) {
            nodes.add(new ServerNode(nodeUrl));
        }
    }
}
