package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class ConAvaliacoesServlet
 */
@WebServlet("/listar")
public class ConAvaliacoesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private LivrosWebService livrosAPI;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConAvaliacoesServlet() {
        super();
        this.livrosAPI = new LivrosWebService();
        // TODO Auto-generated constructor stub
    }

protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
        Map<String, String> filtro = new HashMap<>();
        JSONArray avaliacoes = livrosAPI.consultaAvaliacoes(filtro);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pwHTML = response.getWriter();
        // Início do HTML
        pwHTML.println("<!DOCTYPE html>");
        pwHTML.println("<html lang=\"pt-br\">");
        pwHTML.println("<head>");
        pwHTML.println("  <meta charset=\"utf-8\" />");
        pwHTML.println("  <title>Lista de Avaliações</title>");
        pwHTML.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\" />");
        pwHTML.println("  <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\" />");
        pwHTML.println("  <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin />");
        pwHTML.println("  <link href=\"https://fonts.googleapis.com/css2?family=Overpass+Mono:wght@300..700&display=swap\" rel=\"stylesheet\" />");
        
        // CSS
        pwHTML.println("  <style>");
        pwHTML.println("    body { font-family: 'Overpass Mono', monospace; font-weight: 400; background-color: #eaeaea;}");
        pwHTML.println("    #avaliacoes { display: flex; flex-wrap: wrap; gap: 3rem; justify-content: center; }");
        pwHTML.println("    .avaliacaoItem { display: flex; flex-direction: column; align-items: center; position: relative; width: 300px; background: #f2f2f2; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        pwHTML.println("    .avaliacaoItem img { object-fit: cover; height: 200px; display: block; margin: 0 auto 15px; }");
        pwHTML.println("    .avaliacaoItem h3 { text-align: center; }");
        pwHTML.println("    .comentario { padding: 10px; border-radius: 4px; margin-top: 10px; }");
        pwHTML.println("    .estrelas { display: flex; justify-content: center; gap: 5px; font-size: 24px; margin: 10px 0; }");
        pwHTML.println("    .fa-star { color: lightgray; }");
        pwHTML.println("    .checked { color: gold; }");
        pwHTML.println("    .half-checked { color: rgba(255, 217, 0, 0.6); }");
        pwHTML.println("    .topnav { background-color: #333; display: flex; justify-content: space-between; padding: 0 16px; color: white; }");
        pwHTML.println("    .topnav a { color: #f2f2f2; padding: 14px 16px; text-decoration: none; font-size: 17px; }");
        pwHTML.println("    .topnav a:hover { background-color: #ddd; color: black; }");
        pwHTML.println("    .topnav a.active { background-color: #04aa6d; color: white; }");
        pwHTML.println("    .app-name { font-size: 20px; font-weight: bold; }");
        pwHTML.println("    .vermelho { color: red; font-size: 22px; position: absolute; top: 10px; right: 10px; }");
        pwHTML.println("  </style>");
        pwHTML.println("</head>");
        
        pwHTML.println("<body>");
        pwHTML.println("  <div class=\"topnav\">");
        pwHTML.println("    <div class=\"app-name\">📚 LivroOwl</div>");
        pwHTML.println("    <div class=\"nav-links\">");
        pwHTML.println("      <a class=\"active\" href=\"\">Avaliações</a>");
        pwHTML.println("      <a href=\"avaliar\">Avaliar</a>");
        pwHTML.println("    </div>");
        pwHTML.println("  </div>");

        pwHTML.println("  <div id=\"avaliacoes\"></div>");

        // Injeção do JavaScript com os dados do backend
        pwHTML.println("  <script>");
        pwHTML.print("    const avaliacoes = ");
        pwHTML.print(avaliacoes.toString()); // Insere o array JSON do Java como objeto JS
        pwHTML.println(";");

        pwHTML.println("    const avaliacoesDiv = document.getElementById('avaliacoes');");
        pwHTML.println("    function renderEstrelas(valor) {");
        pwHTML.println("      const estrelasHTML = [];");
        pwHTML.println("      for (let i = 1; i <= 5; i++) {");
        pwHTML.println("        if (valor >= i) estrelasHTML.push('<i class=\"fa fa-star checked\"></i>');");
        pwHTML.println("        else if (valor >= i - 0.5) estrelasHTML.push('<i class=\"fa fa-star half-checked\"></i>');");
        pwHTML.println("        else estrelasHTML.push('<i class=\"fa fa-star\"></i>');");
        pwHTML.println("      }");
        pwHTML.println("      return estrelasHTML.join('');");
        pwHTML.println("    }");

        pwHTML.println("    avaliacoes.forEach((avaliacao) => {");
        pwHTML.println("      const item = document.createElement('div');");
        pwHTML.println("      item.classList.add('avaliacaoItem');");
        pwHTML.println("      item.innerHTML = `");
        pwHTML.println("        ${avaliacao.liked ? '<i class=\\\"fa fa-heart vermelho\\\"></i>' : ''}");
        pwHTML.println("        <h3>${avaliacao.nomeLivro}</h3>");
        pwHTML.println("        <div class='date'>${new Date(avaliacao.timestamp_avaliado).toLocaleDateString()}</div>");
        pwHTML.println("        <img src='${avaliacao.urlCover}' alt='Capa do livro'>");
        pwHTML.println("        <div class='estrelas'>${renderEstrelas(parseFloat(avaliacao.estrelas))}</div>");
        pwHTML.println("        ${avaliacao.comentario ? `<div class='comentario'>${avaliacao.comentario}</div>` : ''}`;");
        pwHTML.println("      avaliacoesDiv.appendChild(item);");
        pwHTML.println("    });");

        pwHTML.println("  </script>");
        pwHTML.println("</body>");
        pwHTML.println("</html>");
    } catch (Exception e) {
        e.printStackTrace();
        response.getWriter().append("Erro ao obter informações.");
    }
}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
