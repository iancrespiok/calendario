package calendarios;

import calendarios.eventos.Evento;
import calendarios.eventos.EventoUnico;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calendario {
  List<Evento> eventos = new ArrayList<Evento>();

  public Calendario() {
  }

  public List<Evento> getEventos() {
    if (eventos.isEmpty()) {
      return null;
    }
    return eventos;
  }

  public void agendar(Evento evento) {
    eventos.add(evento);
  }

  public boolean estaAgendado(Evento evento) {
    return eventos.contains(evento);
  }

  public List<EventoUnico> eventosEntreFechas(LocalDate inicio, LocalDate fin) {
    List<EventoUnico> listEventos = new ArrayList<>();
    eventos.forEach(evento -> listEventos.addAll(evento.repeticionesEntre(inicio, fin)));
    return listEventos;
  }

  public List<Evento> eventosSolapadosCon(Evento evento) {
    eventos.forEach(e -> System.out.println(e.getNombre()));
    return eventos.stream().filter(e -> e.estaSolapadoCon(evento)).collect(Collectors.toList());
  }
}
