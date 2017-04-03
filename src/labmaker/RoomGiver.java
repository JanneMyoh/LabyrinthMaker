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
public class RoomGiver {
    
    //koska koko homma on tarkoitus viedä unityyn(C#) ei tämän toteutus ole tärkeä, sillä toiminta periaate on omanlaisensa unityssä
    //tämän luokan tehtävä tässä kohtaa on ainoastaan antaa huone joka sopii annettuun tilaan.
    
    int[][] huoneet; //alkio: {korkeus,leveys}
    
    public RoomGiver()
    {
        huoneet = new int[][]{
            new int[]{2,2},
            new int[]{2,3},
            new int[]{3,3},
            new int[]{3,4},
            new int[]{4,4},
            new int[]{4,2},
            new int[]{5,5},
            new int[]{5,4},
            new int[]{5,3},
            new int[]{6,6},
            new int[]{6,5},
            new int[]{6,4},
            new int[]{7,7},
            new int[]{7,6},
            new int[]{7,5},
            new int[]{8,8}
            
        };
    }
    
    //arvotaan jokin annettuun tilaan sopiva huone
    public Huone giveRoom(int lev, int kor)
    {
        //etsitään ne huoneet jotka sopivat tilaan
        ArrayList<Integer> sallitut = new ArrayList<>();
        for(int i = 0; i < huoneet.length; i++)
        {
            if(huoneet[i][0] <= lev && huoneet[i][1] <= kor)
            {
                //sallittu
                sallitut.add(i);
            }else if(huoneet[i][0] <= kor && huoneet[i][1] <= lev)
            {
                //huoneen transpoosi on sallittu
                sallitut.add(i);
            }
        }
        //arvotaan huone
        Random rand = new Random();
        int kode = sallitut.get(rand.nextInt(sallitut.size()));
        Huone pal = new Huone(huoneet[kode][0], huoneet[kode][1]);
        if(huoneet[kode][0] > lev || huoneet[kode][1] > kor)
        {
            //kyseessä oli huoneen transpoosi
            pal = new Huone(huoneet[kode][1], huoneet[kode][0]);
        }
        return pal;
    }
    
}
