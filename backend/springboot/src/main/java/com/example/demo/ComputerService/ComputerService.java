package com.example.demo.ComputerService;

import com.example.demo.entity.DeviceInfo;
import com.example.demo.entity.LastSequence;
import com.example.demo.entity.MqttDataModel;
import com.example.demo.repository.ComputerRepository;
import com.example.demo.repository.DeviceInfoRepository;
import com.example.demo.repository.LastSequenceRepository;
import com.example.demo.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;

public class ComputerService {

    public static void createLastSequence(Integer start, Integer ende, Integer deviceId, LastSequenceRepository lastSequenceRepository){
        LastSequence lastSequence = LastSequence.builder()
                .id(lastSequenceRepository.findLastId() + 1)
                .label("automatic last sequence")
                .start(start)
                .ende(ende)
                .deviceId(deviceId)
                .build();
        lastSequenceRepository.save(lastSequence);
    }

    public static void createLastSequence(Integer start, Integer ende, Integer deviceId, String label, LastSequenceRepository lastSequenceRepository){
        LastSequence lastSequence = LastSequence.builder()
                .id(lastSequenceRepository.findLastId()+1)
                .label(label)
                .start(start)
                .ende(ende)
                .deviceId(deviceId)
                .build();
        lastSequenceRepository.save(lastSequence);
    }

    public static void createDeviceInfo(Integer id, DeviceInfoRepository deviceInfoRepository){
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .id(id)
                .build();
        deviceInfoRepository.save(deviceInfo);
    }

    public static void DailyThreshold(Integer device, int yesterday, StatisticsRepository statisticsRepository, ComputerRepository computerRepository, LastSequenceRepository lastSequenceRepository){
        double dailyThreshold = statisticsRepository.returnHistoricalMax(device)*0.4;

        LinkedList<MqttDataModel> list = computerRepository.returnRawDataForSingleDevice(yesterday, device);
        MqttDataModel mqttDataModel = new MqttDataModel(device,yesterday+86401,0.0);
        list.add(mqttDataModel);
        LinkedList<MqttDataModel> tempLastSq = new LinkedList<>();

         for(MqttDataModel mq : list){
             if(mq.getEnergy_value() < dailyThreshold){
                 if(!tempLastSq.isEmpty() && tempLastSq.getLast().getTimestamp() - tempLastSq.get(0).getTimestamp() >= 30 ){
                     createLastSequence(tempLastSq.get(0).getTimestamp(), tempLastSq.getLast().getTimestamp(), device,lastSequenceRepository);
                 }
                 tempLastSq.clear();
                 continue;
             }
             tempLastSq.add(mq);
         }
    }

    public static void DailySequenceSearch(DeviceInfoRepository deviceInfoRepository,ComputerRepository computerRepository, StatisticsRepository statisticsRepository,LastSequenceRepository lastSequenceRepository){
        LinkedList<Integer> list = deviceInfoRepository.returnDeviceID();
        for(Integer i : list){
            DailyThreshold(i, calcYesterday(),statisticsRepository,computerRepository,lastSequenceRepository);
        }
    }

    public static int calcYesterday(){
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime yesterday = current.minusDays(1);
        return (int)(yesterday.toEpochSecond(ZoneOffset.UTC) + 3600);
    }
}
