"# football-standings" 


### Prerequisites

- Java 17
- Maven

## Running the Application Locally
### Steps

Build the Project:  
- mvn clean package

Run the Application:  
 - mvn spring-boot:run

### Access the Application
 - The application will be accessible at http://localhost:8080/api/football/standings.

### API Documentation
- Swagger UI will be available at http://localhost:8080/swagger-ui.html.
- The API documentation will be available at http://localhost:8080/v3/api-docs.

### API Endpoints
- GET /api/football/standings?{countryName}&{leagueName}&{teamName}
- The endpoint returns the overall league position of a team in a given country and league.

### Sample Request
- GET /api/football/standings?countryName=England&leagueName=Premier League&teamName=Chelsea

### Sample Response
```json
{
  "countryId": 41,
  "countryName": "England",
  "leagueId": 148,
  "leagueName": "Premier League",
  "teamId": 61,
  "teamName": "Chelsea",
  "overallLeaguePosition": 4
}
```

## Run Using Docker
- Build the Docker image: `docker build -t football-standings .`
- Run the Docker container: `docker run -p 8080:8080 football-standings`
- The application will be accessible at http://localhost:8080/api/football/standings.
- Swagger UI will be available at http://localhost:8080/swagger-ui.html.


## Ci/CD Pipeline
- The project uses Jenkins for CI/CD. The Jenkinsfile is configured to build, test, and deploy the application.