package com.baeldung.telegram;

import static com.baeldung.telegram.Constants.ACCOUNT_START_TEXT;
import static com.baeldung.telegram.UserState.AWAITING_ACCOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;

public class ResponseAccountHandler {
    private SilentSender sender = null;
    private final Map<Long, UserState> chatStates;
    private UserRepository userRepository;
    private AccountRepository accountRepository;
    
    public ResponseAccountHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
        userRepository = SpringUtils.getBean(UserRepository.class);
        accountRepository = SpringUtils.getBean(AccountRepository.class);
    }

	public void replyToStart(MessageContext ctx) {
    	long chatId = ctx.chatId();
    	Long uid = ctx.user().getId();
    	SendMessage message = new SendMessage();
        message.setChatId(chatId);
    	
        User user = userRepository.findById(uid).orElse(null);
    	if (user == null) {
    		User userNew = new User();
    		userNew.setUid(uid);
            userRepository.save(userNew);
            
            message.setText(ACCOUNT_START_TEXT);
            sender.execute(message);
    	} else {
//    		DateTime beginDate = DateUtil.beginOfDay(DateUtil.date());
//    		DateTime endDate = DateUtil.endOfDay(DateUtil.date());
//    		List<Account> accountList = accountRepository.findAllByCreatedDateBetween(beginDate.toJdkDate(), endDate.toJdkDate());
    		String accountInfo = getAccount(chatId);
    		
    		message.setText(accountInfo);
            sender.execute(message);
    	}
        
        chatStates.put(chatId, AWAITING_ACCOUNT);
    }

	private String getAccount(long chatId) {
		// TODO Auto-generated method stub
		
		List<Account> accountList = accountRepository.findAllByChatid(chatId);
		List<Account> accountInList = new ArrayList<Account>();
		List<Account> accountOutList = new ArrayList<Account>();
		String replyAccountInTxt = "";
		String replyAccountOutTxt = "";
		BigDecimal totalIn = new BigDecimal("0.00");
		BigDecimal totalOut = new BigDecimal("0.00");
		for (Account account: accountList) {
			if (account.getIncome().floatValue() > 0) {
				accountInList.add(account);
				totalIn = totalIn.add(account.getIncome());
				replyAccountInTxt += StringUtils.format("{}   {} {}", DateUtil.format(account.getCreatedDate(), "HH:mm:ss"), account.getIncome(), "\r\n");
			} else {
				accountOutList.add(account);
				totalOut = totalOut.add(account.getIncome());
				replyAccountOutTxt += StringUtils.format("{}   {} {}", DateUtil.format(account.getCreatedDate(), "HH:mm:ss"), account.getIncome(), "\r\n");
			}
		}
		String replyAccountTxt = StringUtils.format("-------普通模式-------{}", "\r\n") +
		StringUtils.format("{}  日小记{}", DateUtil.today(), "\r\n") + 
		StringUtils.format("入款：  {} 笔{}", accountInList.size(), "\r\n") + 
		replyAccountInTxt + 
		StringUtils.format("下发：  {} 笔{}", accountInList.size(), "\r\n") +
		replyAccountOutTxt+
		StringUtils.format("--------------------------{}", "\r\n") +
		StringUtils.format("当前费率：{}  %{}", 0.00 , "\r\n") +
	    StringUtils.format("入款金额：{}  %{}", totalIn , "\r\n") +
	    StringUtils.format("应下金额：{}  %{}", totalIn , "\r\n") +
	    StringUtils.format("已下金额：{}  %{}", totalOut , "\r\n") +
	    StringUtils.format("未下金额：{}  %{}", totalIn.add(totalOut)  , "\r\n") +
	    StringUtils.format("{}", "\r\n") + 
	    StringUtils.format("手续费用： 0.00 元{}", "\r\n");
		
		
		return replyAccountTxt;
	}
	
	public void replyToAccountButtons(long chatId, org.telegram.telegrambots.meta.api.objects.User user, Message message) {
	    if (message.getText().equalsIgnoreCase("结束记账")) {
	        stopChat(chatId);
	    }

	    switch (chatStates.get(chatId)) {
	        case AWAITING_ACCOUNT -> replyToAccount(chatId, user, message);
	        default -> unexpectedMessage(chatId);
	    }
	}
	
	private void replyToAccount(long chatId, org.telegram.telegrambots.meta.api.objects.User user, Message message) {
	    if (message.getText().matches("[-+z]{1}\\d+(?:\\.\\d+)?")) {
	    	String op =  message.getText().substring(0, 1);
	    	if (op.equals("z")) {
	    		String txt1 = doRateCacul(chatId, "z", new BigDecimal(message.getText().substring(1)));
	    		SendMessage sendMessage = new SendMessage();
	            sendMessage.setChatId(chatId);
	            sendMessage.setText(txt1);
	            
	            sender.execute(sendMessage);
	    	} else {
	    		doAccount(chatId, user.getId(), new BigDecimal(message.getText()));
	    		
	    		SendMessage sendMessage = new SendMessage();
	            sendMessage.setChatId(chatId);
	            sendMessage.setText(getAccount(chatId));
	            
	            sender.execute(sendMessage);
	    	}
	    } else if (message.getText().matches("\\d+(?:\\.\\d+)?u$")) {
	    	String txt2 = doRateCacul(chatId, "u", new BigDecimal(message.getText().substring(0, message.getText().length() - 1)));
	    	SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(txt2);
            
            sender.execute(sendMessage);
	    } else {
	    	unexpectedAccountMessage(chatId);
	    }
	}
	
	private String doRateCacul(long chatId, String flag, BigDecimal bigDecimal) {
		// TODO Auto-generated method stub
		String url = "https://www.okx.com/api/v5/market/exchange-rate";//指定URL
		Map<String, Object> map = new HashMap<>();//存放参数
		map.put("A", 100);
		map.put("B", 200);
		HashMap<String, String> headers = new HashMap<>();//存放请求头，可以存放多个请求头
		headers.put("xxx", "xxxx");
		//发送get请求并接收响应数据
		Double rate = 0.00d;
		try {
			//.addHeaders(headers).form(map)
			String result= HttpUtil.createGet(url).execute().body();
			OkxResp OkxRespObj = JSONObject.parseObject(result, OkxResp.class);
			
			if (OkxRespObj.getCode().equals("0")) {
				rate = Double.valueOf(OkxRespObj.getData().get(0).getUsdCny()); 
			} else  {
				System.out.print(result);
			}
		} catch (Exception e) {
            System.out.print(e.getMessage());
        }
		
		//发送post请求并接收响应数据
//		String result= HttpUtil.createPost(url).addHeaders(headers).form(map).execute().body();

		String returnStr = "7.36";
		if ("u".equals(flag)) {
			//https://www.okx.com/api/v5/market/exchange-rate
			returnStr = 
					StringUtils.format("欧易(okx) USDT实时汇率 {}", "\r\n") +
					StringUtils.format("--------------------------{}", "\r\n") +
					StringUtils.format("{}      XXXXX{}", rate , "\r\n") +
					StringUtils.format("--------------------------{}", "\r\n") +
					StringUtils.format("实时价格（三档）： {}", "\r\n") +
					StringUtils.format("--------------------------{}", "\r\n") +
				    StringUtils.format("{} 元 / {} = {} USDT{}", bigDecimal, rate, bigDecimal.doubleValue() /rate  , "\r\n");
		} else {
			//https://www.okx.com/api/v5/market/exchange-rate
			returnStr = 
					StringUtils.format("欧易(okx) USDT实时汇率 {}", "\r\n") +
					StringUtils.format("--------------------------{}", "\r\n") +
					StringUtils.format("{}      XXXXX{}", rate , "\r\n") +
					StringUtils.format("--------------------------{}", "\r\n") +
					StringUtils.format("实时价格（三档）： {}", "\r\n") +
					StringUtils.format("--------------------------{}", "\r\n") +
				    StringUtils.format("{} USDT * {} = {} 元{}", bigDecimal, rate, bigDecimal.doubleValue() *rate  , "\r\n");
			
		}
		
		return returnStr;
	}

	private void doAccount(long chatId, Long uid, BigDecimal income) {
		// TODO Auto-generated method stub
		Account account = new Account();
		account.setUid(uid);
		account.setChatid(chatId);
		account.setIncome(income);
		account.setCreatedDate(DateUtil.date());
		accountRepository.save(account);
	}
	private void unexpectedAccountMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("记账指令错误. 例如发送 +100 或者 -100");
        sender.execute(sendMessage);
    }
    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("I did not expect that.");
        sender.execute(sendMessage);
    }

	private void stopChat(long chatId) {
	    SendMessage sendMessage = new SendMessage();
	    sendMessage.setChatId(chatId);
	    sendMessage.setText("感谢您的使用.再见!\n发送 开始记账  再次开始！");
	    chatStates.remove(chatId);
	    sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
	    sender.execute(sendMessage);
	}

	public boolean userIsActive(Long chatId) {
	    return chatStates.containsKey(chatId);
	}
	
	
}




