package im.adamant.android.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import im.adamant.android.R;
import im.adamant.android.avatars.AvatarGenerator;
import im.adamant.android.core.AdamantApiWrapper;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {

    @Inject
    AdamantApiWrapper api;

    @Inject
    AvatarGenerator avatarGenerator;

    @BindView(R.id.fragment_bottom_navigation_iv_avatar)
    ImageView avatar;

    @BindView(R.id.fragment_bottom_navigation_tv_address)
    TextView address;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
        ButterKnife.bind(this, view);

        if (api.isAuthorized()){
            avatarGenerator.buildAvatar(
                        api.getKeyPair().getPublicKeyString().toLowerCase(),
                        getResources().getDimension(R.dimen.fragment_bottom_navigation_avatar_size),
                        getActivity(),
                        true
                ).subscribe(bitmap -> {
                    avatar.setImageBitmap(bitmap);
            });

            address.setText(api.getAccount().getAddress());
        }

        return view;
    }
}
