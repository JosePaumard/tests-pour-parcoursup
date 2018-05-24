Feature: Algorithme de calcul de l'ordre d'appel section 4.1
  Calcul de l'ordre d'appel dans un groupe soumis
  au seul taux minimum de boursiers

  Scenario Template: Choix du premier candidat
    Given les candidats sont <liste_candidats>
    And le taux minimum de boursiers est <qb>
    When l'appel est calculé
    Then l'ordre d'appel est <ordre_appel>
    Examples:
      | qb | liste_candidats | ordre_appel |
      | 0  | B1 C2           | B1 C2       |
      | 0  | C1 B2           | C1 B2       |
      | 1  | B1 C2           | B1 C2       |
      | 1  | C1 B2           | B2 C1       |

  Scenario Template: Choix du k-ième candidat
    Given les candidats sont <liste_candidats>
    And le taux minimum de boursiers est <qb>
    And les candidats sont <liste_candidats>
    When l'appel est calculé
    Then l'ordre d'appel est <ordre_appel>
    Examples:
      | qb | liste_candidats | ordre_appel    |
      | 0  | C1 C2 C3 C4 C5  | C1 C2 C3 C4 C5 |
      | 0  | B1 B2 B3 B4 B5  | B1 B2 B3 B4 B5 |
      | 0  | B1 B2 C3 C4 C5  | B1 B2 C3 C4 C5 |
      | 0  | C1 C2 C3 B4 B5  | C1 C2 C3 B4 B5 |
      | 1  | C1 C2 C3 C4 C5  | C1 C2 C3 C4 C5 |
      | 1  | B1 C2 C3 C4 C5  | B1 C2 C3 C4 C5 |
      | 1  | B1 C2 C3 B4 C5  | B1 C2 C3 B4 C5 |
      | 1  | B1 C2 C3 C4 B5  | B1 C2 C3 C4 B5 |
      | 1  | B1 C2 C3 B4 B5  | B1 C2 C3 B4 B5 |
      | 30 | C1 C2 C3 C4 C5  | C1 C2 C3 C4 C5 |
      | 30 | B1 C2 C3 C4 C5  | B1 C2 C3 C4 C5 |
      | 30 | B1 C2 C3 B4 C5  | B1 C2 C3 B4 C5 |
      | 30 | B1 C2 C3 C4 B5  | B1 C2 C3 B5 C4 |
      | 30 | B1 C2 C3 B4 B5  | B1 C2 C3 B4 B5 |
      | 40 | B1 C2 C3 B4 B5  | B1 C2 B4 C3 B5 |
