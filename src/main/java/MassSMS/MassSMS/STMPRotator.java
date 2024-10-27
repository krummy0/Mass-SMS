package MassSMS.MassSMS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class STMPRotator {
	private List<STMP> stmpList;
	private int i = 0;
	
	public STMPRotator(List<Map<String, String>> stmpMap) {
		stmpList = new ArrayList<>();
		for (Map<String, String> map : stmpMap) {
			stmpList.add(new STMP(map));
		}
	}
	
	public boolean send(String from, String to, String subject, String body) {
		if (i >= stmpList.size())
			i=0;
		boolean ret = stmpList.get(i).sendEmail(from, to, subject, body);
		i++;
		return ret;
	}
	
	public void test(String from, String to) {
		for (STMP stmp : stmpList) {
			stmp.sendEmail(from, to, "", stmp.getHost());
		}
	}
}
