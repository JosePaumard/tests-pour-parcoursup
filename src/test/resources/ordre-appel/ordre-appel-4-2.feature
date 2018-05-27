Feature: Algorithme de calcul de l'ordre d'appel section 4.2
  Calcul de l'ordre d'appel dans un groupe soumis
  à deux taux, boursiers et non-résidents

  Les tests sont rangés dans quatre groupes, suivant
  que le taux de boursier ou de non-résident est contraignant
  ou non.
  Dans tous les cas, on teste que la relation de récurrence
  est bien vérifiée, ce qui suffit à prouver que l'implémentation
  suit bien les spécifications.
  On teste tous les cas, y compris dégénérés, lorsque l'on n'a
  qu'un candidat dans la liste.
  La convention est la suivante :
  - C est un candidat ni boursier ni non-résident
  - B est un candidat boursier non non-résident
  - R est un candidat non boursier et non-résident
  - T est un candidat boursier et non-résident

  Scenario Template: Vérification de l'ordre d'appel
    Given les candidats sont <liste_candidats>
    And le taux minimum de boursiers est 0
    And le taux minimum de résidents est 0
    When l'appel est calculé
    Then l'ordre d'appel est <ordre_appel>
    Examples:
      | liste_candidats | ordre_appel |

      | C1 B2 R3 T4     | C1 B2 R3 T4 |
      | C1 B2 T3 R4     | C1 B2 T3 R4 |
      | C1 R2 B3 T4     | C1 R2 B3 T4 |
      | C1 R2 T3 B4     | C1 R2 T3 B4 |
      | C1 T2 B3 R4     | C1 T2 B3 R4 |
      | C1 T2 R3 B4     | C1 T2 R3 B4 |

      | B1 C2 R3 T4     | B1 C2 R3 T4 |
      | B1 C2 T3 R4     | B1 C2 T3 R4 |
      | B1 R2 C3 T4     | B1 R2 C3 T4 |
      | B1 R2 T3 C4     | B1 R2 T3 C4 |
      | B1 T2 C3 R4     | B1 T2 C3 R4 |
      | B1 T2 R3 C4     | B1 T2 R3 C4 |

      | R1 C2 B3 T4     | R1 C2 B3 T4 |
      | R1 C2 T3 B4     | R1 C2 T3 B4 |
      | R1 B2 C3 T4     | R1 B2 C3 T4 |
      | R1 B2 T3 C4     | R1 B2 T3 C4 |
      | R1 T2 C3 B4     | R1 T2 C3 B4 |
      | R1 T2 B3 C4     | R1 T2 B3 C4 |

      | T1 C2 B3 R4     | T1 C2 B3 R4 |
      | T1 C2 R3 B4     | T1 C2 R3 B4 |
      | T1 B2 C3 R4     | T1 B2 C3 R4 |
      | T1 B2 R3 C4     | T1 B2 R3 C4 |
      | T1 R2 C3 B4     | T1 R2 C3 B4 |
      | T1 R2 B3 C4     | T1 R2 B3 C4 |

      | B1 R2 T3        | B1 R2 T3    |
      | B1 T2 R3        | B1 T2 R3    |
      | R1 B2 T3        | R1 B2 T3    |
      | R1 T2 B3        | R1 T2 B3    |
      | T1 B2 R3        | T1 B2 R3    |
      | T1 R2 B3        | T1 R2 B3    |

      | C1 R2 T3        | C1 R2 T3    |
      | C1 T2 R3        | C1 T2 R3    |
      | R1 C2 T3        | R1 C2 T3    |
      | R1 T2 C3        | R1 T2 C3    |
      | T1 C2 R3        | T1 C2 R3    |
      | T1 R2 C3        | T1 R2 C3    |

      | C1 B2 T3        | C1 B2 T3    |
      | C1 T2 B3        | C1 T2 B3    |
      | B1 C2 T3        | B1 C2 T3    |
      | B1 T2 C3        | B1 T2 C3    |
      | T1 C2 B3        | T1 C2 B3    |
      | T1 B2 C3        | T1 B2 C3    |

      | C1 B2 R3        | C1 B2 R3    |
      | C1 R2 B3        | C1 R2 B3    |
      | B1 C2 R3        | B1 C2 R3    |
      | B1 R2 C3        | B1 R2 C3    |
      | R1 C2 B3        | R1 C2 B3    |
      | R1 B2 C3        | R1 B2 C3    |

      | B1 R2           | B1 R2       |
      | R1 B2           | R1 B2       |

      | C1 R2           | C1 R2       |
      | R1 C2           | R1 C2       |

      | C1 B2           | C1 B2       |
      | B1 C2           | B1 C2       |

      | T1 R2           | T1 R2       |
      | R1 T2           | R1 T2       |

      | C1 T2           | C1 T2       |
      | T1 C2           | T1 C2       |

      | T1 B2           | T1 B2       |
      | B1 T2           | B1 T2       |

      | C1 C2           | C1 C2       |
      | C1 C2           | C1 C2       |

      | C1              | C1          |

      | B1              | B1          |

      | T1              | T1          |


  Scenario Template: Choix du premier candidat
    Given les candidats sont <liste_candidats>
    And le taux minimum de boursiers est 1
    And le taux minimum de résidents est 0
    When l'appel est calculé
    Then l'ordre d'appel est <ordre_appel>
    Examples:
      | liste_candidats | ordre_appel |

      | C1 B2 R3 T4     | B2 C1 R3 T4 |
      | C1 B2 T3 R4     | B2 C1 T3 R4 |
      | C1 R2 B3 T4     | B3 C1 R2 T4 |
      | C1 R2 T3 B4     | T3 C1 R2 B4 |
      | C1 T2 B3 R4     | T2 C1 B3 R4 |
      | C1 T2 R3 B4     | T2 C1 R3 B4 |

      | B1 C2 R3 T4     | B1 C2 R3 T4 |
      | B1 C2 T3 R4     | B1 C2 T3 R4 |
      | B1 R2 C3 T4     | B1 R2 C3 T4 |
      | B1 R2 T3 C4     | B1 R2 T3 C4 |
      | B1 T2 C3 R4     | B1 T2 C3 R4 |
      | B1 T2 R3 C4     | B1 T2 R3 C4 |

      | R1 C2 B3 T4     | B3 R1 C2 T4 |
      | R1 C2 T3 B4     | T3 R1 C2 B4 |
      | R1 B2 C3 T4     | B2 R1 C3 T4 |
      | R1 B2 T3 C4     | B2 R1 T3 C4 |
      | R1 T2 C3 B4     | T2 R1 C3 B4 |
      | R1 T2 B3 C4     | T2 R1 B3 C4 |

      | T1 C2 B3 R4     | T1 C2 B3 R4 |
      | T1 C2 R3 B4     | T1 C2 R3 B4 |
      | T1 B2 C3 R4     | T1 B2 C3 R4 |
      | T1 B2 R3 C4     | T1 B2 R3 C4 |
      | T1 R2 C3 B4     | T1 R2 C3 B4 |
      | T1 R2 B3 C4     | T1 R2 B3 C4 |

      | B1 R2 T3        | B1 R2 T3    |
      | B1 T2 R3        | B1 T2 R3    |
      | R1 B2 T3        | B2 R1 T3    |
      | R1 T2 B3        | T2 R1 B3    |
      | T1 B2 R3        | T1 B2 R3    |
      | T1 R2 B3        | T1 R2 B3    |

      | C1 R2 T3        | T3 C1 R2    |
      | C1 T2 R3        | T2 C1 R3    |
      | R1 C2 T3        | T3 R1 C2    |
      | R1 T2 C3        | T2 R1 C3    |
      | T1 C2 R3        | T1 C2 R3    |
      | T1 R2 C3        | T1 R2 C3    |

      | C1 B2 T3        | B2 C1 T3    |
      | C1 T2 B3        | T2 C1 B3    |
      | B1 C2 T3        | B1 C2 T3    |
      | B1 T2 C3        | B1 T2 C3    |
      | T1 C2 B3        | T1 C2 B3    |
      | T1 B2 C3        | T1 B2 C3    |

      | C1 B2 R3        | B2 C1 R3    |
      | C1 R2 B3        | B3 C1 R2    |
      | B1 C2 R3        | B1 C2 R3    |
      | B1 R2 C3        | B1 R2 C3    |
      | R1 C2 B3        | B3 R1 C2    |
      | R1 B2 C3        | B2 R1 C3    |

      | B1 R2           | B1 R2       |
      | R1 B2           | B2 R1       |

      | C1 R2           | C1 R2       |
      | R1 C2           | R1 C2       |

      | C1 B2           | B2 C1       |
      | B1 C2           | B1 C2       |

      | T1 R2           | T1 R2       |
      | R1 T2           | T2 R1       |

      | C1 T2           | T2 C1       |
      | T1 C2           | T1 C2       |

      | T1 B2           | T1 B2       |
      | B1 T2           | B1 T2       |

      | C1 C2           | C1 C2       |
      | C1 C2           | C1 C2       |

      | C1              | C1          |

      | B1              | B1          |

      | T1              | T1          |

  Scenario Template: Choix du premier candidat
    Given les candidats sont <liste_candidats>
    And le taux minimum de boursiers est 0
    And le taux minimum de résidents est 1
    When l'appel est calculé
    Then l'ordre d'appel est <ordre_appel>
    Examples:
      | liste_candidats | ordre_appel |

      | C1 B2 R3 T4     | R3 C1 B2 T4 |
      | C1 B2 T3 R4     | T3 C1 B2 R4 |
      | C1 R2 B3 T4     | R2 C1 B3 T4 |
      | C1 R2 T3 B4     | R2 C1 T3 B4 |
      | C1 T2 B3 R4     | T2 C1 B3 R4 |
      | C1 T2 R3 B4     | T2 C1 R3 B4 |

      | B1 C2 R3 T4     | R3 B1 C2 T4 |
      | B1 C2 T3 R4     | T3 B1 C2 R4 |
      | B1 R2 C3 T4     | R2 B1 C3 T4 |
      | B1 R2 T3 C4     | R2 B1 T3 C4 |
      | B1 T2 C3 R4     | T2 B1 C3 R4 |
      | B1 T2 R3 C4     | T2 B1 R3 C4 |

      | R1 C2 B3 T4     | R1 C2 B3 T4 |
      | R1 C2 T3 B4     | R1 C2 T3 B4 |
      | R1 B2 C3 T4     | R1 B2 C3 T4 |
      | R1 B2 T3 C4     | R1 B2 T3 C4 |
      | R1 T2 C3 B4     | R1 T2 C3 B4 |
      | R1 T2 B3 C4     | R1 T2 B3 C4 |

      | T1 C2 B3 R4     | T1 C2 B3 R4 |
      | T1 C2 R3 B4     | T1 C2 R3 B4 |
      | T1 B2 C3 R4     | T1 B2 C3 R4 |
      | T1 B2 R3 C4     | T1 B2 R3 C4 |
      | T1 R2 C3 B4     | T1 R2 C3 B4 |
      | T1 R2 B3 C4     | T1 R2 B3 C4 |

      | B1 R2 T3        | R2 B1 T3    |
      | B1 T2 R3        | T2 B1 R3    |
      | R1 B2 T3        | R1 B2 T3    |
      | R1 T2 B3        | R1 T2 B3    |
      | T1 B2 R3        | T1 B2 R3    |
      | T1 R2 B3        | T1 R2 B3    |

      | C1 R2 T3        | R2 C1 T3    |
      | C1 T2 R3        | T2 C1 R3    |
      | R1 C2 T3        | R1 C2 T3    |
      | R1 T2 C3        | R1 T2 C3    |
      | T1 C2 R3        | T1 C2 R3    |
      | T1 R2 C3        | T1 R2 C3    |

      | C1 B2 T3        | T3 C1 B2    |
      | C1 T2 B3        | T2 C1 B3    |
      | B1 C2 T3        | T3 B1 C2    |
      | B1 T2 C3        | T2 B1 C3    |
      | T1 C2 B3        | T1 C2 B3    |
      | T1 B2 C3        | T1 B2 C3    |

      | C1 B2 R3        | R3 C1 B2    |
      | C1 R2 B3        | R2 C1 B3    |
      | B1 C2 R3        | R3 B1 C2    |
      | B1 R2 C3        | R2 B1 C3    |
      | R1 C2 B3        | R1 C2 B3    |
      | R1 B2 C3        | R1 B2 C3    |

      | B1 R2           | R2 B1       |
      | R1 B2           | R1 B2       |

      | C1 R2           | R2 C1       |
      | R1 C2           | R1 C2       |

      | C1 B2           | C1 B2       |
      | B1 C2           | B1 C2       |

      | T1 R2           | T1 R2       |
      | R1 T2           | R1 T2       |

      | C1 T2           | T2 C1       |
      | T1 C2           | T1 C2       |

      | T1 B2           | T1 B2       |
      | B1 T2           | T2 B1       |

      | C1 C2           | C1 C2       |
      | C1 C2           | C1 C2       |

      | C1              | C1          |

      | B1              | B1          |

      | T1              | T1          |


  Scenario Template: Choix du premier candidat
    Given les candidats sont <liste_candidats>
    And le taux minimum de boursiers est 1
    And le taux minimum de résidents est 1
    When l'appel est calculé
    Then l'ordre d'appel est <ordre_appel>
    Examples:
      | liste_candidats | ordre_appel |

      | C1 B2 R3 T4     | T4 C1 B2 R3 |
      | C1 B2 T3 R4     | T3 C1 B2 R4 |
      | C1 R2 B3 T4     | T4 C1 R2 B3 |
      | C1 R2 T3 B4     | T3 C1 R2 B4 |
      | C1 T2 B3 R4     | T2 C1 B3 R4 |
      | C1 T2 R3 B4     | T2 C1 R3 B4 |

      | B1 C2 R3 T4     | T4 B1 C2 R3 |
      | B1 C2 T3 R4     | T3 B1 C2 R4 |
      | B1 R2 C3 T4     | T4 B1 R2 C3 |
      | B1 R2 T3 C4     | T3 B1 R2 C4 |
      | B1 T2 C3 R4     | T2 B1 C3 R4 |
      | B1 T2 R3 C4     | T2 B1 R3 C4 |

      | R1 C2 B3 T4     | T4 R1 C2 B3 |
      | R1 C2 T3 B4     | T3 R1 C2 B4 |
      | R1 B2 C3 T4     | T4 R1 B2 C3 |
      | R1 B2 T3 C4     | T3 R1 B2 C4 |
      | R1 T2 C3 B4     | T2 R1 C3 B4 |
      | R1 T2 B3 C4     | T2 R1 B3 C4 |

      | T1 C2 B3 R4     | T1 C2 B3 R4 |
      | T1 C2 R3 B4     | T1 C2 R3 B4 |
      | T1 B2 C3 R4     | T1 B2 C3 R4 |
      | T1 B2 R3 C4     | T1 B2 R3 C4 |
      | T1 R2 C3 B4     | T1 R2 C3 B4 |
      | T1 R2 B3 C4     | T1 R2 B3 C4 |

      | B1 R2 T3        | T3 B1 R2    |
      | B1 T2 R3        | T2 B1 R3    |
      | R1 B2 T3        | T3 R1 B2    |
      | R1 T2 B3        | T2 R1 B3    |
      | T1 B2 R3        | T1 B2 R3    |
      | T1 R2 B3        | T1 R2 B3    |

      | C1 R2 T3        | T3 C1 R2    |
      | C1 T2 R3        | T2 C1 R3    |
      | R1 C2 T3        | T3 R1 C2    |
      | R1 T2 C3        | T2 R1 C3    |
      | T1 C2 R3        | T1 C2 R3    |
      | T1 R2 C3        | T1 R2 C3    |

      | C1 B2 T3        | T3 C1 B2    |
      | C1 T2 B3        | T2 C1 B3    |
      | B1 C2 T3        | T3 B1 C2    |
      | B1 T2 C3        | T2 B1 C3    |
      | T1 C2 B3        | T1 C2 B3    |
      | T1 B2 C3        | T1 B2 C3    |

      | C1 B2 R3        | B2 R3 C1    |
      | C1 R2 B3        | B3 R2 C1    |
      | B1 C2 R3        | B1 R3 C2    |
      | B1 R2 C3        | B1 R2 C3    |
      | R1 C2 B3        | B3 R1 C2    |
      | R1 B2 C3        | B2 R1 C3    |

      | B1 R2           | B1 R2       |
      | R1 B2           | B2 R1       |

      | C1 R2           | R2 C1       |
      | R1 C2           | R1 C2       |

      | C1 B2           | B2 C1       |
      | B1 C2           | B1 C2       |

      | T1 R2           | T1 R2       |
      | R1 T2           | T2 R1       |

      | C1 T2           | T2 C1       |
      | T1 C2           | T1 C2       |

      | T1 B2           | T1 B2       |
      | B1 T2           | T2 B1       |

      | C1 C2           | C1 C2       |
      | C1 C2           | C1 C2       |

      | C1              | C1          |

      | B1              | B1          |

      | T1              | T1          |
