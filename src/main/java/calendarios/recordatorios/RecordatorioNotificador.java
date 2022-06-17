package calendarios.recordatorios;

import calendarios.Usuario;
import calendarios.eventos.Evento;
import java.time.Duration;

public class RecordatorioNotificador extends Recordatorio {
  public RecordatorioNotificador(Evento evento, Duration tiempoAntesOcurrencia, Usuario usuario) {
    super(evento, tiempoAntesOcurrencia, usuario);
  }

  public void recordar() {
    System.out.println("RECORDATORIO: " +  getEvento().getNombre());
  }
}
