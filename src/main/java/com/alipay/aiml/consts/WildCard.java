package com.alipay.aiml.consts;

/**
 * Created by mujian on 19/06/15.
 *
 * @author mujian
 */
public enum WildCard {
    ZeroMore("+"),
    OneMore("*"),
    ZeroMorePriority("+"),
    OneMorePriority("*");

    private final String sumbol;

    WildCard(String sumbol) {
        this.sumbol = sumbol;
    }

    public String get() {
        return sumbol;
    }
}