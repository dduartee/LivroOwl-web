package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AvaliaLivroServlet
 */
@WebServlet("/avaliar")
public class AvaliarLivroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private LivrosWebService livrosAPI;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AvaliarLivroServlet() {
        super();
        this.livrosAPI = new LivrosWebService();
        // TODO Auto-generated constructor stub
    }
    /**
     * Método adaptador que converte os dados do livro e envia para o backend.
     */
    private static Map<String, String> parseBookToBackend(Map<String, String> randomBookData) {
        
        // Adaptando os dados do livro para o formato adequado para o backend
        Map<String, String> livroData = new HashMap<>();
        livroData.put("idLivro", randomBookData.get("id"));
        livroData.put("nome", randomBookData.get("nome"));
        livroData.put("autor", randomBookData.get("autor"));
        livroData.put("publish_date", randomBookData.get("dataPub"));
        livroData.put("capaId", randomBookData.get("capaId"));
        livroData.put("coverURL", randomBookData.get("coverURL"));
        livroData.put("genero", randomBookData.get("genero"));

        return livroData;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Criando a instância da classe OpenLibraryAPI
        OpenLibraryAPI api = new OpenLibraryAPI();
        
        try {
            // Obtendo os dados do livro aleatório
            Map<String, String> book = api.getRandomBook(); // {coverURL=https://covers.openlibrary.org/b/id/10841058-M.jpg, capaId=10841058, dataPub=2016, genero=Fiction, nome=The Liberation of Sita, id=/works/OL24321939W, autor=Volga}
            System.out.println(book);
            Map<String, String> livroData = parseBookToBackend(book);
            // Envia os dados do livro para o backend e retorna a resposta
            Map<String, String> respostaAPI = livrosAPI.pushLivro(livroData);
            Boolean success = Boolean.valueOf(respostaAPI.get("success"));
            if(success == false) {
            	// Caso a resposta seja "false", significa que algo deu errado
                String errorMessage = respostaAPI.get("message");
                
                // Lógica para tratar o erro
                System.out.println("Erro ao cadastrar livro: " + errorMessage);
                
                // Você pode enviar uma resposta de erro para o cliente, caso esteja em um contexto web
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Define o status HTTP como 400 (Bad Request)
                response.getWriter().write("Erro ao cadastrar livro: " + errorMessage);
            }
            Integer idLivroDB = Integer.valueOf(respostaAPI.get("idLivro"));
            System.out.println(respostaAPI);
            // Configuração da resposta (HTML)
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");

            // Gerar o HTML dinâmico usando o método helper
            String htmlContent = generateBookHTML(book, idLivroDB);

            // Escreve o HTML gerado na resposta
            PrintWriter pwHTML = response.getWriter();
            pwHTML.println(htmlContent);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().append("Erro ao obter informações do livro.");
        }
    }

 // Método que gera o HTML do livro
    private String generateBookHTML(Map<String, String> book, Integer idLivroDB) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"pt-br\">");
        html.append("<head>");
        html.append("<meta charset=\"utf-8\">");
        html.append("<title>Avaliação</title>");
        html.append("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");

        // Fonte Overpass Mono
        html.append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">");
        html.append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>");
        html.append("<link href=\"https://fonts.googleapis.com/css2?family=Overpass+Mono:wght@300..700&display=swap\" rel=\"stylesheet\">");

        html.append("<style>");
        html.append("body { font-family: 'Overpass Mono', monospace; font-weight: 400; display: flex; flex-direction: column; align-items: center; margin: 0; background-color: #eaeaea; }");

        // Topbar estilo LivroOwl
        html.append(".topnav { background-color: #333; display: flex; justify-content: space-between; padding: 0 16px; color: white; width: 100%; box-sizing: border-box; }");
        html.append(".topnav a { color: #f2f2f2; padding: 14px 16px; text-decoration: none; font-size: 17px; }");
        html.append(".topnav a:hover { background-color: #ddd; color: black; }");
        html.append(".topnav a.active { background-color: #04aa6d; color: white; }");
        html.append(".app-name { font-size: 20px; font-weight: bold; }");

        html.append("#avaliacao { width: 100%; max-width: 500px; background-color: #f2f2f2; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); margin-top: 40px; }");
        html.append("input[type=text], select, textarea { width: 100%; padding: 12px; margin: 8px 0; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }");
        html.append("input[type=submit] { width: 100%; background-color: #4CAF50; color: white; padding: 14px; border: none; border-radius: 4px; cursor: pointer; margin-top: 10px; }");
        html.append("input[type=submit]:hover { background-color: #45a049; }");
        html.append(".containerEstrelas { display: flex; flex-direction: column; align-items: center; width: 100%; }");
        html.append(".interacaoSliderEstrelas { display: flex; flex-direction: column; justify-content: center; width: 250px; }");
        html.append("#estrelas { display: flex; flex-direction: row; justify-content: space-evenly; gap: 5px; margin: 10px 0; font-size: 24px; }");
        html.append(".fa-star { color: lightgray; cursor: pointer; }");
        html.append(".checked { color: gold; }");
        html.append(".half-checked { color: rgba(255, 217, 0, 0.6); }");
        html.append("#rangeEstrelas { width: 100%; margin: 10px 0; }");
        html.append("#coverLivro { width: 100%; max-width: 200px; height: auto; min-height: 200px; display: block; margin: 0 auto 15px auto; }");
        html.append("h3 { text-align: center; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        // Topbar inserida no body
        html.append("<div class=\"topnav\">");
        html.append("<div class=\"app-name\">📚 LivroOwl</div>");
        html.append("<div class=\"nav-links\">");
        html.append("<a href=\"listar\">Avaliações</a>");
        html.append("<a class=\"active\" href=\"\">Avaliar</a>");
        html.append("</div>");
        html.append("</div>");

        html.append("<div id=\"avaliacao\">");
        html.append("<form method=\"post\">");
        html.append("<h3 id=\"nomeLivro\">" + book.get("nome") + "</h3>");
        html.append("<input type=\"hidden\" name=\"idLivro\" id=\"idLivro\" value=\"" + idLivroDB + "\">");
        html.append("<img src=\"" + book.get("coverURL") + "\" id=\"coverLivro\" alt=\"Capa do Livro\" />");

        html.append("<div class=\"containerEstrelas\">");
        html.append("<div class=\"interacaoSliderEstrelas\">");
        html.append("<div id=\"estrelas\">");
        for (int i = 1; i <= 5; i++) {
            html.append("<span class=\"star\" data-index=\"" + i + "\"><i class=\"fa fa-star\"></i></span>");
        }
        html.append("</div>");
        html.append("<input type=\"range\" min=\"0.5\" max=\"5\" value=\"0\" step=\"0.5\" name=\"estrelas\" id=\"rangeEstrelas\">");
        html.append("</div>");
        html.append("</div>");

        html.append("<p><label for=\"w3review\">Comentários sobre o livro</label></p>");
        html.append("<textarea id=\"w3review\" name=\"comentario\" rows=\"4\"></textarea>");
        html.append("<label><input type=\"checkbox\" name=\"liked\" value=\"Gostei\" /> Like</label>");
        html.append("<input type=\"submit\" value=\"Avaliar\">");
        html.append("</form>");
        html.append("</div>");

        // Script de interatividade
        html.append("<script>");
        html.append("document.addEventListener('DOMContentLoaded', function () {");
        html.append("  const rangeInput = document.getElementById('rangeEstrelas');");
        html.append("  const estrelas = document.querySelectorAll('#estrelas .fa');");

        html.append("  function atualizarEstrelas(valor) {");
        html.append("    estrelas.forEach((estrela, index) => {");
        html.append("      estrela.classList.remove('checked', 'half-checked');");
        html.append("      const pos = index + 1;");
        html.append("      if (valor >= pos) {");
        html.append("        estrela.classList.add('checked');");
        html.append("      } else if (valor >= pos - 0.5) {");
        html.append("        estrela.classList.add('half-checked');");
        html.append("      }");
        html.append("    });");
        html.append("  }");

        html.append("  rangeInput.addEventListener('input', () => {");
        html.append("    atualizarEstrelas(parseFloat(rangeInput.value));");
        html.append("  });");

        html.append("  estrelas.forEach((estrela, index) => {");
        html.append("    estrela.addEventListener('click', () => {");
        html.append("      const valor = index + 1;");
        html.append("      rangeInput.value = valor;");
        html.append("      atualizarEstrelas(valor);");
        html.append("    });");
        html.append("  });");

        html.append("  atualizarEstrelas(parseFloat(rangeInput.value));");
        html.append("});");
        html.append("</script>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configura a resposta para a codificação correta
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        // Obtém os dados enviados no formulário
        String idLivro = "";
        String estrelas = "";
        String comentario = "";
        Integer liked = 0;
        try {
        	idLivro = request.getParameter("idLivro"); // Aqui, você pode pegar o nome do livro ou outras informações
            estrelas = request.getParameter("estrelas"); // Número de estrelas
            comentario = request.getParameter("comentario"); // Comentário sobre o livro
            liked = request.getParameter("liked").equals("Gostei") ? 1 : 0;
		} catch (Exception e) {
		}
        
        
     // Obtendo a data e hora atual
        LocalDateTime now = LocalDateTime.now();

        // Formatando a data no formato desejado (yyyy-MM-dd'T'HH:mm:ss)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String timestampAvaliado = now.format(formatter);  // Data atual formatada
        // Realiza um debug imprimindo os valores recebidos
        System.out.println("Debug do doPost:");
        System.out.println("Nome do livro: " + idLivro);
        System.out.println("Número de estrelas: " + estrelas);
        System.out.println("Comentário: " + comentario);
        System.out.println("timestampAvaliado: " + timestampAvaliado);
        Map<String, String> avaliacaoData = parseAvaliationToBackend(idLivro, estrelas, comentario, liked,
				timestampAvaliado);
        System.out.println("avaliacaoData: "+avaliacaoData);
        Map<String, String> resultado = livrosAPI.pushAvaliacao(avaliacaoData); // Envia os dados e obtém a resposta
        System.out.println("resultado: "+resultado); // {success=true, timestamp_avaliado=1750636800, id=2, message=Avaliação cadastrada com sucesso!}
        Boolean success = Boolean.valueOf(resultado.get("success"));
        if (success) {
            // Se a avaliação foi cadastrada com sucesso, redireciona o usuário para a página de avaliação
            System.out.println("Avaliação cadastrada com sucesso!");
            // Redireciona o usuário para a página de avaliação, ou página principal, ou qualquer outra página relevante
            response.sendRedirect("listar"); // URL de destino para a avaliação de sucesso
        } else {
        	String errorMessage = resultado.get("message");
            // Se houve algum erro, exibe a mensagem de erro
            System.out.println("Erro ao cadastrar avaliação: " + resultado.get("message"));
            // Pode redirecionar o usuário para uma página de erro ou exibir uma mensagem de erro
            response.getWriter().write("<p><strong>Mensagem de erro:</strong> " + errorMessage + "</p>");
        }
    }
	private Map<String, String> parseAvaliationToBackend(String idLivro, String estrelas, String comentario, Integer liked,
			String timestampAvaliado) {
		Map<String, String> avaliacaoData = new HashMap<>();
        avaliacaoData.put("idLivro", idLivro); // ID do livro
        avaliacaoData.put("estrelas", estrelas); // Número de estrelas
        avaliacaoData.put("comentario", comentario); // Comentário
        avaliacaoData.put("liked", liked.toString()); // Gostei (booleano convertido para String)
        avaliacaoData.put("timestamp_avaliado", timestampAvaliado); // Timestamp da avaliação
		return avaliacaoData;
	}

}
