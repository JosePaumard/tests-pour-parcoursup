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

Feature: Algorithme de calcul de la position max d'admission d'un internat
  Algorithme de calcul de la position maximum d’admission Bmax

  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 1500 candidats
    And un taux d'ouverture de 100
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 300       |
      | 2    | 340       |
      | 15   | 860       |
      | 30   | 1460      |
      | 31   | 1500      |
      | 45   | 1500      |
      | 60   | 1500      |
      | 61   | 1500      |
      | 65   | 1500      |

  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 1500 candidats
    And un taux d'ouverture de 70
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 300       |
      | 2    | 328       |
      | 15   | 692       |
      | 30   | 1112      |
      | 31   | 1140      |
      | 45   | 1140      |
      | 60   | 1500      |
      | 61   | 1500      |
      | 65   | 1500      |


  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 300 candidats
    And un taux d'ouverture de 100
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 300       |
      | 2    | 300       |
      | 15   | 300       |
      | 30   | 300       |
      | 31   | 300       |
      | 45   | 300       |
      | 60   | 300       |
      | 61   | 300       |
      | 65   | 300       |


  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 300 candidats
    And un taux d'ouverture de 70
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 300       |
      | 2    | 300       |
      | 15   | 300       |
      | 30   | 300       |
      | 31   | 300       |
      | 45   | 300       |
      | 60   | 300       |
      | 61   | 300       |
      | 65   | 300       |

  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 40 candidats
    And un taux d'ouverture de 100
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 40        |
      | 2    | 40        |
      | 15   | 40        |
      | 30   | 40        |
      | 31   | 40        |
      | 45   | 40        |
      | 60   | 40        |
      | 61   | 40        |
      | 65   | 40        |


  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 40 candidats
    And un taux d'ouverture de 70
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 40        |
      | 2    | 40        |
      | 15   | 40        |
      | 30   | 40        |
      | 31   | 40        |
      | 45   | 40        |
      | 60   | 40        |
      | 61   | 40        |
      | 65   | 40        |

  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 299 candidats
    And un taux d'ouverture de 100
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 299       |
      | 2    | 299       |
      | 15   | 299       |
      | 30   | 299       |
      | 31   | 299       |
      | 45   | 299       |
      | 60   | 299       |
      | 61   | 299       |
      | 65   | 299       |


  Scenario Template: Calcul de Bmax
    Given un internat disposant de 300 places
    And une liste de 299 candidats
    And un taux d'ouverture de 70
    When l'appel est lancé le jour <jour>
    Then <candidats> sont appelés
    Examples:
      | jour | candidats |
      | 1    | 299       |
      | 2    | 299       |
      | 15   | 299       |
      | 30   | 299       |
      | 31   | 299       |
      | 45   | 299       |
      | 60   | 299       |
      | 61   | 299       |
      | 65   | 299       |

