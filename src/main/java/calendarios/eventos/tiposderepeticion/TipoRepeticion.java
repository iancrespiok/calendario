package calendarios.eventos.tiposderepeticion;

import java.time.LocalDate;

public abstract class TipoRepeticion {
  public TipoRepeticion() {
  }

  public abstract Boolean verifica(LocalDate dia);
}
