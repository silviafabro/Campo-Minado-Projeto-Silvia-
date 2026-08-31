# Relatório Técnico - Campo Minado em Java

**Aluna:** Silvia de Oliveira Fabro  
**Disciplina:** Programação Orientada a Objetos  
**Instituição:** UniCesumar

---

## 1. Visão Geral do Projeto
Este projeto consiste na implementação do clássico jogo **Campo Minado (Minesweeper)** em Java, utilizando os conceitos de Programação Orientada a Objetos (POO). O sistema foi projetado para rodar tanto em modo **Console** quanto em modo **Interface Gráfica (Swing)**, mantendo a regra de negócio totalmente desacoplada da camada de visualização.

---

## 2. Decisões de Encapsulamento (`Celula.java`)

A classe `Celula` representa a menor unidade do tabuleiro e encapsula rigorosamente o seu estado interno, garantindo a integridade do jogo.

### Decisões tomadas:
* **Atributos Privados:** Todos os atributos (`minada`, `revelada`, `marcada`, `minasVizinhas`) possuem o modificador de acesso `private`. Nenhuma classe externa pode alterar diretamente o estado de uma célula.
* **Controle via Métodos (Getters/Setters e Ações):**
  * A alteração do estado ocorre por métodos de ação específicos, como `revelar()` e `alternarMarcacao()`.
  * Os métodos impedem ações inválidas. Por exemplo, uma célula já revelada não pode ser marcada com bandeira.
* **Ocultamento do Contador de Minas:** O número de minas vizinhas é mantido como atributo interno do objeto, acessado via `getMinasVizinhas()`, garantindo que o tabuleiro apenas solicite a informação sem interferir no seu cálculo interno.

---

## 3. Lógica do Algoritmo de Cascata e Uso do `ArrayList` (`Tabuleiro.java`)

O "efeito cascata" ocorre quando o jogador revela uma célula vazia (que possui 0 minas vizinhas). O sistema deve automaticamente revelar todas as células vizinhas adjacentes até encontrar células que possuam minas ao seu redor.

### Implementação da Cascata:
1. **Verificação de Limites:** O método `revelar(int linha, int coluna)` valida se as coordenadas estão dentro dos limites da matriz `grade[][]`.
2. **Critério de Parada:** Se a célula selecionada já estiver revelada ou marcada com bandeira, a execução do fluxo para aquela célula é interrompida.
3. **Uso de `ArrayList`:**
   * Para gerenciar as células vizinhas e realizar a expansão sem risco de estouro de pilha (*StackOverflowError*) em tabuleiros grandes, utiliza-se a classe `java.util.ArrayList`.
   * Ao revelar uma célula com 0 minas vizinhas, o método obtém uma lista de objetos `Celula` vizinhos armazenados em um `ArrayList`.
   * O algoritmo percorre incrementalmente essa lista, aplicando o método de revelação recursivo/iterativo sobre cada célula vizinha válida.

---

## 4. Testes Unitários (`JUnit 5`)

A suíte de testes unitários foi desenvolvida para validar as regras cruciais de negócio isoladamente, garantindo a estabilidade das classes de modelo:

* **`CelulaTest.java` (6 testes):**
  * Alteração e proteção de estados (`minada`, `revelada`, `marcada`).
  * Comportamento da trava de marcação em células reveladas.
  * Atribuição e leitura correta do contador de minas vizinhas.

* **`TabuleiroTest.java` (8 testes):**
  * Correta inicialização da matriz `grade[][]` e distribuição exata da quantidade de minas.
  * Cálculo correto de vizinhança (canto, borda e centro do tabuleiro).
  * Funcionamento da propagação da cascata ao clicar em áreas vazias.
  * Validação das condições de vitória (todas as células não minadas reveladas) e derrota (mina atingida).

     ## Evidências de Funcionamento (Prints da Partida)

### 1. Vitória na Partida
![Partida Vencida]<img width="517" height="257" alt="WhatsApp Image 2026-08-31 at 10 51 01" src="https://github.com/user-attachments/assets/d79e774c-23a1-4bcf-821e-c41c3c165288" />


### 2. Derrota na Partida
![Partida Perdida]<img width="757" height="862" alt="WhatsApp Image 2026-08-31 at 10 49 25" src="https://github.com/user-attachments/assets/aa85f31a-7d3c-4e74-9e38-86c249767be8" />



3. Encapsulamento completo dos atributos de cada objeto.
4. Uso prático de `ArrayList` na resolução de problemas de busca e expansão (cascata).
5. Desacoplamento entre lógica do jogo e visualização gráfica.
