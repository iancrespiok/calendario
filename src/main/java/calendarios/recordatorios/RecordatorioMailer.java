package calendarios.recordatorios;

import calendarios.Usuario;
import calendarios.eventos.Evento;
import calendarios.servicios.ShemailLib;
import java.time.Duration;

public class RecordatorioMailer extends Recordatorio {
  private ShemailLib shemailLib;

  public RecordatorioMailer(Evento evento,
                            Duration tiempoAntesOcurrencia,
                            Usuario usuario,
                            ShemailLib shemailLib) {
    super(evento, tiempoAntesOcurrencia, usuario);
    this.shemailLib = shemailLib;
  }

  public void recordar() {
    shemailLib.enviarMailA(getUsuario().getEmail(),
        "Recordatorio de "
            + getEvento().getNombre(),
        "Hola! Te recordamos que el evento: "
            + getEvento().getNombre()
            + " sucede en "
            + getTiempoAntesOcurrencia());
  }
}
