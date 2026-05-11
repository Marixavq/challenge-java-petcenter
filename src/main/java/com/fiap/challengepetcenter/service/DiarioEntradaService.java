package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiarioEntradaService {

    @Autowired
    private DiarioEntradaRepository diarioEntradaRepository;

    public DiarioEntrada salvar(DiarioEntrada diarioEntrada) {
        return diarioEntradaRepository.save(diarioEntrada);
    }

    public List<DiarioEntrada> listarTodos() {
        return diarioEntradaRepository.findAll();
    }

    public DiarioEntrada buscarPorId(Long id) {
        return diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado"));
    }

    public List<DiarioEntrada> buscarPorData(LocalDate data) {
        return diarioEntradaRepository.findByData(data);
    }

    public DiarioEntrada atualizar(Long id, DiarioEntrada diarioEntradaAtualizado) {
        DiarioEntrada diarioEntradaExistente = buscarPorId(id);
        diarioEntradaExistente.setPet(diarioEntradaAtualizado.getPet());
        diarioEntradaExistente.setData(diarioEntradaAtualizado.getData());
        //diarioEntradaExistente.setAtualizadoEm(LocalDateTime.now());
        diarioEntradaExistente.setResumo(diarioEntradaAtualizado.getResumo());
        diarioEntradaExistente.setHumorGeral(diarioEntradaAtualizado.getHumorGeral());
        diarioEntradaExistente.setStatus(diarioEntradaAtualizado.getStatus());

        return diarioEntradaRepository.save(diarioEntradaExistente);
    }

    public void deletar(Long id) {
        if (!diarioEntradaRepository.existsById(id)) {
            throw new RuntimeException("DiarioEntrada não encontrado");
        }
        diarioEntradaRepository.deleteById(id);
    }
}

