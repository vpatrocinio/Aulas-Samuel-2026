# Paradigmas: Imperativo e Declarativo

Os paradigmas de programação mostram que um mesmo problema pode ser resolvido de formas diferentes. A principal diferença entre eles está na maneira como a solução é construída. No paradigma **imperativo**, utilizado por linguagens como **Java**, o programador precisa informar passo a passo como o computador deve executar cada ação, controlando toda a lógica do programa. Já no paradigma **declarativo**, presente em linguagens como **Prolog**, o foco é descrever apenas o que se deseja obter, deixando que a própria linguagem encontre uma solução. O paradigma imperativo acaba sendo mais intuitivo para quem já está acostumado com Java, enquanto o declarativo exige uma forma diferente de pensar. No fim, nenhum é melhor que o outro, pois ambos conseguem resolver o mesmo problema, apenas utilizando abordagens diferentes.

### Código em Java (Imperativo)

```java
int[] numeros = {2, 4, 6, 8, 10};
int procurado = 6;
boolean encontrado = false;

for (int numero : numeros) {
    if (numero == procurado) {
        encontrado = true;
        break;
    }
}

System.out.println(encontrado ? "Número encontrado!" : "Número não encontrado!");
```

Nesse exemplo, o programador define todo o processo da busca, percorrendo a lista e comparando os valores até encontrar o número desejado.

### Código em Prolog (Declarativo)

```prolog
numero(2).
numero(4).
numero(6).
numero(8).
numero(10).

encontrado(X) :- numero(X).

?- encontrado(6).
```

Em Prolog, basta declarar os fatos e realizar uma consulta. O interpretador utiliza essas informações para encontrar a resposta, sem que seja necessário programar o passo a passo da busca.

Em resumo, ambos os códigos chegam ao mesmo resultado, mas de maneiras diferentes. Enquanto o **Java** mostra **como** resolver o problema, o **Prolog** informa apenas **o que** deve ser encontrado.
````
