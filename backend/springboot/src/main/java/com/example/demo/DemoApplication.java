package com.example.demo;


import com.example.demo.ComputerService.ComputerService;
import com.example.demo.entity.Statistics;
import com.example.demo.repository.ComputerRepository;
import com.example.demo.repository.DeviceInfoRepository;
import com.example.demo.repository.LastSequenceRepository;
import com.example.demo.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories("com.example.demo.repository")
@EnableScheduling
public class DemoApplication implements CommandLineRunner {
	@Autowired
	private ComputerRepository computerRepository;

	@Autowired
	private StatisticsRepository statisticsRepository;

	@Autowired
	private DeviceInfoRepository deviceInfoRepository;

	@Autowired
	private LastSequenceRepository lastSequenceRepository;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//scheduledTask();
		//CallDailySequenceSearch();
	}

	@Scheduled(cron = "0 0 1 * * *")
	public void CallDailySequenceSearch(){
		ComputerService.DailySequenceSearch(deviceInfoRepository,computerRepository,statisticsRepository,lastSequenceRepository);
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void scheduledTask() {
		//Your task
		LocalDateTime current = LocalDateTime.now();
		LocalDateTime yesterday = current.minusDays(1);
		//UTC + 1 - should be adjusted end of March
		List<Object[]> Mean =  computerRepository.findDailyMean((int)yesterday.toEpochSecond(ZoneOffset.UTC) + 3600);
		List<Object[]> Idle =  computerRepository.findIdle((int)yesterday.toEpochSecond(ZoneOffset.UTC) + 3600);
		List<Object[]> Max =  computerRepository.findDailyMax((int)yesterday.toEpochSecond(ZoneOffset.UTC) + 3600);

		LinkedList<Integer> idList = deviceInfoRepository.returnDeviceID();

		for(int i = 0; i < Max.size(); i++){
			BigInteger big = (BigInteger) Max.get(i)[0];
			if(!idList.contains(big.intValue())){
				ComputerService.createDeviceInfo(big.intValue(), deviceInfoRepository);
			}
		}

		for (int i = 0; i < Max.size(); i++) {
			BigInteger big = (BigInteger)Mean.get(i)[0];
			Statistics statistics = Statistics.builder()
					.id(big.intValue())
					.timestamp((int) (yesterday.toEpochSecond(ZoneOffset.UTC) + 3600))
					.idle((Double) Idle.get(i)[1])
					.max((Double) Max.get(i)[1])
					.average((BigDecimal) Mean.get(i)[1])
					.build();
			System.out.println(statistics.getId());
			statisticsRepository.save(statistics);

		}
	}
}
