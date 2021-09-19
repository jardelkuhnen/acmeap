package com.jardelkuhnen.acmeap.controller;

import com.jardelkuhnen.acmeap.domain.Cliente;
import com.jardelkuhnen.acmeap.domain.Fatura;
import com.jardelkuhnen.acmeap.domain.Instalacao;
import com.jardelkuhnen.acmeap.exception.RecursoNotFoundException;
import com.jardelkuhnen.acmeap.repository.ClienteRepository;
import com.jardelkuhnen.acmeap.repository.FaturaRepository;
import com.jardelkuhnen.acmeap.repository.InstalacaoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/faturas")
@Api(value = "Acme AP Fatura Service", produces = MediaType.APPLICATION_JSON_VALUE)
public class FaturaController {

    @Autowired
    private FaturaRepository faturaRepository;

    @Autowired
    private InstalacaoRepository instalacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @ApiOperation(value = "Mostra a lista de faturas")
    // Controle de versão explicito na URI
    @GetMapping
    public List<Fatura> getAllFaturas() {

        ArrayList<Fatura> listaFaturas = new ArrayList<Fatura>();
        try {
            listaFaturas = (ArrayList<Fatura>) faturaRepository.findAll();
        } catch (Exception e) {
            // TODO: handle exception
            throw new RecursoNotFoundException("Erro ao recuperar faturas");
        }


        return listaFaturas;
    }

    @ApiOperation(value = "Consulta uma fatura pelo código")
    @GetMapping("/by-codigo/{codigo}")
    public Optional<Fatura> getFatura(@PathVariable String codigo) {

        Optional<Fatura> fatura = null;

        try {
            fatura = faturaRepository.findByCodigo(codigo);
            if (fatura.get() == null)
                throw new RecursoNotFoundException("codigo de fatura inválido - " + codigo);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RecursoNotFoundException("codigo de fatura inválido - " + codigo);
        }


        return fatura;
    }

    @ApiOperation(value = "Consulta as faturas pelo CPF do cliente")
    @GetMapping("/by-cpf/{cpf}")
    public List<Fatura> getFaturasPorCPF(@PathVariable String cpf) {

        Optional<Cliente> cliente = null;
        List<Instalacao> listaInstalacao;

        try {
            cliente = clienteRepository.findByCpf(cpf);
            if (cliente.get() == null)
                throw new RecursoNotFoundException("CPF - " + cpf);

            listaInstalacao = instalacaoRepository.findByCliente(cliente.get());
        } catch (Exception e) {
            // TODO: handle exception
            throw new RecursoNotFoundException("CPF inválido - " + cpf);
        }

        List<Fatura> listaFaturasCliente = new ArrayList<Fatura>();

        listaInstalacao.stream()
                .forEach(item -> item.getListaFatura().stream().forEach(fatura -> listaFaturasCliente.add(fatura)));

        return listaFaturasCliente;
    }

    @ApiOperation(value = "Gerar uma nova fatura")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> gerarFatura(@RequestBody Fatura fatura) {

        Optional<Instalacao> instalacaoRecuperada;
        URI location = null;

        try {

            instalacaoRecuperada = instalacaoRepository.findByCodigo(fatura.getInstalacao().getCodigo());
            if (instalacaoRecuperada.get() == null)
                throw new RecursoNotFoundException("codigo instalacao - " + fatura.getInstalacao().getCodigo());

            fatura.setInstalacao(instalacaoRecuperada.get());

            Fatura faturaCriada = faturaRepository.save(fatura);
            location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(faturaCriada.getId()).toUri();

        } catch (Exception e) {
            // TODO: handle exception
            throw new RecursoNotFoundException("Erro ao gerar fatura para a instalacao - " + fatura.getInstalacao().getCodigo());
        }

        return ResponseEntity.created(location).build();
    }

}