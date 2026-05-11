package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.model.Registro;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository registroRepository;

    public Registro salvar(Registro registro) {
        return registroRepository.save(registro);
    }

    public List<Registro> listarTodos() {
        return registroRepository.findAll();
    }

    public Registro buscarPorId(Long id) {
        return registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado"));
    }


    public Registro atualizar(Long id, Registro registroAtualizado) {
        Registro registroExistente = buscarPorId(id);
        registroExistente.setEntrada(registroAtualizado.getEntrada());
        registroExistente.setTipo(registroAtualizado.getTipo());
        registroExistente.setSubtipo(registroAtualizado.getSubtipo());
        registroExistente.setValor(registroAtualizado.getValor());
        registroExistente.setUnidade(registroAtualizado.getUnidade());
        registroExistente.setNota(registroAtualizado.getNota());

        return registroRepository.save(registroExistente);
    }

    public void deletar(Long id) {
        if (!registroRepository.existsById(id)) {
            throw new RuntimeException("Registro não encontrado");
        }
        registroRepository.deleteById(id);
    }
}

