package im.adamant.android.ui.navigators;

import android.app.Activity;
import android.content.Intent;

import im.adamant.android.Screens;
import im.adamant.android.ui.SplashScreen;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;

public abstract class DefaultNavigator implements Navigator {
    protected Activity attachedActivity;

    public DefaultNavigator(Activity attachedActivity) {
        this.attachedActivity = attachedActivity;
    }

    @Override
    public void applyCommands(Command[] commands) {
        for (Command command : commands){
            defaultApply(command);
        }
    }

    private void defaultApply(Command command) {
        if (command instanceof Forward) {
            Forward forward = (Forward) command;
            switch (forward.getScreenKey()) {
                case Screens.SPLASH_SCREEN: {
                    Intent intent = new Intent(attachedActivity.getApplicationContext(), SplashScreen.class);
                    attachedActivity.startActivity(intent);
                    attachedActivity.finish();
                }
                break;

                default:
                    forward((Forward) command);
            }
        } else if (command instanceof SystemMessage) {
            message((SystemMessage) command);
        } else if (command instanceof Back) {
            back((Back) command);
        } else if (command instanceof BackTo) {
            backTo((BackTo) command);
        } else if (command instanceof Replace) {
            replace((Replace) command);
        }
    }

    protected abstract void forward(Forward forwardCommand);
    protected abstract void back(Back backCommand);
    protected abstract void backTo(BackTo backToCommand);
    protected abstract void message(SystemMessage systemMessageCommand);
    protected abstract void replace(Replace replaceCommand);

}
