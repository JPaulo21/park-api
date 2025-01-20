package com.jp.parkapi.service;

import com.jp.parkapi.entity.Vaga;
import com.jp.parkapi.exception.CodigoUniqueViolationException;
import com.jp.parkapi.exception.EntityNotFoundException;
import com.jp.parkapi.exception.VagaDisponivelException;
import com.jp.parkapi.repository.VagaRepository;
import com.jp.parkapi.util.MessageLocale;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jp.parkapi.entity.Vaga.StatusVaga.LIVRE;

@RequiredArgsConstructor
@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final MessageLocale messageLocale;

    @Transactional
    public Vaga salvar(Vaga vaga){
        try{
            return vagaRepository.save(vaga);
        } catch (DataIntegrityViolationException ex){
            throw new CodigoUniqueViolationException(
                    messageLocale.i18n("exception.vaga.codigo.ja.cadastrado", vaga.getCodigo())
            );
        }
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorCodigo(String codigo){
        return vagaRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException(messageLocale.i18n("exception.vaga.notfound", codigo))
        );
    }

    @Transactional(readOnly = true)
    public Vaga buscarVagaLivre() {
        return vagaRepository.findFirstByStatus(LIVRE).orElseThrow(
                () -> new VagaDisponivelException(messageLocale.i18n("exception.vaga.nao.disponivel"))
        );
    }
}
