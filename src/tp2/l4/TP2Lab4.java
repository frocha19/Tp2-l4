package tp2.l4;

import entities.Pais;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TP2Lab4 {

    public static void main(String[] args) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("TP2-l4");
        EntityManager em = emf.createEntityManager();
        JSONParser parser = new JSONParser();
        String restUrl = "https://restcountries.eu/rest/v2/callingcode/";
        Pais paisSql = new Pais();
        for (int codigo = 1; codigo <= 300; codigo++) {
            try {
                URL rutaJson = new URL(restUrl + codigo);
                URLConnection yc = rutaJson.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                JSONArray a = (JSONArray) parser.parse(in.readLine());
                if (a != null) {
                    for (Object object : a) {
                        em.getTransaction().begin();
                        JSONObject paisJson = (JSONObject) object;
                        if (paisJson.get("name").toString().length() <= 50) {
                            paisSql.setNombrePais((String) paisJson.get("name"));
                        } else {
                            paisSql.setNombrePais((String) paisJson.get("name").toString().substring(50));
                        }
                        paisSql.setCapitalPais((String) paisJson.get("capital"));
                        paisSql.setPoblacion((Long) paisJson.get("population"));
                        paisSql.setRegion((String) paisJson.get("region"));
                        List coorGeo = (List) paisJson.get("latlng");
                        paisSql.setLatitud((double) coorGeo.get(0));
                        paisSql.setLongitud((double) coorGeo.get(1));
                        paisSql.setCodigoPais(codigo);
                        em.merge(paisSql);
                        em.flush();
                        em.getTransaction().commit();
                    }
                    System.out.println("Pais encontrado con el codigo: " + codigo);
                } else {
                    continue;
                }
                in.close();
            } catch (Exception e) {
                System.out.println("No existe un pais con el código: " + codigo);
            }
        }
        em.close();
        emf.close();
        System.gc();
    }
}
