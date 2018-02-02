# dacpl

## Introduction

The _Dynamic Access Control Product Line (DAC-PL)_ is a case study performed as a proof-of-concept for model-integrating software development. DAC-PL is described in the article "Model-Integrating Software Development" by Mahdi Derakhshanmanesh, Jürgen Ebert, Gregor Engels, and Marvin Grieger, which is going to appear as article in Springer's journal "Software- and Systems Modeling" in 2018.

This site contains the _sources of the case study_ in the state achieved in 2015 during the first year of the MoSAiC-project (supported by Deutsche Forschungsgemeinschaft (DFG) under grants EB 119/11-1 and EN 184/6-1.). They are supplied here to allow readers to have a closer look at the system cited in the article.

## Installation procedure for DAC-PL

When installing the DAC-PL-system, you should have pre-installed 
- Java JDK 8,
- Apache Ant 1.10.1.

1. **Install** Eclipse Mars from www.eclipse.org/mars/

   DAC-PL has been developed with the Eclipse IDE for Java developers using version Mars. To avoid compatibility problems, we recommend to install this version first.

2. **Open** Eclipse and **install** the Eclipse PDE feature.

   The Plugin Development Environment (PDE) supports development and execution of OSGi-components based on Eclipse's Equinox, an implementation of the OSGi core framework specification.

3. **Create** an Eclipse workspace as a directory named "dacpl-ws", **download** JGralab from github.com/jgralab/jgralab, **unzip** it, **rename** it to "jgralab", and **move** it also into "dacpl-ws".

4. **Download** the source directories from [github/jgralab/dacpl/](github/jgralab/dacpl/), also **move** them into "dacpl-ws" as subdirectories, and **change** in Eclipse to this new workspace.

5. **Change** to "jgralab" and **execute**
```
        ant
```

   This step establishes JGraLab as the _modeling infrastructure_ of DAC-PL.

6. **Change** to "dac-meta-model" and **execute**
```
	    ant -buildfile build-metamodel-api.xml
```
	
   This step creates the _language-specific infrastructure_ which is an API generated from the (SUM)-metamodel, offering language-specific create/read/update/delete (CRUD) operations on models.

7. **Import** all DACPL-directories as plugins into "dac-pl".

   Now, all components are transformed to (Equinox)-plugins.

8. **Execute** "dac-simulation-2d" as OSGi-Application.
