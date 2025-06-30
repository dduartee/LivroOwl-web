package controller;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LivrosWebService {

	    private final String baseURL = "http://localhost/api"; // Atualize a URL do seu servidor Tomcat

	    /**
	     * Envia os dados do livro para o backend e retorna um Map<String, String> com a resposta
	     */
	    public Map<String, String> pushLivro(Map<String, String> livroData) {
	        String url = baseURL + "/cadLivro.php";
	        return sendPostRequestReturnMap(url, livroData);
	    }

	    /**
	     * Envia a avaliação para o backend e retorna um Map<String, String> com a resposta
	     */
	    public Map<String, String> pushAvaliacao(Map<String, String> avaliacaoData) {
	        String url = baseURL + "/cadAvaliacao.php";
	        return sendPostRequestReturnMap(url, avaliacaoData);
	    }

	    /**
	     * Consulta avaliações no backend e retorna um Map<String, String> com a resposta
	     */
	    public JSONArray consultaAvaliacoes(Map<String, String> filtroData) {
	        String url = baseURL + "/conAvaliacao.php";
	        return sendPostRequestReturnArray(url, filtroData);
	    }

	    /**
	     * Envia uma requisição POST para a URL informada com o conteúdo do mapa de dados.
	     * Retorna um Map<String, String> com a resposta do backend.
	     */
	    private Map<String, String> sendPostRequestReturnMap(String urlString, Map<String, String> data) {
	        // Cria a URL do backend
	        URL url;
	        try {
	            url = new URL(urlString);
	        } catch (IOException e) {
	            throw new RuntimeException("Erro ao criar URL: " + e.getMessage());
	        }

	        // Cria o JSON a partir do mapa de dados
	        JSONObject jsonRequest = new JSONObject(data);

	        // Envia a requisição POST
	        HttpURLConnection conn;
	        try {
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json; utf-8");
	            conn.setRequestProperty("Accept", "application/json");
	            conn.setDoOutput(true);

	            // Envia os dados no formato JSON
	            try (OutputStream os = conn.getOutputStream()) {
	                byte[] input = jsonRequest.toString().getBytes("utf-8");
	                os.write(input, 0, input.length);
	            }

	            // Recebe a resposta do servidor
	            StringBuilder responseContent = new StringBuilder();
	            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
	                String line;
	                while ((line = br.readLine()) != null) {
	                    responseContent.append(line.trim());
	                }
	            }

	            // Fecha a conexão
	            conn.disconnect();

	            // Retorna a resposta como um Map
	            System.out.println(responseContent.toString());
	            JSONObject jsonResponse = new JSONObject(responseContent.toString());
	            Map<String, String> resultMap = new HashMap<>();
	            jsonResponse.keys().forEachRemaining(key -> {
	                resultMap.put(key, jsonResponse.optString(key, ""));
	            });
	            return resultMap;

	        } catch (IOException e) {
	            throw new RuntimeException("Erro na requisição HTTP: " + e.getMessage());
	        }
	    }
	    /**
	     * Envia uma requisição POST para a URL informada com o conteúdo do mapa de dados.
	     * Retorna um Map<String, String> com a resposta do backend.
	     */
	    private JSONArray sendPostRequestReturnArray(String urlString, Map<String, String> data) {
	        // Cria a URL do backend
	        URL url;
	        try {
	            url = new URL(urlString);
	        } catch (IOException e) {
	            throw new RuntimeException("Erro ao criar URL: " + e.getMessage());
	        }

	        // Cria o JSON a partir do mapa de dados
	        JSONObject jsonRequest = new JSONObject(data);

	        // Envia a requisição POST
	        HttpURLConnection conn;
	        try {
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json; utf-8");
	            conn.setRequestProperty("Accept", "application/json");
	            conn.setDoOutput(true);

	            // Envia os dados no formato JSON
	            try (OutputStream os = conn.getOutputStream()) {
	                byte[] input = jsonRequest.toString().getBytes("utf-8");
	                os.write(input, 0, input.length);
	            }

	            // Recebe a resposta do servidor
	            StringBuilder responseContent = new StringBuilder();
	            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
	                String line;
	                while ((line = br.readLine()) != null) {
	                    responseContent.append(line.trim());
	                }
	            }

	            // Fecha a conexão
	            conn.disconnect();

	            // Retorna a resposta como um Map
	            System.out.println(responseContent.toString());
	            JSONArray jsonResponse = new JSONArray(responseContent.toString());
	            return jsonResponse;

	        } catch (IOException e) {
	            throw new RuntimeException("Erro na requisição HTTP: " + e.getMessage());
	        }
	    }
	}
