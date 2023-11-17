package com.baeldung.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import jakarta.annotation.Resource;

@SpringBootApplication
public class SpringBootTelegramApplication {
	
	
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringBootTelegramApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            
         // Set up Http proxy
            DefaultBotOptions botOptions = new DefaultBotOptions();            
            
            String name = ctx.getEnvironment().getProperty("xiaok168bot.name");
            String token = ctx.getEnvironment().getProperty("xiaok168bot.token");
            
            String proxyhost = ctx.getEnvironment().getProperty("xiaok168bot.proxyhost");
            String proxyport = ctx.getEnvironment().getProperty("xiaok168bot.proxyport");
            String proxytype = ctx.getEnvironment().getProperty("xiaok168bot.proxytype");
            botOptions.setProxyHost(proxyhost);
			botOptions.setProxyPort(Integer.valueOf(proxyport));
            // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
            botOptions.setProxyType(DefaultBotOptions.ProxyType.valueOf(proxytype));
            
//            Environment environment, DefaultBotOptions botOptions
            // Register your newly created AbilityBot
            AccountBot accountBot = new AccountBot(name, token, botOptions);
            
            botsApi.registerBot(accountBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
