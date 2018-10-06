package im.adamant.android.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;

import im.adamant.android.Constants;
import im.adamant.android.ui.LoginScreen;

import static android.app.Activity.RESULT_OK;

public class QrCodeHelper {

    public String parse(InputStream imageStream) {
        final Bitmap bMap = BitmapFactory.decodeStream(imageStream);
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> tmpHintsMap = new EnumMap<DecodeHintType, Object>(
                DecodeHintType.class);
        tmpHintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        tmpHintsMap.put(DecodeHintType.POSSIBLE_FORMATS,
                EnumSet.allOf(BarcodeFormat.class));
        tmpHintsMap.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);

        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap, tmpHintsMap);
            contents = result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }

        return contents;
    }

    public File makeImageFile(String prefix) {
        File imageDirectory = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                "adamant"
        );

        if(!imageDirectory.exists()){
            boolean created = imageDirectory.mkdirs();
            if (!created){
                LoggerHelper.e("MakeQrFile", "Dir " + imageDirectory.getAbsolutePath() + " not created!");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(System.currentTimeMillis());

        return new File(imageDirectory, prefix + timeStamp + ".png");
    }


    public void registerImageInGallery(Context ctx, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    public String parseActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        String qrCodeString = "";
        if (data == null){return qrCodeString;}

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case Constants.SCAN_QR_CODE_RESULT: {
                    if (data.getData() == null) {return qrCodeString;}

                    String qrCode = data.getData().toString();

                    if (qrCode != null){
                        qrCodeString = qrCode;
                    }
                }
                break;

                case Constants.IMAGE_FROM_GALLERY_SELECTED_RESULT: {
                    if (data.getData() == null) {return qrCodeString;}

                    final Uri imageUri = data.getData();
                    try {
                        final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                        String qrCode = parse(imageStream);

                        if (qrCode != null){
                            qrCodeString = qrCode;
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }

        return qrCodeString;
    }
}
