package lab05.service;

import lab05.model.Frete;
import lab05.model.Cliente;
import lab05.model.Cidade;
import lab05.repository.FreteRepository;
import lab05.repository.ClienteRepository;
import lab05.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FreteService {
    private static final BigDecimal VALOR_FIXO = new BigDecimal("10.00");

    @Autowired
    private FreteRepository freteRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private CidadeRepository cidadeRepository;

    @Transactional
    public Frete cadastrarFrete(Frete frete) {
        Cliente cliente = clienteRepository.findById(frete.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Cidade cidade = cidadeRepository.findById(frete.getCidade().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));
        frete.setCliente(cliente);
        frete.setCidade(cidade);
        frete.setValor(calcularValorFrete(frete.getPeso(), cidade.getTaxaEntrega()));
        return freteRepository.save(frete);
    }

    public BigDecimal calcularValorFrete(BigDecimal peso, BigDecimal taxaEntrega) {
        return peso.multiply(VALOR_FIXO).add(taxaEntrega);
    }

    public Optional<Frete> freteMaisCaro() {
        return freteRepository.findAll().stream().max(Comparator.comparing(Frete::getValor));
    }

    public Optional<Cidade> cidadeComMaisFretes() {
        return freteRepository.findAll().stream()
                .map(Frete::getCidade)
                .collect(java.util.stream.Collectors.groupingBy(c -> c, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey);
    }
}