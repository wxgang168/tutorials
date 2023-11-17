package com.baeldung.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;

@Configuration
public class BotConfig {

    @Value("${xiaok168bot.token}")
    private static String botToken;

    @Value("${xiaok168bot.name}")
    private static String botName;
    
    @Value("${xiaok168bot.proxyhost}")
    private static String proxyHost;

    @Value("${xiaok168bot.proxyport}")
    private static Integer proxyPort;
 // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
    @Value("${xiaok168bot.proxytype}")
    private static ProxyType proxyType;

	public static String getBotToken() {
		return botToken;
	}

	public void setBotToken(String botToken) {
		BotConfig.botToken = botToken;
	}

	public static String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		BotConfig.botName = botName;
	}

	public static String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		BotConfig.proxyHost = proxyHost;
	}

	public static Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		BotConfig.proxyPort = proxyPort;
	}

	public static ProxyType getProxyType() {
		return proxyType;
	}

	public void setProxyType(ProxyType proxyType) {
		BotConfig.proxyType = proxyType;
	}
}
