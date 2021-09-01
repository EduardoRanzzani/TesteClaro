package br.com.claro.testeclaro.service.impl;

import br.com.claro.testeclaro.model.Endereco;
import br.com.claro.testeclaro.service.EnderecoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EnderecoServiceImpl implements EnderecoService {

    /**
     * Método chamado na {@link ContatoServiceImpl} para atualizar os dados de endereço a partir de um cep informado.
     *
     * @param cep cep informado no cadastro
     * @return Objeto do tipo {@link Endereco} com os dados obtidos através da API
     */
    @Override
    public Endereco findByCEP(String cep) {
        if (StringUtils.isNotBlank(cep)) {
            RestTemplate template = new RestTemplate();
            UriComponents uri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("viacep.com.br")
                    .path("ws/" + cep + "/json")
                    .build();

            ResponseEntity<Endereco> entity = template.getForEntity(uri.toUriString(), Endereco.class);
            Endereco endereco = entity.getBody();

            if (endereco != null && endereco.getCep() != null) {
                return endereco;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao buscar endereço, CEP Inválido");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao buscar endereço, CEP precisa ser preenchido");
        }
    }

}
