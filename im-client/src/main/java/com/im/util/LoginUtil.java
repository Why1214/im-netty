package com.im.util;

import com.im.attribute.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

public class LoginUtil {

    /**
     * 登录成功之后进行标志
     *
     * @param channel 通道
     */
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> attrLogin = channel.attr(Attributes.LOGIN);
        return attrLogin != null;
    }
}
