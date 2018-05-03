package com.example.utils.disruptor;

import com.lmax.disruptor.EventFactory;
import lombok.Data;
import lombok.Getter;

/**
 * @author qianliao.zhuang
 * Disruptor RingBuffer Data
 */
@Data
public class MessageEvent {

    public final static EventFactory<MessageEvent> EVENT_FACTORY = new EventFactory<MessageEvent>() {
        @Override
        public MessageEvent newInstance() {
            return new MessageEvent();
        }
    };

    @Getter
    private String message;
}
