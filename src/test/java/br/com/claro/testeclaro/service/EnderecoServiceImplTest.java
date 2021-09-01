package br.com.claro.testeclaro.service;

import br.com.claro.testeclaro.model.Endereco;
import lombok.Data;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@Data
@SpringBootTest
@DisplayName("Testando classe EnderecoService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnderecoServiceImplTest {
    @Autowired
    private EnderecoService enderecoService;

    private final String validCEP = "79050-190";
    private final String invalidCEP = "00000-000";

    @Test
    @Order(1)
    @DisplayName("Buscando dados do endereço com um cep válido")
    public void findByCEPValid() {
        Endereco endereco = enderecoService.findByCEP(validCEP);
        assertTrue(endereco != null);
        assertEquals(validCEP, endereco.getCep());
    }

    @Test
    @Order(2)
    @DisplayName("Buscando dados do endereço com um cep inválido")
    public void findByCepInvalid() {
        try {
            Endereco endereco = enderecoService.findByCEP(invalidCEP);
            assertFalse(endereco != null);
        } catch (ResponseStatusException e) {
            assertTrue(e.getStatus().name().equals("BAD_REQUEST"));
        }
    }

}
