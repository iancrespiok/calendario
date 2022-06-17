package calendarios.eventos.tiposderepeticion;

import java.time.LocalDate;

public class Diaria extends TipoRepeticion {
  public Diaria() {
  }

  public Boolean verifica(LocalDate dia) {
    return true;
  }
}
