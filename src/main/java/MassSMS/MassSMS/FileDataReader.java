package MassSMS.MassSMS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDataReader {
    private final String[] keys;
    private final List<Map<String, String>> res;
    private final String delineator;
    private final boolean optional;
    private String optionalKey;

    public FileDataReader(String[] keys, String path, String delineator) {
    	this.optional = false;
    	this.keys = keys;
    	this.delineator = delineator;
        this.res = readFile(path);
    }
    
    public FileDataReader(String[] keys, String path, String optionalKey, String delineator) {
    	this.optional = true;
    	this.optionalKey = optionalKey;
    	this.keys = keys;
    	this.delineator = delineator;
        this.res = readFile(path);
    }
    
    public List<Map<String, String>> get() {
    	return res;
    }
    public List<String> get(String key) {
    	List<String> ret = new ArrayList<>();
    	for (Map<String, String> map : res) {
    		ret.add(map.get(key));
    	}
    	return ret;
    }

    private List<Map<String, String>> readFile(String filePath) {
        List<Map<String, String>> dataMap = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	String[] values;
            	if (delineator == null)
            		values = new String[] {line};
            	else
            		values = line.split(delineator);

                // Check if the number of values matches the number of keys
                if ((!optional && values.length != keys.length) ||
                		(optional && (values.length != keys.length && values.length != keys.length+1))) {
                    System.err.println("Warning: Line does not match expected number of keys: " + line);
                    continue;
                }

                Map<String, String> tmp = new HashMap<>();
                for (int i = 0; i < keys.length; i++) {
                    tmp.put(keys[i], values[i].trim());
                    //if optional is enabled and it is finished with required
                    if (optional && i == keys.length-1 && values.length == i+1) {
                        tmp.put(optionalKey, values[i+1].trim());
                    }
                    //if was not provided save empty string
                    else if (optional && i == keys.length-1) {
                        tmp.put(optionalKey, "");
                    }
                }
                dataMap.add(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataMap;
    }
}