package io.netty.handler.codec.compression;

import com.github.luben.zstd.Zstd;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static io.netty.handler.codec.compression.ZstdConstants.*;

public class ZstdEncoderTest {

    private ZstdEncoder encoder = new ZstdEncoder();

    @Test
    public void testInternalNioBuffer() {
        String sample = randomString(1024);
        byte[] sampleBytes = sample.getBytes();
        ByteBuf byteBuf = Unpooled.directBuffer(DEFAULT_BLOCK_SIZE);
        int idx = byteBuf.writerIndex();
        ByteBuffer buffer = byteBuf.internalNioBuffer(idx + HEADER_LENGTH, sampleBytes.length);
        buffer.put(sampleBytes);
        byteBuf.writerIndex(idx + HEADER_LENGTH + sampleBytes.length);
        byteBuf.readerIndex(idx + HEADER_LENGTH);
        Assert.assertEquals(sample, byteBuf.toString(Charset.defaultCharset()));
    }

    @Test
    public void testEncodeWithDefaultBlockSize() {
        String sample = randomString(DEFAULT_BLOCK_SIZE);
        EmbeddedChannel channel = new EmbeddedChannel(encoder);
        ByteBuf byteBuf = Unpooled.directBuffer(DEFAULT_BLOCK_SIZE);
        byte[] sampleBytes = sample.getBytes();
        byteBuf.ensureWritable(sampleBytes.length);
        byteBuf.writeBytes(sampleBytes);
        Assert.assertTrue(channel.writeOutbound(byteBuf));
        ByteBuf target = channel.readOutbound();
        Assert.assertNotNull(target);
        ByteBuffer buffer = ByteBuffer.allocateDirect(sampleBytes.length);
        buffer.clear();
        target.readerIndex(target.readerIndex() + HEADER_LENGTH);
        ByteBuffer source = target.internalNioBuffer(target.readerIndex(), target.readableBytes());
        Zstd.decompress(buffer, source);

        buffer.flip();
        ByteBuf buf = Unpooled.wrappedBuffer(buffer);
        String val = buf.toString(Charset.defaultCharset());
        Assert.assertEquals(sample, val);
    }

    @Test
    public void testEncodeUnCompressed() {
        String sample = randomString(MIN_BLOCK_SIZE);
        EmbeddedChannel channel = new EmbeddedChannel(encoder);

        ByteBuf byteBuf = Unpooled.directBuffer(MIN_BLOCK_SIZE);
        byte[] sampleBytes = sample.getBytes();
        byteBuf.ensureWritable(sampleBytes.length);
        byteBuf.writeBytes(sampleBytes);
        Assert.assertTrue(channel.writeOutbound(byteBuf));

        ByteBuf target = channel.readOutbound();
        Assert.assertNotNull(target);

        target.readerIndex(HEADER_LENGTH);

        Assert.assertEquals(sample, target.toString(Charset.defaultCharset()));
    }

    public static String randomString(int length) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + (int) (26 * Math.random())));
        }

        return sb.toString();
    }

}