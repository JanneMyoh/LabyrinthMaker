/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package labmaker;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author janne
 */
public class Huone {
    
    private int[] posit;
    boolean posSet = false;
    private int korkeus,leveys;
    private ArrayList<Ovi> ovet;
    
    
    public Huone()
    {
        ovet = new ArrayList<>();
    }
    
    public Huone(int kor, int lev)
    {
        this();
        korkeus = Math.max(1, kor);
        leveys = Math.max(1, lev);
        
    }
    
    public Huone(int[] pos, int kor, int lev)
    {
        this(kor,lev);
    
        posit = new int[3];
        ovet = new ArrayList<>();
        for(int i = 0; i < posit.length && i < pos.length; i++)
        {
            posit[i] = pos[i];
        }
        posSet = true;
   
    }
    
    public int getPos(int i)
    {
        return posit[i];
    }
    
    public int getLeveys(){return leveys;}
    public int getKorkeus(){return korkeus;}
    

    public void setPos(int[] pos){
        if(!posSet){ 
            posit = new int[3];
            for(int i = 0; i < pos.length; i++)
            {
                posit[i] = pos[i];
            }
        }
    }
    
    public void printTiedot()
    {
        System.out.println("Huoneen paikka:" + posit[0] + "," + posit[1] + " koko: " + leveys + "x" + korkeus);
    }
    
   
    public void addOvi(Ovi o)
    {
        ovet.add(o);
    }
    
    public int[][] getOvet()
    {
        int[][] pal = new int[ovet.size()][2];
        for(int i = 0; i< ovet.size(); i++)
        {
            pal[i][0] = ovet.get(i).getPos()[0];
            pal[i][1] = ovet.get(i).getPos()[1];
        }
        return pal;
    }
    
    //laskee alueen jolla tämä ja annettu huoneen reunat koskettavat
    //[alku x koord, alku y koord,suunta: 0 vaaka, 1 pysty, pituus]
    public int[] calculateReunaOverlp(Huone o, boolean vaaka)
    {
        if(vaaka)
        {
            //tarkistetaan ovatko ne korkeutensa puolesta linjassa
            if(Math.min(posit[1]+korkeus,o.getPos(1)+o.getKorkeus()) != Math.max(posit[1], o.getPos(1)))
            {
                //huoneet ovat eri y tasoilla
                //System.out.println("Eri y tasoilla");
                return new int[]{0,0,0,-1};
            }
        }else{
            {
            //tarkistetaan ovatko ne vaaka suunnassa linjassa
            if(Math.min(posit[0]+leveys,o.getPos(0)+o.getLeveys()) != Math.max(posit[0], o.getPos(0)))
            {
                //huoneet ovat eri y tasoilla
                //System.out.println("Eri x tasoilla");
                return new int[]{0,0,0,-1};
            }
        }
        }
        int tamanC = posit[1];
        int tamanD = korkeus;
        int toisenC = o.getPos(1);
        int toisenD = o.getKorkeus();
        if(vaaka)
        {
        tamanC = posit[0];
        tamanD = leveys;
        toisenC = o.getPos(0);
        toisenD = o.getLeveys();
        }
        int yla,ala, pit, tkoo;
        yla = Math.min(tamanC+tamanD, toisenC+toisenD);
        ala = Math.max(tamanC, toisenC);
        pit = yla - ala;
        if(vaaka)
        {
            tkoo = posit[1];
            if(posit[1] < o.getPos(1))
            {
                tkoo += korkeus-1;
                
            }
        }else{
            tkoo = posit[0];
            if(posit[0] < o.getPos(0))
            {
                tkoo += leveys-1;
                
            }
        }
        int[] pal = new int[4];
        if(vaaka)
        {
            pal[0] = ala;
            pal[1] = tkoo;
            pal[2] = 0;
            
        }else{
            pal[0] = tkoo;
            pal[1] = ala;
            pal[2] = 1;
        }
        pal[3] = pit;
        return pal;
    }
    
    //lasketaan molemmat sivut
    public int[] calculateReunaOverlp(Huone o)
    {
        int[] pal = calculateReunaOverlp(o, true);
        if(pal[3] <= 0) pal = calculateReunaOverlp(o, false);
        return pal;
    }
    
    //tarkistaa pääseekö tästä huoneesta annettuun huoneeseen
    public boolean connectionExsists(Huone h)
    {
        for(Ovi o: ovet)
        {
            //Tässä vaaditaan että huoneet ovat konkreettisesti samoja olioita, tai että ainakin molemmat osoittavat samaan muisti paikkaan
            if(o.getDestination() == h) //voidaanko tehdä näin? vai pitäisikö tehdä compareTo metodi?
            {
                return true;
            }
        }
        return false;
    }
    
   
    
    
    
}
