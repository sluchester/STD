package engtelecom.std.labRest.controller;

import engtelecom.std.labRest.entities.Pessoa;
import engtelecom.std.labRest.exception.PessoaNaoEncontradaException;
import engtelecom.std.labRest.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/pessoas", "/pessoas/"})
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping
    //obter todas as pessoas que estão no nosso banco de dados
    public List<Pessoa> listarPessoas(){
        return this.agendaService.listarTodasPessoas();
    }

    //retornar pessoa com id informado  /pessoas/
    @GetMapping("/{id}")
    public Pessoa obterPessoaPorId(@PathVariable Long id){
        Pessoa p = this.agendaService.obterPessoa(id);

        if(p == null){
            throw new PessoaNaoEncontradaException(id);
        }
        return p;
    }

    //excluir uma pessoa associada ao id informado na  URL
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirPessoa(@PathVariable Long id){
        if(!this.agendaService.excluir(id)){
            throw new PessoaNaoEncontradaException(id);
        }
    }

    //add uma pessoa ao banco de dados
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pessoa adicionarPessoa(@RequestBody Pessoa pessoa){
        return this.agendaService.adicionarPessoa(pessoa);
    }

    //atualizar pessoa
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Pessoa atualizarPessoa(@RequestBody Pessoa pessoa){
        Pessoa p = this.agendaService.atualizar(pessoa);

        if(p == null){
            throw new PessoaNaoEncontradaException(pessoa.getId());
        }
        return p;
    }

    @ControllerAdvice
    class PessoaNaoEncontrada{
        @ResponseBody
        @ExceptionHandler(PessoaNaoEncontradaException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String pessoaNaoEncontrada(PessoaNaoEncontradaException p){
            return p.getMessage();
        }
    }
}
