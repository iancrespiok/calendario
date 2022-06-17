package calendarios.eventos.tiposderepeticion;

import java.time.LocalDate;

public class Mensual extends TipoRepeticion {
  private int diaDelMes;

  public Mensual(int diaDelMes) {
    this.diaDelMes = diaDelMes;
  }

  public Boolean verifica(LocalDate dia) {
    return diaDelMes == dia.getDayOfMonth();
  }
}
