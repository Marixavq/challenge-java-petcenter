package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.RegistroRequestDTO;
import com.fiap.challengepetcenter.dto.response.RegistroResponseDTO;
import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Registro;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroService {

    private final RegistroRepository registroRepository;
    private final DiarioEntradaRepository diarioEntradaRepository;
    private final UserRepository userRepository;

    @Autowired
    public RegistroService(RegistroRepository registroRepository, DiarioEntradaRepository diarioEntradaRepository, UserRepository userRepository) {
        this.registroRepository = registroRepository;
        this.diarioEntradaRepository = diarioEntradaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RegistroResponseDTO salvar(RegistroRequestDTO requestDTO) {
        User usuarioLogado = getUsuarioAutenticado();

        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(requestDTO.entradaId())
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado com ID: " + requestDTO.entradaId()));

        if (!diarioEntrada.getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode criar um registro no diário de outro usuário");
        }

        Registro registro = new Registro();
        registro.setEntrada(diarioEntrada);
        registro.setTipo(requestDTO.tipo());
        registro.setSubtipo(requestDTO.subtipo());
        registro.setValor(requestDTO.valor());
        registro.setUnidade(requestDTO.unidade());
        registro.setNota(requestDTO.nota());

        Registro registroSalvo = registroRepository.save(registro);

        return RegistroResponseDTO.fromEntity(registroSalvo);
    }

    @Transactional(readOnly = true)
    public Page<RegistroResponseDTO> listarTodos(Pageable pageable) {
        Page<Registro> registros = registroRepository.findAll(pageable);

        return registros.map(RegistroResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public RegistroResponseDTO buscarPorId(Long id) {
        Registro registro = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado com ID: " + id));
        return RegistroResponseDTO.fromEntity(registro);
    }

    @Transactional
    public RegistroResponseDTO atualizar(Long id, RegistroRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Registro registroExistente = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado com ID: " + id));

        if (!registroExistente.getEntrada().getPet().getUser().getId()
                .equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode atualizar um registro de outro usuário");
        }

        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(requestDTO.entradaId())
                .orElseThrow(() -> new RuntimeException("DiarioEntrada não encontrado com ID: " + requestDTO.entradaId()
                ));

        if (!diarioEntrada.getPet().getUser().getId()
                .equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode associar o registro ao diário de outro usuário");
        }

        registroExistente.setEntrada(diarioEntrada);
        registroExistente.setTipo(requestDTO.tipo());
        registroExistente.setSubtipo(requestDTO.subtipo());
        registroExistente.setValor(requestDTO.valor());
        registroExistente.setUnidade(requestDTO.unidade());
        registroExistente.setNota(requestDTO.nota());

        Registro registroAtualizado = registroRepository.save(registroExistente);

        return RegistroResponseDTO.fromEntity(registroAtualizado);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        Registro registroExistente = registroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado com ID: " + id));

        if (!registroExistente.getEntrada().getPet().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não pode excluir um registro de outro usuário");
        }

        registroRepository.deleteById(id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}

