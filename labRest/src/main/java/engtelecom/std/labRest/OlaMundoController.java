package engtelecom.std.labRest;

import engtelecom.std.labRest.entities.Saudacao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OlaMundoController {

    //to execute curl http://localhost:8080/saudacao
    //@GetMapping("/saudacao")
    //public String Ola(){
    /*public Saudacao Ola(){
        return new Saudacao(12, "hello world");
    }*/

    //to execute curl "http://localhost:8080/saudacao?id=5&msg=valor"
    @GetMapping("/saudacao")
    //public String Ola(){
    public Saudacao Ola(@RequestParam(value = "id", defaultValue = "1") long id,
                        @RequestParam(value = "msg", defaultValue = "Ola Mundo") String mensagem) {
        return new Saudacao(id, mensagem);
    }
}
