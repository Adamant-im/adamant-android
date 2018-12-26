package im.adamant.android.interactors.wallets;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.adamant.android.core.AdamantApiWrapper;
import im.adamant.android.core.encryption.Hex;
import im.adamant.android.core.exceptions.NotAuthorizedException;
import im.adamant.android.helpers.ChatsStorage;
import im.adamant.android.ui.entities.CurrencyTransferEntity;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LiskWalletFacade implements WalletFacade {
    private AdamantApiWrapper api;

    public LiskWalletFacade(AdamantApiWrapper api) {
        this.api = api;
    }

    @Override
    public BigDecimal getBalance() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getAddress() {
        try {
            return generateAddress();
        } catch (Exception ex) {
            return "error: " + ex.getMessage();
        }

    }

    @Override
    public SupportedWalletFacadeType getCurrencyType() {
        return SupportedWalletFacadeType.LSK;
    }

    @Override
    public String getTitle() {
        return "LISK WALLET";
    }

    @Override
    public int getPrecision() {
        return 8;
    }

    @Override
    public int getBackgroundLogoResource() {
        return 0;
    }

    @Override
    public void setChatStorage(ChatsStorage chatStorage) {

    }

    @Override
    public Single<List<CurrencyTransferEntity>> getLastTransfers() {
        return Single.just(new ArrayList<>());
    }

    @Override
    public boolean isAvailableAirdropLink() {
        return false;
    }

    @Override
    public int getAirdropLinkResource() {
        return 0;
    }

    @Override
    public String getAirdropLinkString() {
        return null;
    }

    @Override
    public boolean isSupportFundsSending() {
        return false;
    }

    @Override
    public Flowable<BigDecimal> getFee() {
        return Flowable.empty();
    }

    @Override
    public String getCurrencyAddress(String adamantAddress, String adamantPublicKey) {
        return null;
    }

    @Override
    public int getIconForEditText() {
        return 0;
    }

    @Override
    public boolean isSupportComment() {
        return false;
    }

    //Test address code

//    public static func address(fromPublicKey publicKey: String) -> String {
//        let bytes = SHA256(publicKey.hexBytes()).digest()
//        let identifier = byteIdentifier(from: bytes)
//        return "\(identifier)L"
//    }
//
//    internal static func byteIdentifier(from bytes: [UInt8]) -> String {
//        guard bytes.count >= 8 else { return "" }
//        let leadingBytes = bytes[0..<8].reversed()
//        let data = Data(bytes: Array(leadingBytes))
//        let value = UInt64(bigEndian: data.withUnsafeBytes { $0.pointee })
//        return "\(value)"
//    }

    private String generateAddress() throws NotAuthorizedException {
        if (!api.isAuthorized()) {throw new NotAuthorizedException("Not Authorized");}

        byte[] publicKey = api.getLiskKeyPair().getPublicKey();
        byte[] byteDigest = Hex.SHA256digest(publicKey);

        if (byteDigest.length < 8) {return "";}

        byte[] significantBytes = Arrays.copyOf(byteDigest,8);
        Hex.reverse(significantBytes);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(significantBytes);
        buffer.flip();//need flip
        long address = buffer.getLong();

        return address + "L";
    }


}
