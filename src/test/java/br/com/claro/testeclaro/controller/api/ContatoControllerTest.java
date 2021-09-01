package br.com.claro.testeclaro.controller.api;

import br.com.claro.testeclaro.model.entity.Contato;
import br.com.claro.testeclaro.service.ContatoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.print.attribute.standard.Media;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContatoController.class)
@DisplayName("Testando classe ContatoController")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContatoControllerTest {

    @MockBean
    private ContatoService contatoService;

    @Autowired
    private MockMvc mockMvc;

    private Contato returnContato;
    private final String URI_DEFAULT = "/api/contatos";
    private final String validEmail = "eduranzzani@gmail.com";
    private final String invalidEmail = "teste.invalid.email";
    private final String nonexistentEmail = "blabla@bla.com";

    @BeforeEach
    void setUp() {
        returnContato = Contato.builder()
                .email(validEmail)
                .nome("Eduardo Leite Ranzzani")
                .telefone("(67) 99246-6935")
                .cep("79050190")
                .endereco("Rua Jornalista Leite Neto")
                .cidade("Campo Grande")
                .uf("MS")
                .dataCadastro(new Date())
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Deve retornar um erro ao tentar salvar um contato com dados inválidos")
    void saveInvalidContato() throws Exception {
        Contato inputContato = Contato.builder()
                .email(invalidEmail)
                .nome("Eduardo Leite Ranzzani")
                .telefone("(67) 99246-6935")
                .cep("79050190")
                .build();

        Mockito.when(contatoService.save(inputContato)).thenReturn(Optional.empty());
        String jsonInput = mapToJson(inputContato);

        mockMvc.perform(MockMvcRequestBuilders.post(URI_DEFAULT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    @Order(2)
    @DisplayName("Deve salvar um contato ao fazer a requisição POST")
    void saveValidContato() throws Exception {
        Contato inputContato = Contato.builder()
                .email(validEmail)
                .nome("Eduardo Leite Ranzzani")
                .telefone("(67) 99246-6935")
                .cep("79050190")
                .build();

        Mockito.when(contatoService.save(inputContato)).thenReturn(Optional.of(returnContato));
        String jsonInput = mapToJson(inputContato);

        mockMvc.perform(MockMvcRequestBuilders.post(URI_DEFAULT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(validEmail)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome", Matchers.is("Eduardo Leite Ranzzani")))
                .andDo(print());
    }

    @Test
    @Order(3)
    @DisplayName("Deve listar todos os contatos ao fazer a requisição GET")
    void findAll() throws Exception {
        Mockito.when(contatoService.findAll()).thenReturn(Arrays.asList(returnContato));

        mockMvc.perform(MockMvcRequestBuilders.get(URI_DEFAULT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", Matchers.is(validEmail)))
                .andDo(print());
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar um erro ao fazer a requisição informando um e-mail inválido")
    void findByInvalidEmail() throws Exception {
        Mockito.when(contatoService.findById(invalidEmail)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(URI_DEFAULT + "/{email}", invalidEmail))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(5)
    @DisplayName("Deve retornar um erro ao tentar localizar um contato que não existe")
    void findByNonexistentEmail() throws Exception {
        Mockito.when(contatoService.findById(nonexistentEmail)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(URI_DEFAULT + "/{email}", nonexistentEmail))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(6)
    @DisplayName("Deve listar um contato que contenha o e-mail informado ao fazer a requisição GET")
    void findByValidEmail() throws Exception {
        Mockito.when(contatoService.findById(validEmail)).thenReturn(Optional.of(returnContato));

        mockMvc.perform(MockMvcRequestBuilders.get(URI_DEFAULT + "/{email}", validEmail))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(validEmail)))
                .andDo(print());
    }


    @Test
    @Order(7)
    @DisplayName("Deve retornar um erro ao tentar apagar um contato inexistente")
    void deleteNonexistentEmail() throws Exception {
        Mockito.when(contatoService.delete(nonexistentEmail)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(URI_DEFAULT + "/{email}", nonexistentEmail))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(8)
    @DisplayName("Deve retornar um erro ao tentar apagar um contato com e-mail inválido")
    void deleteInvalidEmail() throws Exception {
        Mockito.when(contatoService.delete(invalidEmail)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(URI_DEFAULT + "/{email}", invalidEmail))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(8)
    @DisplayName("Deve apagar um contato que contenha o e-mail informado ao fazer a requisição DELETE")
    void delete() throws Exception {
        Mockito.when(contatoService.delete(validEmail)).thenReturn(Optional.of(returnContato));

        mockMvc.perform(MockMvcRequestBuilders.delete(URI_DEFAULT + "/{email}", validEmail))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("Contato " + validEmail + " deletado com sucesso"))
                .andDo(print());
    }

    @Test
    @Order(9)
    @DisplayName("Deve retornar um erro ao tentar atualizar um contato que não existe na requisição PUT")
    void updateNonexistentEmailPut() throws Exception {
        Contato inputContato = Contato.builder()
                .email("eduranzzani@gmail.com")
                .nome("Eduardo Leite Ranzzani")
                .telefone("(67) 999246-6935")
                .cep("79050190")
                .build();

        String jsonInput = mapToJson(inputContato);

        Mockito.when(contatoService.update(nonexistentEmail, inputContato)).thenReturn(Optional.empty());

        mockMvc.perform(put(URI_DEFAULT + "/{email}", nonexistentEmail)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(10)
    @DisplayName("Deve atualizar um contato informado por email na requisição PUT")
    void updatePut() throws Exception {
        Contato contatoUpdate = Contato.builder()
                .email("eduranzzani@gmail.com")
                .nome("Eduardo Leite Ranzzani")
                .telefone("(67) 999246-6935")
                .cep("79081200")
                .build();

        Contato contatoAtualizado = Contato.builder()
                .email("eduranzzani@gmail.com")
                .nome("Eduardo Leite Ranzzani")
                .telefone("(67) 999246-6935")
                .cep("79081200")
                .endereco("Rua Monte Alto")
                .cidade("Campo Grande")
                .uf("MS")
                .dataCadastro(new Date())
                .build();

        String jsonInput = mapToJson(contatoUpdate);

        Mockito.when(contatoService.update(validEmail, contatoUpdate)).thenReturn(Optional.of(contatoAtualizado));

        mockMvc.perform(put(URI_DEFAULT + "/{email}", validEmail)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.endereco", Matchers.is("Rua Monte Alto")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cep", Matchers.is("79081200")))
                .andDo(print());
    }

    @Test
    @Order(11)
    @DisplayName("Deve retornar um erro ao tentar atualizar um contato que não existe na requisição PATCH")
    void updateNonexistentEmailPatch() throws Exception {
        Map<Object, Object> novosDados = new HashMap<>();
        novosDados.put("nome", "Eduardo");
        returnContato.setNome("Eduardo");
        String jsonInput = mapToJson(novosDados);

        Mockito.when(contatoService.patchUpdate(nonexistentEmail, novosDados)).thenReturn(Optional.empty());
        mockMvc.perform(patch(URI_DEFAULT + "/{email}", nonexistentEmail)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    @Order(12)
    @DisplayName("Deve atualizar um contato informado por email na requisição PATCH")
    void updatePatch() throws Exception {
        Map<Object, Object> novosDados = new HashMap<>();
        novosDados.put("nome", "Eduardo");
        returnContato.setNome("Eduardo");
        String jsonInput = mapToJson(novosDados);

        Mockito.when(contatoService.patchUpdate(validEmail, novosDados)).thenReturn(Optional.of(returnContato));
        mockMvc.perform(patch(URI_DEFAULT + "/{email}", validEmail)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }


    /**
     * Maps an Object into a JSON String. Uses a Jackson ObjectMapper.
     */
    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
