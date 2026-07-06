import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Imobiliaria imobiliaria = new Imobiliaria();

        Inquilino[] inquilinos = new Inquilino[10];
        Imovel[] imoveis = new Imovel[10];

        int qtdInquilinos = 0;
        int qtdImoveis = 0;
        int opcao;

        do {
            mostrarMenu();

            opcao = lerInteiro(sc, "Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    qtdInquilinos = criarInquilino(sc, inquilinos, qtdInquilinos);
                    break;
                case 2:
                    qtdImoveis = criarImovel(sc, imoveis, qtdImoveis);
                    break;
                case 3:
                    criarContrato(sc, imobiliaria, inquilinos, qtdInquilinos,
                            imoveis, qtdImoveis);
                    break;
                case 4:
                    encerrarContrato(sc, imobiliaria);
                    break;
                case 5:
                    imobiliaria.listarContratosAtivos();
                    break;
                case 6:
                    demonstracao(imobiliaria);
                    break;
                case 0:
                    System.out.println("Sistema encerrado, obrigado por utilizar!");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
        sc.close();
    }

    public static int lerInteiro(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String linha = sc.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }

    public static double lerDouble(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String linha = sc.nextLine().trim();
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite apenas números decimais.");
            }
        }
    }

    public static String lerNome(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String nome = sc.nextLine().trim();
            if (nome.isEmpty()) {
                System.out.println("Entrada inválida! O nome não pode estar vazio.");
                continue;
            }
            boolean soNumeros = true;
            for (int i = 0; i < nome.length(); i++) {
                char c = nome.charAt(i);
                if (c < '0' || c > '9') {
                    soNumeros = false;
                    break;
                }
            }
            if (soNumeros) {
                System.out.println("Entrada inválida! Nome não pode conter apenas números.");
                continue;
            }
            return nome;
        }
    }

    public static String lerCPF(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String cpf = sc.nextLine().trim();
            if (cpf.isEmpty()) {
                System.out.println("Entrada inválida! O CPF não pode estar vazio.");
                continue;
            }
            boolean valido = true;
            int digitos = 0;
            for (int i = 0; i < cpf.length(); i++) {
                char c = cpf.charAt(i);
                if (c >= '0' && c <= '9') {
                    digitos++;
                } else if (c == '-') {
                } else {
                    valido = false;
                    break;
                }
            }
            if (!valido) {
                System.out.println("Entrada inválida! CPF deve conter apenas números (ex: 00000000000 ou 0000000-00).");
                continue;
            }
            if (digitos != 11) {
                System.out.println("Entrada inválida! CPF deve ter 11 dígitos.");
                continue;
            }
            return cpf;
        }
    }

    public static String lerTelefone(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String tel = sc.nextLine().trim();
            if (tel.isEmpty()) {
                System.out.println("Entrada inválida! O telefone não pode estar vazio.");
                continue;
            }

            boolean valido = true;
            int digitos = 0;
            for (int i = 0; i < tel.length(); i++) {
                char c = tel.charAt(i);
                if (c >= '0' && c <= '9') {
                    digitos++;
                } else if (c == '(' || c == ')' || c == ' ' || c == '-') {
                } else {
                    valido = false;
                    break;
                }
            }
            if (!valido) {
                System.out.println("Entrada inválida! Telefone deve conter apenas números (ex: (45)9999999).");
                continue;
            }
            if (digitos < 8 || digitos > 11) {
                System.out.println("Entrada inválida! Telefone deve ter entre 8 e 11 dígitos.");
                continue;
            }
            return tel;
        }
    }

    public static void mostrarMenu() {
        System.out.println("\n==============================");
        System.out.println("            MENU");
        System.out.println("==============================");
        System.out.println("1 - Cadastrar inquilino");
        System.out.println("2 - Cadastrar imóvel");
        System.out.println("3 - Cadastrar contrato");
        System.out.println("4 - Encerrar contrato");
        System.out.println("5 - Listar contratos ativos");
        System.out.println("6 - Demonstração");
        System.out.println("0 - Sair");
    }

    public static int criarInquilino(Scanner sc, Inquilino[] inquilinos, int qtdInquilinos) {

        if (qtdInquilinos == inquilinos.length) {
            System.out.println("Limite de inquilinos atingido.");
            return qtdInquilinos;
        }

        System.out.println("\n===== CADASTRO DE INQUILINO =====");
        String nome = lerNome(sc, "Nome: ");
        String cpf = lerCPF(sc, "CPF: ");
        String telefone = lerTelefone(sc, "Telefone: ");

        inquilinos[qtdInquilinos] = new Inquilino(nome, cpf, telefone);
        System.out.println("Inquilino cadastrado com sucesso!");
        return qtdInquilinos + 1;
    }

    public static int criarImovel(Scanner sc, Imovel[] imoveis, int qtdImoveis) {

        if (qtdImoveis == imoveis.length) {
            System.out.println("Limite de imóveis atingido.");
            return qtdImoveis;
        }

        System.out.println("\n===== CADASTRO DE IMÓVEL =====");
        System.out.println("1 - Apartamento");
        System.out.println("2 - Casa");

        int tipo = lerInteiro(sc, "Escolha: ");

        System.out.print("Endereço: ");
        String endereco = sc.nextLine();

        double valor = lerDouble(sc, "Valor mensal: ");

        if (tipo == 1) {
            int andar = lerInteiro(sc, "Andar: ");
            imoveis[qtdImoveis] = new Apartamento(endereco, valor, andar);
        } else if (tipo == 2) {
            int opcaoQuintal;

            do {
                System.out.println("\nPossui quintal?");
                System.out.println("1 - Sim");
                System.out.println("2 - Não");

                opcaoQuintal = lerInteiro(sc, "Escolha: ");

                if (opcaoQuintal != 1 && opcaoQuintal != 2) {
                    System.out.println("Opção inválida! Tente novamente.");
                }
            } while (opcaoQuintal != 1 && opcaoQuintal != 2);
            boolean quintal = (opcaoQuintal == 1);
            imoveis[qtdImoveis] = new Casa(endereco, valor, quintal);
        } else {
            System.out.println("Tipo de imóvel inválido!");
            return qtdImoveis;
        }
        System.out.println("Imóvel cadastrado!");
        return qtdImoveis + 1;
    }

    public static void criarContrato(Scanner sc, Imobiliaria imobiliaria, Inquilino[] inquilinos, int qtdInquilinos, Imovel[] imoveis,
                                     int qtdImoveis) {

        if (qtdInquilinos == 0 || qtdImoveis == 0) {
            System.out.println("Cadastre pelo menos um inquilino e um imóvel.");
            return;
        }
        System.out.println("\n===== NOVO CONTRATO =====");
        System.out.println("\nInquilinos:");

        for (int i = 0; i < qtdInquilinos; i++) {
            System.out.println(i + " - " + inquilinos[i].getNome());
        }

        int indiceInquilino = lerInteiro(sc, "Escolha o inquilino: ");
        if (indiceInquilino < 0 || indiceInquilino >= qtdInquilinos) {
            System.out.println("Índice de inquilino inválido!");
            return;
        }

        System.out.println("\nImóveis:");
        for (int i = 0; i < qtdImoveis; i++) {
            System.out.println(i + " - ");
            imoveis[i].mostrarInformacoes();
        }

        int indiceImovel = lerInteiro(sc, "Escolha o imóvel: ");
        if (indiceImovel < 0 || indiceImovel >= qtdImoveis) {
            System.out.println("Índice de imóvel inválido!");
            return;
        }

        Inquilino inquilinoEscolhido = inquilinos[indiceInquilino];
        Imovel imovelEscolhido = imoveis[indiceImovel];

        if (imobiliaria.inquilinoTemContratoAtivo(inquilinoEscolhido)) {
            System.out.println("Erro: Este inquilino já possui um contrato ativo!");
            return;
        }

        if (imobiliaria.imovelTemContratoAtivo(imovelEscolhido)) {
            System.out.println("Erro: Este imóvel já possui um contrato ativo!");
            return;
        }

        System.out.print("Data de início: ");
        String inicio = sc.nextLine();
        System.out.print("Data final: ");
        String fim = sc.nextLine();

        int meses = lerInteiro(sc, "Quantidade de meses: ");

        Contrato contrato = new Contrato(inquilinoEscolhido, imovelEscolhido, inicio, fim, meses);
        imobiliaria.adicContrato(contrato);
        System.out.println("Contrato cadastrado!");
    }

    public static void encerrarContrato(Scanner sc, Imobiliaria imobiliaria) {
        System.out.println("\n===== ENCERRAR CONTRATO =====");
        imobiliaria.listarTodosContratos();

        int indice = lerInteiro(sc, "\nDigite o índice do contrato: ");
        imobiliaria.encerrarContrato(indice);
        System.out.println("Contrato encerrado com sucesso!");
    }

    public static void demonstracao(Imobiliaria imobiliaria){
        Inquilino pedro = new Inquilino("João","0000000-00","(45)9999999");
        Inquilino sofia = new Inquilino("Sofia","1111111-11","(45)888888");
        Apartamento ap = new Apartamento("Rua Bonita",800,4);
        Casa casa = new Casa("Rua Bacana", 1800, true);
        Contrato c1 = new Contrato(pedro, ap, "04/01/2026","04/07/2026",7);
        Contrato c2 = new Contrato(sofia, casa, "15/04/2026","03/12/2027",20);
        c1.encerrarContrato();
        imobiliaria.adicContrato(c1);
        imobiliaria.adicContrato(c2);
        imobiliaria.listarContratosAtivos();
    }
}