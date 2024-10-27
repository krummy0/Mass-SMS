package MassSMS.MassSMS;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SMS {
    private List<List<String>> smsAdresses = new ArrayList<>(); //List of leads. Each lead contains a list of resolved gateways
    private List<Map<String, String>> leads;
    private JSONArray carrierMap;
    private int valid = 0;
    private int invalid = 0;
    
    public SMS(String lead, String apiKey, boolean usaCanada) {
        List<Map<String, String>> leads = new ArrayList<>();
        Map<String, String> leadMap = new HashMap<>();
        leadMap.put("number", lead);
        leads.add(leadMap);
        List<String> apiList = new ArrayList<>();
        apiList.add(apiKey);

        setup(leads, apiList, usaCanada);
    }

    public SMS(List<Map<String, String>> leads, List<String> apiKey, boolean usaCanada) {
    	setup(leads, apiKey, usaCanada);
    }
    
    public void setup(List<Map<String, String>> leads, List<String> apiKey, boolean usaCanada) {
    	carrierMap = readJsonFile("Resources/map.json");
    	this.leads = leads;
    	
    	String apiCode = "CU";
    	if (!usaCanada)
    		apiCode = "CI";
        List<JSONObject> json = new ArrayList<JSONObject>();
        for (int i = 0; i < leads.size(); i++) {
        	String url = "https://api.data247.com/v3.0?key=" + apiKey.get(i % apiKey.size()) +
        				 "&api=" + apiCode;
        	String next = url + "&phone=" + leads.get(i).get("number");
        	next += "&addfields=sms_address";
        	JSONObject result = sendGetRequest(next);
        	json.add(result);
        }
        
        
        for (JSONObject resultObj : json) {
            JSONObject response = (JSONObject) resultObj.get("response");
            String responseStatus = (String) response.get("status");
            
            if ("OK".equals(responseStatus)) {
                // Get the results array
                JSONArray resultsArray = (JSONArray) response.get("results");
                
                // Iterate through each result
                for (Object obj : resultsArray) {
                	List<String> tmp = new ArrayList<>();
                    JSONObject result = (JSONObject) obj;
                    String send = (String) result.get("sms_address");
                    if (send != null && !send.isBlank()) {
                        tmp.add(send);
                        smsAdresses.add(tmp);
                    } else {
                        String carrier = (String) result.get("carrier_name");
                        String country = (String) result.get("country");
                        String phone = (String) result.get("phone");
                        if (carrier.contains(":"))
                        	carrier = carrier.split(":")[0];
                        else if (carrier.contains("/"))
                        	carrier = carrier.split("/")[0];
                        if (carrier.contains("Wireless"))
                        	carrier = carrier.split("Wireless")[0];
                        
                        List<JSONObject> carrierList = findMatchingCarriers(carrier, country);
                        for (JSONObject carrierObj : carrierList) {
                        	String at = (String) carrierObj.get("email-to-sms");
                        	tmp.add(phone + at);
                        	valid++;
                        }
                        smsAdresses.add(tmp);
                    }
                }
            } else {
                System.out.println("Response status is not OK: " + responseStatus);
                invalid++;
                //keep index inline by adding null if invalid
                smsAdresses.add(null);
            }
        }
    }
    
    public Map<String, String> getPersonal(int index) {
    	return leads.get(index);
    }
    
    private List<JSONObject> findMatchingCarriers(String carrier, String country) {
        List<JSONObject> matchingCarriers = new ArrayList<>();

        // Normalize the country string
        String trimmedCountry = country.trim().toLowerCase();

        // Split the carrier string into words
        String[] carrierWords = carrier.trim().toLowerCase().split("\\s+");

        for (Object obj : carrierMap) {
            JSONObject jsonObject = (JSONObject) obj;

            // Normalize the country from the JSON object
            String jsonCountry = ((String) jsonObject.get("country")).trim().toLowerCase();

            // Check if the countries match
            if (jsonCountry.equals(trimmedCountry)) {
                String jsonCarrier = ((String) jsonObject.get("carrier")).trim().toLowerCase();

                // Check if the JSON carrier contains all words from the input carrier
                boolean allWordsMatch = true;
                for (String word : carrierWords) {
                    if (!jsonCarrier.contains(word)) {
                        allWordsMatch = false;
                        break;
                    }
                }

                if (allWordsMatch) {
                    matchingCarriers.add(jsonObject);
                }
            }
        }
        return matchingCarriers;
    }
    
    private static JSONArray readJsonFile(String filePath) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;

        try (FileReader reader = new FileReader(filePath)) {
            jsonArray = (JSONArray) parser.parse(reader);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("An error occurred while parsing the JSON: " + e.getMessage());
        }

        return jsonArray;
    }
    
    public static JSONObject sendGetRequest(String urlString) {
        String responseBody = "";

        try {
            URI uri = new URI(urlString); // Create URI from the string
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri) // Use URI directly
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = response.body();

            // Parse the response to JSONObject
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null; // Return null if there's an error
    }
    
    public List<List<String>> getAdresses() {
    	return smsAdresses;
    }
    
    public int getValid() {
    	return valid;
    }
    public int getInvalid() {
    	return invalid;
    }
}
