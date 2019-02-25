package im.adamant.android.dagger.activities;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.adamant.android.R;
import im.adamant.android.dagger.fragments.BottomLoginModule;
import im.adamant.android.dagger.fragments.FragmentScope;

import dagger.Module;
import im.adamant.android.helpers.HtmlHelper;
import im.adamant.android.ui.adapters.WelcomeCardsAdapter;
import im.adamant.android.ui.entities.WelcomeCard;
import im.adamant.android.ui.fragments.BottomLoginFragment;

@Module
public abstract class LoginScreenModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = {BottomLoginModule.class})
    public abstract BottomLoginFragment provideBottomFragment();

    @ActivityScope
    @Provides
    public static List<WelcomeCard> provideWelcomeCards(Context context) {
        WelcomeCard welcomeCard1 = new WelcomeCard(
                R.drawable.ic_welcome_card1,
                HtmlHelper.fromHtml(context.getString(R.string.welcome_card_1))
        );
        WelcomeCard welcomeCard2 = new WelcomeCard(
                R.drawable.ic_welcome_card2,
                HtmlHelper.fromHtml(context.getString(R.string.welcome_card_2))
        );
        WelcomeCard welcomeCard3 = new WelcomeCard(
                R.drawable.ic_welcome_card3,
                HtmlHelper.fromHtml(context.getString(R.string.welcome_card_3))
        );
        WelcomeCard welcomeCard4 = new WelcomeCard(
                R.drawable.ic_welcome_card4,
                HtmlHelper.fromHtml(context.getString(R.string.welcome_card_4))
        );
        WelcomeCard welcomeCard5 = new WelcomeCard(
                R.drawable.ic_welcome_card5,
                HtmlHelper.fromHtml(context.getString(R.string.welcome_card_5))
        );

        List<WelcomeCard> cards = new ArrayList<>();
        cards.add(welcomeCard1);
        cards.add(welcomeCard2);
        cards.add(welcomeCard3);
        cards.add(welcomeCard4);
        cards.add(welcomeCard5);

        return cards;
    }

    @ActivityScope
    @Provides
    public static WelcomeCardsAdapter provideWelcomeCardAdapter(List<WelcomeCard> cards) {
        return new WelcomeCardsAdapter(cards);
    }
}
