package br.com.claro.testeclaro.service;

import br.com.claro.testeclaro.model.Endereco;

public interface EnderecoService {
	Endereco findByCEP(String cep);
}
