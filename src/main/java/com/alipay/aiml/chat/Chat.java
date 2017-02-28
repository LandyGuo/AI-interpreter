package com.alipay.aiml.chat;

import com.alipay.aiml.bot.Bot;
import com.alipay.aiml.consts.AimlConst;
import com.alipay.aiml.providers.Provider;

/**
 * Created by mujian on 6/18/15.
 *
 * @author mujian
 */
public class Chat {
    private final static String DEFAULT_NICKNAME = "ChatRobot";
    private final Bot bot;
    private final Provider provider;
    private String nickname = DEFAULT_NICKNAME;
    private ChatState state;

    public Chat(Bot bot, Provider provider) {
        this.bot = bot;
        this.provider = provider;
    }

    public void start() {
        provider.write("Hello! Welcome to chat with " + bot.getName() + ".\n");
        state = new ChatState(nickname);

        String message;
        while (true) {
            message = read();
            String response = bot.multisentenceRespond(message, state);
            state.newState(message, response);
            write(response.trim());
        }
    }


    private String read() {
        provider.write(nickname + ": ");
        String textLine = provider.read();
        return textLine == null || textLine.isEmpty() ? AimlConst.null_input : textLine.trim();
    }

    private void write(String message) {
        provider.write(bot.getName() + ": " + message + "\n");
    }
}
