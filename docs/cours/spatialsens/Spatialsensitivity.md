---
tags: spatial sensitivity
title: Spatial sensitivity
slideOptions:
  theme: 'white'
  transition: 'fade'
 
---
  <style>
.reveal .slides section img { 
  background: none;
  border: none;
  box-shadow: none;
  display: block;
  margin: 10px auto;
}
</style>

[TOC]

Spatial sensitivity


----
# Introduction

## Context

*Classical problems in geography / spatial sciences : MAUP, scale dependency, spatial non-stationarity*

---
### New approach by OpenMOLE

=> spatial configuration are parameters too

 - Space matters
 - Synthetic generators
 - Sensitivity to data noise

---
### Contents of this module

 - Generation of spatial synthetic data
     - microscopic scale (buildings)
     - mesoscopic scale (population grid)
     - macroscopic scale (system of cities)

 - Perturbation of real datasets

 - Spatial indicators for model outputs

 - Practice




---
# Spatial synthetic data

## General context




---
## Generating building layouts

*At the microscopic scale (district): building layouts*
[Raimbault and Perret, 2019]


---

<img width=300 src=https://miniocodimd.openmole.org:443/codimd/uploads/upload_260338e6eab71fba9ae929e390722ff9.png/>
<img width=350 src=https://miniocodimd.openmole.org:443/codimd/uploads/upload_0b1e4cd937119a5cbbed947ba658f31b.png/>


### Results

![](https://miniocodimd.openmole.org:443/codimd/uploads/upload_58272921ff9d3cbd03ec12560b63c5a5.png)

### Point cloud

![](https://miniocodimd.openmole.org:443/codimd/uploads/upload_f3622e53ef9ccf5b7fad1eeff3929eae.png)





----
### Population grid

*At the mesoscopic scale: population grid*
[Raimbault, 2018]

 - Reaction-diffusion model
 - Urban form measures


----

![](https://miniocodimd.openmole.org:443/codimd/uploads/upload_f2f2a929afa3162e5a2f5df90227a692.png)

---

*PSE on the morphological space*

<img width=400 src=https://miniocodimd.openmole.org:443/codimd/uploads/upload_cc5a01cb6a529febbde6107979e6ee32.png/>






----
## Synthetic systems of cities

*At the macroscopic scale: systems of cities*

 - Evolutive urban theory: systems of cities follow general stylized facts 
 - rank-size law
 - central place theory


----
# Perturbation of data

 *How does noise in real data impacts the result ?*

 - WIP

 *How does perturbation of real data allows to explore scenario*
 - Forcity example

----
# Spatial indicators for model outputs

*In the spatial approach, spatial model indicators are also important: what kind of spatial structure does the model produce ?*

 
 - previous form indicators at different scales
 - spatial statistics

----
## Spatial form as indicators


*spatial correlations ?*

----
## Spatial statistics

(examples)

----
**Moran index**

*Spatial autocorrelation at a given range*

Given spatial weights $w_{ij}$

$$
I = \frac{N}{\sum_{i,j} w_{ij}} \cdot \frac{\sum_{i,j}w_{ij} \cdot (X_i - \bar{X}) (X_j = \bar{X})}{\sum_i (X_i - \bar{X})^2}
$$

----
**Optimal autocorrelation spatial scales**

% example from EnergyPrice ?

----
**Ripley K function**

*Quantifying level of clustering regarding a null model*


----
**Geographically Weighted Regression**






----
# Application: sensitivity to spatial configuration

*Method flowchart*

<img width=500 src=https://miniocodimd.openmole.org:443/codimd/uploads/upload_8a2d364caade89b648fd1ae36cff9362.png/>


----
**Quantification of spatial sensitivity**

Relative distance of phase diagrams

$$ d_r\left(\mu_{\vec{\alpha}_1},\mu_{\vec{\alpha}_2}\right) = 2 \cdot \frac{d(\mu_{\vec{\alpha}_1},\mu_{\vec{\alpha}_2})^2}{Var\left[\mu_{\vec{\alpha}_1}\right] + Var\left[\mu_{\vec{\alpha}_2}\right]}
$$


----
**Application: Schelling model**

*Why could the Schelling model be sensitive to space ?*

(Banos, 2012)

<img width=250 src=https://miniocodimd.openmole.org:443/codimd/uploads/upload_803863b93b3c8fa6b590c3c50c8d41ba.png/>
<img width=250 src=https://miniocodimd.openmole.org:443/codimd/uploads/upload_7b0ec80e4f2f29275ce1d0e4a7521b1f.png/>

----

**Sensitivity of the Schelling model**

![](https://miniocodimd.openmole.org:443/codimd/uploads/upload_3e8af699ecfb51a74499b331972d3f7d.png)


----
**Application: Sugarscape model**

*A model of resource collection*

----





----
# References

Banos, A. (2012). Network effects in Schelling's model of segregation: new evidence from agent-based simulation. Environment and Planning B: Planning and Design, 39(2), 393-405.

Raimbault, J. and Perret, J., 2019. Generating urban morphologies at large scales. *Forthcoming in proceedings of Artifical Life 2019*

Raimbault, J. (2018). Calibration of a density-based model of urban morphogenesis. PloS one, 13(9), e0203516.









