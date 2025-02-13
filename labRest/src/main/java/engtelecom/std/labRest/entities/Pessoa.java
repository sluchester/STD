package engtelecom.std.labRest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
public class Pessoa {
    private Long id;
    @NonNull
    private String nome;
    @NonNull
    private String email;
}
