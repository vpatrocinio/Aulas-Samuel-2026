import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

// Layout customizado que quebra componentes em linhas (usado nas etiquetas de gênero).

public class LayoutQuebraLinha extends FlowLayout {

    public LayoutQuebraLinha(int alinhamento, int hgap, int vgap) {
        super(alinhamento, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container alvo) {
        return calcular(alvo);
    }

    @Override
    public Dimension minimumLayoutSize(Container alvo) {
        return calcular(alvo);
    }

    private Dimension calcular(Container alvo) {
        synchronized (alvo.getTreeLock()) {
            int largura = alvo.getWidth();
            if (largura <= 0 && alvo.getParent() != null) largura = alvo.getParent().getWidth();
            if (largura <= 0) largura = 600;

            Insets margem = alvo.getInsets();
            int larguraUtil = largura - margem.left - margem.right - getHgap() * 2;

            int x = 0, alturaLinha = 0, alturaTotal = 0;
            for (int i = 0; i < alvo.getComponentCount(); i++) {
                Component c = alvo.getComponent(i);
                if (!c.isVisible()) continue;
                Dimension d = c.getPreferredSize();
                if (x > 0 && x + d.width > larguraUtil) {
                    alturaTotal += alturaLinha + getVgap();
                    x = 0;
                    alturaLinha = 0;
                }
                x += d.width + getHgap();
                alturaLinha = Math.max(alturaLinha, d.height);
            }
            alturaTotal += alturaLinha;
            return new Dimension(largura, alturaTotal + margem.top + margem.bottom + getVgap() * 2);
        }
    }
}
