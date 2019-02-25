package im.adamant.android.ui.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.adamant.android.R;
import im.adamant.android.ui.entities.WelcomeCard;

public class WelcomeCardHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView textView;

    public WelcomeCardHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.list_item_welcome_card_iv_image);
        textView = itemView.findViewById(R.id.list_item_welcome_card_tv_text);
    }

    public void bind(WelcomeCard welcomeCard) {
        imageView.setImageResource(welcomeCard.getImageResource());
        textView.setText(welcomeCard.getText());
    }
}
