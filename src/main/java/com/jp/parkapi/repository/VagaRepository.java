package com.jp.parkapi.repository;

import com.jp.parkapi.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    Optional<Vaga> findByCodigo(String codigo);
}
