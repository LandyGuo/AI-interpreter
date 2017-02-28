package com.alipay.aiml.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by mujian on 18/10/16.
 *
 * @author mujian
 */
public class ConsoleProvider implements Provider {

    private BufferedReader reader;

    public ConsoleProvider() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String read() {
        String textLine = null;
        try {
            textLine = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textLine;
    }

    @Override
    public void write(String message) {
        System.out.print(message);
    }
}
