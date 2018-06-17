# Copyright (C) 2018 José Paumard
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

@OrdreAppel
Feature: Algorithme de calcul de l'ordre d'appel section 4.1
  Calcul de l'ordre d'appel dans un groupe soumis
  au seul taux minimum de boursiers.

  L'algorithme est défini par une relation de récurrence,
  il suffit donc de constater qu'il fonctionne à un rang
  particulier pour prouver qu'il est implémenté conformément
  à la spécification.
  La convention est la suivante :
  - C est un candidat non boursier
  - B est un candidat boursier

  Scenario Template: Cas dégénéré d'une liste d'un seul candidat
    Given les candidats sont <liste_candidats>
    And le taux minimum de boursiers est <qb>
    When l'appel est calculé
    Then l'ordre d'appel est <ordre_appel>
    Examples:
      | qb | liste_candidats | ordre_appel |
      | 0  | B1              | B1          |
      | 0  | C1              | C1          |
      | 1  | B1              | B1          |
      | 1  | C1              | C1          |


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
