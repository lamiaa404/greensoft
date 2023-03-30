package com.example.demo.ComputerServiceTests;

import com.example.demo.ComputerService.ComputerService;
import com.example.demo.entity.DeviceInfo;
import com.example.demo.repository.DeviceInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class DeviceTests {

    @Mock
    private DeviceInfoRepository deviceInfoRepository;


    @Test
    public void testCreateDeviceInfo(){
        Integer id = 1000;

        ComputerService.createDeviceInfo(id, deviceInfoRepository);

        verify(deviceInfoRepository, times(1)).save(any(DeviceInfo.class));

    }
}
