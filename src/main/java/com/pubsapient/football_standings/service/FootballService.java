package com.pubsapient.football_standings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubsapient.football_standings.models.FootballStanding;
import com.pubsapient.football_standings.utils.ConnectivityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FootballService {
    private boolean isOffline = false;
    private final ResourceLoader resourceLoader;
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public FootballService(ResourceLoader resourceLoader,
                           RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           @Value("${api.football.base-url}") String baseUrl,
                           @Value("${api.football.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.resourceLoader = resourceLoader;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public List<FootballStanding> getStandings(String countryName, String leagueName, String teamName) {
        checkAndUpdateConnectivityStatus();
        System.out.println("Offline mode->"+isOffline);
        if (isOffline) {
            return getMockData(countryName, leagueName, teamName);
        }
        return fetchFromExternalAPI(countryName, leagueName, teamName);
    }

    private void checkAndUpdateConnectivityStatus() {
        this.isOffline = !ConnectivityUtil.isOnline();
    }


    // without third part library
    private List<FootballStanding> getMockData(String countryName,String leagueName,String teamName) {
        Resource resource = resourceLoader.getResource("classpath:footballStandings.json");
        try {
            String json = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            List<FootballStanding> footballStandings = parseJson(json);
            return getCollect(countryName, leagueName, teamName, footballStandings);
        } catch (IOException e) {
            throw new RuntimeException("Error reading football standings data", e);
        }
    }

    private List<FootballStanding> getCollect(String countryName, String leagueName, String teamName, List<FootballStanding> footballStandings) {
        return footballStandings.stream()
                .filter(s -> isValidParameter(countryName) ? matchesFilter(s.getCountryName(), countryName) : true)
                .filter(s -> isValidParameter(leagueName) ? matchesFilter(s.getLeagueName(), leagueName) : true)
                .filter(s -> isValidParameter(teamName) ? matchesFilter(s.getTeamName(), teamName) : true)
                .collect(Collectors.toList());
    }

    private static List<FootballStanding> parseJson(String json) {
        List<FootballStanding> personList = new ArrayList<>();
        json = json.trim();  // Trim the string to remove leading/trailing white spaces
        json = json.substring(1, json.length() - 1); // Remove the opening and closing square brackets
        json = json.replaceAll("\\s*", ""); // Remove all whitespace, including newlines and spaces
        String[] objects = json.split("\\},\\{");

        for (String object : objects) {
            String cleanedObject = object.replace("{", "").replace("}", "");
            String[] keyValuePairs = cleanedObject.split(",");

            FootballStanding footballStanding = new FootballStanding();
            for (String pair : keyValuePairs) {
                String[] entry = pair.split(":");
                String key = entry[0].replace("\"", "").trim();
                String value = entry[1].replace("\"", "").trim();

                if ("country_name".equals(key)) {
                    footballStanding.setCountryName(value);
                } else if ("league_id".equals(key)) {
                    footballStanding.setLeagueId(value);
                }
                else if ("league_name".equals(key)) {
                    footballStanding.setLeagueName(value);
                }
                else if ("team_id".equals(key)) {
                    footballStanding.setTeamId(value);
                }
                else if ("team_name".equals(key)) {
                    footballStanding.setTeamName(value);
                }
                else if ("overall_league_position".equals(key)) {
                    footballStanding.setOverallLeaguePosition(value);
                }
            }

            personList.add(footballStanding);
        }
        return personList;
    }

    private List<FootballStanding> fetchFromExternalAPI(String countryName, String leagueName, String teamName) {
        try {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("action", "get_standings")
                .queryParam("league_id", 152)
                .queryParam("APIkey", apiKey)
                .toUriString();
        ResponseEntity<FootballStanding[]> response = restTemplate.getForEntity(url, FootballStanding[].class);
        List<FootballStanding> standings = List.of(response.getBody());
        return getCollect( countryName, leagueName, teamName, standings);

        } catch (RestClientException e) {
            throw new RuntimeException("Failed to retrieve data from external API", e);
        }
    }

    private boolean matchesFilter(String field, String filter) {
        return field != null && filter != null && !filter.isEmpty() && field.toLowerCase().contains(filter.toLowerCase());
    }

    private boolean isValidParameter(String param) {
        return param != null && !param.trim().isEmpty() && !param.trim().equalsIgnoreCase("null");
    }
}