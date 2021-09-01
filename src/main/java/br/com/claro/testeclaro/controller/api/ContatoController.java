package br.com.claro.testeclaro.controller.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.claro.testeclaro.model.entity.Contato;
import br.com.claro.testeclaro.service.ContatoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contatos")
public class ContatoController {
    private final ContatoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contato save(@RequestBody @Valid Contato contato) {
        return service.save(contato)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao salvar contato"));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Contato> findAll() {
        return service.findAll();
    }

    @GetMapping("{email}")
    public ResponseEntity<Contato> findById(@PathVariable("email") String email) {
        return ResponseEntity.ok(service.findById(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado")));
    }

    @DeleteMapping("{email}")
    public ResponseEntity<String> delete(@PathVariable("email") String email) {
        service.delete(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado"));
        return ResponseEntity.ok("Contato " + email + " deletado com sucesso");
    }

    @PutMapping("{email}")
    public ResponseEntity<Contato> update(@PathVariable("email") String email, @RequestBody Contato contato) {
        return ResponseEntity.ok(service.update(email, contato)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado")));
    }

    @PatchMapping("{email}")
    public ResponseEntity<Contato> update(@PathVariable("email") String email,
                                          @RequestBody Map<Object, Object> campos) {
        return ResponseEntity.ok(service.patchUpdate(email, campos)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado")));
    }

}
