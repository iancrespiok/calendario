package calendarios.eventos.tiposderepeticion;

import java.time.LocalDate;
import java.time.Period;

public class CadaN extends TipoRepeticion {
  private LocalDate fechaInicio;
  private Period period; // TODO Cambiar a Duration

  public CadaN(LocalDate fechaInicio, Period period) {
    this.fechaInicio = fechaInicio;
    this.period = period;
  }

  public Boolean verifica(LocalDate dia) {
    LocalDate iteracion = fechaInicio;
    if (iteracion.isEqual(dia)) {
      return true;
    }
    while (!iteracion.isAfter(dia)) {
      if (iteracion.isEqual(dia)) {
        return true;
      }
      iteracion = iteracion.plus(period);
    }
    return false;
  }
}
