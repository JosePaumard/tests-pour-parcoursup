### Fork d'Algorithmes de Parcoursup

#### Introduction

Ce dépôt contient une bifurcation de la publication de l'algorithme de Parcoursup disponible ici sur [framagit.org/parcoursup/algorithmes-de-parcoursup](https://framagit.org/parcoursup/algorithmes-de-parcoursup).

Cette publication en accès libre et sous licence libre est sans aucun doute bienvenue, mais on peut aussi comprendre que, telle qu'elle est faite, elle apporte également une grande frustration aux personnes qui auraient aimer en savoir plus sur la façon dont l'ordre d'appel de Parcoursup est construit, ainsi que pour celles qui auraient aimer pouvoir vérifier que les algorithmes implémentés correspondent bien à ce qui a été spécifié.

En tant que développeur, on peut également être frustré par la structure du projet Java en lui-même : l'absence de test notamment.

Cela dit, l'objectif premier de la publication de Parcoursup étant de satisfaire à des contraintes légales, on ne peut pas dire que ce rôle n'est pas rempli, et l'on ne peut que saluer cette initiative, qui a le mérite de la transparence.

#### Objectifs

Mon objectif est simplement de montrer qu'à partir du jeu de spécifications fournies dans [le document PDF](https://framagit.org/parcoursup/algorithmes-de-parcoursup/blob/master/doc/presentation_algorithmes_parcoursup.pdf) qui accompagne le code Java, il est possible, un jeu de tests qui permettent de valider la correction du fonctionnement de l'algorithme implémenté, en suivant [les règles du Behavior Driven Development](https://skillsmatter.com/skillscasts/923-how-to-sell-bdd-to-the-business) telles qu'énoncées par Kent Beck et Dan North au tout début des années 2000.

Ce dépôt contient donc les éléments suivants :
- une copie de [parcoursup](framagit.org/parcoursup/algorithmes-de-parcoursup), rangé dans un répertoire `/src/main/java`,
- un `pom.xml` permettant de gérer les dépendances [Maven](https://maven.apache.org/) et l'empaquetage,
- une structure de tests écrits en [Gherkin](https://github.com/cucumber/gherkin-java),
- l'interfaçage de [JUnit](https://junit.org/junit5/) avec [Cucumber](https://github.com/cucumber/cucumber) permettant d'exécuter ces tests.

J'insiste sur le fait que les tests Gherkin / Cucumber ont été écrits *à partir des spécifications*, et non pas du code de Parcoursup. Une approche BDD aurait consisté à écrire ces tests en premier, avant de se lancer dans l'écriture du code.

#### État actuel du projet

La section 4 ("ordreappel") est couverte par 260 tests.
L'algorithme porte sur l'appel des candidats en fonction des critères suivants :

- le classement;
- le fait que le candidat est boursier ou non;
- le fait que le candidat est local ou non.

La section 5 ("propositions") comporte la définition de 2 algorithmes :

- le calcul d'un paramètre B, qui constitue le classement du dernier candidat appelé dans pour une formation. On comprend que ce classement doit progresser au fur et à mesure que les candidats acceptent des formations, et par ricochet, se désistent pour d'autres. Dans la pratique, une formation de 50 places ne recrute que rarement les 50 premiers candidats qu'elle reçoit. 72 tests valident le calcul de B.
- Ce calcul du paramètre B est ensuite utilisé pour appeler des candidats aux formations Parcoursup. Cet ordre d'appel est assez complexe, et rendu encore plus complexe si les formations comportent des internats. Cette partie est couverte par 64 tests.

----

#### Remerciements

Je tiens à remercier [Hugo Gimbert](http://www.labri.fr/perso/gimbert/#Code), auteur de Parcoursup, pour son aimable soutien dans ce travail.

#### [Licence](License.txt)
(C) 2018 José Paumard, Apache Licence
