package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.DTO.VeterinarioRequestDTO;
import com.fiap.challengepetcenter.DTO.VeterinarioResponseDTO;
import com.fiap.challengepetcenter.exception.DiarioEntradaComDependenciasException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.TipoUsuario;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.model.Veterinario;
import com.fiap.challengepetcenter.repository.PetVeterinarioRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import com.fiap.challengepetcenter.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UserRepository userRepository;
    private final PetVeterinarioRepository petVeterinarioRepository;


    @Autowired
    public VeterinarioService(VeterinarioRepository veterinarioRepository, UserRepository userRepository, PetVeterinarioRepository petVeterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
        this.userRepository = userRepository;
        this.petVeterinarioRepository = petVeterinarioRepository;
    }

    @Transactional
    public VeterinarioResponseDTO salvar(VeterinarioRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + requestDTO.userId()));

        if (user.getTipoUsuario() != TipoUsuario.VETERINARIO) {
            throw new RecursoNaoEncontradoException("O usuário informado não possui perfil de veterinário");
        }
        
        Veterinario veterinario = new Veterinario();
        veterinario.setUser(user);
        veterinario.setCrmv(requestDTO.crmv());
        veterinario.setEspecialidade(requestDTO.especialidade());
        veterinario.setDescricao(requestDTO.descricao());

        Veterinario veterinarioSalvo = veterinarioRepository.save(veterinario);

        return VeterinarioResponseDTO.fromEntity(veterinarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> listarTodos(Pageable pageable) {
        return veterinarioRepository.findAll(pageable)
                .map(VeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorId(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinario não encontrado com ID: " + id));
        return VeterinarioResponseDTO.fromEntity(veterinario);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> buscarPorUserId(Long userId, Pageable pageable) {
        return veterinarioRepository.findByUserId(userId, pageable)
                .map(VeterinarioResponseDTO::fromEntity);
    }

    @Transactional
    public VeterinarioResponseDTO atualizar(Long id, VeterinarioRequestDTO requestDTO) {
        Veterinario veterinarioExistente = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado com ID: " + id));

        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + requestDTO.userId()));

        veterinarioExistente.setUser(user);
        veterinarioExistente.setCrmv(requestDTO.crmv());
        veterinarioExistente.setEspecialidade(requestDTO.especialidade());
        veterinarioExistente.setDescricao(requestDTO.descricao());

        Veterinario veterinarioAtualizado = veterinarioRepository.save(veterinarioExistente);

        return VeterinarioResponseDTO.fromEntity(veterinarioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!veterinarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Veterinário não encontrado com ID: " + id);
        }

        if (petVeterinarioRepository.existsByVeterinario_IdAndAtivo(id, true)) {
            throw new DiarioEntradaComDependenciasException("Não é possível excluir o veterinário pois existem pets vinculados a ele");
        }

        veterinarioRepository.deleteById(id);
    }

}
