package campominado.ui;

import campominado.modelo.Tabuleiro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JogoCampoMinadoGUI extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JogoCampoMinadoGUI::new);
    }

    private Tabuleiro tabuleiro;
    private JButton[][] botoes;
    private JPanel painelTabuleiro;
    private JPanel painelEstatisticas;
    private JPanel painelSelecao;

    // Elementos da barra de estatísticas
    private JLabel lblMinas;
    private JLabel lblTempo;
    private JLabel lblReveladas;
    private JLabel lblJogadas;

    private Timer timer;
    private int tempoSegundos;
    private int jogadas;
    private boolean jogoIniciado;

    // Cores do Tema Rosa
    private final Color COR_FUNDO = new Color(255, 240, 245);
    private final Color COR_PAINEL = new Color(255, 228, 225);
    private final Color COR_BOTÃO_TELA = new Color(255, 182, 193);
    private final Color COR_TEXTO = new Color(139, 58, 98);
    private final Color COR_CELULA_OCULTA = new Color(255, 192, 203);
    private final Color COR_CELULA_REVELADA = new Color(255, 245, 247);

    public JogoCampoMinadoGUI() {
        setTitle("Campo Minado da Silvia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(10, 10));

        exibirTelaSelecao();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void exibirTelaSelecao() {
        if (painelTabuleiro != null) remove(painelTabuleiro);
        if (painelEstatisticas != null) remove(painelEstatisticas);

        painelSelecao = new JPanel(new BorderLayout(15, 15));
        painelSelecao.setBackground(COR_FUNDO);
        painelSelecao.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Boas-vindas
        JLabel lblTitulo = new JLabel("Campo Minado da Silvia", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        painelSelecao.add(lblTitulo, BorderLayout.NORTH);

        // Grid de cards de dificuldade
        JPanel painelCards = new JPanel(new GridLayout(2, 2, 15, 15));
        painelCards.setBackground(COR_FUNDO);

        JButton btnIniciante = criarBotaoCard("Iniciante", "9x9 • 10 Minas", 9, 9, 10);
        JButton btnIntermediario = criarBotaoCard("Intermediário", "16x16 • 40 Minas", 16, 16, 40);
        JButton btnAvancado = criarBotaoCard("Avançado", "16x30 • 99 Minas", 16, 30, 99);
        JButton btnPersonalizado = criarBotaoPersonalizado();

        painelCards.add(btnIniciante);
        painelCards.add(btnIntermediario);
        painelCards.add(btnAvancado);
        painelCards.add(btnPersonalizado);

        painelSelecao.add(painelCards, BorderLayout.CENTER);

        add(painelSelecao, BorderLayout.CENTER);
        revalidate();
        repaint();
        pack();
        setLocationRelativeTo(null);
    }

    private JButton criarBotaoCard(String titulo, String sub, int linhas, int colunas, int minas) {
        JButton btn = new JButton("<html><center><b>" + titulo + "</b><br><font size='3'>" + sub + "</font></center></html>");
        estilizarBotaoCard(btn);
        btn.addActionListener(e -> iniciarJogo(linhas, colunas, minas));
        return btn;
    }

    private JButton criarBotaoPersonalizado() {
        JButton btn = new JButton("<html><center><b>Personalizado</b><br><font size='3'>Escolha as regras</font></center></html>");
        estilizarBotaoCard(btn);
        btn.addActionListener(e -> {
            try {
                String l = JOptionPane.showInputDialog(this, "Linhas:");
                String c = JOptionPane.showInputDialog(this, "Colunas:");
                String m = JOptionPane.showInputDialog(this, "Minas:");
                if (l != null && c != null && m != null) {
                    int linhas = Integer.parseInt(l);
                    int colunas = Integer.parseInt(c);
                    int minas = Integer.parseInt(m);
                    iniciarJogo(linhas, colunas, minas);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Valores inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        return btn;
    }

    private void estilizarBotaoCard(JButton btn) {
        btn.setBackground(COR_BOTÃO_TELA);
        btn.setForeground(COR_TEXTO);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 80));
    }

    private void iniciarJogo(int linhas, int colunas, int minas) {
        tabuleiro = new Tabuleiro(linhas, colunas, minas);
        botoes = new JButton[linhas][colunas];
        jogadas = 0;
        tempoSegundos = 0;
        jogoIniciado = false;

        if (painelSelecao != null) remove(painelSelecao);

        configurarBarraEstatisticas();
        configurarPainelTabuleiro(linhas, colunas);

        configurarTimer();

        revalidate();
        repaint();
        pack();
        setLocationRelativeTo(null);
    }

    private void configurarBarraEstatisticas() {
        painelEstatisticas = new JPanel(new GridLayout(1, 5, 10, 0));
        painelEstatisticas.setBackground(COR_PAINEL);
        painelEstatisticas.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblMinas = new JLabel("Minas: " + getMinasRestantes(), SwingConstants.CENTER);
        lblTempo = new JLabel("Tempo: 0s", SwingConstants.CENTER);
        lblReveladas = new JLabel("Reveladas: 0", SwingConstants.CENTER);
        lblJogadas = new JLabel("Jogadas: 0", SwingConstants.CENTER);

        JButton btnVoltar = new JButton("Menu");
        btnVoltar.setBackground(COR_BOTÃO_TELA);
        btnVoltar.setForeground(COR_TEXTO);
        btnVoltar.setFocusPainted(false);
        btnVoltar.addActionListener(e -> {
            if (timer != null) timer.stop();
            exibirTelaSelecao();
        });

        lblMinas.setForeground(COR_TEXTO);
        lblTempo.setForeground(COR_TEXTO);
        lblReveladas.setForeground(COR_TEXTO);
        lblJogadas.setForeground(COR_TEXTO);

        painelEstatisticas.add(lblMinas);
        painelEstatisticas.add(lblTempo);
        painelEstatisticas.add(lblReveladas);
        painelEstatisticas.add(lblJogadas);
        painelEstatisticas.add(btnVoltar);

        add(painelEstatisticas, BorderLayout.NORTH);
    }

    private void configurarPainelTabuleiro(int linhas, int colunas) {
        painelTabuleiro = new JPanel(new GridLayout(linhas, colunas, 2, 2));
        painelTabuleiro.setBackground(COR_PAINEL);
        painelTabuleiro.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(35, 35));
                btn.setBackground(COR_CELULA_OCULTA);
                btn.setFont(new Font("SansSerif", Font.BOLD, 12));
                btn.setFocusPainted(false);

                final int l = i;
                final int c = j;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            cliqueEsquerdo(l, c);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            cliqueDireito(l, c);
                        }
                    }
                });

                botoes[i][j] = btn;
                painelTabuleiro.add(btn);
            }
        }

        add(painelTabuleiro, BorderLayout.CENTER);
    }

    private void configurarTimer() {
        if (timer != null) timer.stop();
        timer = new Timer(1000, e -> {
            tempoSegundos++;
            lblTempo.setText("Tempo: " + tempoSegundos + "s");
        });
    }

    private void cliqueEsquerdo(int l, int c) {
        if (tabuleiro.isMarcada(l, c) || tabuleiro.isRevelada(l, c)) return;

        if (!jogoIniciado) {
            jogoIniciado = true;
            timer.start();
        }

        jogadas++;
        tabuleiro.revelar(l, c);
        atualizarInterface();

        if (tabuleiro.isMinada(l, c)) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Fim de jogo! Você acertou uma mina.", "Derrota", JOptionPane.INFORMATION_MESSAGE);
            revelarTodoTabuleiro();
        } else if (tabuleiro.verificarVitoria()) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Parabéns! Você venceu em " + tempoSegundos + " segundos!", "Vitória", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cliqueDireito(int l, int c) {
        if (tabuleiro.isRevelada(l, c)) return;

        if (!jogoIniciado) {
            jogoIniciado = true;
            timer.start();
        }

        tabuleiro.alternarMarcacao(l, c);
        atualizarInterface();
    }

    private void atualizarInterface() {
        int reveladas = 0;
        int linhas = tabuleiro.getLinhas();
        int colunas = tabuleiro.getColunas();

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton btn = botoes[i][j];

                if (tabuleiro.isRevelada(i, j)) {
                    reveladas++;
                    btn.setBackground(COR_CELULA_REVELADA);
                    if (tabuleiro.isMinada(i, j)) {
                        btn.setText("💣");
                    } else if (tabuleiro.getMinasVizinhas(i, j) > 0) {
                        btn.setText(String.valueOf(tabuleiro.getMinasVizinhas(i, j)));
                        btn.setForeground(COR_TEXTO);
                    } else {
                        btn.setText("");
                    }
                } else if (tabuleiro.isMarcada(i, j)) {
                    btn.setText("🚩");
                    btn.setForeground(Color.RED);
                } else {
                    btn.setText("");
                    btn.setBackground(COR_CELULA_OCULTA);
                }
            }
        }

        lblMinas.setText("Minas: " + getMinasRestantes());
        lblReveladas.setText("Reveladas: " + reveladas);
        lblJogadas.setText("Jogadas: " + jogadas);
    }

    private void revelarTodoTabuleiro() {
        tabuleiro.revelarTodasAsMinas();
        atualizarInterface();
    }

    private int getMinasRestantes() {
        int marcadas = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMarcada(i, j)) {
                    marcadas++;
                }
            }
        }
        return tabuleiro.getTotalMinas() - marcadas;
    }
}
