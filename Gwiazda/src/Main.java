public class Main {
    public static void main(String[] args) {    // test działania programu, dane są wymyślone
        Gwiazda test1 = new Gwiazda("ABC1234", 26,55,43.42f,12,59,13,3.9d,14.4f,"Baran","PD",3000,0.1f);
        Gwiazda test2 = new Gwiazda("OOP0000", 62, 8,8.26f,3,6,21,-26.74f,100.0f,"Baran", "PN",2000,1.45f);
        Gwiazda test3 = new Gwiazda("TES7777", 33, 60, 59.99f, 6, 53, 11, 13.03f, 2.0f, "Kasjopeja", "PD", 9999, 1.14f);
        Gwiazda test4 = new Gwiazda("LAF9052", 32, 22, 59.99f, 7, 0, 32, 9.53f, 0.4f, "Kasjopeja", "PN", 123456, 0.7f);
        Gwiazda.wyswietl();
        Gwiazda.usun("Alfa Baran");
        Gwiazda.wyswietl();
        System.out.println("============================================================================================");
        System.out.println(Gwiazda.wyszukaj("Kasjopeja"));  // działają poprawnie ale wyświetlają się w jednej linii
        System.out.println("============================================================================================");
        System.out.println(Gwiazda.wyszukaj("PN"));
        System.out.println("============================================================================================");
        System.out.println(Gwiazda.wyszukajSupernowe());

    }
}