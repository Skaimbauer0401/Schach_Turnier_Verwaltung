
# Chess Tournament Management System

## Project Overview
This application is a comprehensive Chess Tournament Management System designed to facilitate the organization and management of chess tournaments. It provides a platform for both administrators and players to interact with tournament data, track match results, and manage player statistics.

## Features

### User Management
- **User Registration**: Players can create accounts with username and password
- **User Authentication**: Secure login system with password encryption
- **Role-Based Access**: Different interfaces for administrators and regular players
- **Player Statistics**: Tracking of wins, losses, and draws for each player

### Tournament Management
- **Tournament Creation**: Admins can create new tournaments with name, start date, and end date
- **Tournament Editing**: Ability to modify tournament details
- **Tournament Deletion**: Option to remove tournaments from the system
- **Player Participation**: Players can join existing tournaments

### Match Management
- **Match Recording**: System tracks matches between players
- **Result Tracking**: Records wins, losses, and draws
- **Tournament Rankings**: View player standings within tournaments

### User Interface
- **Admin Dashboard**: Comprehensive interface for tournament management
- **Player Dashboard**: Personal view of tournaments and statistics
- **Tournament View**: Detailed information about specific tournaments
- **Responsive Design**: User-friendly interface accessible on different devices

## Technical Architecture

### Backend
- **Framework**: Spring Boot
- **Language**: Java
- **Database**: Derby
- **API**: RESTful web services
- **Security**: Password encryption with secure hashing

### Frontend
- **Technologies**: HTML, CSS, JavaScript
- **Design**: Responsive layout with intuitive user interface
- **Data Exchange**: JSON format for API communication

## Getting Started

### Prerequisites
- Java Development Kit (JDK)
- Maven or Gradle for dependency management
- Database

### Installation
1. Clone the repository
2. Configure the database connection in `src/main/java/threem/update/schach_turnier_verwaltung/backend/database_important/database_connection`
3. Build the project using Maven/Gradle
4. Run the Spring Boot application

### Usage
1. Access the application through a web browser
2. Register as a new user or login with existing credentials
3. Regular users can join tournaments and view their statistics
4. Administrators can create, edit, and manage tournaments

## Contributors
- Samuel Bauer & Sebastian Schönleitner
