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
public class LabMaker {

    private static ArrayList<Huone> huoneet;
    private static int reunanYli = 5;
    private static int alueenKorkeus = 8;
    private static RoomGiver rg;
    
    static int minAla = 0;
   static  int maxYla = 3;
    static int minVase = 0;
   static int maxOike = 3;
    
   static boolean virhe = false;
    
   static private  final int sakko = 100;
   static private  final int adOviChanse = 25; //toden näköisyys että tehdään kahden vierekkäisen huoneen välille ovi
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Random rand = new Random();
        boolean jatketaan = true;
        int kierros = 0;
        rg = new RoomGiver();
        while(kierros < 1 && jatketaan)
        {
            huoneet = new ArrayList<>();
        huoneet.add(new Huone(new int[]{0,0}, 2,2));
        
        ArrayList<Huone> validHuoneet = new ArrayList<>();
        validHuoneet.add(huoneet.get(0));
        
        while(huoneet.size() < 12 && validHuoneet.size() > 0 && jatketaan)
        {
            int pos = rand.nextInt(validHuoneet.size());
            Huone uusi = lisaaHuone(huoneet.get(pos));
            if(uusi == null)
            {
                validHuoneet.remove(pos);
            }else{
                validHuoneet.add(uusi);
                huoneet.add(uusi);
                updateDimensions(uusi);
                if(!Ovi.makeOvet(huoneet.get(pos), uusi))
                {
                    System.out.println("JOTAIN MENI VIKAAN, EI SAATU LISÄTTYÄ OVEA!");
                    System.out.println("Isäntä huone: " + huoneet.get(pos).getPos(0) + ","+ huoneet.get(pos).getPos(1) + " " + huoneet.get(pos).getLeveys() + "x"+ + huoneet.get(pos).getKorkeus());
                    System.out.println("uusi huone: " + uusi.getPos(0) + ","+ uusi.getPos(1) + " " + uusi.getLeveys() + "x"+ + uusi.getKorkeus());
                    jatketaan = false;
                    
                }
            }
        }
        kierros++;
        }
        //katsotaan ovatko huoneet vierekkäin ja jos on, mahdollisesti tehdään ovi niiden välille mikäli sitä ei jo ole olemassa.
        for(int i = 0; i < huoneet.size()-1; i++)
        {
            for(int j = i+1; j < huoneet.size(); j++)
            {
                int[] vier = huoneet.get(i).calculateReunaOverlp(huoneet.get(j));
                if(vier[3] > 0)
                {
                    //tarkistetaan onoko huoneiden välillä jo ovi
                    if(!huoneet.get(i).connectionExsists(huoneet.get(j))) //viddu, nuolenpäätä!
                    {
                        //huoneiden välillä ei ole ovea.
                        if(rand.nextInt(100)<= adOviChanse)
                        {
                            //lisätään huoneiden välille ovi
                            System.out.println("Tehtiin extra ovi");
                            Ovi.makeOvet(huoneet.get(i), huoneet.get(j));
                        }
                    }
                }
            }
        }
        drawMap();
    }
    
    //tätä tulee muuttaa
    public static Kartta generateMap(int stx, int sty, int xlen, int ylen, boolean useDetailed)
    {
        Kartta kartta = new Kartta(stx, sty, xlen, ylen);
        for(Huone h: huoneet)
        {
            if(useDetailed)
            {
                kartta.drawDetailed(h);
            }else{
            kartta.drawTo(h);
            }
        }
        return kartta;
    }
    
    //yritetään lisätä huone tämä huoneen viereen.
    //palauttaa true jos lisäys onnistui, muuten false.
    public static Huone lisaaHuone(Huone h)
    {
        boolean pal = true;
        //lasketaan paljonko kullakin sivulla on varattuja ruutuja
        float[] sivArvo = new float[4];
        for(int i = 0; i < sivArvo.length; i++)
        {
            float sakkoFactor = sakko * ((float)(maxOike-minVase)/(maxYla-minAla) - 1);
            if(i == 0 || i == 2)
            {
                sakkoFactor = sakko * ((float)(maxYla-minAla)/(maxOike-minVase) - 1);
            }
            sivArvo[i] = calculateOcupied(h, i) + sakkoFactor;
        }
        //etsitään pienin arvo sivArvo arraystä
        for(int j = 0; j < 4; j++)
        {
        int pieninIn = 0;
        for(int i = 1; i < sivArvo.length; i++)
        {
            if(sivArvo[pieninIn] > sivArvo[i])
            {
                pieninIn = i;
            }
        }
        //etsitään valitulta sivulta suorakaide jonka sisään huone laitetaan
        int wid = h.getLeveys() + 2*reunanYli;
        int hei = alueenKorkeus;
        int kx = h.getPos(0) - reunanYli;
        int ky = h.getPos(1) + h.getKorkeus();
        if(pieninIn == 2)
        {
            ky = h.getPos(1)-alueenKorkeus;
        }
        if(pieninIn == 1 || pieninIn == 3)
        {
            wid = alueenKorkeus;
            hei = h.getKorkeus()+ 2*reunanYli;
            ky = h.getPos(1) - reunanYli;
            kx = h.getPos(0) + h.getLeveys();
            if(pieninIn == 3)
            {
                kx = h.getPos(0) - alueenKorkeus;
            }
        }
            //System.out.println("generating map:" +kx + ","+ ky +" " +wid+"x"+ hei);
        Kartta k = generateMap(kx, ky, wid, hei, false);
        //muutetaan kartta niin että etsittävä sivu on pohjassa
        if(pieninIn == 2)
        {
            k.mirror();
        }else if(pieninIn == 1 || pieninIn == 3)
        {
            k.transpose();
            if(pieninIn == 3)
            {
                k.mirror();
            }
        }
        int rarSiv = h.getLeveys();
        if(pieninIn == 1 ||pieninIn == 3)
        {
            rarSiv = h.getKorkeus();
        }
        int[] kohde = k.calculateBox(new int[]{h.getPos(0),h.getPos(1)},rarSiv); //<-- virhe? otetaan aina leveys vaikka tulisi ottaa leveys tai korkeus, riippuen miltä puolelta etsitään!
        
        //tarkistetaan että saatu laatikko on tarpeeksi iso
        if(kohde[2] < 3 || kohde[3] < 4)
        {
            //kyseessä ei ole tarpeeksi suuri tila. Koska tämä oli suurin mahdollinen tila, ei sivulla ole hyväksyttävää tilaa
            sivArvo[pieninIn] = Integer.MAX_VALUE;
        }else{
            
            //System.out.println("Tehdään huone, parametrit: i " + pieninIn + " kulma: " + kohde[0] + "," + kohde[1] + " koko: " + kohde[3] + "x" + kohde[2]);
            //kyseessä on hyväksyttävä tila. Asetetaan siihen huone
            //arvotaan huoneen koko
            //TÄMÄ VOIDAAN EHKÄ KORVATA HUONEEN CALCULATEADJECTANCY METODILLA!
            Random rand = new Random();
            Huone uusiHuone = rg.giveRoom(kohde[2], kohde[3]);
            int huoneenLeveys = uusiHuone.getLeveys();
            int huoneenKorkeus = uusiHuone.getKorkeus();
            //lasketaan huoneen vasen ala nurkka, riippuen seinästä
            int x = kohde[0];
            int y = kohde[1];
            int yla, ala;
            if(pieninIn == 0 || pieninIn == 2)
            {
                yla = Math.min(h.getPos(0) + h.getLeveys() -1, kohde[0] + kohde[3] - huoneenLeveys);
                ala = Math.max(kohde[0], h.getPos(0)-huoneenLeveys+1);
                try{
                x = ala + rand.nextInt(yla-ala+1);
                }catch(IllegalArgumentException e)
                {
                    System.out.println("VIRHE: " + ala + " " + yla);
                }
                if(pieninIn == 2)
                {
                    y = y+kohde[2]- huoneenKorkeus;
                }
            }else{
                yla = Math.min(h.getPos(1) + h.getKorkeus() -1, kohde[1] + kohde[2] - huoneenKorkeus);
                ala = Math.max(kohde[1], h.getPos(1)-huoneenKorkeus+1);
                try{
                y = ala + rand.nextInt(yla-ala+1);
                }catch(IllegalArgumentException e)
                {
                    System.out.println("VIRHE: " + ala + " " + yla);
                }
                if(pieninIn == 3)
                {
                    x = x+kohde[3]- huoneenLeveys;
                }
            }   
            uusiHuone.setPos(new int[]{x,y,0});
            if(h.calculateReunaOverlp(uusiHuone)[3] <= 0)
            {
                //Huoneet eivät olleet viereääkäin. Tulostetaan tiedot ja lopetetaan...
                System.out.println("Virhe, uusi huone ei vanhan vieressä");
                System.out.println("Yla: " + yla + " ala: " + ala);
                System.out.println("Isäntä huone: " + h.getPos(0) + "," + h.getPos(1) + " " + h.getLeveys() + "x" + h.getKorkeus());
                System.out.println("uusi huone: " + uusiHuone.getPos(0) + "," + uusiHuone.getPos(1) + " " + uusiHuone.getLeveys() + "x" + uusiHuone.getKorkeus());
                System.out.println("Kartta: " + kx + "," + ky + " " +wid + "x" + hei);
                String mirrored = "Ei";
                String transposed = "Ei";
                if(k.mirrored) mirrored = "kyllä";
                if(k.transposed) transposed = "Kyllä";
                System.out.println("Kartta: mirrored: "+ mirrored + " transposed: " + transposed);
                System.out.println("Kohde: " + kohde[0] + "," + kohde[1] + " " + kohde[3]+ "x" + kohde[2]);
                k.printMap();
            }
            return uusiHuone;

        }
        }
        return null;
    }
    
    
    
    public static int calculateOcupied(Huone h, int suunta)
    {
        int[] lahto = new int[]{h.getPos(0), h.getPos(1)};
        int height = alueenKorkeus;
        int width = 2*reunanYli + h.getLeveys();
        //shiftataan tutkittavan alueen kulma oikeaan pisteeseen
        if(suunta == 0 || suunta == 2)
        {//katsotaanko ylä tai ala puolelata
            lahto[0] = lahto[0] - reunanYli;
            if(suunta == 0)
            {//katsotaan ylhäältä
                lahto[1] = lahto[1] + h.getKorkeus();
            }else{//katsotaan alhaalta
                lahto[1] = lahto[1]-alueenKorkeus;
            }
        }else{//katsotaanko vasemmalta tai oikealta
            lahto[1] = lahto[1] - reunanYli;
            height = 2*reunanYli + h.getKorkeus();
            width = alueenKorkeus;
            if(suunta == 1)
            {//katsotaan oikealta
                lahto[0] = lahto[0] + h.getLeveys();
            }else{//katsotaan vassemmalle
                lahto[0] = lahto[0]-alueenKorkeus;
            }
        }
        Kartta k = generateMap(lahto[0], lahto[1], width, height, false);
        //k.printMap();
        return k.filledSpaces();
    }
    
    public static void updateDimensions(Huone h)
    {
        if(h.getPos(0) < minVase) minVase = h.getPos(0);
        if(h.getPos(1) < minAla) minAla = h.getPos(1);
        if(h.getPos(0)+h.getLeveys() > maxOike) maxOike = h.getPos(0)+h.getLeveys();
        if(h.getPos(1)+h.getKorkeus()> maxYla) maxYla = h.getPos(1)+h.getKorkeus();
    }
    
    
    public static void drawMap()
    {       
        generateMap(minVase, minAla, maxOike - minVase, maxYla - minAla, true).printMap();
        for(Huone h: huoneet)
        {
            h.printTiedot();
        }
        System.out.println("Ylä ala suhde(h/w): " + ((float)maxYla-minAla)/(maxOike-minVase));
    }
    
    
    
    
}
