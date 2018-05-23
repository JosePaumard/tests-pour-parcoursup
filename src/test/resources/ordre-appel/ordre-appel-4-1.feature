Feature: Algorithme de calcul de l'ordre d'appel section 4.1
  Calcul de l'ordre d'appel dans un groupe soumis
  au seul taux minimum de boursiers

  Scenario Template: Choix du premier candidat
    Given l'ensemble d'appel est vide
    And le taux minimum de boursier est <qb>
    And les candidats sont <liste_candidats>
    When l'appel est calculé
    Then le candidat suivant appelé est <candidat_appele>
    Examples:
      | qb | liste_candidats | candidat_appele |
      | 0  | B1 C2           | B1              |
      | 0  | C1 B2           | C1              |
      | 1  | B1 C2           | C2              |
      | 1  | C1 B2           | C1              |

  Scenario Template: Choix du k-ième candidat
    Given l'ensemble d'appel est <choix_en_cours>
    And le taux minimum de boursier est <qb>
    And les candidats sont <liste_candidats>
    When l'appel est calculé
    Then le candidat suivant appelé est <candidat_appele>
    Examples:
      | choix_en_cours | qb | liste_candidats | candidat_appele |
      | C1 C2 C3       | 0  | C4 C5           | C4              |
      | B1 B2 B3       | 0  | B4 B5           | B4              |
      | C1 C2 C3       | 1  | C4 C5           | C4              |
      | B1 C2 C3       | 1  | C4 C5           | C4              |
      | B1 C2 C3       | 1  | B4 C5           | B4              |
      | B1 C2 C3       | 1  | C4 B5           | C4              |
      | B1 C2 C3       | 1  | B4 B5           | B4              |
      | C1 C2 C3       | 40 | C4 C5           | C4              |
      | B1 C2 C3       | 40 | C4 C5           | C4              |
      | B1 C2 C3       | 40 | B4 C5           | B4              |
      | B1 C2 C3       | 40 | C4 B5           | B5              |
      | B1 C2 C3       | 40 | B4 B5           | B4              |
