package br.com.claro.testeclaro.repository;

import br.com.claro.testeclaro.model.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, String> {

}
