import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class Gwiazda implements Serializable {

    enum Polkula {
        PN,
        PD
    }

    final String nazwaPattern = "^[A-Z]{3}[0-9]{4}$";
    private String nazwa = "";
    private String nazwaKatalogowa = "";
    private static final String[] literyGreckie = {"Alfa", "Beta", "Gamma", "Delta", "Epsilon", "Dzeta", "Eta",
     "Teta", "Jota", "Kappa", "Lambda", "Mi", "Ni", "Ksi", "Omikron", "Pi" , "Ro", "Sigma",
     "Tau", "Ypsilon", "Fi", "Chi", "Psi", "Omega"};
    private ArrayList<Object> deklinacja = new ArrayList<>();
    private int deklinacjaStopnie = 0;
    private int deklinacjaMinuty = 0;
    private float deklinacjaSekundy = 0.0f;
    private ArrayList<Object> rektascensja = new ArrayList<>();
    private int rektascensjaH = 0;
    private int rektascensjaM = 0;
    private int rektascensjaS = 0;
    private double obserwowanaWielkoscGwiazdowa;
    private double absolutnaWielkoscGwiazdowa = 0.0d;
    private float odleglosc = 0.0f;
    private static HashMap<String, ArrayList<Gwiazda>> gwiazdozbiory = new HashMap<>();
    private String gwiazdozbior = "";
    private Polkula polkula;
    private int temperatura = 0;
    private float masa = 0.0f;

    public Gwiazda(String nazwa, int stopnieDeklinacji, int minutyDeklinacji, float sekundyDeklinacji, int rektascensjaH, int rektascensjaM, int rektascensjaS,
                    double obserwowanaWielkoscGwiazdowa, float odleglosc, String gwiazdozbior, String polkula, int temperatura, float masa) {
        if (Pattern.matches(nazwaPattern, nazwa)) {
            this.nazwa = nazwa;
        }
        else{
            throw new IllegalArgumentException("Nazwa gwiazdy nie spełnia wymagań. ");
        }
        if (polkula.equals("PN")){
            this.polkula = Polkula.PN;
        }
        else if (polkula.equals("PD")){
            this.polkula = Polkula.PD;
        }
        else{
            throw new IllegalArgumentException("Dostępne opcje: PN (południowa), PD (północna)");
        }
        if (stopnieDeklinacji == 90) {
            this.deklinacjaStopnie = stopnieDeklinacji;
            this.deklinacjaMinuty = 0;
            this.deklinacjaSekundy = 0.0f;
        }
        else if (stopnieDeklinacji >= 0 && stopnieDeklinacji < 90 ){
            this.deklinacjaStopnie = stopnieDeklinacji;
            if (minutyDeklinacji == 60){
                this.deklinacjaMinuty = minutyDeklinacji;
                this.deklinacjaSekundy = 0.0f;
            }
            else if (minutyDeklinacji >= 0 && minutyDeklinacji < 60){
                this.deklinacjaMinuty = minutyDeklinacji;
                if (sekundyDeklinacji >= 0 && sekundyDeklinacji <= 60.0f){
                    this.deklinacjaSekundy = sekundyDeklinacji;
                }
                else{
                    throw new IllegalArgumentException("Sekundy deklinacji powinny być w zakresie 0-60");
                }
            }
            else{
                throw new IllegalArgumentException("Minuty deklinacji powinny być w zakresie 0-60");
            }
        }
        else{
            throw new IllegalArgumentException("Stopnie deklinacji powinny być w zakresie 0-90");
        }
        this.deklinacja.add(this.deklinacjaStopnie);
        this.deklinacja.add(this.deklinacjaMinuty);
        this.deklinacja.add(this.deklinacjaSekundy);
        if (rektascensjaH == 24){
            this.rektascensjaH = rektascensjaH;
            this.rektascensjaM = 0;
            this.rektascensjaS = 0;
        }
        else if (rektascensjaH >= 0 && rektascensjaH < 24){
            this.rektascensjaH = rektascensjaH;
            if (rektascensjaM == 0){
                this.rektascensjaM = rektascensjaM;
                this.rektascensjaS = 0;
            }
            else if (rektascensjaM >= 0 && rektascensjaM < 60){
                this.rektascensjaM = rektascensjaM;
                if (rektascensjaS >= 0 && rektascensjaS <= 60){
                    this.rektascensjaS = rektascensjaS;
                }
                else{
                    throw new IllegalArgumentException("Sekundy rektascensji powinny być w zakresie 0-60");
                }
            }
            else{
                throw new IllegalArgumentException("Minuty rektascensji powinny być w zakresie 0-60");
            }
        }
        else{
            throw new IllegalArgumentException("Godziny rektascensji powinny być w zakresie 0-24");
        }
        this.rektascensja.add(this.rektascensjaH);
        this.rektascensja.add(this.rektascensjaM);
        this.rektascensja.add(this.rektascensjaS);
        if (obserwowanaWielkoscGwiazdowa >= -26.74d && obserwowanaWielkoscGwiazdowa <= 15.00d){
            this.obserwowanaWielkoscGwiazdowa = obserwowanaWielkoscGwiazdowa;
        }
        else{
            throw new IllegalArgumentException("Obserwowana wielość gwiazdowa powinna być w zakresie -26.74 - 15.00");
        }
        this.odleglosc = odleglosc;
        double r = odleglosc / 3.26d;
        this.absolutnaWielkoscGwiazdowa = this.obserwowanaWielkoscGwiazdowa - 5 * Math.log10(r) + 5;
        this.gwiazdozbior = gwiazdozbior;
        if (temperatura >= 2000){
            this.temperatura = temperatura;
        }
        else{
            throw new IllegalArgumentException("Minimalna wartość temperatury to 2000.");
        }
        if (masa >= 0.1 && masa <= 50.0f){
            this.masa = masa;
        }
        else{
            throw new IllegalArgumentException("Masa powinna być w zakresie 0.1 - 50. ");
        }
        gwiazdozbiory.putIfAbsent(gwiazdozbior, new ArrayList<>());
        gwiazdozbiory.get(gwiazdozbior).add(this);
        ustawNazwyKat(gwiazdozbior);
        serializuj(this);
    }

    public static void ustawNazwyKat(String gwiazdozbior){
        for (int i = 0; i < gwiazdozbiory.get(gwiazdozbior).size(); i++){
            gwiazdozbiory.get(gwiazdozbior).get(i).nazwaKatalogowa = literyGreckie[i] + " " + gwiazdozbior;
        }
    }

    public static void serializuj(Gwiazda gwiazda){
        try {
            String folderPath = "src\\gwiazdy\\" + gwiazda.gwiazdozbior;
            File folder = new File(folderPath);

            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (created) {
                    System.out.println("Folder utworzony: " + folderPath);
                } else {
                    System.out.println("Nie udało się utworzyć folderu: " + folderPath);
                }
            }

            String filePath = folderPath + "\\" + gwiazda.nazwa + ".obj";
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(gwiazda);
            out.close();
            fileOut.close();

            System.out.println("Obiekt zapisany w: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString(){
        return "[1] " + this.nazwa + "  [2] " + this.nazwaKatalogowa + "     [3] " + this.deklinacja + "    [4] " + this.rektascensja + "  [5] " +
                this.obserwowanaWielkoscGwiazdowa + "      [6] " + this.absolutnaWielkoscGwiazdowa + "      [7] " + this.temperatura + "    [8] " + this.masa +
                 "      [9] " + this.odleglosc + "      [10] " + this.polkula;
    }
    public static void wyswietl(){
        System.out.println("Gwiazdozbiór    1 Nazwa  2 Nazwa Katalogowa   3 Deklinacja      4 Rektascensja   5 Obserwowana Wielkość Gwiazdowa" +
                "    6 Absolutna Wielkość Gwiazdowa    7 Temperatura    8 Masa   9 Odległość  10 Półkula");
        for (Map.Entry<String, ArrayList<Gwiazda>> entry : gwiazdozbiory.entrySet()){
            System.out.println(entry.getKey());
            for (Gwiazda gwiazda : entry.getValue()){
                System.out.println("               " + gwiazda);
            }
        }
    }

    public static ArrayList<Gwiazda> wyszukaj(String input){
        ArrayList<Gwiazda> wynik = new ArrayList<>();
        try {
            Polkula p = Polkula.valueOf(input);
            for (ArrayList<Gwiazda> gwiazdozbior : gwiazdozbiory.values()){
                for (Gwiazda gwiazda : gwiazdozbior){
                    if (gwiazda.polkula == p){
                        wynik.add(gwiazda);
                    }
                }
            }
        } catch (Exception e){
            if (gwiazdozbiory.containsKey(input)) {
                wynik = gwiazdozbiory.get(input);
            } else {
                wynik = new ArrayList<>();  // pusta lista jeśli input nie pasuje do niczego
            }
        }
        return wynik;
    }


    public static ArrayList<Gwiazda> wyszukaj(float parseki){
        ArrayList<Gwiazda> wynik = new ArrayList<>();
        for (ArrayList<Gwiazda> gwiazdozbior : gwiazdozbiory.values()){
            for (Gwiazda gwiazda : gwiazdozbior){
                if (gwiazda.odleglosc == parseki * 3.2616f){
                    wynik.add(gwiazda);
                }
            }
        }
        return wynik;
    }

    public static ArrayList<Gwiazda> wyszukaj(int dolnaGranica, int gornaGranica){
        ArrayList<Gwiazda> wynik = new ArrayList<>();
        for (ArrayList<Gwiazda> gwiazdozbior : gwiazdozbiory.values()){
            for (Gwiazda gwiazda : gwiazdozbior){
                if (gwiazda.temperatura > dolnaGranica && gwiazda.temperatura < gornaGranica){
                    wynik.add(gwiazda);
                }
            }
        }
        return wynik;
    }

    public static ArrayList<Gwiazda> wyszukaj(double dolnaGranica, double gornaGranica){
        ArrayList<Gwiazda> wynik = new ArrayList<>();
        for (ArrayList<Gwiazda> gwiazdozbior : gwiazdozbiory.values()){
            for (Gwiazda gwiazda : gwiazdozbior){
                if (gwiazda.absolutnaWielkoscGwiazdowa > dolnaGranica && gwiazda.temperatura < gornaGranica){
                    wynik.add(gwiazda);
                }
            }
        }
        return wynik;
    }

    public static ArrayList<Gwiazda> wyszukajSupernowe(){
        ArrayList<Gwiazda> wynik = new ArrayList<>();
        for (ArrayList<Gwiazda> gwiazdozbior : gwiazdozbiory.values()){
            for (Gwiazda gwiazda : gwiazdozbior){
                if (gwiazda.masa > 1.44f){
                    wynik.add(gwiazda);
                }
            }
        }
        return wynik;
    }

    public static void usun(String nazwaKataloga){
        String gwiazdozbior = nazwaKataloga.split(" ")[1];
        for (Gwiazda gwiazda : gwiazdozbiory.get(gwiazdozbior)){
            if (gwiazda.nazwaKatalogowa.equals(nazwaKataloga)){
                gwiazdozbiory.get(gwiazda.gwiazdozbior).remove(gwiazda);
                ustawNazwyKat(gwiazda.gwiazdozbior);
            }
        }
    }
}