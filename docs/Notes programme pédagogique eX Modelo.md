# Notes programme pédagogique eX Modelo

- La description ODD du modele zombie est [ici](https://hackmd.openmole.org/bUAKg46dRP-pzLnsGdMFXQ#)



## TODO
### Contenu pédagogique

- [ ] Reformuler le modèle Vigilence :coffee: 
- [ ] Reformuler le modèle Coopération et faire la fiche  :two_men_holding_hands:
- [ ] Calibrer les générateurs spatiaux  :seedling:, analyser la sensibilité du modèle zombie à l'espace :rocket:
- [ ] Calibrer le modèle ODE
- [ ] Préparer la présentation du challenge
- [ ] Fiches Challenge :tada:
    - [ ] Finaliser la fiche Trap :bomb:
    - [ ] Finaliser la fiche Space :space_invader:
    - [ ] Finaliser la fiche Antidote :pill: 
    - [ ] Finaliser la fiche Army :gun: 
- [ ] CM :mortar_board: 
    - [ ] Présentation du modèle "réalité" avec ODD + Schéma fct
    - [ ] CM intro OpenMOLE / prise en main d'OpenMOLE (+ modèles)
    - [ ] CM Stochasticité et Direct Sampling
    - [ ] CM espace ?
    - [ ] CM Analyse de sensibilité (Morris & Saltelli)?
    - [ ] CM Calibration NSGA2 et ABC
    - [ ] CM Profiles
    - [ ] CM PSE
- [ ] Success stories?

### Logistique

- [ ] Connexion internet
- [ ] Faire les essais calcul / CRIANN
- [ ] Définir horaires
- [ ] Répartition salles de cours/TP
- [ ] Boum :tada:
- [ ] Nombre de chambres (combien de personnes par chambre etc.)
- [ ] Récupérer tous les sous


## Jalons du programme pédagogique

- introduction stochasticité
- introduction d'observables sur paramètres utilisé pour la calibration 
- calibration sur jeu de données fourni

## pistes sur le modèle

- nom du modèle ?
- rejouabilité ? seed ? 
- behavior tree / state machine pour la représentation des états ? 
- couplage ODE / ABM pour des mécanismes ?
- Faire un decay pour les zombies
- notions de rôles pour les humains (medics, trappeurs, livreurs de serum, etc), tourelles ?
- cycle attaque / defense (jour nuit par exemple ?)
- zombie drop des items, recoltable par les humains
- des observables (pour pouvoir faire des plots par rapport aux paramètres)
- hierarchies des comportements possibles : priorité à la pose de tourelles, priorité récolte munition, etc. ?

### Explorations thématiques

- travailler sur l'aspect spatial : générer des alternatives du terrain ? sensibilité du modèle à la configuration spatiale (SpaceMatters)
- aide à la décision/scénarios (exemple: optimisation de politiques d'intervention pour améliorer une réponse à la crise)


### Modèles alternatifs
 - une approche ODE à même échelle (objectif : comportement proche)
 - une approche ODE à échelle microscopique ("à-la" MicMac, ex. dans un hôpital)
 - une approche réseau de villes (ODE couplées au niveau macro)?

## Challenge

NB: A ce stade nous considérons que le modèle zombieland est validé et que nous connaissons les valeurs de chacun de ses paramètres. Elles sont mentionnées dans le code dans l'objet `simulation.physic`.

Le challenge consiste à étudier des scénarios sur le modèle zombieland. Les élèves auront à leur disposition une description détaillée du modèle avec : 
 - les mécanismes
 - les paramètres
 - une liste d'observables codés par nos soins
 - des exemples de scripts OpenMOLE pour les aider à formuler leurs questions
 - des exemples de fiches en format challenge sur ce modèle faites par nos soins

L'objectif du challenge sera de construire une réflexion autour du modèle, de la nourrir et de l'étayer par des questions et des explorations OpenMOLE. Les équipes présenteront leur démarche le dernier jour.

Pour préparer le challenge, on peut nous-mêmes faire la démarche. Et construire des fiches sur notre réflexion qui serviront d'exemple aux élèves. Ce travail prendra bien entendu plus de tps que pour les élèves puisque nous partons de rien. Nous devons aussi commencer à construire une fiche détaillée pour expliquer le modèle et les indicateurs. 


Fiches ZombieLand (en cours de rédaction):
 - Intervention militaire (Romain): https://hackmd.openmole.org/3Cuobii4Rt6HmCTKCf_HRQ#
 - Urbanisme zombie-proof (Juste/Julien): https://hackmd.openmole.org/yih61gL7R6-wC-VpI8kvzQ 
- Pièges (Paul/Seb) : https://hackmd.openmole.org/eKRPcjv7TOqPuNlthrQTOg#
- Antidote (François/Hélène) : https://hackmd.openmole.org/ihUpFcKSSmeulee4wIxx4g
- Coopération (Guillaume/Mathieu): https://hackmd.openmole.org/PEzdAlBJRp6qB7bUSX8KXA#

## Bulles
:red_circle: Prioritaire
:computer: Expérimentation
:art: Décorum
:bar_chart: Visualisation
:camel: Infrastructures


1- Scénarisation (Role Play, physique, psychologique, fiche scénario / jdr par groupe ) :art: 

3- Theatralisation :art: 

9- Pedagogie / Méthodes :art: :red_circle: 

8- Support pédagogique et pérénisation :art: :bar_chart:   

12- Questions théoriques :computer: :red_circle: 

4- Mécanismes. Et aussi: ODE ? Réseaux :computer:

11- Mesures :computer: :bar_chart: 

10- ExplorationS :computer: 

13- Organisation spatiale des cours :school: 

5- Infrastructure (Rstudio, chat, fibre, etc) :camel: 


## Issues pour eX Modelo
- [ ] visualiser seulmenent les tâches / capsules tagguées USER dans les executions


## Mécanismes

Fiche complète ici : 
https://hackmd.openmole.org/bUAKg46dRP-pzLnsGdMFXQ?both

# Cas d'étude: Impact de la discretion et de la vigilance sur le nombre d'infections (Profiles) (a mettre sur le gitlab)

::: info
Les zombies attaquent à vue
:::


Lorsqu'ils voient un être humain proche d'eux, ils se mettent à courir vers leur proie. Bien qu'assez lents lorsqu'ils errent sans but et faciles à éviter, ils deviennent plus dangereux lorsqu'ils courent vers leur victimes. 

Les zombies semblent avoir les sens émoussés. On peut s'approcher plus proche d'eux sans faire de bruits et sans être remarqué. Il y a donc deux principales stratégies d'évitement qui sont naturellement apparues dans la population humaine: entretenir une vigilance accrue pour rester à distance de tout zombie en fuyant dès qu'un s'approche ou maximiser sa discretion pour passer inaperçu même au milieu d'une horde. Les zombie ont une vision tout a fait capable, mais il faut un événement sonore pour les sortir de leur torpeur errante.

Nous cherchons à diffuser un message général à la population pour les aider à survivre: il s'agit de les inciter à choisir la vigilance ou la discretion. Le but de cette étude est d'évaluer l'efficacité de chaque stratégie à l'échelle de la population.

L'institut pour l'étude du comportement des zombies (IECZ) a étudié les risques de la détection d'un humain par un zombie en fonction du taux de vigilance de la discretion du premier. La discretion influence la distance à partir de laquelle un humain déclenche l'attaque d'un zombie. La vigilance joue sur la distance à laquelle un être humain voit un zombie approcher. Les études sur des zombies isolés montrent que sans effort particulier de discretion, un être humain déclenche en moyenne l'attaque d'un zombie à 10m. En étant discret, celle-ci peut tomber à 2m en moyenne. Sans effort particulier de vigilance, un être humain voit un zombie approcher en moyenne à 8m. Cette distance peut être accrue à 25m en moyenne chez des humains vigilants. **R : Que se passe t il en cas de position statique ?**

La discretion d'un humain affecte négativement la vitesse de déplacement.

Nous avons conçu un modèle multi-agent spatialisé pour étudier l'effet de la vigilance et de la discretion à une échelle collective.


## Modèle 

Deux ensemble de points, l'un représentant des zombies et l'autre des humains, sont situés dans un plan borné qui représente un espace nu, fermé, sans obstacles. Chaque point se déplace aléatoirement. 

La vigilance et la discretion affectent les distances à laquelle les humains et les zombies se détectent. Plus un humain est vigilant, plus la distance à laquelle il repère un zombie, $d_h$ augmente:
$$d_h = d_{h0} + b * v$$
où $d_{h0}$ est la distance de vision au niveau de vigilence de base, $v$ la vigilance. $b$ est fixé à **???** d'apres les études ...**TODO**.

La discretion limite la vitesse de déplacement des humains. Les humains se déplacent à une vitesse 
$$v_h = v_{h0} - a * d$$
où $v_{h0}$, la vitesse de base de déplacement, est diminuée par la discretion $d$ multipliée par un facteur $a$.  et $v_{h0}$ sont fixés, d'après des observations à **???** et **???**. Le facteur $a$ a été mesuré par des études précédentes à **???**. La vitesse de déplacement des zombies est fixée à **???**

**TODO: à revoir et compléter en fonction du modèle** Lorsqu'un zombie arrive à moins de D1 mètres d'un humain, il le prend en chasse. Lorsqu'un humain est approché par un zombie à moins de D2 mètres, il prend la fuite. Humains et zombies ont une endurance qui leur permet de courir un certain temps. Au delà, leur course cesse. Ils continue à chasser ou fuir à leur vitesse de marche normale. Lorsque la distance entre le zombie et sa proie repasse au dessus de D2 mètres, celui-ci cesse sa chasse. De même, l'humain cesse de fuir dès que son assaillant s'éloigne de plus de D2 mètres de lui.


(inclure la visu du modèle)

On s'intéresse en sortie du modèle au nombre d'humains detectés par des zombies au cours d'une simulation.


## Étude en simulation

Pour décider entre recommander à la population la discrétion ou la vigilance, on veut évaluer l'effet de chacune sur le taux de détection d'humains par les zombies. La méthode des profiles nous permet de savoir comment chaque paramètre joue sur la stratégie optimale.

(inclure la figure résultats des profiles)


----

## Moyens d'intervention

- Faire sonner une siréne => alete les humain
- Faire intervenir l'armée => humain avec bcp endurance et fightBack + comportement aggressif envers les zombies
- Diminuer la capaciter les zombies à contaminer (infection range)
- Diminuer l'endurance des zombies
- Augmenter la vitesse ou l'endurance des humains (distribuer des stéroides)
- Indiquer la position des refuges dans l'environement (panneau d'info)
- Construire des refuges
- Augmenter la perception des humains
- Brouiller les phéromones des zombies
- Modifier la configuration spatiale
- **à completer**


----

# Faits stylisés du modèle Zombieland
:computer: signifie ici "implémenté ou pas loin de l'être si jamais ça compile"

### Déplacement 
- walk/run (**walkSpeed** / **humanRunSpeed** / **zombieRunSpeed**)
- rotation (**humanMaxRotation** / **zombieMaxRotation**)
- champ de potentiel (pour la pente) 
- proba d'épuisement (à chaque pas de temps de course, il y a une proba d'être épuisé et d'arrêtr de courrir). Quand il remarche, il y a 1-proba de récupérer. (**humanExhaustionProbability** / **humanExhaustionProbability**)

### Perception 

- distance (**humanPerception** / **zombiePerception**)
- phéromone, 1 unité de pheromon difussé par les zombies à la poursuite d'un humain sur la case ou il passe pendant la porsuite. Les pheromones s'évaporent à chaque pas de tps (**pheromonEvaporation**)

### Combat

- proba **fightback** pour H: capacité des H à contrer un Z en le tuant. C'est une proba. 1 - fightback = la probabilité pour un Z de zombifier un H ==à coder==

### Communication 
*Informed*: être informé signifie connaître une zone de l'espace où il est interessant d'aller (Rescue Zone par exemple) et où les H vont se diriger dès qu'ils sont au courant
- probabilité de percevoir l'information depuis un H informé: **humanAwarenessProbability**
- probabilité d'être informé au début de la simulation: **humanInformedRatio**


### Structure des populations / Distribution spatiale

==à coder==

- il existe une distribution (statistique) sur les caractéristiqques de la population (H et Z ) 

- OU BIEN la population est homogène

### Interaction 

Peut être vue comme une interaction H→H

- probabilité de suivre un humain qui court **humanFollowModeProbability**


### Espace ? 
Pour le moment , on raisonne à carte fixée (Place Jaude à clermont) pour le zombieland. Dans spatialsens, l'espace est généré au départ.
Mais y aura plein de questions intéressantes à poser sur l'influence de la carte (tore ou pas , close ou pas , zones particulières (safe house ou les Z ne peuvent pas aller, hellpits qui font spawner des Z)

----

## Sous-modèles, questions et méthodes reliées

Idée globale : rattacher à un sous-modèle une ou plusieurs questions, dont on pourrait donner une réponse en appliquant les méthodes OM connues.

Pour les besoins de la théatralisation/scénarisation on peux faire varier les paramètres du modèle fictif pour donner, pour le même sous modèle, des jeux de données un peu différents. Par exemple, on peux donner un jeu de données avec ou sans brouillard pour le sous modèle 1. 

Etude de viabilité : trouver le nombre de zombie au dessus duquel pour tout jeu de paramètres des zombies, on n'observe pas de survie des humains.


### Sous Modèle 1 : Discretion et Vigilence // vigilence


:::info
I used to avoid people like they were zombies before they were zombies. Now that they are all now zombies, I kinda miss people.
*Zombieland, 2009*
:::


Cf le texte de scénarisation plus haut , section «Cas d’étude: Impact de la discretion et de la vigilance sur le nombre d’infections.»

> [name=Romain Reuillon]
> Le param d'évaporation de pheromones peut etre aussi considéré comme un moyen de perception. Ça permetrait d'avoir 3 params pour justifier un peu plus les profiles. Avec 2 on voit direct ce qu'il se passe avec un direct sampling en plotant la sortie.
> 
> Voici la signature du modéle vigilence:
> ```scala
> def vigilence(world: World, humans: Int, zombies: Int, walkSpeed: Double, rotation: Int, infectionRange: Double, humanPerception: Double, zombiePerception: Double, pheromonEvaporation: Double, rng: Random, steps: Int): (List[Simulation], List[Vector[Event]])
> ```
> 
> Les parametres lié au déplacement doivent etre passée puisque ce méchanisme fait parti du modéle. Cependant, on peut donner des valeurs peusdo-calibrées à partir des données pour les fixer. 
> Pour rotation on peut donner la rotation humaine et considérer que c'est la même pour les zombies (meme si c'est pas le cas dans zombieland).

**Objectif du sous-modèle** : étudier l'impact des distances de perception à l'échelle des populations H et Z 

**Espace** : clos, vide,  sans murs, éventuellement torique

**Méthodes** : profils sur distances D& et D2

**Sortie** : nombre d'infection en une journée (à minimiser donc)

**Feedback thématique** : recommandation de comportements aux H : vaut-il mieux être discret ou vigilant ? 

**Paramètres à fixer** : tous sauf D1 et D2 ? 
TODO remplir
**Mécanismes à désactiver** : follow 
course  ? 
TODO remplir 


----
### Sous-Modèle 2 : Design urbain anti-zombies // spatialsens


**Objectif du sous-modèle** : mieux comprendre l'influence de la configuration spatiale de la carte sur la dynamique de l'épidémie.

**Espace** : C'est la chose à étudier : on peut faire varier les paramètres du générateur de carte

**Méthodes** : Calibration / PSE / OSE

**Sortie** : quantification de l'épidémie (e.g. nombre d'infectés à la fin de la journée)

**Feedback thématique** : les structures urbaines "efficaces" pour contrer une épidémie de zombie => recommandations d'ingénierie pour la  guerilla urbaine

**Paramètres à fixer** : tous les paramètres du modèle de Z et H
\+ laisser une petite marge autour des  valeurs des paramètres de Z pour préserver un peu d'incertitude sur les caractéristiques des Z, donc les dynamiques. 

**Mécanismes à désactiver** : Aucun ? TODO  déterminer le niveau de simplification du modèle

----
### Sous Modèle 3 :  modèle ODE marche ou crève (TEAM Survie)

:::info
The infection is spreading faster than anyone could have anticipated.
*Resident Evil: Apocalypse, 2002*
:::

**Objectif du sous-modèle** : modéliser la dynamique des populations pendant l'épidémie en 4 réservoirs de population de H marchant (Hm), H courant (Hc), Z marchant (Zm), Z courant (Zc)

Méthodologiquement : il peut y avoir convergence de modèles entre modèle SMA et ODE (e.g. SMA avec un certain paramétrage de la virulence des infections peut tendre vers un état proche de celui calculé par la résolution du systême d'équations du modèle ODE)

Pour aller plus loin : trouver les conditions dans lesquelles les modèles sont "équivalents fonctionnellement", i.e. calculent des états du monde exprimés en deux formalismes différents, mais présentant les mêmes caractéristiques (quanti ou quali)


**Espace** : aucun lol

**Méthodes** : PSE (pour comparer avec le modèle agent)

**Sortie** : TODO (pré-intuition : écart des sorties de deux modèles)
=> pour l'instant, comparaison des sorties (*i.e.* évolution du nombre de personnes dans chaque catégorie) par moindres carrés

**Feedback thématique** : Peut-on faire confiance aux modèles bizarres des geeks scientifiques de la team opéracheunaule ?
Leurs paramètres sont-ils liés à ceux des autres modèles(SMA) ? 
Comment en tirer parti ? 


**Paramètres à fixer** : à voir selon les paramètres qui peuvent être comparés entre les deux modèles ABM / ODE ? 

**Mécanismes à désactiver** : aucun lol
> [name=Hélène]
> On peut envisager de rajouter des mécanismes pour se rapprocher plus finement du modèle SMA (en théorie).

----

### Sous Modèle 4 : Attention un zombie !! (TEAM TELECOM)

:::info
They're coming to get you, Barbara!
*Night of the Living Dead, 1968*
:::

Possiblement : à fusionner avec le sous-modèle 1 (car ça reste une étude des processus d'interaction des agents et ça concerne les mêmes paramètres de distances de perception/communication)


**Objectif du sous-modèle** : montrer l'effet de la communication entre H 

**Espace** : Carte comprenant une  «rescue zone» , des hatch   

**Méthodes** : Sampling et Profiles

**Sortie** : Quantification de l'épidémie ( nombre d'infectés en fin de simu)

**Feedback thématique** : Recommandations à la population survivante sur la façon/ le besoin de communiquer pour mieux survivre.
Quel message diffuser en boucle à la radio ? 

**Paramètres à fixer** : Tous sauf ceux de la communication (rayon de perception) et eventuellement le nombre de H informés en début de simu (cf. message éducatif préventifs comme pour les séismmes et les tsnuamis)



# Programme

|||||||
|:--:|:--:|:--:|:--:|:--:|:--:|
|VIE **CM** *TD*|Lundi|Mardi|Mercredi|Jeudi|Vendredi|
|8h|:bread:|:cake:|:bread:|:cake:|:bread:|
|9h|**Enjeux école, Presentation OM générale** ; *visite guidée de l'interface: fichiers .oms, prise en main* |**Calibration/Fitness** ///Fitness moindre carré ||||
|10h30|P|A|U|S|E|
|11h|**Pres modele zombie** ; *stochasticité, aggrégation (+plan complet): premiere exploration du modèle zombies Gestion fichiers + visu*|**Application scenario intervention (optimisation)**||||
|12h30|R|E|P|A|S|
|14h|*Plan complet ; analyse sensibilité*|**Profiles** : analyse de resultats de profiles (on prépare un profil sur les 3 paramètres; ils n'en lancent pas)||||
|15h30|P|A|U|S|E|
|16h|*Application autonome sur question spécifique*|*Analyse résultats de l'optim du matin* et **PSE**||||
|17h30|:beers:|:beers:||||
|18h30|Presentations pro 4min|Presentations pro 4min||||
|18h30|R|E|P|A|S|
  
Lundi: début 8h30 ?



20-30 mins : modèle zombies : interface, mécanismes, 


# Course list

(*with required materials*)

### Introduction

 - [ ] Context and objective of the school (*course*)
 - [ ] General presentation of OpenMOLE (*course*)
 - [ ] A guided tour of the interface (*practice*)

### Model presentation
    
 - [ ] Zombie model (includes model GUI) (*practice*)
 - [ ] Replication and direct sampling (*practice*)

### Sensitivity analysis

 - [ ] Complete plan and Saltelli (*practice*)
 - [ ] Autonomous application on a specific question (*practice*)

### Advanced methods
    
 - [ ] Fitness and calibration (*course and practice*)
 - [ ] Optimization applied to intervention scenarios (*practice*) - BIEN PREVOIR QUESTIONS TRES RESTREINTES ET QUESTIONS TRES OUVERTES POUR LES GENS QUI VONT VITE INCLUANT DU COUPLAGE DE TACHES HETEROGENES Trouver une fitness racontant une histoire (minimiser les morts H, maximiser la vitesse de destruction Z, etc: se placer de différents points de vue)
 - [ ] Profiles (*course*)
 - [ ] Result analysis (profile and/or optimization ?) (*practice*)
 - [ ] PSE (*course*)

### Modules

 - [ ] ODE (*course and practice*)
 - [ ] Spatial sensitivity (*course and practice*)
 - [ ] ABC (*course and practice*)

### Challenge

 - [ ] Full model presentation, tutorials presentation (*course*)
 - [ ] Challenge description (objectives, rules) (*course*)
 - [ ] Deliverables (*practice*)










