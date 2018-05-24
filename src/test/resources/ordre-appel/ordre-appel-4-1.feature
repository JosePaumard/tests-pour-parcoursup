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
      | 0  | B1 B2           | B1 B2       |
      | 0  | C1 C2           | C1 C2       |
      | 1  | B1 C2           | B1 C2       |
      | 1  | C1 B2           | B2 C1       |
      | 1  | B1 B2           | B1 B2       |
      | 1  | C1 C2           | C1 C2       |
