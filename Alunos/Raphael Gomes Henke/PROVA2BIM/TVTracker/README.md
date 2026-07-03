# TV Tracker

Sistema desktop (Java Swing) para acompanhar séries de TV, usando a API pública
do TVMaze (https://www.tvmaze.com/api).

## Requisitos

- **JDK 11 ou superior** instalado (o projeto usa `java.net.http.HttpClient`,
  disponível desde o Java 11). Não há dependências externas — tudo (inclusive
  o parser/serializador JSON) foi implementado com bibliotecas padrão do Java.
- Conexão com a internet (apenas para a busca de séries na API do TVMaze; o
  resto do sistema, incluindo visualizar listas já salvas, funciona offline).

## Como compilar e executar

### Linux / macOS
```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

### Windows
Dê duplo clique em `run.bat`, ou pelo terminal:
```
run.bat
```

### Manualmente (qualquer sistema)
```bash
mkdir out
javac -d out -encoding UTF-8 -sourcepath src src/com/tvtracker/Main.java
java -cp out com.tvtracker.Main
```

## Estrutura do projeto

```
src/com/tvtracker/
 ├─ Main.java                 -> ponto de entrada, trata exceções não capturadas
 ├─ model/
 │   ├─ Show.java              -> dados de uma série (nome, idioma, gêneros, nota, etc)
 │   ├─ UserData.java          -> nome do usuário + listas (favoritos/assistidas/quero assistir)
 │   ├─ ListType.java          -> enum dos três tipos de lista
 │   ├─ SortCriteria.java      -> enum dos critérios de ordenação
 │   └─ ShowSorter.java        -> lógica de ordenação das listas
 ├─ json/
 │   ├─ JsonParser.java        -> parser JSON próprio (sem dependências externas)
 │   └─ JsonWriter.java        -> serializador JSON próprio (pretty-print)
 ├─ service/
 │   ├─ TVMazeClient.java      -> chamadas HTTP à API do TVMaze
 │   ├─ StorageService.java    -> persistência local em JSON (pasta tvtracker_data/)
 │   └─ ApiException.java      -> exceção específica de erros de API
 └─ ui/
     ├─ LoginFrame.java         -> tela de identificação do usuário (nome/apelido)
     ├─ MainFrame.java          -> tela principal: busca + abas das 3 listas
     ├─ ShowListPanel.java      -> painel reutilizável de cada lista (com ordenação)
     ├─ ShowTableModel.java     -> modelo de tabela para exibir séries
     └─ ShowDetailsDialog.java  -> janela de detalhes / adicionar-remover de listas
```

## Funcionalidades

- Identificação do usuário por nome/apelido (uso local, sem senha).
- Busca de séries por nome usando a API do TVMaze.
- Exibição de nome, idioma, gêneros, nota geral, estado, datas de estreia/término
  e emissora — tanto nos resultados de busca quanto na tela de detalhes.
- Três listas por usuário: Favoritos, Já Assistidas e Quero Assistir, com
  opção de adicionar/remover séries de cada uma.
- Ordenação das listas por nome (A-Z), nota geral, estado ou data de estreia.
- Persistência automática em JSON (pasta `tvtracker_data/`, um arquivo por
  usuário), mantendo os dados entre execuções do programa.
- Tratamento de exceções em toda a aplicação (rede, leitura/gravação de
  arquivos, parsing de JSON): o sistema nunca fecha inesperadamente, sempre
  exibindo uma mensagem de erro amigável.

## Observações de design (POO)

- Separação em camadas: `model` (dados), `service` (API/persistência) e `ui` (telas).
- `Show` conhece como se converter de/para os dois formatos JSON que usa
  (API do TVMaze e armazenamento local), mantendo essa lógica encapsulada.
- `ShowListPanel` é reutilizado nas três abas de listas (Favoritos, Assistidas,
  Quero Assistir), evitando duplicação de código de UI.
- Chamadas de rede rodam em `SwingWorker` (thread separada), mantendo a
  interface responsiva e evitando travamentos.
