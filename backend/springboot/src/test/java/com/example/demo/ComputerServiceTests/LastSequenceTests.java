package com.example.demo.ComputerServiceTests;

import static org.mockito.Mockito.*;

import com.example.demo.ComputerService.ComputerService;
import com.example.demo.entity.LastSequence;
import com.example.demo.repository.LastSequenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class LastSequenceTests {

    @Mock
    private LastSequenceRepository lastSequenceRepository;

    @Test
    public void testCreateLastSequence() {
        Integer start = 1;
        Integer end = 2;
        Integer deviceId = 123;

        // Mock the behavior of the LastSequenceRepository
        when(lastSequenceRepository.findLastId()).thenReturn(1);

        // Call the function
        ComputerService.createLastSequence(start, end, deviceId, lastSequenceRepository);

        // Verify that the save method was called on the repository
        verify(lastSequenceRepository, times(1)).save(any(LastSequence.class));
    }

    @Test
    public void testCreateLastSequenceWithLabel() {
        Integer start = 1;
        Integer end = 2;
        Integer deviceId = 123;
        String label ="test last sequence";

        // Mock the behavior of the LastSequenceRepository
        when(lastSequenceRepository.findLastId()).thenReturn(1);

        // Call the function
        ComputerService.createLastSequence(start, end, deviceId, label, lastSequenceRepository);

        // Verify that the save method was called on the repository
        verify(lastSequenceRepository, times(1)).save(any(LastSequence.class));
    }


}

