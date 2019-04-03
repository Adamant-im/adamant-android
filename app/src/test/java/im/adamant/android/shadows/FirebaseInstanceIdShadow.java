package im.adamant.android.shadows;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.robolectric.annotation.Implements;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import im.adamant.android.TestConstants;

@Implements(FirebaseInstanceId.class)
public class FirebaseInstanceIdShadow {
    @NonNull
    public Task<InstanceIdResult> getInstanceId() {

        final InstanceIdResult fakeResult = new InstanceIdResult() {
            @NonNull
            @Override
            public String getId() {
                return TestConstants.FAKE_FCM_ID;
            }

            @NonNull
            @Override
            public String getToken() {
                return TestConstants.FAKE_FCM_TOKEN;
            }
        };

        return new Task<InstanceIdResult>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public InstanceIdResult getResult() {
                return fakeResult;
            }

            @Nullable
            @Override
            public <X extends Throwable> InstanceIdResult getResult(@NonNull Class<X> aClass) throws X {
                return fakeResult;
            }

            @Nullable
            @Override
            public Exception getException() {
                return null;
            }

            @NonNull
            @Override
            public Task<InstanceIdResult> addOnSuccessListener(@NonNull OnSuccessListener<? super InstanceIdResult> onSuccessListener) {
                onSuccessListener.onSuccess(fakeResult);
                return this;
            }

            @NonNull
            @Override
            public Task<InstanceIdResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super InstanceIdResult> onSuccessListener) {
                onSuccessListener.onSuccess(fakeResult);
                return this;
            }

            @NonNull
            @Override
            public Task<InstanceIdResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super InstanceIdResult> onSuccessListener) {
                onSuccessListener.onSuccess(fakeResult);
                return this;
            }

            @NonNull
            @Override
            public Task<InstanceIdResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<InstanceIdResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<InstanceIdResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }
        };
    }
}
