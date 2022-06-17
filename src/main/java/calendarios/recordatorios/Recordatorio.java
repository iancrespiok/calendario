package calendarios.recordatorios;

import calendarios.Usuario;
import calendarios.eventos.Evento;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Recordatorio {
  private Evento evento;
  private Duration tiempoAntesOcurrencia;
  private Usuario usuario;

  public Recordatorio(Evento evento, Duration tiempoAntesOcurrencia, Usuario usuario) {
    this.evento = evento;
    this.tiempoAntesOcurrencia = tiempoAntesOcurrencia;
    this.usuario = usuario;
  }

  public Duration getTiempoAntesOcurrencia() {
    return tiempoAntesOcurrencia;
  }

  public Evento getEvento() {
    return evento;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void run(LocalDateTime localDateTime) {
    if (esMomentoDeRecordar(localDateTime)) {
      recordar();
    }
  }

  protected abstract void recordar();

  public Boolean esMomentoDeRecordar(LocalDateTime localDateTime) {
    return evento.sucedeMomento(localDateTime.plus(tiempoAntesOcurrencia));
  }
}
