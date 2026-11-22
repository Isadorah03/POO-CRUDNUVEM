package CRUDNUVEM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

// Aluno: Isadora de Souza Pereira Rodrigues
// RA: 0024416
// CRUD de produtos com banco de dados MySQL na nuvem (Railway)

public class CrudNuvem {

	public static void main(String[] args) {

		// 1. DADOS DE CONEXÃO (o endereço e a chave do banco no Railway)

		String url = "jdbc:mysql://interchange.proxy.rlwy.net:10834/railway";
		String usuarioBanco = "root";
		String senhaBanco = "cYrbHoOjPzNYammUBoXbCpclBFTemcXv";

		// Scanner: é o "ouvido" do programa, lê o que a pessoa digita no console
		Scanner sc = new Scanner(System.in);

		try {

			// 2. O DRIVER E A CONEXÃO (o "cabo" entre o Java e o MySQL)

			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conexao = DriverManager.getConnection(url, usuarioBanco, senhaBanco);
			System.out.println("Conectado ao banco na nuvem!");


			// 3. CRIA A TABELA (a "gaveta" dentro do banco), se ela ainda não existir

			String sqlTabela = "CREATE TABLE IF NOT EXISTS PRODUTO ("
					+ "CODIGO INT PRIMARY KEY, "
					+ "NOME VARCHAR(100), "
					+ "PRECO DECIMAL(10,2))";
			conexao.createStatement().executeUpdate(sqlTabela);

			// 4. O MENU (repete até a pessoa digitar 0)

			int opcao = -1;

			while (opcao != 0) {

				System.out.println("\n===== CRUD DE PRODUTOS =====");
				System.out.println("1 - Inserir");
				System.out.println("2 - Listar");
				System.out.println("3 - Atualizar");
				System.out.println("4 - Deletar");
				System.out.println("0 - Sair");
				System.out.print("Escolha uma opcao: ");

				opcao = sc.nextInt();

				switch (opcao) {

				// C - CREATE: guarda um produto novo
				case 1:
					System.out.print("Codigo do produto: ");
					int codigo = sc.nextInt();
					sc.nextLine(); // limpa o "Enter" que sobrou antes de ler texto

					System.out.print("Nome do produto: ");
					String nome = sc.nextLine();

					System.out.print("Preco do produto: ");
					double preco = sc.nextDouble();

					// Formulário com lacunas (?): o Java preenche cada uma depois
					String sqlInserir = "INSERT INTO PRODUTO (CODIGO, NOME, PRECO) VALUES (?, ?, ?)";
					PreparedStatement stmtInserir = conexao.prepareStatement(sqlInserir);
					stmtInserir.setInt(1, codigo);
					stmtInserir.setString(2, nome);
					stmtInserir.setDouble(3, preco);
					stmtInserir.executeUpdate(); // executeUpdate: quem MUDA dados

					System.out.println("Produto inserido com sucesso!");
					stmtInserir.close();
					break;

				// R - READ: busca um produto pelo código
				case 2:
					System.out.print("Digite o codigo do produto: ");
					int codigoBusca = sc.nextInt();

					String sqlListar = "SELECT * FROM PRODUTO WHERE CODIGO = ?";
					PreparedStatement stmtListar = conexao.prepareStatement(sqlListar);
					stmtListar.setInt(1, codigoBusca);
					ResultSet rs = stmtListar.executeQuery(); // executeQuery: quem CONSULTA

					// rs.next() anda para a próxima linha do resultado; false = não achou nada
					if (rs.next()) {
						System.out.println("\n===== DADOS DO PRODUTO =====");
						System.out.println("Codigo: " + rs.getInt("CODIGO"));
						System.out.println("Nome: " + rs.getString("NOME"));
						System.out.println("Preco: " + rs.getBigDecimal("PRECO"));
					} else {
						System.out.println("Produto nao localizado!");
					}

					rs.close();
					stmtListar.close();
					break;

				// U - UPDATE: troca o preço de um produto
				case 3:
					System.out.print("Codigo do produto para atualizar: ");
					int codigoAtualizar = sc.nextInt();

					System.out.print("Digite o novo preco: ");
					double novoPreco = sc.nextDouble();

					String sqlAtualizar = "UPDATE PRODUTO SET PRECO = ? WHERE CODIGO = ?";
					PreparedStatement stmtAtualizar = conexao.prepareStatement(sqlAtualizar);
					stmtAtualizar.setDouble(1, novoPreco);
					stmtAtualizar.setInt(2, codigoAtualizar);

					// executeUpdate devolve quantas linhas foram alteradas
					int alterados = stmtAtualizar.executeUpdate();

					if (alterados > 0) {
						System.out.println("Preco atualizado com sucesso!");
					} else {
						System.out.println("Produto nao localizado!");
					}

					stmtAtualizar.close();
					break;

				// D - DELETE: apaga um produto
				case 4:
					System.out.print("Codigo do produto para deletar: ");
					int codigoDeletar = sc.nextInt();

					String sqlDeletar = "DELETE FROM PRODUTO WHERE CODIGO = ?";
					PreparedStatement stmtDeletar = conexao.prepareStatement(sqlDeletar);
					stmtDeletar.setInt(1, codigoDeletar);

					int deletados = stmtDeletar.executeUpdate();

					if (deletados > 0) {
						System.out.println("Produto deletado com sucesso!");
					} else {
						System.out.println("Produto nao localizado!");
					}

					stmtDeletar.close();
					break;

				// SAIR

				case 0:
					System.out.println("Saindo do sistema...");
					break;

				default:
					System.out.println("Opcao invalida!");
				}
			}

			// Fecha tudo no final, como desligar a luz ao sair da sala
			conexao.close();
			sc.close();

		} catch (Exception e) {
			// Se algo der errado (senha, internet, driver...), a mensagem aparece aqui
			System.out.println("Erro: " + e.getMessage());
		}
	}
}