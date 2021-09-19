package com.jardelkuhnen.acmeap.repository;

import com.jardelkuhnen.acmeap.domain.Cliente;
import com.jardelkuhnen.acmeap.domain.Instalacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstalacaoRepository extends JpaRepository<Instalacao, Long> {

    Optional<Instalacao> findByCodigo(String codigo);

    List<Instalacao> findByCliente(Cliente cliente);


}
