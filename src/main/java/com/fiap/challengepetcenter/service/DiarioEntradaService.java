package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.DTO.*;
import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiarioEntradaService {

    private final DiarioEntradaRepository diarioEntradaRepository;
    private final PetRepository petRepository;

    @Autowired
    public DiarioEntradaService(DiarioEntradaRepository diarioEntradaRepository, PetRepository petRepository) {
        this.diarioEntradaRepository = diarioEntradaRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public DiarioEntradaResponseDTO salvar(DiarioEntradaRequestDTO requestDTO) {
        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RuntimeException("Pet não encontrado"));

        DiarioEntrada diarioEntrada = new DiarioEntrada();
        diarioEntrada.setPet(pet);
        diarioEntrada.setData(requestDTO.data());
        diarioEntrada.setResumo(requestDTO.resumo());
        diarioEntrada.setHumorGeral(requestDTO.humorGeral());
        diarioEntrada.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaSalvo = diarioEntradaRepository.save(diarioEntrada);

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaSalvo);
    }


    @Transactional(readOnly = true)
    public List<DiarioEntradaResponseDTO> listarTodos() {
        List<DiarioEntrada> entradas = diarioEntradaRepository.findAll();

        return entradas.stream()
                .map(DiarioEntradaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiarioEntradaResponseDTO buscarPorId(Long id) {
        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado"));
        return DiarioEntradaResponseDTO.fromEntity(diarioEntrada);
    }

    @Transactional(readOnly = true)
    public List<DiarioEntradaResponseDTO> buscarPorData(LocalDate data) {
        List<DiarioEntrada> entradas = diarioEntradaRepository.findByData(data);

        return entradas.stream()
                .map(DiarioEntradaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public DiarioEntradaResponseDTO atualizar(Long id, DiarioEntradaRequestDTO requestDTO) {
        DiarioEntrada diarioEntradaExistente = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado"));

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RuntimeException("Pet não encontrado"));

        diarioEntradaExistente.setPet(pet);
        diarioEntradaExistente.setData(requestDTO.data());
        //diarioEntradaExistente.setAtualizadoEm(LocalDateTime.now());
        diarioEntradaExistente.setResumo(requestDTO.resumo());
        diarioEntradaExistente.setHumorGeral(requestDTO.humorGeral());
        diarioEntradaExistente.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaAtualizado = diarioEntradaRepository.save(diarioEntradaExistente);

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaAtualizado);

    }

    @Transactional
    public void deletar(Long id) {
        if (!diarioEntradaRepository.existsById(id)) {
            throw new RuntimeException("DiarioEntrada não encontrado");
        }
        diarioEntradaRepository.deleteById(id);
    }
}

