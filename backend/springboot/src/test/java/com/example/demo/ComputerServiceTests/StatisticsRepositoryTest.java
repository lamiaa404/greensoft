package com.example.demo.ComputerServiceTests;

import com.example.demo.entity.DeviceInfo;
import com.example.demo.entity.Statistics;
import com.example.demo.repository.ComputerRepository;
import com.example.demo.repository.StatisticsRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


//@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StatisticsRepositoryTest {
    @Autowired
    StatisticsRepository statisticsRepository;

    @BeforeAll
    public  void setUp() {
        Statistics statistics =  Statistics.builder()
                .id(123)
                .timestamp(100)
                .idle(4.00)
                .max(40)
                .average(BigDecimal.valueOf(20))
                .build();
        statisticsRepository.save(statistics);
        Statistics statistics1 =  Statistics.builder()
                .id(123)
                .timestamp(1000)
                .idle(3.00)
                .max(80)
                .average(BigDecimal.valueOf(40))
                .build();
        statisticsRepository.save(statistics1);
        Statistics statistics2 =  Statistics.builder()
                .id(123)
                .timestamp(10000)
                .idle(2.00)
                .max(100)
                .average(BigDecimal.valueOf(30))
                .build();
        statisticsRepository.save(statistics2);
    }


    @Test
    @Transactional
    public void returnHistoricalMaxTest() {
        // Call the function
        double actualMax = statisticsRepository.returnHistoricalMax(123);
        // Verify the results
        assertEquals(100, actualMax, 0.01);
    }

    @Test
    public void returnIdleTest() {
        List<Object[]> actualIdles = statisticsRepository.returnIdle(100, 1000);
        assertEquals(2, actualIdles.size());
        assertEquals(4.00, actualIdles.get(0)[2]);
    }
    @Test
    public void returnAverageTest() {
        List<Object[]> actualAverage = statisticsRepository.returnMean(100, 1000);
        assertEquals(2, actualAverage.size());
        assertEquals(20, ((BigDecimal)actualAverage.get(0)[2]).intValue());
    }

    @Transactional
    @AfterAll
    public void tearDown() {
        try {
            statisticsRepository.deleteByIdAndTimestamp(123, 100);
            statisticsRepository.deleteByIdAndTimestamp(123, 1000);
            statisticsRepository.deleteByIdAndTimestamp(123, 10000);
        } catch (Exception e) {
        System.out.println(e);
        }
    }
}
