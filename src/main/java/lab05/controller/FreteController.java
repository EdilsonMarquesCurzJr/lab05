package lab05.controller;

import lab05.model.Frete;
import lab05.model.Cliente;
import lab05.repository.FreteRepository;
import lab05.repository.ClienteRepository;
import lab05.service.FreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fretes")
public class FreteController {
    @Autowired
    private FreteService freteService;
    @Autowired
    private FreteRepository freteRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Frete frete) {
        try {
            Frete salvo = freteService.cadastrarFrete(frete);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Frete>> listarPorCliente(@PathVariable Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        List<Frete> fretes = freteRepository.findByClienteOrderByValorDesc(cliente);
        return ResponseEntity.ok(fretes);
    }
}