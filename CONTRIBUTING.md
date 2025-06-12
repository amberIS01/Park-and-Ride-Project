# Contributing to Park & Ride

Thank you for your interest in contributing to Park & Ride! This document provides guidelines and instructions for contributing to this project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Environment Setup](#development-environment-setup)
- [Coding Standards](#coding-standards)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Feature Requests](#feature-requests)
- [Community](#community)

## Code of Conduct

Our project adheres to a Code of Conduct that all contributors are expected to follow. Please read [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) before contributing.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR-USERNAME/park-and-ride.git`
3. Add the original repository as upstream: `git remote add upstream https://github.com/original-owner/park-and-ride.git`
4. Create a new branch for your work: `git checkout -b feature/your-feature-name`

## Development Environment Setup

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- MySQL or H2 database

### Setting up the environment
1. Install dependencies: `mvn install`
2. Configure application.properties with your database credentials
3. Run the application: `mvn spring-boot:run`

## Coding Standards

We follow standard Java coding conventions:

- Use 4 spaces for indentation, not tabs
- Class names should be in CamelCase starting with an uppercase letter
- Method names should be in camelCase starting with a lowercase letter
- Constants should be in UPPER_SNAKE_CASE
- Add appropriate Javadoc comments for classes and methods
- Follow the principle of single responsibility
- Write unit tests for new functionality

For frontend code:
- Follow HTML5 standards
- Use Bootstrap classes for styling where appropriate
- Keep JavaScript code modular and well-documented

## Pull Request Process

1. Ensure your code follows our coding standards
2. Update documentation as necessary
3. Include unit tests for new functionality
4. Make sure all tests pass
5. Submit your pull request with a clear description of the changes
6. Link any relevant issues in your pull request description
7. Wait for review from maintainers

## Reporting Bugs

When reporting bugs, please include:

1. A clear and descriptive title
2. Steps to reproduce the bug
3. Expected behavior
4. Actual behavior
5. Screenshots (if applicable)
6. Environment details (OS, browser, Java version, etc.)

Use the issue tracker to report bugs.

## Feature Requests

We welcome feature requests! Please provide:

1. A clear and descriptive title
2. Detailed explanation of the feature
3. Any potential implementation ideas
4. Why this feature would be beneficial

Use the issue tracker to submit feature requests.

## Community

- Join our discussions in the issue tracker
- Follow project updates
- Share the project with others

---

Thank you for contributing to Park & Ride! Your efforts help make this project better for everyone. 