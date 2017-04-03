/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package labmaker;

import java.util.ArrayList;

/**
 *
 * @author janne
 */
public class Kartta {
    
    private boolean[][] kartta;
    private int[] pos;
    
    public boolean mirrored = false;
    public boolean transposed = false;
    
    
    public Kartta(){
     this(0,0,10,10);
    }
    
    public Kartta(int xP, int yP, int xl, int yl)
    {
        kartta =  new boolean[xl][yl];
        pos = new int[]{xP,yP};
    }
    
    //piirtää annetun huoneen itseensä
    public void drawTo(Huone annettu)
    {
        //muutetaan tämän paikka matriisin koordinaatistoon
        //int[] relPos = transformToCoordinates(new int[]{annettu.getPos(0) - pos[0], annettu.getPos(1) - pos[1]});
        int[] relPos = new int[]{annettu.getPos(0) - pos[0], annettu.getPos(1) - pos[1]};
        //piirretään laatikko karttaan
        for(int i = Math.max(0, relPos[0]); i < Math.min (relPos[0]+annettu.getLeveys(),kartta.length);i++ )
        {
            for(int j = Math.max(0,relPos[1]); j < Math.min (relPos[1]+annettu.getKorkeus(),kartta[0].length); j++)
            {
                kartta[i][j] = true;
            }
        }
    }
    
    //piirtää vain ulko seinät
    public void drawDetailed(Huone annettu)
    {
        int i = -1;
        boolean korkeusDone = false;
        int[] relPos = new int[]{annettu.getPos(0) - pos[0], annettu.getPos(1) - pos[1]};
        try{
        for(i = 0; i < annettu.getKorkeus(); i++)
        {
            kartta[relPos[0]][relPos[1]+i] = true;
            kartta[relPos[0]+annettu.getLeveys()-1][relPos[1]+i] = true;
        }
        korkeusDone = true;
        for(i = 0; i < annettu.getLeveys(); i++)
        {
            kartta[relPos[0]+i][relPos[1]] = true;
            kartta[relPos[0]+i][relPos[1]+annettu.getKorkeus()-1] = true;
        }
        }catch(IndexOutOfBoundsException e)
        {
            String k = "korkeus";
            if(korkeusDone) k = "leveys";
            System.out.println("VIRHE: EI SAATU PIIRRETTYÄ HUONETTA!");
            System.out.println("Tiedot: i: " + i + " oltiin piirtämässä " + k);
            System.out.println("Annettu huone: " + annettu.getPos(0) + "," + annettu.getPos(1) + " " + annettu.getLeveys() + "x" + annettu.getKorkeus());
            System.out.println("Kartta: " + pos[0] + "," + pos[1] + " " + kartta.length + "x" + kartta[0].length);
        }
        int[][] ovet = annettu.getOvet();
        for(int[] p: ovet)
        {
            kartta[p[0]-pos[0]][p[1]-pos[1]] = false;
        }
    }
    

    
    public void printMap()
    {
        String line = "";
        for(int j = kartta[0].length-1; j >= 0; j--)
        {
            for(int i = 0; i < kartta.length; i++)
            {
                if(kartta[i][j])
                {
                    line = line + "X";    
                        }else{
                    line = line + ".";    
                }
            }
            System.out.println(line);
            line = "";
        }
    }
    
    public void transpose()
    {
        transposed = !transposed;
        boolean[][] newKartta = new boolean[kartta[0].length][kartta.length];
        for(int i = 0; i < kartta.length; i++)
        {
            for(int j = 0; j < kartta[0].length; j++)
            {
                newKartta[j][i] = kartta[i][j];
            }
        }
        kartta = newKartta;
    }
    
    public void mirror()
    {
        mirrored = !mirrored;
        boolean[][] newKartta = new boolean[kartta.length][kartta[0].length];
        for(int i = 0; i < kartta.length; i++)
        {
            for(int j = 0; j < kartta[0].length; j++)
            {
                newKartta[i][j] = kartta[i][kartta[i].length -j-1];
            }
        }
        kartta = newKartta;
    }
    
    //muuttaa annetun pisteen kartan koordinaatistoon
    private int[] transformToCoordinates(int[] raw)
    {
        int[] transformed = new int[]{raw[0] - pos[0],raw[1] - pos[1] };
        if(transposed)
        {
            int tmp = transformed[0];
            transformed[0] = transformed[1];
            transformed[1] = tmp;
        }
        if(mirrored)
        {
            transformed[1] = kartta[0].length - transformed[1] -1;
        }
        return transformed;
    }
    
    //laskee mahdollisimman ison suorakaiteen tässä kartassa, niin että suorakaiteen ala kylki on kartan alaosassa kiinni
    //palautus:[vAK.x,vAK.y, korkeus,leveys]
    //koordinaatit da dimensiot ovat muunnettu yleiseen koordinaatistoon
    public int[] calculateBox(int[] rox, int ow)
    {
        
        int ox = transformToCoordinates(rox)[0];
        //System.out.println("Piste " + rox[0] + "," + rox[1] + " muuttui pisteeksi " + transformToCoordinates(rox)[0] + "," + transformToCoordinates(rox)[1]);
        int[] suorakaiteet = new int[kartta.length];
        int besti = 0;
        int besth = 0;
        int bestw = 0;
        for(int i = 0; i < kartta.length; i++)
        {
            //lasketaan vapaa korkeus
            int kork = 0;
            while(kork < kartta[i].length && !kartta[i][kork])
            {
                kork++;
            }
            suorakaiteet[i] = kork;
            //käydään aktiiviset suorakaiteet läpi, ja katsotaan mitkä katkeaa
            for(int j = 0; j <= i; j++) 
            {
                if(suorakaiteet[j] > kork)
                {
                    //suorakaide katkeaa. lasketaan sen pinta ala ja verrataan sitä parhaaseen
                    if( isValid(j, i-j, ox, ow) && suorakaiteet[j]*(i-j) > besth*bestw)
                    {
                        //katkennut suorakaide oli parempi
                        besti = j;
                        besth = suorakaiteet[j];
                        bestw = i-j;
                    }
                    //asetetaan uuden suorakaiteen korkeudeksi nykyinen korkeus
                    suorakaiteet[j] = kork;
                }
            }
            
        }
        //suljetaan kaikki suorakaiteet
         for(int j = 0; j < kartta.length; j++) //tässä tapahtuu nyt pieni kummallisuus... on mahdollista että tämä ruutu on isoin suorakaide, vaikka se ei ole vielä katkennut. Ei kuitenknaan ehkä haittaa
            {

                    //suorakaide katkeaa. lasketaan sen pinta ala ja verrataan sitä parhaaseen
                    if(isValid(j, (kartta.length-j), ox, ow) && suorakaiteet[j]*(kartta.length-j) > besth*bestw)
                    {
                        //katkennut suorakaide oli parempi
                        besti = j;
                        besth = suorakaiteet[j];
                        bestw = kartta.length-j;
                    }
            }
        
        //ollaan löydetty kandidaatti, ehkä. Ainoastaan tulee käsitellä tilanne missä arvot ovat edelleen 0.
        //parhaan pisteen koordinaatti on (besti,0). tulee muuttaa se normaaliin koordinaatistoon
        //tehdään peilaus mikäli tarvetta
        int[] koord = new int[]{besti,0};
        if(mirrored)
        {
            koord[1] = kartta[0].length - besth; //poikkeus lasketusta! y=0, aina näin ollen sitä ei tarvita funktiossa
        }
        if(transposed)
        {
            //flipataan koordinaatit
            int tmp = koord[0];
            koord[0] = koord[1];
            koord[1] = tmp;
            //flipataan dimensiot
            tmp = besth;
            besth = bestw;
            bestw = tmp;
        }
        int[] koordLop = new int[]{koord[0] + pos[0],koord[1] + pos[1]};
        return new int[]{koordLop[0],koordLop[1],besth,bestw};
    }
    
    //tarkisteteaan että ehdokas suorakaide on annetulla alueella
    private boolean isValid( int bx, int bw, int ox, int ow)
    {
        return bx + bw > ox && bx < ox+ow;
    }
    
    public int filledSpaces()
    {
        int pal = 0;
        for(boolean[] r: kartta)
        {
            for(boolean b: r)
            {
                if(b) pal++;
            }
        }
        return pal;
    }
    
}
