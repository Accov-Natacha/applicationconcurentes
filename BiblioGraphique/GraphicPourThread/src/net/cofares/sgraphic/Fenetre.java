package net.cofares.sgraphic;

import java.awt.BasicStroke;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

/*-------------------------------------------------------------------*/
 /*          ATTENTION : SWING N'EST PAS THREAD-SAFE                  */
 /*-------------------------------------------------------------------*/
 /*---------------------------------------------------------------------------------*/
class UneLigne extends JComponent {

    final static BasicStroke stroke = new BasicStroke(10.0f);
    Color couleur;
    int x1, y1, x2, y2;

    public UneLigne(int x1, int y1, int x2, int y2, String couleur) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        setOpaque(false);

        try {
            this.couleur = Couleur.parseCouleur(couleur);
        } catch (ColorNotFoundException e) {
            System.out.println("Couleur incorrecte. Noir utilisé à la place");
            this.couleur = Color.black;
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paint(g2);
        // Now we can compute the corner points...
        g2.setColor(couleur);
        g2.setStroke(stroke);
        g2.drawLine(x1, y1, x2, y2);
    }

}

/*---------------------------------------------------------------------------------*/
class UnPoint extends UneLigne {

    public UnPoint(int x1, int y1, String couleur) {
        super(x1, y1, x1, y1, couleur);
    }
}

/*---------------------------------------------------------------------------------*/
 /*---------------------------------------------------------------------------------*/
public class Fenetre extends JFrame {

    private int largeur, hauteur;
    private Container cp;

    /*-----------------------------  LES CONSTRUCTEURS  ------------------------------*/
    /**
     * Cree une fenetre de dimensions width x height, avec un titre title sans
     * couleur de fond
     *
     * @param width
     * @param height
     * @param title
     */
    public Fenetre(int width, int height, String title) {
        //Create and set up the window.
        super(title);
        largeur = width;
        hauteur = height;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //setSize(width, height);
        //Display the window.
        setMinimumSize(new Dimension(width, height));
        cp = this.getContentPane();
    }

    /**
     * Cree une fenetre de dimensions width x height, avec un titre title et une
     * couleur de fond couleur
     *
     * @param width
     * @param height
     * @param title
     * @param couleur
     */
    public Fenetre(int width, int height, String title, String couleur) {
        this(width, height, title);
        remplir(couleur);
    }

    /*-------------------------   REMPLIR LA FENETRE   ------------------------*/
    /**
     * Remplit le fond de la fenetre avec la couleur couleur
     *
     * @param couleur
     */
    public void remplir(String couleur) {
        try {
            cp.setBackground(Couleur.parseCouleur(couleur));
        } catch (ColorNotFoundException e) {
            System.out.println("Couleur de fond incorrecte. Blanc utilisé à la place");
            cp.setBackground(Color.white);
        }
        setVisible(true);
    }

    /*---------------------------   TRACER UN POINT   --------------------------*/
    private UnPoint tracerPoint(int x, int y, String couleur) {
        try {
            if (x < 0 || x >= largeur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en largeur");
            }
            if (y < 0 || y >= hauteur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en hauteur");
            }
            synchronized (cp) {
                UnPoint pt = new UnPoint(x, y, couleur);
                add(pt);
                pt.setVisible(true);
                return pt;
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

    private void tracerPoint(int x, int y) {
        tracerPoint(x, y, "black");
    }

    /**
     * Affiche le point pt en avec la couleur couleur
     *
     * @param pt
     * @param couleur
     */
    public void tracerPoint(Point pt, String couleur) {
        tracerPoint(pt.x, pt.y, couleur);
    }

    /**
     * Affiche le point pt en noir
     *
     * @param pt
     */
    public void tracerPoint(Point pt) {
        tracerPoint(pt.x, pt.y, "black");
    }

    /*--------------------------   TRACER UNE LIGNE   --------------------------*/
    /**
     * Trace une ligne en couleur entre les points (x1, y1) et (x2, y2)
     */
    private void tracerLigne(int x1, int y1, int x2, int y2, String couleur) {
        try {
            if (x1 < 0 || x1 >= largeur || x2 < 0 || x2 >= largeur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en largeur");
            }
            if (y1 < 0 || y1 >= hauteur || y2 < 0 || y2 >= hauteur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en hauteur");
            }
            synchronized (cp) {
                UneLigne l = new UneLigne(x1, y1, x2, y2, couleur);
                add(l);
                setVisible(true);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Trace une ligne noire entre les points (x1, y1) et (x2, y2)
     */
    private void tracerLigne(int x1, int y1, int x2, int y2) {
        tracerLigne(x1, y1, x2, y2, "black");
    }

    /**
     * Trace une ligne en couleur entre les points pt1 et pt2
     *
     * @param pt1
     * @param pt2
     */
    public void tracerLigne(Point pt1, Point pt2, String couleur) {
        tracerLigne(pt1.x, pt1.y, pt2.x, pt2.y, couleur);
    }

    /**
     * Trace une ligne noire entre les points pt1 et pt2
     */
    public void tracerLigne(Point pt1, Point pt2) {
        tracerLigne(pt1.x, pt1.y, pt2.x, pt2.y, "black");
    }

    /*--------------------   TRACER UNE LIGNE POINT A POINT  -------------------*/
    /**
     * Trace point par point une ligne en couleur du point (x1, y1) au point
     * (x2, y2)
     */
    private void tracerLignePointAPoint(int x1, int y1, int x2, int y2, String couleur) {
        /* intervalle entre le trace de 2 points. Pour une meilleure visualisation du parallelisme */
        final int DELAI = 5;
        Component pc;
        int x, y, Xincr, Yincr;

        try {
            if (x1 < 0 || x1 >= largeur || x2 < 0 || x2 >= largeur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en largeur");
            }
            if (y1 < 0 || y1 >= hauteur || y2 < 0 || y2 >= hauteur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en hauteur");
            }

            try {
                Couleur.parseCouleur(couleur);
            } catch (ColorNotFoundException e) {
                System.out.println("Couleur du crayon incorrecte. Noir utilisé à la place");
                couleur = "black";
            }

            // Bresenham algorithm. Implementation entirely poached from Kenny Hoff (95).
            int dX = x2 > x1 ? x2 - x1 : x1 - x2;
            int dY = y2 > y1 ? y2 - y1 : y1 - y2;;

            if (x1 > x2) {
                Xincr = -1;
            } else {
                Xincr = 1;
            }
            if (y1 > y2) {
                Yincr = -1;
            } else {
                Yincr = 1;
            }

            x = x1;
            y = y1;
            try {
                if (dX >= dY) {
                    int dPr = dY << 1;
                    int dPru = dPr - (dX << 1);
                    int P = dPr - dX;

                    while (dX >= 0) {
                        /* on n'utilise pas tracerPoint pour ne pas repeter la definition de g 
      		 		* et la verification de la couleur */
                        synchronized (cp) {
                            pc = add(new UnPoint(x, y, couleur));
                            setVisible(true);
                        }
                        //Thread.yield();
                        attendre(DELAI);		// pour eviter une execution trop rapide...
                        //remove(pc);
                        //setVisible(false);
                        if (P > 0) {
                            x += Xincr;
                            y += Yincr;
                            P += dPru;
                        } else {
                            x += Xincr;
                            P += dPr;
                        }
                        dX--;
                    }
                } else {
                    int dPr = dX << 1;
                    int dPru = dPr - (dY << 1);
                    int P = dPr - dY;

                    while (dY >= 0) {
                        synchronized (cp) {
                            pc = add(new UnPoint(x, y, couleur));
                            setVisible(true);
                        }
                        //Thread.yield();
                        attendre(DELAI);
                        //remove(pc);
                        //setVisible(false);
                        if (P > 0) {
                            x += Xincr;
                            y += Yincr;
                            P += dPru;
                        } else {
                            y += Yincr;
                            P += dPr;
                        }
                        dY--;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Probleme :  thread interrompu");
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    /*--------------------   TRACER UNE LIGNE POINT A POINT  -------------------*/
    /**
     * Trace point par point une ligne en couleur du point (x1, y1) au point
     * (x2, y2)
     */
    private void deplacerPointAPoint(int x, int y, int dx, int dy, String couleur) {
        /* intervalle entre le trace de 2 points. Pour une meilleure visualisation du parallelisme */
        final int DELAI = 15;
        Component pc;

        try {
            if (x < 0 || x >= largeur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en largeur");
            }
            if (y < 0 || y >= hauteur) {
                throw new IndexOutOfBoundsException("Erreur coordonnées : pixel sort en hauteur");
            }

            try {
                Couleur.parseCouleur(couleur);
            } catch (ColorNotFoundException e) {
                System.out.println("Couleur du crayon incorrecte. Noir utilisé à la place");
                couleur = "black";
            }

            while (true) {
                synchronized (cp) {
                    pc = add(new UnPoint(x, y, couleur));
                    setVisible(true);
                }
                attendre(DELAI);
                synchronized (cp) {
                    remove(pc);
                }
                if (x + dx <= 0 || x + dx >= largeur) {
                    dx = -dx;
                }
                x += dx;
                if ((y + dy) <= 0 || (y + dy) >= hauteur) {
                   
                    dy = -dy;
                }
                y += dy;
                 System.out.printf("y=%d dy=%d H=%d\n", y,dy, hauteur);
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Trace point par point une ligne noire du point (x1, y1) au point (x2, y2)
     */
    private void tracerLignePointAPoint(int x1, int y1, int x2, int y2) {
        tracerLignePointAPoint(x1, y1, x2, y2, "black");
    }

    /**
     * Trace point par point une ligne en couleur du point pt1 au point pt2
     */
    public void tracerLignePointAPoint(Point pt1, Point pt2, String couleur) {
        tracerLignePointAPoint(pt1.x, pt1.y, pt2.x, pt2.y, couleur);
    }

    public void deplacerPointAPoint(Point pt1, Point DX, String couleur) {
        deplacerPointAPoint(pt1.x, pt1.y, DX.x, DX.y, couleur);
    }

    /**
     * Trace point par point une ligne noire du point pt1 au point pt2
     */
    public void tracerLignePointAPoint(Point pt1, Point pt2) {
        tracerLignePointAPoint(pt1.x, pt1.y, pt2.x, pt2.y, "black");
    }

    public void deplacerPointAPoint(Point pt1, Point DX) {
        deplacerPointAPoint(pt1.x, pt1.y, DX.x, DX.y, "black");
    }

    /*--------------------------  L'ATTENTE  -------------------------------------------*/
 /* pour remplacer Thread.yield() si l'execution est trop rapide !!! 
   (on n'arrive pas bien a visualiser le parallelisme) */
    private static void attendre(long duree) throws InterruptedException {
        Thread.sleep(duree);
    }

}
