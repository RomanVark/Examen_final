package ni.edu.uam.practicasprofesionales.ui;
import java.util.List;
public record EntityDefinition<T>(Class<T> type, String singular, String plural, Grupo grupo, List<FieldSpec> fields) {
    @Override public String toString() { return plural; }
}
