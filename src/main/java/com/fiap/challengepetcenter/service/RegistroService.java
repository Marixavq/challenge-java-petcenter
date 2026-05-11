package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.Registro;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository petRepository;

    public Registro salvar(Registro registro) {
        return petRepository.save(registro);
    }

    public List<Registro> listarTodos() {
        return petRepository.findAll();
    }
/*
    public Registro buscarPorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado"));
    }
*/
}