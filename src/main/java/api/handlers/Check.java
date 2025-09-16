package api.handlers;

import io.javalin.http.Context;

import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Check {
    public static void getData(Context ctx) {
        final String url = "https://www.lotto.in/nagaland-lottery/"+ctx.pathParam("time");
        try {

            final Document doc = Jsoup.connect(url).get();
            Element table = doc.selectFirst("div#main table:first-of-type");

            String series = ctx.pathParam("series");
            String number = ctx.pathParam("number");

            List<List<String>> tableData = new ArrayList<>();

            Elements rows = table.select("tr");

            for (Element row : rows) {
                List<String> rowData = new ArrayList<>();
                
                Elements cells = row.select("th, td");
                for (Element cell : cells) {
                    rowData.add(cell.text());
                }
                if (rowData.size() > 2) {
                    rowData.remove(0);
                }
                tableData.add(rowData);
            }

            tableData.remove(0);
            tableData.remove(tableData.size()-1);

            String winSeries = tableData.get(0).get(0);
            winSeries = winSeries.substring(0, 3);

            if (series.equals(winSeries) && number.equals(tableData.get(1).get(0))) {
                Map<String, Object> response = new HashMap<>();
                response.put("number", number);
                response.put("isWinner", true);
                response.put("prize", "₹1 Crore");
                ctx.json(response);
                return;
            } else if (!series.equals(winSeries) && number.equals(tableData.get(1).get(0))) {
                Map<String, Object> response = new HashMap<>();
                response.put("number", number);
                response.put("isWinner", true);
                response.put("prize", "₹1,000/-");
                ctx.json(response);
                return;
            }

            String fnumber = number.substring(1,number.length());

            for (int i = 2; i < tableData.size(); i++) {
                String arr[] = tableData.get(i).get(0).split(", ");
                for (int j = 0; j < arr.length; j++) {
                    if (i == 2) {
                        if (number.equals(arr[j])) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("number", number);
                            response.put("isWinner", true);
                            response.put("prize", tableData.get(i).get(1));
                            ctx.json(response);
                            return;
                        }
                    } else {
                        if (fnumber.equals(arr[j])) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("number", number);
                            response.put("isWinner", true);
                            response.put("prize", tableData.get(i).get(1));
                            ctx.json(response);
                            return;
                        }
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("number", number);
            response.put("isWinner", false);
            response.put("prize", "no prize");
            ctx.json(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
