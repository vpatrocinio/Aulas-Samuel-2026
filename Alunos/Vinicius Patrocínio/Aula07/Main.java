public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);

    Loja loja = null;
    Cliente cliente = null;
    Vendedor vendedor = null;
    Gerente gerente = null;
    Item[] itens = null;
    Pedido pedido = null;

    int opcao;

    do {
        apresentarMenu();

        opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:
                loja = cadastrarLoja(scanner);
                break;
            case 2:
                cliente = cadastrarCliente(scanner);
                if (loja != null) {
                    loja.getClientes()[0] = cliente;
                }
                break;
            case 3:
                vendedor = cadastrarVendedor(scanner, loja);
                if (loja != null) {
                    loja.getVendedores()[0] = vendedor;
                }
                break;
            case 4:
                gerente = cadastrarGerente(scanner, loja);
                break;
            case 5:
                itens = cadastrarItens(scanner);
                break;
            case 6:
                pedido = criarPedido(cliente, vendedor, loja, itens);
                break;
            case 7:
                mostrarDados(loja, cliente, vendedor, gerente, pedido);
                break;
            case 8:
                processarPedido(pedido);
                break;
            case 9:
                System.out.println("Programa encerrado.");
                break;
            default:
                System.out.println("Opção inválida.");
        }
    } while (opcao != 9);
    scanner.close();
}
public static void apresentarMenu(){
    System.out.println("\n----- MY PLANT -----");
    System.out.println("1 - Cadastrar Loja");
    System.out.println("2 - Cadastrar Cliente");
    System.out.println("3 - Cadastrar Vendedor");
    System.out.println("4 - Cadastrar Gerente");
    System.out.println("5 - Cadastrar Itens");
    System.out.println("6 - Criar Pedido");
    System.out.println("7 - Mostrar Dados");
    System.out.println("8 - Processar Pedido");
    System.out.println("9 - Sair");
    System.out.print("Escolha: ");
}

public static Loja cadastrarLoja(Scanner scanner) {

    String nomeFantasia = lerTexto(scanner, "Nome Fantasia: ");

    System.out.print("Razão Social: ");
    String razaoSocial = scanner.nextLine();

    System.out.print("CNPJ: ");
    String cnpj = scanner.nextLine();

    System.out.println("\n----- Endereço da Loja -----");

    System.out.print("Estado: ");
    String estado = scanner.nextLine();

    System.out.print("Cidade: ");
    String cidade = scanner.nextLine();

    System.out.print("Bairro: ");
    String bairro = scanner.nextLine();

    System.out.print("Rua: ");
    String rua = scanner.nextLine();

    int numero = lerInteiro(scanner, "Número: ");

    System.out.print("Complemento: ");
    String complemento = scanner.nextLine();

    Endereco endereco = new Endereco(estado, cidade, bairro, rua, numero, complemento);

    Loja loja = new Loja(nomeFantasia, razaoSocial, cnpj, endereco,
            new Vendedor[10],
            new Cliente[10]
    );
    System.out.println("\nLoja cadastrada com sucesso!");
    return loja;
}

public static Cliente cadastrarCliente(Scanner scanner) {

    String nome = lerTexto(scanner, "Nome: ");
    int idade = lerInteiro(scanner, "Idade: ");

    System.out.println("\n----- Endereço do Cliente -----");

    System.out.print("Estado: ");
    String estado = scanner.nextLine();

    System.out.print("Cidade: ");
    String cidade = scanner.nextLine();

    System.out.print("Bairro: ");
    String bairro = scanner.nextLine();

    System.out.print("Rua: ");
    String rua = scanner.nextLine();

    System.out.print("Número: ");
    int numero = scanner.nextInt();
    scanner.nextLine();

    System.out.print("Complemento: ");
    String complemento = scanner.nextLine();

    Endereco endereco = new Endereco(estado, cidade, bairro, rua, numero, complemento);
    System.out.println("\nCliente cadastrado com sucesso!");
    return new Cliente(nome, idade, endereco);
}

public static Vendedor cadastrarVendedor(Scanner scanner, Loja loja) {

    if (loja == null) {
        System.out.println("Cadastre uma loja primeiro!");
        return null;
    }

    String nome = lerTexto(scanner, "Nome do vendedor: ");
    int idade = lerInteiro(scanner, "Idade: ");
    double salarioBase = lerDouble(scanner, "Salário Base: ");

    double[] salarios = new double[3];

    System.out.println("\nDigite os últimos 3 salários:");

    for (int i = 0; i < salarios.length; i++) {
        System.out.print((i + 1) + "º salário: ");
        salarios[i] = scanner.nextDouble();
    }

    scanner.nextLine();

    System.out.println("\n=== Endereço do Vendedor ===");

    System.out.print("Estado: ");
    String estado = scanner.nextLine();

    System.out.print("Cidade: ");
    String cidade = scanner.nextLine();

    System.out.print("Bairro: ");
    String bairro = scanner.nextLine();

    System.out.print("Rua: ");
    String rua = scanner.nextLine();

    System.out.print("Número: ");
    int numero = scanner.nextInt();
    scanner.nextLine();

    System.out.print("Complemento: ");
    String complemento = scanner.nextLine();

    Endereco endereco = new Endereco(estado, cidade, bairro, rua, numero, complemento);

    Vendedor vendedor = new Vendedor(nome, idade, endereco, loja, salarioBase, salarios);
    System.out.println("\nVendedor cadastrado com sucesso!");
    return vendedor;
}

public static Gerente cadastrarGerente(Scanner scanner, Loja loja) {

    if (loja == null) {
        System.out.println("Cadastre uma loja primeiro!");
        return null;
    }

    System.out.print("Nome do gerente: ");
    String nome = scanner.nextLine();

    System.out.print("Idade: ");
    int idade = scanner.nextInt();

    System.out.print("Salário base: ");
    double salarioBase = scanner.nextDouble();

    double[] salarios = new double[3];

    System.out.println("\nDigite os últimos 3 salários:");

    for (int i = 0; i < salarios.length; i++) {
        System.out.print((i + 1) + "º salário: ");
        salarios[i] = scanner.nextDouble();
    }
    scanner.nextLine();

    System.out.println("\n=== Endereço do Gerente ===");

    System.out.print("Estado: ");
    String estado = scanner.nextLine();

    System.out.print("Cidade: ");
    String cidade = scanner.nextLine();

    System.out.print("Bairro: ");
    String bairro = scanner.nextLine();

    System.out.print("Rua: ");
    String rua = scanner.nextLine();

    System.out.print("Número: ");
    int numero = scanner.nextInt();
    scanner.nextLine();

    System.out.print("Complemento: ");
    String complemento = scanner.nextLine();

    Endereco endereco = new Endereco(estado, cidade, bairro, rua, numero, complemento);
    Gerente gerente = new Gerente(nome, idade, endereco, loja, salarioBase, salarios);
    System.out.println("\nGerente cadastrado com sucesso!");
    return gerente;
}

public static Item[] cadastrarItens(Scanner scanner) {

    int quantidade = lerInteiro(scanner, "Quantidade de itens: ");

    Item[] itens = new Item[quantidade];

    for (int i = 0; i < quantidade; i++) {

        System.out.println("\n=== Item " + (i + 1) + " ===");

        int id = lerInteiro(scanner, "ID: ");
        String nome = lerTexto(scanner, "Nome: ");
        String tipo = lerTexto(scanner, "Tipo: ");
        double valor = lerDouble(scanner, "Valor: ");
        itens[i] = new Item(id, nome, tipo, valor);
    }
    System.out.println("\nItens cadastrados com sucesso!");
    return itens;
}

public static Pedido criarPedido(Cliente cliente, Vendedor vendedor, Loja loja, Item[] itens) {

    if (cliente == null) {
        System.out.println("Cadastre um cliente primeiro!");
        return null;
    }

    if (vendedor == null) {
        System.out.println("Cadastre um vendedor primeiro!");
        return null;
    }

    if (loja == null) {
        System.out.println("Cadastre uma loja primeiro!");
        return null;
    }

    if (itens == null || itens.length == 0) {
        System.out.println("Cadastre pelo menos um item!");
        return null;
    }

    Date dataCriacao = new Date();
    Date dataPagamento = new Date();

    Date dataVencimentoReserva = new Date(
            dataCriacao.getTime() + 86400000
    );

    Pedido pedido = new Pedido(dataCriacao, dataPagamento, dataVencimentoReserva,
            cliente, vendedor, loja, itens);
    System.out.println("\nPedido criado com sucesso!");
    System.out.println("Valor total: R$ " + pedido.calcularValorTotal());
    return pedido;
}

public static void mostrarDados(Loja loja, Cliente cliente, Vendedor vendedor, Gerente gerente,
                                Pedido pedido) {

    System.out.println("\n---------- DADOS CADASTRADOS ----------");

    if (loja != null) {
        System.out.println("\n----- LOJA -----");
        loja.apresentar();
    } else {
        System.out.println("\nLoja não cadastrada.");
    }

    if (cliente != null) {
        System.out.println("\n----- CLIENTE -----");
        cliente.apresentar();
    } else {
        System.out.println("\nCliente não cadastrado.");
    }

    if (vendedor != null) {
        System.out.println("\n----- VENDEDOR -----");
        vendedor.apresentar();
        System.out.println("Média Salarial: R$ " + vendedor.calcularMedia());
        System.out.println("Bônus: R$ " + vendedor.calcularBonus());
    } else {
        System.out.println("\nVendedor não cadastrado.");
    }

    if (gerente != null) {
        System.out.println("\n----- GERENTE -----");
        gerente.apresentar();
        System.out.println("Média Salarial: R$ " + gerente.calcularMedia());
        System.out.println("Bônus: R$ " + gerente.calcularBonus());
    } else {
        System.out.println("\nGerente não cadastrado.");
    }

    if (pedido != null) {
        System.out.println("\n----- PEDIDO -----");
        System.out.println(pedido.gerarDescricaoVenda());
    } else {
        System.out.println("\nPedido não criado.");
    }
}

public static void processarPedido(Pedido pedido) {

    if (pedido == null) {
        System.out.println("Crie um pedido primeiro!");
        return;
    }
    ProcessaPedido processador = new ProcessaPedido();
    boolean sucesso = processador.processar(pedido);

    if (sucesso) {
        System.out.println("\nPedido processado com sucesso!");
    } else {
        System.out.println("\nNão foi possível processar o pedido.");
    }
}

public static int lerInteiro(Scanner scanner, String mensagem) {
    while (true) {

        System.out.print(mensagem);

        if (scanner.hasNextInt()) {
            int valor = scanner.nextInt();
            scanner.nextLine();
            return valor;

        } else {
            System.out.println("Erro! Digite apenas números inteiros.");
            scanner.nextLine();
        }
    }
}

public static double lerDouble(Scanner scanner, String mensagem) {
    while (true) {

        System.out.print(mensagem);

        if (scanner.hasNextDouble()) {
            double valor = scanner.nextDouble();
            scanner.nextLine();
            return valor;

        } else {
            System.out.println("Erro! Digite um número válido.");
            scanner.nextLine();
        }
    }
}

public static String lerTexto(Scanner scanner, String mensagem) {
    while (true) {

        System.out.print(mensagem);
        String texto = scanner.nextLine().trim();

        if (texto.isEmpty()) {
            System.out.println("Erro! O campo não pode ficar vazio.");
            continue;
        }
        if (texto.matches("\\d+")) {
            System.out.println("Erro! Digite um texto válido.");
            continue;
        }
        return texto;
    }
}