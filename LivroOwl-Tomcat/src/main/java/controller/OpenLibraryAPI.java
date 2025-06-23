package controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class OpenLibraryAPI {

    private final String baseURL = "http://openlibrary.org";
    private final Queue<Integer> indexQueue = new LinkedList<>();

    public String getWorkURL(String workId) {
        return baseURL + workId + ".json";
    }

    public String getBookCoverURL(int coverId) {
        return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
    }

    public Map<String, String> getWork(String id) throws Exception {
        String url = getWorkURL(id);
        String json = fetch(url);

        JSONObject data = new JSONObject(json);
        String title = data.optString("title", "Título desconhecido");

        String firstSubject = "nao-definido";
        JSONArray subjects = data.optJSONArray("subjects");

        if (subjects != null && subjects.length() > 0) {
            firstSubject = subjects.getString(0);
        }

        Map<String, String> result = new HashMap<>();
        result.put("title", title);
        result.put("subject", firstSubject);
        return result;
    }

    public Map<String, String> getRandomBook() throws Exception {
        String url = baseURL + "/people/mekBot/books/want-to-read.json?limit=20";
        String json = fetch(url);

        JSONObject data = new JSONObject(json);
        JSONArray entries = data.getJSONArray("reading_log_entries");

        if (entries.isEmpty()) {
            throw new Exception("Nenhuma entrada encontrada.");
        }

        if (indexQueue.isEmpty()) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < entries.length(); i++) {
                indices.add(i);
            }
            Collections.shuffle(indices);
            indexQueue.addAll(indices);
        }

        int index = indexQueue.poll();
        JSONObject work = entries.getJSONObject(index).getJSONObject("work");

        Map<String, String> livro = new HashMap<>();
        String workKey = work.getString("key");

        livro.put("id", workKey);
        int coverId = work.optInt("cover_id", -1);
        livro.put("capaId", coverId != -1 ? String.valueOf(coverId) : "");
        livro.put("autor", work.optJSONArray("author_names") != null
                ? work.getJSONArray("author_names").optString(0, "Autor desconhecido")
                : "Autor desconhecido");
        livro.put("dataPub", String.valueOf(work.optInt("first_publish_year", -1)));
        livro.put("coverURL", coverId != -1 ? getBookCoverURL(coverId) : "");

        try {
            Map<String, String> workData = getWork(workKey);
            livro.put("nome", workData.get("title"));
            livro.put("genero", workData.get("subject"));
        } catch (Exception e) {
            livro.put("nome", "Título desconhecido");
            livro.put("genero", "Gênero indefinido");
        }

        return livro;
    }

    private String fetch(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erro na requisição HTTP: " + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return response.toString();
    }
}

