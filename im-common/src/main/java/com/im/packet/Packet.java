package com.im.packet;

import lombok.Data;

@Data
public abstract class Packet {
    private int version;

    public abstract Byte getCommand();
}
