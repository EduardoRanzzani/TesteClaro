package br.com.claro.testeclaro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Testando classe inicial")
class TesteClaroApplicationTest {

    @Test
    public void contextLoads() {
        TesteClaroApplication.main(new String[]{});
    }

}
