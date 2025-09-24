//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
            /*CODICE MIO TEST MAIN
            String[] fields = { "Nome", "Cognome","DataN", "MateriaP"};
            String[] r1 = { "Mario", "Rossi", "01/01/2000", "Matematica" };

            Relazione r = new Relazione("Relazione studenti", fields);
            r.insert(r1);
            System.out.println(r.toString());

            System.out.println(r.proiezione(new String[]{"Nome", "Cognome"}));
            System.out.println(r.crossProduct(r1));//sistemare
            System.out.println(r1.union(r2));

             */


            //MAIN CON FILE CSV
            Relazione r = Relazione.load("", "Studente");

            Relazione r1 = Relazione.load("", "Aula");

            Relazione r2 = Relazione.load("", "Docente");

            if(r !=null && r1 !=null && r2 !=null) {
                System.out.println(r.proiezione(new String[]{"Nome", "Cognome"}));
                System.out.println(r.crossProduct(r1));
                System.out.println(r.union(r2));
                System.out.println(r.join(r2, "Classe = ClasseId"));
            }
        }
    }
