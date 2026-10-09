# Scolarité App

JavaFX desktop application that displays the training offer of a university (university, collegium, discipline, master, semesters, UEs) as a navigable tree. Built with the MVC architecture and the Composite pattern.

The assignment is available in [docs/sujet.pdf](docs/sujet.pdf).

## Features

- Navigation tree of all levels, with breadcrumb and clickable child cards
- Details of each level: contact (own or inherited), number of distinct UEs, list of UEs
- Detection of shared UEs (counted only once)
- Search by level name, type, UE name or UE code
- Text presentation of any level, with copy to clipboard
- Light and dark themes

## Project structure

```
src/main/java/scolarite/
├── App.java            JavaFX application (builds Model, View, Controller)
├── Launcher.java       Main class (graphical or console mode)
├── model/              Levels, UE groups, UEs, example offer
├── view/               Main view, tree cells, text presentation
└── controller/         MainController
src/main/resources/scolarite/view/style.css
src/test/java/scolarite/model/NiveauTest.java
```

## Requirements

- JDK 21 or later
- Maven 3.9 or later

## How to run

Clone the repository:

```bash
git clone https://github.com/mbianytabs/scolarite-app.git
cd scolarite-app
```

Launch the graphical application:

```bash
mvn javafx:run
```

Run the unit tests:

```bash
mvn test
```

Console mode (prints the text presentation of the whole offer): run the `scolarite.Launcher` class with the `--console` argument, for example from IntelliJ IDEA (Run configuration, Program arguments: `--console`).

In IntelliJ IDEA, you can also simply open the project folder and run `scolarite.Launcher`.
