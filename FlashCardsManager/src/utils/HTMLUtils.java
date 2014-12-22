package utils;

import java.util.ArrayList;
import java.util.ListIterator;

public class HTMLUtils {
	
	private ArrayList<String> htmlTags = new ArrayList<String>();
	
	private void loadHtmlTags() {
		htmlTags.add("<html>");
		htmlTags.add("</html>");
		htmlTags.add("<head>");
		htmlTags.add("</head>");
		htmlTags.add("<body>");
		htmlTags.add("</body>");
		htmlTags.add("<b>");
		htmlTags.add("</b>");
		htmlTags.add("<i>");
		htmlTags.add("</i>");
		htmlTags.add("<u>");
		htmlTags.add("</u>");
		htmlTags.add("<ol>");
		htmlTags.add("</ol>");
		htmlTags.add("<ul>");
		htmlTags.add("</ul>");
		htmlTags.add("<li>");
		htmlTags.add("</li>");
		htmlTags.add("</p>");
	}
	
	public String convertHtmlToPlainText(String html){
		loadHtmlTags();
		StringBuilder sb = new StringBuilder(html);
		ListIterator<String> lit = htmlTags.listIterator();
		while(lit.hasNext()){
			String rep = lit.next();
			if(sb.indexOf(rep) != -1) {
				sb.delete(sb.indexOf(rep), sb.indexOf(rep) + rep.length());
			}
		}
		while(sb.indexOf("<p") != -1) {
			int start = sb.indexOf("<p ");
			int end = sb.indexOf(">", start);
			sb.delete(start, end+1);
		}
		while (sb.indexOf("  ") != -1) {
			int start = sb.indexOf("  ");
			sb.replace(start, start+2, "");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		HTMLUtils parser = new HTMLUtils();
		System.out.println(parser.convertHtmlToPlainText("</head>  <body><p style=margin-top: 0>Test                           antwort2</p></body></html>"));
		
	}
	
}
