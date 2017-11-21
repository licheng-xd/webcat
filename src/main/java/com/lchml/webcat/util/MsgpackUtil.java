package com.lchml.webcat.util;

import com.lchml.webcat.ex.WebcatPackException;
import io.netty.util.CharsetUtil;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lc on 11/20/17.
 */
public class MsgpackUtil {
    private static final Logger logger = LoggerFactory.getLogger(MsgpackUtil.class);

    public static byte[] pack(String src) {
        byte[] bytes = src.getBytes(CharsetUtil.UTF_8);
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        try {
            packer.packRawStringHeader(bytes.length);
            packer.writePayload(bytes);
            packer.close();
            MessageBuffer buffer = packer.toMessageBuffer();
            return buffer.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new WebcatPackException("pack error src:" + src, e);
        }
    }

    public static String upack(byte[] bytes) {
        try {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
            Value value = unpacker.unpackValue();
            unpacker.close();
            return value.asStringValue().asString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new WebcatPackException("unpack error src:" + Arrays.toString(bytes), e);
        }
    }

    public static Map<String, Object> upackMap(byte[] bytes) {
        try {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
            Value value = unpacker.unpackValue();
            unpacker.close();
            return JsonUtil.json2Map(value.asMapValue().toJson());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new WebcatPackException("unpack error src:" + Arrays.toString(bytes), e);
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", 1);
        map.put("2", "123");
        map.put("3", "12");
        String jsonStr = JsonUtil.toJson(map);
        byte[] bytes = MsgpackUtil.pack(jsonStr);
        System.out.println(MsgpackUtil.upack(bytes));
    }
}
