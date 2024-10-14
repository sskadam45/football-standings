package com.pubsapient.football_standings.controller;

import com.pubsapient.football_standings.models.FootballStanding;
import com.pubsapient.football_standings.service.FootballService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/football",produces = "application/json")
public class FootballController {
    @Autowired
    private FootballService footballService;

    @GetMapping("/standings")
    @CrossOrigin(origins = "http://localhost:4200") // Specify the origin
    @Operation(summary = "Get football standings", description = "Retrieves football standings optionally filtered by country, league, and team.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved standings", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FootballStanding.class), examples = {
                            @ExampleObject(
                                    name = "Example response",
                                    summary = "Sample output for the API endpoint",
                                    value = """
                               [{
                                   "country_name": "England",
                                   "league_id": "152",
                                   "league_name": "Premier League",
                                   "team_id": "3077",
                                   "team_name": "Wolves",
                                   "overall_league_position": "20"
                               }]
                               """)
                    })),

                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<?> getStandings(
            @Parameter(description = "Name of the country to filter by") @RequestParam(required = false) String countryName,
            @Parameter(description = "Name of the league to filter by") @RequestParam(required = false) String leagueName,
            @Parameter(description = "Name of the team to filter by") @RequestParam(required = false) String teamName) {

        if (!isValid(countryName) || !isValid(leagueName) || !isValid(teamName)) {
            throw new IllegalArgumentException("Invalid parameters.");
        }
        return ResponseEntity.ok(footballService.getStandings(countryName, leagueName, teamName));
    }

    private boolean isValid(String param) {
        // Implement your parameter validation logic here
        return param == null || param.trim().length() > 0 || param.isEmpty();
    }
}
