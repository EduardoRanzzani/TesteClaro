package br.com.claro.testeclaro.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Endereco {

	private String cep;
	private String logradouro;
	private String bairro;
	private String localidade;
	private String uf;

}
