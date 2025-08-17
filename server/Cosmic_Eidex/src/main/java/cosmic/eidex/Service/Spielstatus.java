package cosmic.eidex.Service;

/**
 * ENUM fuer die verschiedenen Abschnitte in einem Spiel
 */
public enum Spielstatus {

    WARTET_AUF_SPIELSTART{
        @Override
        public String toString() {
            return "Wartet auf Spielstart";
        }
    },
    TRUMPF_WIRD_GEZOGEN{
        @Override
        public String toString() {
            return "Trumpf wird gezogen";
        }
    },
    KARTEN_WERDEN_VERTEILT{
        @Override
        public String toString() {
            return "Karten werden verteilt";
        }
    },
    WARTE_AUF_DRUECKEN{
        @Override
        public String toString() {
            return "Karte druecken";
        }
    },
    WARTET_AUF_SPIELZUEGE{
        @Override
        public String toString() {
            return "Warte auf Spielzuege";
        }
    },
    STICH_WIRD_AUSGEWERTET{
        @Override
        public String toString() {
            return "Stich wird ausgewertet";
        }
    },
    RUNDE_BEENDET{
        @Override
        public String toString() {
            return "Runde beendet";
        }
    },
    PARTIE_BEENDET{
        @Override
        public String toString() {
            return "Partie beendet";
        }
    },
    TURNIER_BEENDET{
        @Override
        public String toString() {
            return "Turnier beendet";
        }
    }

}
