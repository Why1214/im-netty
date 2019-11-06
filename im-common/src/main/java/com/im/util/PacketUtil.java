package com.im.util;

import com.im.packet.LoginRequestPacket;
import com.im.packet.Packet;
import com.im.packet.command.Command;
import com.im.serialize.Serializer;
import com.im.serialize.SerializerAlgorithm;
import com.im.serialize.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

public class PacketUtil {
    private static final int MAGIC_NUMBER = 0x12345678;

    // 默认使用JSON序列化的方式
    private static final Serializer DEFAULT_SERIALIZER = new JSONSerializer();

    // 指令和数据包的映射关系
    private static final Map<Byte, Class<? extends Packet>> PACKET_MAP = new HashMap<>();

    // 序列化算法和序列化算法类的映射关系
    private static final Map<Byte, Serializer> SERIALIZER_MAP = new HashMap<>();

    static {
        PACKET_MAP.put(Command.LOGIN_REQUEST, LoginRequestPacket.class);

        SERIALIZER_MAP.put(SerializerAlgorithm.JSON, new JSONSerializer());
    }

    /**
     * 编码
     *
     * @param packet 数据包信息
     * @return 返回编码后的格式
     */
    public static ByteBuf encode(ByteBufAllocator allocator, Packet packet) {
        // 创建ByteBuf
        ByteBuf data = allocator.ioBuffer();

        // 序列化数据包
        byte[] packetData = DEFAULT_SERIALIZER.serialize(packet);

        data.writeInt(MAGIC_NUMBER);
        data.writeByte(packet.getVersion());
        data.writeByte(DEFAULT_SERIALIZER.getSerializerAlgorithm());
        data.writeByte(packet.getCommand());
        data.writeInt(packetData.length);
        data.writeBytes(packetData);

        return data;
    }

    public static Packet decode(ByteBuf requestData) {
        // 跳过魔书
        requestData.skipBytes(4);
        // 跳过版本号
        requestData.skipBytes(1);
        // 获取序列化算法，以及算法对应的序列化对象
        byte algorithm = requestData.readByte();
        Serializer serializer = SERIALIZER_MAP.get(algorithm);
        if (serializer == null) {
            return null;
        }

        // 获取指令和数据包类型
        byte command = requestData.readByte();
        Class<? extends Packet> packetClass = PACKET_MAP.get(command);
        if (packetClass == null) {
            return null;
        }

        // 获取数据长度
        int dataLength = requestData.readInt();
        // 获取数据
        byte[] dataByte = new byte[dataLength];
        requestData.readBytes(dataByte);

        return serializer.deserialize(packetClass, dataByte);
    }
}
