package com.himoyi.server.tcp;

import com.himoyi.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * 使用装饰者模式对原有的buffer进行增强，以解决半包、粘包问题
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    // 自定义解析器
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }

    /**
     * 初始化解析器
     *
     * @param bufferHandler
     * @return
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {

        // 创建一个固定长度的RecordParser，用于读取消息头
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        // 设置解析器的处理逻辑
        parser.setOutput(new Handler<Buffer>() {

            // 记录消息体的长度
            // 当为-1时，表示本次提取的为消息头
            int size = -1;
            // 用于存储完整的消息
            Buffer resultBuffer = Buffer.buffer();

            /**
             * recordParser读取到的buffer的处理逻辑
             * @param buffer  需要处理的buffer，第一次为消息头，第二次为消息体
             */
            @Override
            public void handle(Buffer buffer) {
                if (-1 == size) {

                    // 获取消息体长度
                    size = buffer.getInt(13);
                    // 将解析器设置为消息体读取长度
                    parser.fixedSizeMode(size);

                    // 保存消息头到结果中
                    resultBuffer.appendBuffer(buffer);
                } else {

                    // 保存消息体到结果中
                    resultBuffer.appendBuffer(buffer);

                    // 被装饰的原方法
                    // 调用传入的Handler处理完整消息（调用处理器寻找服务类进行方法的执行)
                    bufferHandler.handle(resultBuffer);

                    // 设置为消息头读取模式
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    // 恢复原有样式
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });


        return parser;
    }

    @Override
    public void handle(Buffer event) {
        recordParser.handle(event);
    }
}
