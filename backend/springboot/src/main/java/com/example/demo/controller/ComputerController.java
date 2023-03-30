package com.example.demo.controller;

import com.example.demo.ComputerService.ComputerService;
import com.example.demo.entity.DeviceInfo;
import com.example.demo.entity.LastSequence;
import com.example.demo.entity.MqttDataModel;
import com.example.demo.repository.ComputerRepository;
import com.example.demo.repository.DeviceInfoRepository;
import com.example.demo.repository.LastSequenceRepository;
import com.example.demo.repository.StatisticsRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@NoArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/")
// hier reicht der einfach slash weil nginx auf /api an springboot weiterleitet
// sonst wäre es /api/api/ ... etc
public class ComputerController {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private LastSequenceRepository lastSequenceRepository;

    @Autowired
    private DeviceInfoRepository deviceInfoRepository;

    @GetMapping("/computers")
    public ResponseEntity<List< MqttDataModel>> fetchComputers(@RequestParam int id, @RequestParam int start, @RequestParam int end, @RequestParam int precision) {
        List<MqttDataModel> computers = computerRepository.returnRawDataForOneDevice(start,end,precision,id);
        return ResponseEntity.ok().body(computers);
    }


    @GetMapping("/computers/max")
    public ResponseEntity<List< Map<String,Object>>> fetchMax(@RequestParam int start,@RequestParam int end){
        List<Object[]> statistics = statisticsRepository.returnMax(start, end);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] stat : statistics) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", stat[0].toString());
            map.put("timestamp",stat[1].toString());
            map.put("maxValue", stat[2].toString());
            result.add(map);
        }

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/computers/mean")
    public ResponseEntity<List<Map<String, Object>>> fetchMean(@RequestParam int start,@RequestParam int end){
        List<Object[]> statistics =  statisticsRepository.returnMean(start, end);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] stat : statistics) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", stat[0].toString());
            map.put("timestamp", stat[1].toString());
            map.put("meanValue", stat[2].toString());
            result.add(map);
        }
        return ResponseEntity.ok().body(result);

    }

    @GetMapping("/computers/idle")
    public ResponseEntity<List<Map<String, Object>>> fetchIdle(@RequestParam int start,@RequestParam int end){
        List<Object[]> statistics =  statisticsRepository.returnIdle(start, end);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] stat : statistics) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", stat[0].toString());
            map.put("timestamp", stat[1].toString());
            map.put("idleValue", stat[2].toString());
            result.add(map);
        }

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/computers/lastsequence")
    public ResponseEntity<List<LastSequence>> fetchLastSequences(@RequestParam int id, @RequestParam int start, @RequestParam int end) {
        List<LastSequence> lastSequences = lastSequenceRepository.returnAllSequencesOfDevice(id, start,end);
        return ResponseEntity.ok().body(lastSequences);
    }

    @PostMapping(path = "/computers/lastsequence/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> saveLastSequences(@RequestBody LastSequence recvLastSequence) {
        try {
            ComputerService.createLastSequence(
                    recvLastSequence.getStart(),
                    recvLastSequence.getEnde(),
                    recvLastSequence.getDeviceId(),
                    recvLastSequence.getLabel(),
                    this.lastSequenceRepository
            );
            // die map wird dann zu {"status":"success"}
            return Collections.singletonMap("status", "success");
        } catch(Exception ex) {
            // return HTTP 500 with json error body
            return Collections.singletonMap("status", "error");
        }
    }

    @GetMapping("/devices")
    public List<DeviceInfo> fetchDevices(){
        return deviceInfoRepository.returnDevices();
    }

    @PutMapping("/devices/edit")
    public Map<String, String> editDevice(@RequestBody DeviceInfo recvDevice) {
        if (deviceInfoRepository.updateDevice(
                recvDevice.getId(),
                recvDevice.getName(),
                recvDevice.getStandort(),
                recvDevice.getUsers(),
                recvDevice.getTags()) == 1) {
            return Collections.singletonMap("status", "success");
        } else {
            return Collections.singletonMap("status", "error");
        }
    }
}
