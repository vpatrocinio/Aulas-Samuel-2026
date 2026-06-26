# SeriesFlix — Acompanhe suas séries de TV

Sistema desktop em **Java Swing** para buscar e organizar séries de TV usando a
API pública [TVmaze](https://www.tvmaze.com/api). Interface inspirada na Netflix
(tema escuro, busca e cartões com pôster).

## Como executar

Pré-requisito: **JDK 11 ou superior** (testado com JDK 17).

```powershell
# A partir da pasta SeriesTracker
javac -encoding UTF-8 -d out *.java
java -cp out Main
```

Na primeira execução o programa pede um **apelido**. Os dados ficam salvos em
`dados_usuario.json` e são recarregados na próxima abertura.

## Recursos

- **Busca** de séries por nome (API TVmaze).
- Listas de **Favoritos**, **Já assistidas** e **Quero assistir** (adicionar/remover).
- **Ordenação** por nome, nota geral, estado da série e data de estreia.
- **Detalhes** completos: nome, idioma, gêneros, nota, estado, datas de
  estreia/término, emissora e resumo, com pôster.
- **Persistência em JSON** entre execuções (parser e serializador próprios).
- **Tela cheia ⇄ janela** com a tecla `F11` (ou o botão), sem perder funcionalidades.
- **Tratamento de exceções**: falhas de rede, API fora do ar, arquivo corrompido,
  entradas inválidas e erros inesperados são exibidos sem fechar o programa.

## Configuração (`.env`)

```
TVMAZE_API_URL=https://api.tvmaze.com
ARQUIVO_DADOS=dados_usuario.json
```

## Organização do código (POO)

| Arquivo | Responsabilidade |
|---|---|
| `Main.java` | Inicialização, login local e tratador global de exceções |
| `MainFrame.java` | Janela principal: busca, navegação, ordenação, tela cheia |
| `SerieCard.java` / `DetalhesDialog.java` | Componentes visuais (cartão e detalhes) |
| `Serie.java` / `Usuario.java` | Modelos de domínio |
| `TipoLista.java` / `CriterioOrdenacao.java` | Enums de listas e ordenação |
| `TvMazeService.java` | Acesso à API TVmaze |
| `PersistenceService.java` | Leitura/gravação do JSON |
| `Json.java` / `JsonException.java` | Parser/serializador JSON sem bibliotecas externas |
| `EnvLoader.java` | Leitura do `.env` |
| `ImageLoader.java` / `WrapLayout.java` / `Tema.java` / `UI.java` | Apoio de interface |
| `AppException.java` | Exceção de domínio com mensagens amigáveis |
