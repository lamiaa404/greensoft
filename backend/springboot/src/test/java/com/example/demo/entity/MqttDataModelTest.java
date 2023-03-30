package com.example.demo.entity;

import com.example.demo.repository.ComputerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
// echte db benutzen
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MqttDataModelTest {
    @Autowired
    private ComputerRepository repository;

    @Test
    @Transactional
    public void testRepositoryConnection(){
        assertThat(repository).isNotNull();
    }

    @Test
    // @Transactional macht am ende ein db rollback -> keine persistence der testdaten in der db
    @Transactional
    public void testSaveMqttDataModel() {
        MqttDataModel testModel = new MqttDataModel(100, 100, 100.0);

        repository.save(testModel);
        MqttDataModel dbModel = repository.searchByTimestampAndId(100, 100);
        // energie wert überprüfen
        assertEquals(dbModel.getEnergy_value(), 100.0);
    }

}
