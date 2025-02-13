package engtelecom.std.labRest.service;

import engtelecom.std.labRest.entities.Pessoa;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

//http://localhost:8080/swagger-ui/index.html

@Component
public class AgendaService {
    private List<Pessoa> agenda;
    private AtomicLong contadorId;

    public AgendaService() {
        this.agenda = new ArrayList<>();
        this.contadorId = new AtomicLong();

        this.adicionarPessoa(new Pessoa("Juca", "juca@hotmail.com"));
        this.adicionarPessoa(new Pessoa("puca", "puca@hotmail.com"));
        this.adicionarPessoa(new Pessoa("cata", "cata@hotmail.com"));
    }

    public Pessoa adicionarPessoa(Pessoa p) {
        p.setId(contadorId.incrementAndGet());
        this.agenda.add(p);
        return p;
    }

    public List<Pessoa> listarTodasPessoas() {
        return this.agenda;
    }

    public Pessoa obterPessoa(Long id){
        return (Pessoa) this.agenda.stream().filter(p -> p.getId() == id).findAny().orElse(null);
        //return (Pessoa) this.agenda.stream().filter(p -> p.getId().equals(id)).findAny().orElse(null);
    }

    public Pessoa atualizar(Pessoa atualizada){
        Pessoa buscada =
                this.agenda.stream().filter(p -> p.getId() == atualizada.getId()).findAny().orElse(null);

        if(buscada != null){
            buscada.setNome(atualizada.getNome());
            buscada.setEmail(atualizada.getEmail());
        }
        return buscada;
    }

    public boolean excluir(Long id){
        return this.agenda.removeIf(p -> p.getId() == id);
    }
}
