/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package labmaker;

import java.util.Random;

/**
 *
 * @author janne
 */
public class Ovi {
    
    private Ovi vastin;
    private Huone paikka;
    
    private int[] pos; //tämä piste on oven paikka
    
    public Ovi(int[] posi, Huone h)
    {
        pos = posi;
        paikka = h;
    }
    
    public Ovi(int[] posi, Ovi toinen, Huone h)
    {
        pos = posi;
        vastin = toinen;
        paikka = h;
        toinen.setVastin(this);
    }
    
    public void setVastin(Ovi o)
    {
        vastin = o;
    }
    
    public int[] getPos()
    {
        return new int[]{pos[0],pos[1]};
    }
    
    public static boolean makeOvet(Huone h1, Huone h2)
    {
        //lasketaan oven paikkat huoneessa
        int[] paikka = h1.calculateReunaOverlp(h2);
        int[] paikkaToisessa = h2.calculateReunaOverlp(h1);
        //pituuksien tulisi olla samoja
        if(paikka[3] != paikkaToisessa[3])
        {
            System.out.println("VIRHE: viereisyys janat eri pituisia! " + paikka[3] + " " + paikkaToisessa[3]);
            return false;
        }else if(paikka[3] <= 0)
        {
            System.out.println("VIRHE: adjectant pituus: " + paikka[3]);
            return false;
        }
        int x,y,x2,y2,pit;
        Random rand = new Random();
        pit = rand.nextInt(paikka[3]);
        if(paikka[2] == 0)
        {
            x = paikka[0]+pit;
            x2 = paikkaToisessa[0]+pit;
            y = paikka[1];
            y2 = paikkaToisessa[1];
        }else{
            x = paikka[0];
            x2 = paikkaToisessa[0];
            y = paikka[1]+pit;
            y2 = paikkaToisessa[1]+pit;
        }
       
        //ollaan laskettu oven paikka huoneessa h1 ja h2
        Ovi o1 = new Ovi(new int[]{x,y},h1);
        Ovi o2 = new Ovi(new int[]{x2,y2},o1,h2);
        h1.addOvi(o1);
        h2.addOvi(o2);
        return true;
        
    }
    
    public Huone getDestination()
    {
        return vastin.getHuone();
    }
    
    public Huone getHuone()
    {
        return paikka;
    }
    
}
