package br.com.claro.testeclaro.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contato")
public class Contato {
    @Id
    @NotNull(message = "Campo e-mail obrigatório!")
    @Email(message = "Campo e-mail está em um formato inválido")
    @Pattern(regexp = ".+@.+\\..+", message = "Campo e-mail está em um formato inválido")
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;
    @NotEmpty(message = "Campo nome obrigatório!")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;
    @NotEmpty(message = "Campo telefone obrigatório!")
    @Column(name = "telefone", nullable = false, length = 15)
    private String telefone;
    @NotEmpty(message = "Campo cep obrigatório!")
    @Column(name = "cep", nullable = false, length = 9)
    private String cep;
    @Column(name = "endereco", length = 255)
    private String endereco;
    @Column(name = "cidade", length = 29)
    private String cidade;
    @Column(name = "uf", length = 2)
    private String uf;
    @Column(name = "data_cadastro")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date dataCadastro;

}
