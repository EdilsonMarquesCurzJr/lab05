package lab05.repository;

import lab05.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    Optional<Cidade> findByNome(String nome);
}