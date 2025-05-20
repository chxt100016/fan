package com.chxt.stream.rtsp;

import com.chxt.stream.service.RtspStreamService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RtspServerHandler extends ChannelInboundHandlerAdapter {
    private final RtspStreamService streamService;
    private final AtomicInteger frameIndex = new AtomicInteger(0);
    private volatile boolean isStreaming = false;

    public RtspServerHandler(RtspStreamService streamService) {
        this.streamService = streamService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            io.netty.handler.codec.http.HttpRequest request = (io.netty.handler.codec.http.HttpRequest) msg;
            
            if (request.method() == RtspMethods.OPTIONS) {
                handleOptions(ctx);
            } else if (request.method() == RtspMethods.DESCRIBE) {
                handleDescribe(ctx);
            } else if (request.method() == RtspMethods.SETUP) {
                handleSetup(ctx);
            } else if (request.method() == RtspMethods.PLAY) {
                handlePlay(ctx);
            } else if (request.method() == RtspMethods.TEARDOWN) {
                handleTeardown(ctx);
            }
        }
    }

    private void handleOptions(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, HttpResponseStatus.OK);
        response.headers().set("Public", "OPTIONS, DESCRIBE, SETUP, PLAY, TEARDOWN");
        ctx.writeAndFlush(response);
    }

    private void handleDescribe(ChannelHandlerContext ctx) {
        String sdp = "v=0\r\n" +
                "o=- 0 0 IN IP4 127.0.0.1\r\n" +
                "s=H264 Stream\r\n" +
                "c=IN IP4 0.0.0.0\r\n" +
                "t=0 0\r\n" +
                "m=video 0 RTP/AVP 96\r\n" +
                "a=rtpmap:96 H264/90000\r\n";

        ByteBuf content = Unpooled.copiedBuffer(sdp.getBytes());
        FullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, HttpResponseStatus.OK, content);
        response.headers().set("Content-Type", "application/sdp");
        response.headers().set("Content-Length", content.readableBytes());
        ctx.writeAndFlush(response);
    }

    private void handleSetup(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, HttpResponseStatus.OK);
        response.headers().set("Session", "12345");
        ctx.writeAndFlush(response);
    }

    private void handlePlay(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, HttpResponseStatus.OK);
        ctx.writeAndFlush(response);

        if (!isStreaming) {
            isStreaming = true;
            startStreaming(ctx);
        }
    }

    private void handleTeardown(ChannelHandlerContext ctx) {
        isStreaming = false;
        FullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, HttpResponseStatus.OK);
        ctx.writeAndFlush(response);
    }

    private void startStreaming(ChannelHandlerContext ctx) {
        new Thread(() -> {
            List<byte[]> frames = streamService.getFrameData();
            while (isStreaming && ctx.channel().isOpen()) {
                if (!frames.isEmpty()) {
                    int index = frameIndex.getAndIncrement() % frames.size();
                    byte[] frameData = frames.get(index);
                    ByteBuf buffer = Unpooled.wrappedBuffer(frameData);
                    ctx.writeAndFlush(buffer);
                }
                try {
                    Thread.sleep(33); // Approximately 30 fps
                } catch (InterruptedException e) {
                    log.error("Streaming interrupted", e);
                    break;
                }
            }
        }).start();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Channel exception caught", cause);
        isStreaming = false;
        ctx.close();
    }
} 