package calendarios.eventos.tiposderepeticion;

import java.time.LocalDate;
import java.time.Month;

public class Anual extends TipoRepeticion {
  private int diaDelMes;
  private Month mes;

  public Anual(int diaDelMes, Month mes) {
    this.diaDelMes = diaDelMes;
    this.mes = mes;
  }

  public Boolean verifica(LocalDate dia) {
    return (mes == dia.getMonth() && dia.getDayOfMonth() == diaDelMes);
  }
}
