import java.util.ArrayList;
import java.io.*;


public class Relazione {

    private String[] fields_name;

    private String name;

    private ArrayList<String[]> data;

    public Relazione(String name, String[] fields_name) {
        this.name = name;
        this.fields_name = fields_name;
        data = new ArrayList<String[]>();
    }

    @Override
    public String toString() {
        int rowLength = 20 * fields_name.length + (fields_name.length - 1);
        String str = "+";
        str += String.format("%-" + rowLength + "s", "").replace(" ", "-");
        str += "+\n";
        str += "|" + String.format("%-" + rowLength + "s", name) + "|";
        str += "\n+";
        str += String.format("%-" + rowLength + "s", "").replace(" ", "-");
        str += "+\n";

        str += "|";
        for (int i = 0; i < fields_name.length; i++) {
            str += String.format("%-20s", fields_name[i]) + "|";
        }

        str += "\n+" + String.format("%-" + rowLength + "s", "").replace(" ", "-") + "+\n";

        for(String[] row : data) {
            str += "|";
            for (int i = 0; i < fields_name.length; i++) {
                str += String.format("%-20s", row[i]) + "|";
            }
            str += "\n+" + String.format("%-" + rowLength + "s", "").replace(" ", "-") + "+\n";
        }

//    str += "\n+";
//    str += String.format("%-" + rowLength + "s", "").replace(" ", "-");
//    str += "+\n";

        return str;
    }


    public boolean isDuplicate(String[] row) {
        boolean equals;
        for(String[] r : data){
            equals = true;
            for(int i = 0; i < r.length; i++){
                if(!r[i].equals(row[i])){
                    equals = false;
                    i = row.length;
                }
            }
            if(equals) return true;
        }
        return false;
    }


    public void insert(String[] row) {
        if (!isDuplicate(row)) {
            String[] r = new String[fields_name.length];
            System.arraycopy(row, 0, r, 0, r.length);
            data.add(r);

        }
    }

    public void save(String path) {

        try {
            PrintWriter wr = new PrintWriter(new FileWriter(".csv"));
            for (String fn : fields_name) {
                wr.print(fn + ",");
            }
            wr.println("");
            for (String[] row : data) {
                for (String f : row) {
                    wr.print(f + ",");
                }
                wr.println("");
            }
            wr.flush();
            wr.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public static Relazione load(String path, String name) {

        try {
            BufferedReader re = new BufferedReader(new FileReader(path + name + ".csv"));
            String line = re.readLine();
            String[] fn = line.split(",");
            Relazione res = new Relazione(name, fn);
            String[] row;
            while ((line = re.readLine()) != null) {
                row = line.split(",");
                res.insert(row);
            }

            re.close();
            return res;
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        return null;
    }

    public Relazione differenza(Relazione r) {
        Relazione res = new Relazione("R1(differenza)", fields_name);
        for (String[] row : this.data) {
            if (!r.isDuplicate(row)) {
                res.insert(row);
            }
        }
        return res;
    }

    private int getFieldIndex(String fn) {
        for (int i = 0; i < fields_name.length; i++) {
            if (fn.equals(fields_name[i])) return i;
        }
        return -1;
    }

    public Relazione selezione(String condizione) {

        Relazione res = new Relazione("R1(selezione)", fields_name);
        String[] cond = condizione.split(" ");
        int fi = getFieldIndex(cond[0]);

        if (fi != -1) {
            for (String[] row : data) {
                if (cond[1].equals("=")) {
                    if (cond[2].equals(row[fi])) {
                        res.insert(row);
                    }
                } else if (cond[1].equals("<>")) {
                    if (!cond[2].equals(row[fi])) {
                        res.insert(row);
                    }
                }
            }
        }
        return res;
    }

    public Relazione proiezione(String[] fn) {
        Relazione res = new Relazione("R1(proiezione)", fn);
        int[] field_index = new int[fn.length];

        for (int i = 0; i < field_index.length; i++) {
            field_index[i] = getFieldIndex(fn[i]);
        }
        String[] new_row = new String[fn.length];
        for (String[] row : data) {
            for (int i = 0; i < new_row.length; i++) {
                new_row[i] = row[field_index[i]];
            }
            res.insert(new_row);
        }
        return res;
    }


    public Relazione union(Relazione r) {
        Relazione res = new Relazione("R1(union)", fields_name);

        for (String[] row : this.data) {
            res.insert(row);
        }
        for (String[] row : r.data) {
            res.insert(row);
        }
        return res;
    }

    public Relazione crossProduct(Relazione r) {
        String[] new_fields = new String[this.fields_name.length + r.fields_name.length];
        System.arraycopy(this.fields_name, 0, new_fields, 0, this.fields_name.length);
        System.arraycopy(r.fields_name, 0, new_fields, this.fields_name.length, r.fields_name.length);
        Relazione res = new Relazione("R1(cross product)", new_fields);
        String[] new_row = new String[new_fields.length];
        for (String[] row1 : this.data) {
            System.arraycopy(row1, 0, new_row, 0, row1.length);
            for (String[] row2 : r.data) {
                System.arraycopy(row2, 0, new_row, row1.length, row2.length);
                res.insert(new_row);
            }
        }
        return res;
    }

    public Relazione join(Relazione r, String condizione){
        String[] new_fields = new String[this.fields_name.length + r.fields_name.length];

        System.arraycopy(this.fields_name, 0, new_fields, 0, this.fields_name.length);
        System.arraycopy(r.fields_name, 0, new_fields, this.fields_name.length, r.fields_name.length);

        Relazione res = new Relazione("R1(join)", new_fields);

        String[] new_row = new String[new_fields.length];
        String[] cond_split = condizione.split(" ");
        int fithis = getFieldIndex(cond_split[0]);
        int fir = r.getFieldIndex(cond_split[2]);

        for(String[] rthis : this.data){
            System.arraycopy(rthis, 0, new_row, 0, rthis.length);
            for(String[] rr : r.data){
                System.arraycopy(rr, 0, new_row, rthis.length, rr.length);
                if(cond_split[1].equals("=")){
                    if(rthis[fithis].equals(rr[fir])){
                        res.insert(new_row);
                    }
                } else if(cond_split[1].equals("<>")){
                    if(!rthis[fithis].equals(rr[fir])){
                        res.insert(new_row);
                    }
                }
            }
        }
        return res;
    }


//    //natural join diversa dalla join normale
//    public Relazione nJoin(Relazione r, String cond){
//
//    }
}


