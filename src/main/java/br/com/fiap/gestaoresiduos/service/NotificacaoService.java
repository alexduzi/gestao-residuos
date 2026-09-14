package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.domain.Notificacao;
import br.com.fiap.gestaoresiduos.domain.PontoColeta;
import br.com.fiap.gestaoresiduos.domain.Usuario;
import br.com.fiap.gestaoresiduos.dto.request.NotificacaoRequest;
import br.com.fiap.gestaoresiduos.dto.response.NotificacaoResponse;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.repository.NotificacaoRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import br.com.fiap.gestaoresiduos.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontoColetaRepository pontoColetaRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioRepository usuarioRepository,
                              PontoColetaRepository pontoColetaRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pontoColetaRepository = pontoColetaRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> findByUsuario(Long idUsuario) {
        return notificacaoRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(NotificacaoResponse::from)
                .toList();
    }

    @Transactional
    public NotificacaoResponse marcarLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacao nao encontrada: " + id));
        notificacao.setLida("S");
        notificacao = notificacaoRepository.save(notificacao);
        return NotificacaoResponse.from(notificacao);
    }

    @Transactional
    public NotificacaoResponse create(NotificacaoRequest dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + dto.idUsuario()));
        PontoColeta ponto = pontoColetaRepository.findById(dto.idPonto())
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta nao encontrado: " + dto.idPonto()));

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setPontoColeta(ponto);
        notificacao.setMensagem(dto.mensagem());
        notificacao.setDataEnvio(LocalDate.now());
        notificacao.setLida("N");
        notificacao = notificacaoRepository.save(notificacao);
        return NotificacaoResponse.from(notificacao);
    }
}
