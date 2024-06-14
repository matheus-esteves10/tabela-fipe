package br.com.tabelafipe.TabelaFipe.principal;

import br.com.tabelafipe.TabelaFipe.model.Dados;
import br.com.tabelafipe.TabelaFipe.model.Modelo;
import br.com.tabelafipe.TabelaFipe.model.Veiculo;
import br.com.tabelafipe.TabelaFipe.service.ConsumoApi;
import br.com.tabelafipe.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    Scanner scanner = new Scanner(System.in);

    public void exibeMenu() {
        String url = "";

        System.out.println("Para qual tipo de veículo você deseja obter informações da tabela fipe?(carro, moto, caminhão)");
        var tipo = scanner.nextLine();
        String ENDERECO_BASE = "https://parallelum.com.br/fipe/api/v1/";

        if (tipo.toLowerCase().contains("car")){
            url = ENDERECO_BASE + "carros/marcas/";

        } else if (tipo.toLowerCase().contains("mo")){
            url = ENDERECO_BASE + "motos/marcas/";

        } else if (tipo.toLowerCase().contains("cam")) {
            url = ENDERECO_BASE + "caminhoes/marcas/";

        } else {
            System.out.println("Opção inválida");
        }

        String json = consumoApi.obterDados(url);


        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream().
                sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o código da marca do " + tipo);
        var codigoMarca = scanner.nextLine();

        url = url + codigoMarca + "/modelos/";
        json = consumoApi.obterDados(url);
        var modeloLista = conversor.obterDados(json, Modelo.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do veículo a ser buscado: ");
        var nomeVeiculo = scanner.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nInforme o código do modelo: ");
        var modeloDesejado = scanner.nextLine();

        url = url + modeloDesejado + "/anos/";
        json = consumoApi.obterDados(url);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = url + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaçiações por ano");
        veiculos.forEach(System.out::println);

    }

}
