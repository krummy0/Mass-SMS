package MassSMS.MassSMS;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Data {
    private List<Map<String, String>> leadsForSending;
    private List<Map<String, String>> stmpSettings;
    private List<String> apiKeys;
    private Map<String, String> hotWords;
    private List<String> personalizationKeywords;

    // Constructor to load data from a JSON file
    public Data(String file) {
        load(file);
    }
    
    public Data() {
    }

    // Load data from a JSON file
    private void load(String file) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));

            // Load leadsForSending
            JSONArray leadsArray = (JSONArray) jsonObject.get("leadsForSending");
            leadsForSending = new ArrayList<>();
            for (Object lead : leadsArray) {
                leadsForSending.add((Map<String, String>) lead);
            }

            // Load stmpSettings
            JSONArray stmpArray = (JSONArray) jsonObject.get("stmpSettings");
            stmpSettings = new ArrayList<>();
            for (Object stmp : stmpArray) {
                stmpSettings.add((Map<String, String>) stmp);
            }

            // Load apiKeys
            apiKeys = (List<String>) jsonObject.get("apiKeys");

            // Load hotWords
            hotWords = (Map<String, String>) jsonObject.get("hotWords");

            // Load personalizationKeywords
            personalizationKeywords = (List<String>) jsonObject.get("personalizationKeywords");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // Save data to a JSON file
    public boolean save(String file) {
        JSONObject jsonObject = new JSONObject();

        // Save leadsForSending
        JSONArray leadsArray = new JSONArray();
        for (Map<String, String> lead : leadsForSending) {
            leadsArray.add(lead);
        }
        jsonObject.put("leadsForSending", leadsArray);

        // Save stmpSettings
        JSONArray stmpArray = new JSONArray();
        for (Map<String, String> stmp : stmpSettings) {
            stmpArray.add(stmp);
        }
        jsonObject.put("stmpSettings", stmpArray);

        // Save apiKeys
        jsonObject.put("apiKeys", apiKeys);

        // Save hotWords
        jsonObject.put("hotWords", hotWords);

        // Save personalizationKeywords
        jsonObject.put("personalizationKeywords", personalizationKeywords);

        // Write to file
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonObject.toJSONString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Getters and setters for the fields
    public List<Map<String, String>> getLeadsForSending() {
        return leadsForSending;
    }

    public void setLeadsForSending(List<Map<String, String>> leadsForSending) {
        this.leadsForSending = leadsForSending;
    }

    public List<Map<String, String>> getStmpSettings() {
        return stmpSettings;
    }

    public void setStmpSettings(List<Map<String, String>> stmpSettings) {
        this.stmpSettings = stmpSettings;
    }

    public List<String> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public Map<String, String> getHotWords() {
        return hotWords;
    }

    public void setHotWords(Map<String, String> hotWords) {
        this.hotWords = hotWords;
    }

    public List<String> getPersonalizationKeywords() {
        return personalizationKeywords;
    }

    public void setPersonalizationKeywords(List<String> personalizationKeywords) {
        this.personalizationKeywords = personalizationKeywords;
    }
}
