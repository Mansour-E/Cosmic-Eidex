package cosmic.eidex.guiLogik;

import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.gui.ControllerFX.LobbyController;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyControllerLogik extends LobbyController {
    /**
     * Konstruktor für den LobbyController.
     *
     * @param stageManager      Um JavaFX Szenen zu steuern
     */
    public LobbyControllerLogik(StageManager stageManager) {
        super(stageManager);
    }
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Ruft alle Spieler für den angegebenen Raum vom Server ab.
     * @param raumName Name des Spielraums
     * @return Liste der Spielernamen im Raum
     */
    protected List<String> getSpielerImRaum(String raumName) {

        String url = "http://localhost:8080/spielraum/alle";
        SpielraumDTO[] spielraeume = restTemplate.getForObject(url, SpielraumDTO[].class);
        if(spielraeume != null) {
            for (SpielraumDTO s : spielraeume) {
                if (s.getName().equals(raumName)) {
                    return s.getSpieler().stream().map(
                                    Spieler::getNickname)
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gibt die ID eines Raums anhand des Namens zurück.
     * @param name Name des Raums
     * @return Raum-ID oder null, falls nicht gefunden
     */
    protected Long getRaumIdByName(String name) {
        String url = "http://localhost:8080/spielraum/alle";
        SpielraumDTO[] raeume = restTemplate.getForObject(url, SpielraumDTO[].class);
        if (raeume != null) {
            for (SpielraumDTO dto : raeume) {
                if (dto.getName().equalsIgnoreCase(name)) {
                    return dto.getId();
                }
            }
        }
        return null;
    }

}
