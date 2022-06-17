package calendarios.eventos;

import calendarios.Ubicacion;
import calendarios.Usuario;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public abstract class Evento {
  private String nombre;
  private Ubicacion ubicacion;
  private List<Usuario> invitados;
  private LocalTime horaComienzo;
  private Duration duracion;

  public Evento(String nombre,
                Ubicacion ubicacion,
                List<Usuario> invitados,
                LocalTime horaComienzo,
                Duration duracion) {
    this.nombre = nombre;
    this.ubicacion = ubicacion;
    this.invitados = invitados;
    this.horaComienzo = horaComienzo;
    this.duracion = duracion;
  }

  public LocalTime getHoraFinalizacion() {
    return horaComienzo.plus(duracion);
  }

  public String getNombre() {
    return nombre;
  }

  public Ubicacion getUbicacion() {
    return ubicacion;
  }

  public List<Usuario> getInvitados() {
    return invitados;
  }

  public LocalTime getHoraComienzo() {
    return horaComienzo;
  }

  public Duration getDuracion() {
    return duracion;
  }

  public void setHoraComienzo(LocalTime horaComienzo) {
    this.horaComienzo = horaComienzo;
  }

  public void setDuracion(Duration duracion) {
    this.duracion = duracion;
  }

  public void agregarInvitado(Usuario usuario) {
    invitados.add(usuario);
  }

  public void eliminarInvitado(Usuario usuario) {
    invitados.remove(usuario);
  }

  public void modificarUbicacion(Ubicacion ubicacion) {
    this.ubicacion = ubicacion;
  }

  public Duration cuantoFalta() {
    LocalDateTime now = LocalDateTime.now();
    return Duration.ofHours(now.until(proximaIteracion(now), ChronoUnit.HOURS));
  }

  public long tiempoRestante() {
    return cuantoFalta().toMinutes();
  }


  public abstract LocalDateTime proximaIteracion(LocalDateTime momento);

  public Boolean estaSolapadoCon(Evento evento) {
    Boolean ocurreElMismoDiaQueElEvento = ocurreElMismoDiaQue(evento);
    Boolean laHoraDeInicioEstaEnElIntervaloDelEvento = laHoraDeInicioEstaEnElIntervaloDe(evento);
    return ocurreElMismoDiaQueElEvento && laHoraDeInicioEstaEnElIntervaloDelEvento;
  }

  protected abstract Boolean ocurreElMismoDiaQue(Evento evento);

  private boolean laHoraDeInicioEstaEnElIntervaloDe(Evento evento) {
    Boolean elEventoComienzaMientrasYoOcurro = evento.getHoraComienzo().isAfter(horaComienzo)
        && evento.getHoraComienzo().isBefore(getHoraFinalizacion());
    Boolean yoComienzoMientrasElEventoOcurre = horaComienzo.isAfter(evento.getHoraComienzo())
        && horaComienzo.isBefore(evento.getHoraFinalizacion());
    Boolean comenzamosAlMismoTiempo = horaComienzo.equals(evento.getHoraComienzo());
    return elEventoComienzaMientrasYoOcurro
        || yoComienzoMientrasElEventoOcurre
        || comenzamosAlMismoTiempo;
  }

  public abstract Boolean ocurreEntre(LocalDate inicio, LocalDate fin);

  public abstract Boolean sucedeDia(LocalDate dia);

  public Boolean sucedeMomento(LocalDateTime momento) {
    return sucedeDia(momento.toLocalDate()) && horaComienzo.equals(momento.toLocalTime());
  }

  public Boolean sucedeEntreMomentos(LocalDateTime momentoInicial, LocalDateTime momentoFinal) {
    if (sucedeEntreDias(momentoInicial.toLocalDate(), momentoFinal.toLocalDate())) {
      return horaComienzo.isAfter(momentoInicial.toLocalTime())
          && horaComienzo.isBefore(momentoFinal.toLocalTime());
    }
    return false;
  }

  private Boolean sucedeEntreDias(LocalDate diaInicial, LocalDate diaFinal) {
    LocalDate iteracion = diaInicial;
    while (!iteracion.equals(diaFinal.plusDays(1))) {
      if (sucedeDia(iteracion)) {
        return true;
      }
      iteracion = iteracion.plusDays(1);
    }
    return false;
  }

  public Boolean estaEntreHoraInicioYFin(LocalTime hora) {
    Boolean estaEntre = hora.isAfter(horaComienzo) && hora.isBefore(getHoraFinalizacion());
    Boolean esExtremo = hora.equals(horaComienzo) || hora.equals(getHoraFinalizacion());
    return estaEntre || esExtremo;
  }

  public abstract List<EventoUnico> repeticionesEntre(LocalDate inicio, LocalDate fin);
}

