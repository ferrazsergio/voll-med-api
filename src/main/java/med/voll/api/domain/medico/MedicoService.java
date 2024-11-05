package med.voll.api.domain.medico;

import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.infra.exceptions.ResourceNotFoundException;
import med.voll.api.domain.endereco.Endereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    @Autowired
    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public Medico listarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico com ID " + id + " não encontrado."));
    }

    public Medico cadastrar(Medico dados) {
        return medicoRepository.save(dados);
    }

    public Page<DadosListagemMedico> listar(Pageable paginacao) {
        return medicoRepository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
    }

    public Medico atualizar(Long id, DadosAtualizacaoMedico dadosAtualizacaoMedico) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico com ID " + id + " não encontrado."));
        atualizarDados(medico, dadosAtualizacaoMedico);
        return medicoRepository.save(medico);
    }

    public void excluirPorId(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médico com ID " + id + " não encontrado.");
        }
        medicoRepository.deleteById(id);
    }

    public void inativar(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico com ID " + id + " não encontrado."));
        medico.setAtivo(false);
        medicoRepository.save(medico);
    }

    private void atualizarDados(Medico medico, DadosAtualizacaoMedico dadosAtualizacaoMedico) {
        medico.setNome(dadosAtualizacaoMedico.nome());
        medico.setTelefone(dadosAtualizacaoMedico.telefone());

        // Converter DadosEndereco para Endereco
        Endereco endereco = medico.getEndereco();
        DadosEndereco dadosEndereco = dadosAtualizacaoMedico.endereco();
        endereco.setLogradouro(dadosEndereco.logradouro());
        endereco.setCidade(dadosEndereco.cidade());
        endereco.setBairro(dadosEndereco.bairro());
        endereco.setCep(dadosEndereco.cep());
        medico.setEndereco(endereco);
    }
}
