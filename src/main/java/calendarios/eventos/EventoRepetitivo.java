package calendarios.eventos;

import calendarios.Ubicacion;
import calendarios.Usuario;
import calendarios.eventos.tiposderepeticion.TipoRepeticion;
import calendarios.exception.ElEventoYaPaso;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventoRepetitivo extends Evento {
  public LocalDate fechaInicio;
  public LocalDate fechaLimite;
  private TipoRepeticion tipoRepeticion;

  public EventoRepetitivo(String nombre,
                          Ubicacion ubicacion,
                          List<Usuario> invitados,
                          LocalTime horaComienzo,
                          Duration duracion,
                          LocalDate fechaInicio,
                          LocalDate fechaLimite,
                          TipoRepeticion tipoRepeticion) {
    super(nombre, ubicacion, invitados, horaComienzo, duracion);
    this.fechaInicio = fechaInicio;
    this.fechaLimite = fechaLimite;
    this.tipoRepeticion = tipoRepeticion;
  }

  public Boolean ocurreEntre(LocalDate inicioIntervalo, LocalDate finIntervalo) {
    System.out.println("Llegue a ocurre entre " + inicioIntervalo + " y " + finIntervalo);

    LocalDate iteracion = inicioIntervalo;
    while (!iteracion.isEqual(finIntervalo)) {
      System.out.println("Iteracion dia: " + iteracion);
      if (sucedeDia(iteracion)) {
        return true;
      }
      iteracion = iteracion.plusDays(1);
    }
    return false;
  }

  public List<EventoUnico> repeticionesEntre(LocalDate inicioIntervalo, LocalDate finIntervalo) {
    List<EventoUnico> repeticiones = new ArrayList<>();
    LocalDate iteracion = inicioIntervalo;

    while (!iteracion.isEqual(finIntervalo)) {
      if (sucedeDia(iteracion)) {
        EventoUnico repeticionActual = new EventoUnico(getNombre(),
            getUbicacion(),
            getInvitados(),
            getHoraComienzo(),
            getDuracion(),
            iteracion);
        repeticiones.add(repeticionActual);
      }
      iteracion = iteracion.plusDays(1);
    }

    return repeticiones;
  }

  public Boolean sucedeDia(LocalDate dia) {
    return estaEntreFechaInicioYLimite(dia) && tipoRepeticion.verifica(dia);
  }

  public Boolean estaEntreFechaInicioYLimite(LocalDate dia) {
    Boolean estaEntre = dia.isAfter(fechaInicio) && dia.isBefore(fechaLimite);
    Boolean esExtremo = dia.equals(fechaInicio) || dia.equals(fechaLimite);
    return estaEntre || esExtremo;
  }

  public LocalDateTime proximaIteracion(LocalDateTime momento) {
    verificar(momento);

    if (sucedeDia(momento.toLocalDate()) && momento.toLocalTime().isBefore(getHoraComienzo())) {
      return momento.toLocalDate().atTime(getHoraComienzo());
    }

    LocalDate iteracion = momento.toLocalDate().plusDays(1);
    while (!iteracion.equals(fechaLimite)) {
      if (sucedeDia(iteracion)) {
        return iteracion.atTime(getHoraComienzo());
      }
      iteracion = iteracion.plusDays(1);
    }

    return null;
  }

  private void verificar(LocalDateTime momento) {
    if (momento.toLocalDate().isAfter(fechaLimite)) {
      throw new ElEventoYaPaso();
    }
  }

  private LocalDateTime primeraIteracion() {
    LocalDate iteracion = fechaInicio;
    while (!iteracion.equals(fechaLimite.plusDays(1))) {
      if (tipoRepeticion.verifica(iteracion)) {
        return getHoraComienzo().atDate(iteracion);
      }
      iteracion = iteracion.plusDays(1);
    }
    return null;
  }

  private Boolean fechaEstaEntreDosFechas(LocalDate fecha,
                                          LocalDate inicioIntervalo,
                                          LocalDate finIntervalo) {
    return fecha.isAfter(inicioIntervalo) && fecha.isBefore(finIntervalo);
  }

  protected Boolean ocurreElMismoDiaQue(Evento evento) {
    LocalDateTime iteracion = primeraIteracion();
    while (!iteracion.isAfter(fechaLimite.atTime(getHoraComienzo()))) {
      if (evento.sucedeDia(iteracion.toLocalDate())) {
        return true;
      }
      iteracion = proximaIteracion(iteracion);
    }
    return false;
  }


}
