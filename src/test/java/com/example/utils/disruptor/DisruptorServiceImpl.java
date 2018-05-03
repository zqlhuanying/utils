package com.example.utils.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

/**
 * @author qianliao.zhuang
 * Disruptor 用于各个墙的配置更改时，产生的消息推送
 * 其实完全可以不用Disruptor
 */
@Service
public class DisruptorServiceImpl implements InitializingBean {

    private static final int RING_BUFFER_SIZE = 1024 * 1024;
    private Disruptor<MessageEvent> disruptor;
    @Autowired
    private MessageEventHandler messageEventHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        disruptor = new Disruptor<>(
                MessageEvent.EVENT_FACTORY,
                RING_BUFFER_SIZE,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new SleepingWaitStrategy());
        disruptor.handleEventsWith(messageEventHandler);
        disruptor.start();
    }

    public void sendMessage(String message) {
        doSendMessage(message);
    }

    private void doSendMessage(String message) {
        RingBuffer<MessageEvent> ringBuffer = disruptor.getRingBuffer();

        ringBuffer.publishEvent(new EventTranslatorOneArg<MessageEvent, String>() {
          @Override
          public void translateTo(MessageEvent event, long sequence, String data) {
              event.setMessage(data);
          }
      }, message);
    }
}
