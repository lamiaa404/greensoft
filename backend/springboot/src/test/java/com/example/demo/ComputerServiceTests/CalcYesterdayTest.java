package com.example.demo.ComputerServiceTests;

import com.example.demo.ComputerService.ComputerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CalcYesterdayTest {

    @Test
    public void testCalcYesterday() throws Exception {
        // Set the current time to be 2023-03-01T00:00:00 CET
        LocalDateTime current = LocalDateTime.of(2023, 3, 10, 0, 0, 0);
        try (MockedStatic<LocalDateTime> mockDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockDateTime.when(LocalDateTime::now).thenReturn(current);
            // current als timestamp: 1678402800
            // weil yesterday gesucht ist: current-86400 + 3600*2
            // 3600*2 als utc offset
            assertEquals(1678402800-86400+3600*2, ComputerService.calcYesterday());
        }
    }
}
