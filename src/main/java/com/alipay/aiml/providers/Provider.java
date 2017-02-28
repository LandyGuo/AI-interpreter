package com.alipay.aiml.providers;

/**
 * Created by mujian on 18/10/16.
 *
 * @author mujian
 */
public interface Provider {
    String read();

    void write(String message);
}
