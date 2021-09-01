package br.com.claro.testeclaro.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.claro.testeclaro.model.entity.Contato;

public interface ContatoService {

	Optional<Contato> save(Contato contato);

	List<Contato> findAll();

	Optional<Contato> findById(String email);

	Optional<Contato> delete(String email);

	Optional<Contato> update(String email, Contato contato);

	Optional<Contato> patchUpdate(String email, Map<Object, Object> campos);

}
