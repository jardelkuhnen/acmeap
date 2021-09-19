package com.jardelkuhnen.acmeap.repository;

import com.jardelkuhnen.acmeap.domain.Fatura;
import com.jardelkuhnen.acmeap.domain.Instalacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, Long> {

    Optional<Fatura> findByCodigo(String codigo);

    List<Fatura> findByInstalacao(Instalacao instalacao);

}
