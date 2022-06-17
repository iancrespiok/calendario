package calendarios.eventos;

import calendarios.Ubicacion;
import calendarios.Usuario;
import calendarios.exception.ElEventoYaPaso;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventoUnico extends Evento {
  private LocalDate fecha;

  public EventoUnico(String nombre,
                     Ubicacion ubicacion,
                     List<Usuario> invitados,
                     LocalTime horaComienzo,
                     Duration duracion,
                     LocalDate fecha) {
    super(nombre, ubicacion, invitados, horaComienzo, duracion);
    this.fecha = fecha;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public LocalDateTime proximaIteracion(LocalDateTime now) {
    verificar(now);
    return fecha.atTime(getHoraComienzo());
  }

  private void verificar(LocalDateTime momento) {
    if (momento.isAfter(fecha.atTime(getHoraComienzo()))) {
      throw new ElEventoYaPaso();
    }
  }

  public Boolean ocurreEntre(LocalDate inicio, LocalDate fin) {
    return fecha.isAfter(inicio) && fecha.isBefore(fin);
  }

  public Boolean sucedeDia(LocalDate dia) {
    return dia.equals(fecha);
  }

  public List<EventoUnico> repeticionesEntre(LocalDate inicio, LocalDate fin) {
    List<EventoUnico> eventos = new ArrayList<>();
    if (ocurreEntre(inicio, fin)) {
      eventos.add(this);
    }
    return eventos;
  }

  protected Boolean ocurreElMismoDiaQue(Evento evento) {
    return evento.sucedeDia(fecha);
  }
}
