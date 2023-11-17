package com.baeldung.telegram;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;
import static org.telegram.abilitybots.api.util.AbilityUtils.getUser;

import java.util.function.BiConsumer;

import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;


public class AccountBot extends AbilityBot {

    private final ResponseAccountHandler responseAccountHandler;

    public AccountBot(String name, String token , DefaultBotOptions botOptions) {
        super(token, name, botOptions);
        responseAccountHandler = new ResponseAccountHandler(silent, db);
    }

public Ability startBot() {
    return Ability
      .builder()
      .name("开始记账")
      .info(Constants.ACCOUNT_START_DESCRIPTION)
      .locality(ALL)
      .privacy(PUBLIC)
      .action(ctx -> responseAccountHandler.replyToStart(ctx))
      .build();
}

public Reply replyToButtons() {
    BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseAccountHandler.replyToAccountButtons(getChatId(upd), getUser(upd), upd.getMessage());
    return Reply.of(action, Flag.TEXT,upd -> responseAccountHandler.userIsActive(getChatId(upd)));
}

@Override
public long creatorId() {
    return 1L;
}
}
