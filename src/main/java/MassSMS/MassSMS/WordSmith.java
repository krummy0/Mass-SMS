package MassSMS.MassSMS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WordSmith {
    private final Map<String, String> maps;
    private final Map<String, String[]> wordMap;

    public WordSmith(Map<String, String> maps) {
        this.maps = maps;
        this.wordMap = new HashMap<>();
        loadWords();
    }

    private void loadWords() {
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            String keyword = entry.getKey();
            String filePath = entry.getValue();
            String[] words = readWordsFromFile(filePath);
            if (words.length > 0) {
                wordMap.put(keyword, words);
            }
        }
    }
    
    public String substitute(String input, Map<String, String> personal) {
        if (personal != null && !personal.isEmpty()) {
        	for (Map.Entry<String, String> entry : personal.entrySet()) {
	            String keyword = '{' + entry.getKey() + '}';
	            String word = entry.getValue();
	            String randomWord = word;
	            input = input.replaceAll(java.util.regex.Pattern.quote(keyword), randomWord);
        	}	
        }
        return substitute(input);
    }

    public String substitute(String input) {
        Random random = new Random();

        if (wordMap != null) {
        	for (Map.Entry<String, String[]> entry : wordMap.entrySet()) {
	            String keyword = '{' + entry.getKey() + '}';
	            String[] words = entry.getValue();
	            String randomWord = words[random.nextInt(words.length)];
	            input = input.replaceAll(java.util.regex.Pattern.quote(keyword), randomWord);
        	}
        }
        return input;
    }

    private String[] readWordsFromFile(String filePath) {
        StringBuilder words = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return words.toString().split("\\n");
    }
}
