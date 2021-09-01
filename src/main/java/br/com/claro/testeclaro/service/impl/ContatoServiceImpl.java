package br.com.claro.testeclaro.service.impl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import br.com.claro.testeclaro.model.Endereco;
import br.com.claro.testeclaro.model.entity.Contato;
import br.com.claro.testeclaro.repository.ContatoRepository;
import br.com.claro.testeclaro.service.ContatoService;
import br.com.claro.testeclaro.service.EnderecoService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContatoServiceImpl implements ContatoService {
    private final ContatoRepository repository;
    private final EnderecoService enderecoService;

    /**
     * Método chamado na
     * {@link br.com.claro.testeclaro.controller.api.ContatoController} para salvar
     * o {@link Contato} preenchido.
     *
     * @param contato Dados do contato preenchido no endpoint
     * @return Objeto contendo os dados do contato salvo
     */
    @Override
    public Optional<Contato> save(Contato contato) {
        if (StringUtils.isNotBlank(contato.getEmail())) {
            Endereco endereco = enderecoService.findByCEP(contato.getCep());
            contato.setEndereco(endereco.getLogradouro() + ", " + endereco.getBairro());
            contato.setCidade(endereco.getLocalidade());
            contato.setUf(endereco.getUf());

            if (contato.getDataCadastro() == null) {
                contato.setDataCadastro(new Date());
            }
        }
        Contato salvo = repository.save(contato);
        return Optional.of(salvo);
    }

    /**
     * Método chamado na
     * {@link br.com.claro.testeclaro.controller.api.ContatoController} para listar
     * todos os contatos salvos na base de dados.
     *
     * @return lista de contatos salvos
     */
    @Override
    public List<Contato> findAll() {
        return repository.findAll();
    }

    /**
     * Método chamado na
     * {@link br.com.claro.testeclaro.controller.api.ContatoController} para obter
     * os dados do contato informado via e-mail (chave)
     *
     * @param email chave do contato que está sendo buscado
     * @return Objeto contendo os dados do contato informado
     * @throws ResponseStatusException informando que o contato não foi encontrado
     *                                 na base de dados.
     */
    @Override
    public Optional<Contato> findById(String email) {
        return repository.findById(email);
    }

    /**
     * Método chamado na
     * {@link br.com.claro.testeclaro.controller.api.ContatoController} para excluir
     * um contato do banco de dados.
     *
     * @param email chave do contato que será excluído
     * @throws ResponseStatusException informando que o contato não foi encontrado
     *                                 na base de dados.
     * @return
     */
    @Override
    public Optional<Contato> delete(String email) {
        return repository.findById(email).map(contato -> {
            repository.deleteById(contato.getEmail());
            return contato;
        });
    }

    /**
     * Método chamado na
     * {@link br.com.claro.testeclaro.controller.api.ContatoController} para
     * atualizar os dados de um contato informado via e-mail (chave)
     *
     * @param email   chave do contato que está sendo atualizado
     * @param contato Objeto contendo as novas informações do contato que será salvo
     * @return Objeto contendo os dados salvos
     * @throws ResponseStatusException informando que o contato não foi encontrado
     *                                 na base de dados.
     */
    @Override
    public Optional<Contato> update(String email, Contato contato) {
        Optional<Contato> contatoSalvo = findById(email);
        if (contatoSalvo.isPresent()) {
            contato.setEmail(contatoSalvo.get().getEmail());
            contatoSalvo = save(contato);
        }
        return contatoSalvo;
    }

    /**
     * Método chamado na
     * {@link br.com.claro.testeclaro.controller.api.ContatoController} para
     * atualizar os dados de um contato informado via e-mail (chave)
     *
     * @param email  chave do contato que está sendo atualizado
     * @param campos Map contendo quais campos serão atualizados
     * @return Objeto contendo os dados salvos
     * @throws ResponseStatusException informando que o contato não foi encontrado
     *                                 na base de dados.
     */
    @Override
    public Optional<Contato> patchUpdate(String email, Map<Object, Object> campos) {
        Optional<Contato> contatoSalvo = findById(email);
        if (contatoSalvo.isPresent()) {
            Optional<Contato> finalContatoSalvo = contatoSalvo;
            campos.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Contato.class, (String) key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, finalContatoSalvo.get(), value);
            });
            contatoSalvo = save(finalContatoSalvo.get());
        }
        return contatoSalvo;
    }

}
