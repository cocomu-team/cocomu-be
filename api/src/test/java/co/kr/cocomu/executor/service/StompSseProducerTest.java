package co.kr.cocomu.executor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import co.kr.cocomu.executor.dto.message.EventMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class StompSseProducerTest {

    @Mock(strictness = Strictness.LENIENT) private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks private StompSseProducer stompSseProducer;

    @Test
    void 코드_실행_알림이_발생한다() {
        // given
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(EventMessage.class));

        // when
        stompSseProducer.publishRunning(1L);

        // then
        verify(simpMessagingTemplate).convertAndSend(anyString(), any(EventMessage.class));
    }

}