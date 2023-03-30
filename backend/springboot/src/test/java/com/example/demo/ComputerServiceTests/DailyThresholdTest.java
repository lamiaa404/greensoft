package com.example.demo.ComputerServiceTests;

import com.example.demo.ComputerService.ComputerService;
import com.example.demo.entity.LastSequence;
import com.example.demo.entity.MqttDataModel;
import com.example.demo.repository.ComputerRepository;
import com.example.demo.repository.LastSequenceRepository;
import com.example.demo.repository.StatisticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


import java.util.Arrays;
import java.util.LinkedList;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class DailyThresholdTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @Mock
    private ComputerRepository computerRepository;

    @Mock
    private LastSequenceRepository lastSequenceRepository;

    @Mock
    private ComputerService computerService;

    @Test
    public void testDailyThreshold() {
        // Mock data
        int device = 1;
        int yesterday = 123456;
        double max = 100.0;
        double dailyThreshold = max * 0.4;
        LinkedList<MqttDataModel> rawData = new LinkedList<>(Arrays.asList(
                new MqttDataModel(1, 123456790, 50.0),
                new MqttDataModel(1, 123456795, 30.0),
                new MqttDataModel(1, 123456800, 80.0),
                new MqttDataModel(1, 123456810, 90.0),
                new MqttDataModel(1, 123456820, 60.0),
                new MqttDataModel(1, 123456830, 90.0),
                new MqttDataModel(1, 123456840, 20.0),
                new MqttDataModel(1, 123456845, 60.0)
        ));
        when(statisticsRepository.returnHistoricalMax(device)).thenReturn(max);
        when(computerRepository.returnRawDataForSingleDevice(yesterday, device)).thenReturn(rawData);

        // Call method
        computerService.DailyThreshold(device, yesterday, statisticsRepository, computerRepository, lastSequenceRepository);

        // Verify results
        verify(statisticsRepository).returnHistoricalMax(device);
        verify(computerRepository).returnRawDataForSingleDevice(yesterday, device);

        /*try(MockedStatic<ComputerService> mockedComputerService = Mockito.mockStatic(ComputerService.class)){
            mockedComputerService.
            mockedComputerService.verify(ComputerService.createLastSequence(123456790, 123456793, device, lastSequenceRepository), times(1));
            verify(mockedComputerService, times(1)).createLastSequence(123456790, 123456793, device, lastSequenceRepository);
        }
        verify(computerService, times(1)).createLastSequence(123456790, 123456793, device, lastSequenceRepository);
        */


        // Ensure tempLastSq is empty after processing last data point
        LinkedList<MqttDataModel> tempLastSq = new LinkedList<MqttDataModel>();
        for(MqttDataModel mq : rawData){
            if(mq.getEnergy_value() < dailyThreshold){
                if(!tempLastSq.isEmpty() && tempLastSq.getLast().getTimestamp() - tempLastSq.get(0).getTimestamp() >= 30 ){
                    /*
                    try(MockedStatic<ComputerService> mockedComputerService = Mockito.mockStatic(ComputerService.class)){
                        verify(mockedComputerService, times(1)).createLastSequence(tempLastSq.get(0).getTimestamp(), tempLastSq.getLast().getTimestamp(), device, lastSequenceRepository);
                    }
                     */
                    //checks if the save last sequence is actually the one we expect
                    verify(lastSequenceRepository, times(1)).save(any(LastSequence.class));
                    ArgumentCaptor<LastSequence> captor = ArgumentCaptor.forClass(LastSequence.class);
                    verify(lastSequenceRepository).save(captor.capture());
                    LastSequence savedLastSequence = captor.getValue();
                    assertEquals("automatic last sequence", savedLastSequence.getLabel());
                    assertEquals(tempLastSq.get(0).getTimestamp(), savedLastSequence.getStart().intValue());
                    assertEquals(tempLastSq.getLast().getTimestamp(), savedLastSequence.getEnde().intValue());
                    assertEquals(device, savedLastSequence.getDeviceId().intValue());
                }
                tempLastSq.clear();
                continue;
            }
            tempLastSq.add(mq);
        }
        assertTrue(tempLastSq.isEmpty());
    }
}
