package com.example.utils.disruptor;


import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qianliao.zhuang
 */
@Slf4j
@Component
public class MessageEventHandler implements EventHandler<MessageEvent> {

    @Override
    public void onEvent(MessageEvent event, long l, boolean b) throws Exception {
        log.info("receive message: {}", event);
    }
}
