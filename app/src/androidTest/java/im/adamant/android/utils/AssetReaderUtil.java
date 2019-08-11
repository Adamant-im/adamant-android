package im.adamant.android.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class AssetReaderUtil {
    public static String asset(Context context, String assetPath) {
        try {
            InputStream inputStream = context.getAssets().open("network/" + assetPath);
            return inputStreamToString(inputStream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String inputStreamToString(InputStream inputStream, String charsetName) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charsetName))) {
            builder.append(
                    bufferedReader
                            .lines()
                            .collect(
                                    Collectors
                                            .joining(
                                                    System.lineSeparator()
                                            )
                            )
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return builder.toString();
    }
}
