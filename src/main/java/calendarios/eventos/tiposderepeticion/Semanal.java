package calendarios.eventos.tiposderepeticion;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Semanal extends TipoRepeticion {
  private DayOfWeek diaDeLaSemana;

  public Semanal(DayOfWeek diaDeLaSemana) {
    this.diaDeLaSemana = diaDeLaSemana;
  }

  public Boolean verifica(LocalDate dia) {
    return dia.getDayOfWeek() == diaDeLaSemana;
  }
}
