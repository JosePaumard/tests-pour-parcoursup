### Fork d'Algorithmes de Parcoursup

#### Introduction

Ce repository contient un fork de la publication de l'algorithme de Parcoursup disponible ici : [ici](https://framagit.org/parcoursup). 

Cette publication en open source est sans aucun doute bienvenue, mais on peut aussi comprendre que, telle qu'elle est faite, elle apporte également une grande frustration aux personnes qui auraient aimer en savoir plus sur la façon dont l'ordre d'appel de Parcoursup est construit, ainsi que pour celles qui auraient aimer pouvoir vérifier que les algorithmes implémentés correspondent bien à ce qui a été spécifié. 

En tant que développeur, on peut également être frustré par la structure du projet Java en lui-même : l'absence de test notamment.  

Cela dit, l'objectif premier de la publication de Parcoursup étant de satisfaire à des contraintes légales, on ne peut pas dire que ce rôle n'est pas rempli, et l'on ne peut que saluer cette initiative, qui a le mérite de la transparence. 

#### Objectifs

Mon objectif est simplement de montrer qu'à partir du jeu de spécifications fournies dans le document PDF qui accompagne le code Java, il est possible, en suivant les règles du Behavior Driven Development telles qu'énoncées par Kent Beck et Dan North au tout début des années 2000, on peut écrire un jeu de tests qui permettent de valider la correction du fonctionnement de l'algorithme impémenté. 

Ce repository contient donc les éléments suivants : 
- une copie de parcoursup, rangé dans un répertoire /src/main/java
- un pom.xml permettant de gérer les dépendances Maven et le packaging
- une structure de tests écrits en Gherkin
- l'interfaçage de JUnit avec Cucumber permettant d'exécuter ces tests. 

J'insiste sur le fait que les tests Gherkin / Cucumber ont été écrits à partir des spécifications et non pas du code de Parcoursup. Une approche BDD aurait consisté à écrire ces tests en premier, avant de se lancer dans l'écriture du code. 

#### Etat actuel du projet

En cet état de la publication, un peu plus de 400 tests ont été écrits, qui portent essentiellement sur les algorithmes de la section 4. Ils couvrent l'appel des candidats pour les groupes de formation ne possédant par d'internat.

La section 5 définit les algorithmes d'appel des candidats aux groupes de formations comportant des internats. Je suis en train d'écrire ces tests et les publierai lorsqu'ils seront prêts.  