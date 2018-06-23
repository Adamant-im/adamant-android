package im.adamant.android.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class QrCodeHelper {

    public String parse(InputStream imageStream) {
        final Bitmap bMap = BitmapFactory.decodeStream(imageStream);
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap);
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
                Log.e("MakeQrFile", "Dir " + imageDirectory.getAbsolutePath() + " not created!");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(System.currentTimeMillis());

        return new File(imageDirectory, prefix + timeStamp + ".png");
    }

}
