# Inrupt RDF Wrapping for Java

[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE-OF-CONDUCT.md)

This project adheres to the Contributor Covenant [code of conduct](CODE-OF-CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to [engineering@inrupt.com](mailto:engineering@inrupt.com).

This project provides an API for wrapping Commons-RDF structures
as domain-specific objects.

## Using the libraries as a dependency in your own projects

To use the library in your own project you can add the dependency to your build. An example of adding the project in your pom would be:

```
<properties>
    <inrupt.rdf.wrapping.version>x.x.x</inrupt.rdf.wrapping.version>
</properties>
<dependency>
      <groupId>com.inrupt</groupId>
      <artifactId>inrupt-rdf-wrapping-commons</artifactId>
      <version>${inrupt.rdf.wrapping.version}</version>
    </dependency>
```

## Using this repository locally

After cloning the repository locally you can work with the code as follows:

### Code build

The project can be built with Maven and a Java 11+ build environment.

```bash
    ./mvnw install
```

### Running tests

The repository contains multiple tests. Each module has dedicated unit tests.
By running the following command all tests are run:

```bash
    ./mvnw test
```

#### Code coverage

This project uses JaCoCo for generating the code coverage metric that measures how many lines of code are executed during automated tests. To generate the reports (in different formats) run:


```bash
    ./mvnw verify
```

The reports are then placed in the `report/target/site` folder on the project root.

## Issues & Help

### Solid Community Forum

If you have questions about working with Solid or just want to share what you’re
working on, visit the [Solid forum](https://forum.solidproject.org/). The Solid
forum is a good place to meet the rest of the community.

### Bugs and Feature Requests

- For public feedback, bug reports, and feature requests please file an issue
  via [Github](https://github.com/inrupt/rdf-wrapping-java/issues).
- For non-public feedback or support inquiries please use the [Inrupt Service
  Desk](https://inrupt.atlassian.net/servicedesk).

## Documentation

- [RDF Wrapping documentation](https://inrupt.github.io/rdf-wrapping-java/)

## Changelog

See the [CHANGELOG.md](CHANGELOG.md).

## License

MIT © [Inrupt](https://inrupt.com)
