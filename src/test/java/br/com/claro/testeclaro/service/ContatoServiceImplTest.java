package br.com.claro.testeclaro.service;

import br.com.claro.testeclaro.model.Endereco;
import br.com.claro.testeclaro.model.entity.Contato;
import lombok.Data;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Data
@SpringBootTest
@DisplayName("Testando classe ContatoService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContatoServiceImplTest {
    @Autowired
    private ContatoService contatoService;
    @Autowired
    private EnderecoService enderecoService;

    private final String validEmail = "teste@teste.com";
    private final String invalidEmail = "teste.com.br";

    @Test
    @Order(1)
    @DisplayName("Erro ao tentar criar um novo contato sem ID")
    void saveWithNoID() {
        try {
            Optional<Contato> salvo = contatoService.save(new Contato());
            assertFalse(salvo.isPresent());
        } catch (JpaSystemException e) {
            assertTrue(Objects.requireNonNull(e.getMessage()).toLowerCase().contains("ids for this class must be manually assign"));
        }
    }

    @Test
    @Order(2)
    @DisplayName("Erro ao tentar criar um novo contato sem os dados obrigatórios")
    void saveWithNoData() {
        try {
            Contato contato = Contato.builder()
                    .email(validEmail)
                    .build();
            Optional<Contato> salvo = contatoService.save(contato);
            assertFalse(salvo.isPresent());
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("erro"));
        }
    }

    @Test
    @Order(3)
    @DisplayName("Erro ao tentar criar um novo contato com CEP inválido")
    void saveWithInvalidCEP() {
        try {
            Contato contato = Contato.builder()
                    .email(validEmail)
                    .nome("Usuário teste")
                    .telefone("(67) 99999-9999")
                    .cep("00000-000")
                    .build();
            Optional<Contato> salvo = contatoService.save(contato);
            assertFalse(salvo.isPresent());
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("erro"));
        }
    }

    @Test
    @Order(4)
    @DisplayName("Erro ao tentar criar um novo contato com e-mail inválido")
    void saveWithInvalidEmail() {
        try {
            Endereco endereco = enderecoService.findByCEP("79081-200");
            assertNotNull(endereco);
            Contato contato = Contato.builder()
                    .email(invalidEmail)
                    .nome("Usuário teste")
                    .telefone("(67) 99999-9999")
                    .cep(endereco.getCep())
                    .endereco(endereco.getLogradouro() + ", " + endereco.getBairro())
                    .cidade(endereco.getLocalidade())
                    .uf(endereco.getUf())
                    .dataCadastro(new Date())
                    .build();

            Optional<Contato> salvo = contatoService.save(contato);
            assertFalse(salvo.isPresent());
        } catch (Exception e) {
            assertTrue(e.getCause().getCause().toString().toLowerCase().contains("formato inválido"));
        }
    }

    @Test
    @Order(5)
    @DisplayName("Criação de um novo Contato")
    void save() {
        Contato contato = Contato.builder()
                .email(validEmail)
                .nome("Usuário teste")
                .telefone("(67) 99999-9999")
                .cep("79081-200")
                .build();

        Optional<Contato> salvo = contatoService.save(contato);
        assertTrue(salvo.isPresent());
        System.out.println("Contato salvo: " + salvo);
        assertEquals(contato.getEmail(), salvo.get().getEmail());
    }

    @Test
    @Order(6)
    @DisplayName("Buscando todos os contatos")
    void findAll() {
        List<Contato> contatos = contatoService.findAll();
        contatos.forEach(System.out::println);
        assertNotNull(contatos);
        assertFalse(contatos.isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Buscando contato pelo e-mail (ID)")
    void findById() {
        Optional<Contato> contato = contatoService.findById(validEmail);
        assertTrue(contato.isPresent());
        System.out.println("Contato encontrado: " + contato);
        assertEquals(validEmail, contato.get().getEmail());
    }

    @Test
    @Order(8)
    @DisplayName("Tentativa de buscar um contato inexistente pelo e-mail (ID)")
    void findByNonexistentId() {
        Optional<Contato> contato = contatoService.findById(invalidEmail);
        assertFalse(contato.isPresent());
    }

    @Test
    @Order(9)
    @DisplayName("Tentativa de atualizar um contato com e-mail inválido")
    void updateWithInvalidEmail() {
        Contato novosDados = Contato.builder()
                .nome("Eduardo Leite Ranzzani")
                .cep("79050190")
                .telefone("(67) 99246-6935")
                .build();
        try {
            Optional<Contato> atualizado = contatoService.update(invalidEmail, novosDados);
            assertFalse(atualizado.isPresent());
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("erro"));
        }
    }

    @Test
    @Order(10)
    @DisplayName("Tentativa de atualizar um contato com dados inválidos")
    void updateWithInvalidData() {
        Contato novosDados = Contato.builder()
                .nome("Eduardo Leite Ranzzani")
                .cep("00000000")
                .telefone(null)
                .build();
        try {
            Optional<Contato> atualizado = contatoService.update(validEmail, novosDados);
            assertFalse(atualizado.isPresent());
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("erro"));
        }
    }

    @Test
    @Order(11)
    @DisplayName("Atualizando contato pelo e-mail (ID)")
    void update() {
        Contato novosDados = Contato.builder()
                .nome("Eduardo Leite Ranzzani")
                .cep("79050-190")
                .telefone("(67) 99246-6935")
                .build();
        Optional<Contato> atualizado = contatoService.update(validEmail, novosDados);
        assertTrue(atualizado.isPresent());
        System.out.println(atualizado);
        assertNotEquals("79081-200", atualizado.get().getCep());
    }

    @Test
    @Order(12)
    @DisplayName("Tentativa de atualizar um contato via patch com e-mail inválido")
    void patchUpdateWithInvalidEmail() {
        Map<Object, Object> novosDados = new HashMap<>();
        novosDados.put("nome", "Eduardo");

        try {
            Optional<Contato> atualizado = contatoService.patchUpdate(invalidEmail, novosDados);
            assertFalse(atualizado.isPresent());
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("erro"));
        }
    }

    @Test
    @Order(13)
    @DisplayName("Tentativa de atualizar um contato via patch com dados inválidos")
    void patchUpdateWitInvalidData() {
        Map<Object, Object> novosDados = new HashMap<>();
        novosDados.put("cep", "00000000");

        try {
            Optional<Contato> atualizado = contatoService.patchUpdate(validEmail, novosDados);
            assertFalse(atualizado.isPresent());
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("erro"));
        }
    }

    @Test
    @Order(14)
    @DisplayName("Atualizando contato via patch pelo e-mail (ID)")
    void patchUpdate() {
        Map<Object, Object> novosDados = new HashMap<>();
        novosDados.put("cep", "79050-190");

        Optional<Contato> atualizado = contatoService.patchUpdate(validEmail, novosDados);
        assertTrue(atualizado.isPresent());
        System.out.println(atualizado);
        assertNotEquals("79081-200", atualizado.get().getCep());
    }

    @Test
    @Order(15)
    @DisplayName("Apagando contato pelo e-mail (ID)")
    void delete() {
        Optional<Contato> salvo = contatoService.findById(validEmail);
        assertTrue(salvo.isPresent());
        contatoService.delete(salvo.get().getEmail());
        salvo = contatoService.findById(validEmail);
        assertFalse(salvo.isPresent());
    }

    @Test
    @Order(16)
    @DisplayName("Tentativa de apagar um contato inexistente pelo e-mail (ID)")
    void deleteNonexistent() {
        Optional<Contato> salvo = contatoService.findById(invalidEmail);
        assertFalse(salvo.isPresent());
        try {
            contatoService.delete(salvo.get().getEmail());
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("no value present"));
        }
    }
}
